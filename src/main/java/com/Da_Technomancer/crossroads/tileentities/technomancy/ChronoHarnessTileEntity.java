package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.API.technomancy.IFluxLink;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.Da_Technomancer.essentials.packets.SendLongToClient;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
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
import java.util.HashSet;
import java.util.Set;

@ObjectHolder(Crossroads.MODID)
public class ChronoHarnessTileEntity extends TileEntity implements IFluxLink, ITickableTileEntity{

	@ObjectHolder("chrono_harness")
	private static TileEntityType<ChronoHarnessTileEntity> type = null;

	public static final int POWER = 100;
	private static final int FE_CAPACITY = 2_000;//Be careful about raising this- otherwise placing down this machine will instantly cause a flux event from filling the buffer

	private int flux = 0;//Stored flux
	private int fe = 0;//Stored FE
	private int curPower = 0;//Current power generation (fe/t); used for readouts
	private int clientCurPower = 0;//Current power gen on the client; used for rendering. On the server side, tracks last sent value
	private float angle = 0;//Used for rendering. Client side only
	private final HashSet<BlockPos> link = new HashSet<>(1);

	public ChronoHarnessTileEntity(){
		super(type);
	}

	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		chat.add(new TranslationTextComponent("tt.crossroads.chrono_harness.fe", fe, FE_CAPACITY, curPower));
		FluxUtil.addFluxInfo(chat, this, shouldRun() ? curPower / EnergyConverters.getFePerFlux() : 0);
		FluxUtil.addLinkInfo(chat, this);
	}

	public float getRenderAngle(float partialTicks){
		return (float) (angle + partialTicks * Math.PI / 20F * clientCurPower / POWER);
	}

	private boolean hasRedstone(){
		BlockState state = world.getBlockState(pos);
		if(state.getBlock() == CRBlocks.chronoHarness){
			return state.get(ESProperties.REDSTONE_BOOL);
		}
		remove();
		return true;
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayerEntity sendingPlayer){
		if(identifier == 4){
			curPower = (int) message;//Just used as a way of sending power gen
		}
		if(identifier == LINK_PACKET_ID){
			link.add(BlockPos.fromLong(message));
			markDirty();
		}else if(identifier == CLEAR_PACKET_ID){
			link.clear();
			markDirty();
		}
	}

	private boolean shouldRun(){
		return fe < FE_CAPACITY && !hasRedstone();
	}

	@Override
	public void tick(){
		if(world.isRemote){
			angle += Math.PI / 20F * clientCurPower / POWER;//Maximum of 0.5RPS
		}else{
			if(world.getGameTime() % FluxUtil.FLUX_TIME == 1){
				FluxUtil.performTransfer(this, link);
			}

			if(shouldRun()){
				curPower = Math.min(POWER, FE_CAPACITY - fe);
				fe += curPower;
				flux += Math.round((float) curPower / EnergyConverters.getFePerFlux());
				markDirty();
				FluxUtil.checkFluxOverload(this);
			}

			if((curPower == 0 ^ clientCurPower == 0) || Math.abs(curPower - clientCurPower) >= 10){
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
		nbt.putInt("flux", flux);
		nbt.putInt("pow", curPower);
		for(BlockPos linked : link){//Size 0 or 1
			nbt.putLong("link", linked.toLong());
		}

		return nbt;
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		fe = nbt.getInt("fe");
		flux = nbt.getInt("flux");
		curPower = nbt.getInt("pow");
		clientCurPower = curPower;
		if(nbt.contains("link")){
			link.add(BlockPos.fromLong(nbt.getLong("link")));
		}else{
			link.clear();
		}
	}

	@Override
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
		nbt.putInt("pow", curPower);
		for(BlockPos linked : link){//Size 0 or 1
			nbt.putLong("link", linked.toLong());
		}
		return nbt;
	}

	@Override
	public int getFlux(){
		return flux;
	}

	@Override
	public void setFlux(int newFlux){
		flux = newFlux;
		markDirty();
	}

	@Override
	public Set<BlockPos> getLinks(){
		return link;
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
