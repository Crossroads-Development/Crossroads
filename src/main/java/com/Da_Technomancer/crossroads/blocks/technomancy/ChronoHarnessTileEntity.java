package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.electric.IEnergyCapable;
import com.Da_Technomancer.crossroads.api.packets.CRPackets;
import com.Da_Technomancer.crossroads.api.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.api.technomancy.IFluxLink;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.essentials.api.packets.SendLongToTE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class ChronoHarnessTileEntity extends IFluxLink.FluxHelper implements IEnergyCapable{

	public static final BlockEntityType<ChronoHarnessTileEntity> TYPE = CRTileEntity.createType(ChronoHarnessTileEntity::new, CRBlocks.chronoHarness);

	private static final int FE_CAPACITY = 20_000;
	private static final float SPEED = (float) Math.PI / 20F / 400F;//Used for rendering

	private int fe = FE_CAPACITY;//Stored FE. Placed with full FE
	private int curPower = 0;//Current power generation (fe/t); used for readouts
	private int clientCurPower = 0;//Current power gen on the client; used for rendering. On the server side, tracks last sent value
	private float angle = 0;//Used for rendering. Client side only

	private final IEnergyStorage energyHandler = new EnergyHandler();

	public ChronoHarnessTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, null, Behaviour.SOURCE);
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		chat.add(Component.translatable("tt.crossroads.chrono_harness.fe", fe, FE_CAPACITY, curPower));
		FluxUtil.addFluxInfo(chat, this, shouldRun() ? curPower / CRConfig.fePerEntropy.get() : 0);
		super.addInfo(chat, player, hit);
	}

	public float getRenderAngle(float partialTicks){
		return (float) Math.toDegrees(angle + partialTicks * clientCurPower * SPEED);
	}

	private boolean hasRedstone(){
		BlockState state = getBlockState();
		if(state.getBlock() == CRBlocks.chronoHarness){
			return state.getValue(CRProperties.REDSTONE_BOOL);
		}
		setRemoved();
		return true;
	}

	private boolean shouldRun(){
		return !hasRedstone() && !isShutDown();
	}

	@Override
	public void clientTick(){
		super.clientTick();
		angle += clientCurPower * SPEED;
	}

	@Override
	public void serverTick(){
		super.serverTick();
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
			CRPackets.sendPacketAround(level, worldPosition, new SendLongToTE((byte) 4, clientCurPower, worldPosition));
		}

		if(fe != 0){
			//Transfer FE to a machine above
			IEnergyStorage otherEnergyHandler;
			if((otherEnergyHandler = level.getCapability(Capabilities.EnergyStorage.BLOCK, worldPosition.relative(Direction.UP), Direction.DOWN)) != null){
				if(otherEnergyHandler.canReceive()){
					fe -= otherEnergyHandler.receiveEnergy(fe, false);
					setChanged();
				}
			}
			//Transfer FE to a machine below

			if((otherEnergyHandler = level.getCapability(Capabilities.EnergyStorage.BLOCK, worldPosition.relative(Direction.DOWN), Direction.UP)) != null){
				if(otherEnergyHandler.canReceive()){
					fe -= otherEnergyHandler.receiveEnergy(fe, false);
					setChanged();
				}
			}
		}
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		fe = nbt.getInt("fe");
		curPower = nbt.getInt("pow");
		clientCurPower = curPower;
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries){
		CompoundTag nbt = super.getUpdateTag(pRegistries);
		nbt.putInt("pow", curPower);
		return nbt;
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		nbt.putInt("fe", fe);
		nbt.putInt("pow", curPower);

	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayer sendingPlayer){
		if(identifier == 4){
			clientCurPower = (int) message;//Just used as a way of sending power gen
		}
		super.receiveLong(identifier, message, sendingPlayer);
	}

	@Nullable
	@Override
	public IEnergyStorage getEnergyHandler(Direction dir){
		return energyHandler;
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
