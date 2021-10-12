package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.API.technomancy.IFluxLink;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.Da_Technomancer.essentials.packets.SendLongToClient;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;

import com.Da_Technomancer.crossroads.API.technomancy.IFluxLink.Behaviour;

@ObjectHolder(Crossroads.MODID)
public class ChronoHarnessTileEntity extends IFluxLink.FluxHelper{

	@ObjectHolder("chrono_harness")
	public static BlockEntityType<ChronoHarnessTileEntity> type = null;

	private static final int FE_CAPACITY = 20_000;
	private static final float SPEED = (float) Math.PI / 20F / 400F;//Used for rendering

	private int fe = FE_CAPACITY;//Stored FE. Placed with full FE
	private int curPower = 0;//Current power generation (fe/t); used for readouts
	private int clientCurPower = 0;//Current power gen on the client; used for rendering. On the server side, tracks last sent value
	private float angle = 0;//Used for rendering. Client side only

	public ChronoHarnessTileEntity(BlockPos pos, BlockState state){
		super(type, null, Behaviour.SOURCE);
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		chat.add(new TranslatableComponent("tt.crossroads.chrono_harness.fe", fe, FE_CAPACITY, curPower));
		FluxUtil.addFluxInfo(chat, this, shouldRun() ? curPower / CRConfig.fePerEntropy.get() : 0);
		super.addInfo(chat, player, hit);
	}

	public float getRenderAngle(float partialTicks){
		return (float) Math.toDegrees(angle + partialTicks * clientCurPower * SPEED);
	}

	@Override
	public AABB getRenderBoundingBox(){
		//Increase render BB to include links
		return new AABB(worldPosition).inflate(getRange());
	}

	private boolean hasRedstone(){
		BlockState state = getBlockState();
		if(state.getBlock() == CRBlocks.chronoHarness){
			return state.getValue(ESProperties.REDSTONE_BOOL);
		}
		setRemoved();
		return true;
	}

	private boolean shouldRun(){
		return !hasRedstone() && !isShutDown();
	}

	@Override
	public void tick(){
		super.tick();//Handle flux

		if(level.isClientSide){
			angle += clientCurPower * SPEED;
		}else{
			if(shouldRun()){
				curPower = FE_CAPACITY - fe;
				if(curPower > 0){
					fe += curPower;
					addFlux(Math.round((float) curPower / CRConfig.fePerEntropy.get()));
					setChanged();
				}
			}

			if(((curPower == 0) ^ (clientCurPower == 0)) || Math.abs(curPower - clientCurPower) >= 10){
				clientCurPower = curPower;
				CRPackets.sendPacketAround(level, worldPosition, new SendLongToClient((byte) 4, clientCurPower, worldPosition));
			}

			if(fe != 0){
				//Transfer FE to a machine above
				BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(Direction.UP));
				LazyOptional<IEnergyStorage>  otherOpt;
				if(neighbor != null && (otherOpt = neighbor.getCapability(CapabilityEnergy.ENERGY, Direction.DOWN)).isPresent()){
					IEnergyStorage storage = otherOpt.orElseThrow(NullPointerException::new);
					if(storage.canReceive()){
						fe -= storage.receiveEnergy(fe, false);
						setChanged();
					}
				}
				//Transfer FE to a machine below
				neighbor = level.getBlockEntity(worldPosition.relative(Direction.DOWN));
				if(neighbor != null && (otherOpt = neighbor.getCapability(CapabilityEnergy.ENERGY, Direction.UP)).isPresent()){
					IEnergyStorage storage = otherOpt.orElseThrow(NullPointerException::new);
					if(storage.canReceive()){
						fe -= storage.receiveEnergy(fe, false);
						setChanged();
					}
				}
			}
		}
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		fe = nbt.getInt("fe");
		curPower = nbt.getInt("pow");
		clientCurPower = curPower;
	}

	@Override
	public CompoundTag getUpdateTag(){
		CompoundTag nbt = super.getUpdateTag();
		nbt.putInt("pow", curPower);
		return nbt;
	}

	@Override
	public CompoundTag save(CompoundTag nbt){
		super.save(nbt);
		nbt.putInt("fe", fe);
		nbt.putInt("pow", curPower);

		return nbt;
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayer sendingPlayer){
		if(identifier == 4){
			clientCurPower = (int) message;//Just used as a way of sending power gen
		}
		super.receiveLong(identifier, message, sendingPlayer);
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
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
				setChanged();
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
