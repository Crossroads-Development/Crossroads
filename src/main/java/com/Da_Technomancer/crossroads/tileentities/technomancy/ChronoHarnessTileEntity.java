package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.API.technomancy.IFluxLink;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.Da_Technomancer.essentials.packets.SendLongToClient;
import com.Da_Technomancer.essentials.tileentities.ILinkTE;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Set;

@ObjectHolder(Crossroads.MODID)
public class ChronoHarnessTileEntity extends TileEntity implements IFluxLink, ITickableTileEntity{

	@ObjectHolder("chrono_harness")
	public static TileEntityType<ChronoHarnessTileEntity> type = null;

	private static final int FE_CAPACITY = 20_000;
	private static final float SPEED = (float) Math.PI / 20F / 400F;//Used for rendering

	private final FluxHelper fluxHelper = new FluxHelper(this, Behaviour.SOURCE);
	private int fe = FE_CAPACITY;//Stored FE. Placed with full FE
	private int curPower = 0;//Current power generation (fe/t); used for readouts
	private int clientCurPower = 0;//Current power gen on the client; used for rendering. On the server side, tracks last sent value
	private float angle = 0;//Used for rendering. Client side only

	public ChronoHarnessTileEntity(){
		super(type);
	}

	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		chat.add(new TranslationTextComponent("tt.crossroads.chrono_harness.fe", fe, FE_CAPACITY, curPower));
		FluxUtil.addFluxInfo(chat, this, shouldRun() ? curPower / CRConfig.fePerEntropy.get() : 0);
		fluxHelper.addInfo(chat, player, hit);
	}

	public float getRenderAngle(float partialTicks){
		return (float) Math.toDegrees(angle + partialTicks * clientCurPower * SPEED);
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox(){
		//Increase render BB to include links
		return new AxisAlignedBB(pos).grow(getRange());
	}

	private boolean hasRedstone(){
		BlockState state = getBlockState();
		if(state.getBlock() == CRBlocks.chronoHarness){
			return state.get(ESProperties.REDSTONE_BOOL);
		}
		remove();
		return true;
	}

	private boolean shouldRun(){
		return !hasRedstone();
	}

	@Override
	public void tick(){
		if(world.isRemote){
			angle += clientCurPower * SPEED;
		}else{
			//Handle flux
			fluxHelper.tick();

			if(shouldRun()){
				curPower = FE_CAPACITY - fe;
				if(curPower > 0){
					fe += curPower;
					fluxHelper.addFlux(Math.round((float) curPower / CRConfig.fePerEntropy.get()));
					markDirty();
					FluxUtil.checkFluxOverload(this);
				}
			}

			if(((curPower == 0) ^ (clientCurPower == 0)) || Math.abs(curPower - clientCurPower) >= 10){
				clientCurPower = curPower;
				CRPackets.sendPacketAround(world, pos, new SendLongToClient((byte) 4, clientCurPower, pos));
			}

			if(fe != 0){
				//Transfer FE to a machine above
				TileEntity neighbor = world.getTileEntity(pos.offset(Direction.UP));
				LazyOptional<IEnergyStorage>  otherOpt;
				if(neighbor != null && (otherOpt = neighbor.getCapability(CapabilityEnergy.ENERGY, Direction.DOWN)).isPresent()){
					IEnergyStorage storage = otherOpt.orElseThrow(NullPointerException::new);
					if(storage.canReceive()){
						fe -= storage.receiveEnergy(fe, false);
						markDirty();
					}
				}
				//Transfer FE to a machine below
				neighbor = world.getTileEntity(pos.offset(Direction.DOWN));
				if(neighbor != null && (otherOpt = neighbor.getCapability(CapabilityEnergy.ENERGY, Direction.UP)).isPresent()){
					IEnergyStorage storage = otherOpt.orElseThrow(NullPointerException::new);
					if(storage.canReceive()){
						fe -= storage.receiveEnergy(fe, false);
						markDirty();
					}
				}
			}
		}
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt){
		super.read(state, nbt);
		fe = nbt.getInt("fe");
		curPower = nbt.getInt("pow");
		clientCurPower = curPower;
		fluxHelper.read(nbt);
	}

	@Override
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
		nbt.putInt("pow", curPower);
		fluxHelper.write(nbt);
		return nbt;
	}

	@Override
	public int getReadingFlux(){
		return fluxHelper.getReadingFlux();
	}

	@Override
	public void addFlux(int deltaFlux){
		fluxHelper.addFlux(deltaFlux);
	}

	@Override
	public boolean canAcceptLinks(){
		return fluxHelper.canAcceptLinks();
	}

	@Override
	public int getFlux(){
		return fluxHelper.getFlux();
	}

	@Override
	public boolean canBeginLinking(){
		return fluxHelper.canBeginLinking();
	}

	@Override
	public boolean canLink(ILinkTE otherTE){
		return fluxHelper.canLink(otherTE);
	}

	@Override
	public Set<BlockPos> getLinks(){
		return fluxHelper.getLinks();
	}

	@Override
	public boolean createLinkSource(ILinkTE endpoint, @Nullable PlayerEntity player){
		return fluxHelper.createLinkSource(endpoint, player);
	}

	@Override
	public void removeLinkSource(BlockPos end){
		fluxHelper.removeLinkSource(end);
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayerEntity sendingPlayer){
		if(identifier == 4){
			clientCurPower = (int) message;//Just used as a way of sending power gen
		}
		fluxHelper.receiveLong(identifier, message, sendingPlayer);
	}

	@Override
	public void remove(){
		super.remove();
		energyOpt.invalidate();
	}

	private final LazyOptional<IEnergyStorage> energyOpt = LazyOptional.of(EnergyHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == CapabilityEnergy.ENERGY){
			return (LazyOptional<T>) energyOpt;
		}

		return super.getCapability(cap, side);
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putInt("fe", fe);
		nbt.putInt("pow", curPower);
		fluxHelper.write(nbt);

		return nbt;
	}

	private class EnergyHandler implements IEnergyStorage{

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate){
			return 0;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate){
			int extracted = Math.min(maxExtract, fe);
			if(!simulate && extracted > 0){
				fe -= extracted;
				markDirty();
			}
			return extracted;
		}

		@Override
		public int getEnergyStored(){
			return fe;
		}

		@Override
		public int getMaxEnergyStored(){
			return FE_CAPACITY;
		}

		@Override
		public boolean canExtract(){
			return true;
		}

		@Override
		public boolean canReceive(){
			return false;
		}
	}
}
