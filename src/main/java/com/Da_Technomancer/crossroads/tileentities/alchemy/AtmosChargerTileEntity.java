package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.alchemy.AtmosChargeSavedData;
import com.Da_Technomancer.crossroads.API.redstone.IAdvancedRedstoneHandler;
import com.Da_Technomancer.crossroads.blocks.alchemy.AtmosCharger;
import com.Da_Technomancer.crossroads.render.RenderUtil;
import com.Da_Technomancer.crossroads.tileentities.electric.TeslaCoilTopTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickableTileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class AtmosChargerTileEntity extends TileEntity implements ITickableTileEntity, IInfoTE{

	private static final int FE_CAPACITY = 20_000;
	private int fe = 0;
	private boolean extractMode;
	private int renderTimer;
	private double lastRedstone = 0;

	@Override
	public void addInfo(ArrayList<String> chat, PlayerEntity player, @Nullable Direction side, BlockRayTraceResult hit){
		int charge = AtmosChargeSavedData.getCharge(world);
		chat.add(charge + "/" + AtmosChargeSavedData.getCapacity() + "FE in atmosphere (" + MiscUtil.betterRound(100D * charge / AtmosChargeSavedData.getCapacity(), 1) + "%)");
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void tick(){
		BlockState state = world.getBlockState(pos);
		if(world.isRemote || !(state.getBlock() instanceof AtmosCharger)){
			return;
		}
		renderTimer--;
		extractMode = state.get(CRProperties.ACTIVE);

		double newReds = (double) AtmosChargeSavedData.getCharge(world) / (double) AtmosChargeSavedData.getCapacity();
		if(Math.abs(lastRedstone - newReds) >= 0.05D){
			lastRedstone = newReds;
			markDirty();
		}

		if(extractMode){
			if(fe > 0){
				for(Direction side : Direction.HORIZONTALS){
					TileEntity te = world.getTileEntity(pos.offset(side));
					if(te != null && te.hasCapability(CapabilityEnergy.ENERGY, side.getOpposite())){
						IEnergyStorage storage = te.getCapability(CapabilityEnergy.ENERGY, side.getOpposite());
						int moved = storage.receiveEnergy(fe, false);
						if(moved > 0){
							fe -= moved;
							markDirty();
						}
					}
				}
			}

			int oldCharge = AtmosChargeSavedData.getCharge(world);
			int op = Math.min((FE_CAPACITY - fe) / 1000, oldCharge / 1000);
			if(op <= 0){
				return;
			}
			fe += op * 1000;
			AtmosChargeSavedData.setCharge(world, oldCharge - op * 1000);
			markDirty();
			if(renderTimer <= 0){
				renderTimer = 10;
				RenderUtil.addArc(world.provider.getDimension(), pos.getX() - 0.8F, pos.getY() + 5.5F, pos.getZ() + 0.5F, pos.getX() + 0.5F, pos.getY() + 5.5F, pos.getZ() - 0.8F, pos.getX() - 0.8F, pos.getY() + 4.5F, pos.getZ() + 0.5F, 1, 0F, (byte) 10, TeslaCoilTopTileEntity.COLOR_CODES[(int) (world.getGameTime() % 3)]);
				RenderUtil.addArc(world.provider.getDimension(), pos.getX() - 0.8F, pos.getY() + 5.5F, pos.getZ() + 0.5F, pos.getX() + 0.5F, pos.getY() + 5.5F, pos.getZ() + 1.8F, pos.getX() - 0.8F, pos.getY() + 4.5F, pos.getZ() + 0.5F, 1, 0F, (byte) 10, TeslaCoilTopTileEntity.COLOR_CODES[(int) (world.getGameTime() % 3)]);
				RenderUtil.addArc(world.provider.getDimension(), pos.getX() + 1.8F, pos.getY() + 5.5F, pos.getZ() + 0.5F, pos.getX() + 0.5F, pos.getY() + 5.5F, pos.getZ() - 0.8F, pos.getX() + 1.8F, pos.getY() + 4.5F, pos.getZ() + 0.5F, 1, 0F, (byte) 10, TeslaCoilTopTileEntity.COLOR_CODES[(int) (world.getGameTime() % 3)]);
				RenderUtil.addArc(world.provider.getDimension(), pos.getX() + 1.8F, pos.getY() + 5.5F, pos.getZ() + 0.5F, pos.getX() + 0.5F, pos.getY() + 5.5F, pos.getZ() + 1.8F, pos.getX() + 1.8F, pos.getY() + 4.5F, pos.getZ() + 0.5F, 1, 0F, (byte) 10, TeslaCoilTopTileEntity.COLOR_CODES[(int) (world.getGameTime() % 3)]);
			}
		}else{
			int oldCharge = AtmosChargeSavedData.getCharge(world);
			int op = Math.min(fe / 1000, (AtmosChargeSavedData.getCapacity() - oldCharge) / 1000);
			if(op <= 0){
				return;
			}
			fe -= op * 1000;
			AtmosChargeSavedData.setCharge(world, oldCharge + op * 1000);
			markDirty();
			if(renderTimer <= 0){
				renderTimer = 10;
				world.playSound(null, pos.getX() + 0.5F, pos.getY() + 2F, pos.getZ() + 0.5F, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 0.1F, 0F);

				RenderUtil.addArc(world.provider.getDimension(), pos.getX() - 0.8F, pos.getY() + 4.5F, pos.getZ() + 0.5F, pos.getX() + 0.5F, pos.getY() + 4.5F, pos.getZ() - 0.8F, pos.getX() - 0.8F, pos.getY() + 5.5F, pos.getZ() + 0.5F, 1, 0F, (byte) 10, TeslaCoilTopTileEntity.COLOR_CODES[(int) (world.getGameTime() % 3)]);
				RenderUtil.addArc(world.provider.getDimension(), pos.getX() - 0.8F, pos.getY() + 4.5F, pos.getZ() + 0.5F, pos.getX() + 0.5F, pos.getY() + 4.5F, pos.getZ() + 1.8F, pos.getX() - 0.8F, pos.getY() + 5.5F, pos.getZ() + 0.5F, 1, 0F, (byte) 10, TeslaCoilTopTileEntity.COLOR_CODES[(int) (world.getGameTime() % 3)]);
				RenderUtil.addArc(world.provider.getDimension(), pos.getX() + 1.8F, pos.getY() + 4.5F, pos.getZ() + 0.5F, pos.getX() + 0.5F, pos.getY() + 4.5F, pos.getZ() - 0.8F, pos.getX() + 1.8F, pos.getY() + 5.5F, pos.getZ() + 0.5F, 1, 0F, (byte) 10, TeslaCoilTopTileEntity.COLOR_CODES[(int) (world.getGameTime() % 3)]);
				RenderUtil.addArc(world.provider.getDimension(), pos.getX() + 1.8F, pos.getY() + 4.5F, pos.getZ() + 0.5F, pos.getX() + 0.5F, pos.getY() + 4.5F, pos.getZ() + 1.8F, pos.getX() + 1.8F, pos.getY() + 5.5F, pos.getZ() + 0.5F, 1, 0F, (byte) 10, TeslaCoilTopTileEntity.COLOR_CODES[(int) (world.getGameTime() % 3)]);
			}
		}
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		fe = nbt.getInt("fe");
		lastRedstone = nbt.getDouble("reds");
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putInt("fe", fe);
		nbt.putDouble("reds", lastRedstone);
		return nbt;
	}

	private ElecHandler feHandler = new ElecHandler();
	private final IAdvancedRedstoneHandler redsHandler = (boolean measure) -> measure ? 15D * lastRedstone : 0;

	@Override
	public boolean hasCapability(Capability<?> cap, Direction side){
		return (cap == CapabilityEnergy.ENERGY && side != Direction.UP) || (cap == Capabilities.ADVANCED_REDSTONE_CAPABILITY && (side == null || side.getAxis() != Direction.Axis.Y)) || super.hasCapability(cap, side);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == CapabilityEnergy.ENERGY && side != Direction.UP){
			return (T) feHandler;
		}
		if((cap == Capabilities.ADVANCED_REDSTONE_CAPABILITY && (side == null || side.getAxis() != Direction.Axis.Y))){
			return (T) redsHandler;
		}
		return super.getCapability(cap, side);
	}

	private class ElecHandler implements IEnergyStorage{


		@Override
		public int receiveEnergy(int maxReceive, boolean simulate){
			if(extractMode){
				return 0;
			}
			int toMove = Math.min(FE_CAPACITY - fe, maxReceive);

			if(!simulate && toMove > 0){
				fe += toMove;
				markDirty();
			}

			return toMove;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate){
			if(!extractMode){
				return 0;
			}

			int toMove = Math.min(maxExtract, fe);
			if(!simulate){
				fe -= toMove;
				markDirty();
			}
			return toMove;
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
			return extractMode;
		}

		@Override
		public boolean canReceive(){
			return !extractMode;
		}
	}
}
