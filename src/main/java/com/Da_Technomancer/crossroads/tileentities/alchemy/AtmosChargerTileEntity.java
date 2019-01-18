package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.alchemy.AtmosChargeSavedData;
import com.Da_Technomancer.crossroads.API.redstone.IAdvancedRedstoneHandler;
import com.Da_Technomancer.crossroads.blocks.alchemy.AtmosCharger;
import com.Da_Technomancer.crossroads.render.RenderUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class AtmosChargerTileEntity extends TileEntity implements ITickable, IInfoTE{

	private static final int FE_CAPACITY = 20_000;
	private int fe = 0;
	private boolean extractMode;
	private int renderTimer;

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, @Nullable EnumFacing side, float hitX, float hitY, float hitZ){
		int charge = AtmosChargeSavedData.getCharge(world);
		chat.add(charge + "/" + AtmosChargeSavedData.CAPACITY + "FE in atmosphere (" + MiscUtil.betterRound(100D * charge / AtmosChargeSavedData.CAPACITY, 1) + "%)");
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void update(){
		IBlockState state = world.getBlockState(pos);
		if(world.isRemote || !(state.getBlock() instanceof AtmosCharger)){
			return;
		}
		renderTimer--;
		extractMode = state.getValue(Properties.ACTIVE);

		if(extractMode){
			if(fe > 0){
				for(EnumFacing side : EnumFacing.HORIZONTALS){
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
				RenderUtil.addArc(world.provider.getDimension(), pos.getX() - 0.8F, pos.getY() + 5.5F, pos.getZ() + 0.5F, pos.getX() + 0.5F, pos.getY() + 5.5F, pos.getZ() - 0.8F, pos.getX() - 0.8F, pos.getY() + 4.5F, pos.getZ() + 0.5F, 1, 0F, (byte) 10, TeslaCoilTopTileEntity.COLOR_CODES[(int) (world.getTotalWorldTime() % 3)]);
				RenderUtil.addArc(world.provider.getDimension(), pos.getX() - 0.8F, pos.getY() + 5.5F, pos.getZ() + 0.5F, pos.getX() + 0.5F, pos.getY() + 5.5F, pos.getZ() + 1.8F, pos.getX() - 0.8F, pos.getY() + 4.5F, pos.getZ() + 0.5F, 1, 0F, (byte) 10, TeslaCoilTopTileEntity.COLOR_CODES[(int) (world.getTotalWorldTime() % 3)]);
				RenderUtil.addArc(world.provider.getDimension(), pos.getX() + 1.8F, pos.getY() + 5.5F, pos.getZ() + 0.5F, pos.getX() + 0.5F, pos.getY() + 5.5F, pos.getZ() - 0.8F, pos.getX() + 1.8F, pos.getY() + 4.5F, pos.getZ() + 0.5F, 1, 0F, (byte) 10, TeslaCoilTopTileEntity.COLOR_CODES[(int) (world.getTotalWorldTime() % 3)]);
				RenderUtil.addArc(world.provider.getDimension(), pos.getX() + 1.8F, pos.getY() + 5.5F, pos.getZ() + 0.5F, pos.getX() + 0.5F, pos.getY() + 5.5F, pos.getZ() + 1.8F, pos.getX() + 1.8F, pos.getY() + 4.5F, pos.getZ() + 0.5F, 1, 0F, (byte) 10, TeslaCoilTopTileEntity.COLOR_CODES[(int) (world.getTotalWorldTime() % 3)]);
			}
		}else{
			int oldCharge = AtmosChargeSavedData.getCharge(world);
			int op = Math.min(fe / 1000, (AtmosChargeSavedData.CAPACITY - oldCharge) / 1000);
			if(op <= 0){
				return;
			}
			fe -= op * 1000;
			AtmosChargeSavedData.setCharge(world, oldCharge + op * 1000);
			markDirty();
			if(renderTimer <= 0){
				renderTimer = 10;
				world.playSound(null, pos.getX() + 0.5F, pos.getY() + 2F, pos.getZ() + 0.5F, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 0.1F, 0F);

				RenderUtil.addArc(world.provider.getDimension(), pos.getX() - 0.8F, pos.getY() + 4.5F, pos.getZ() + 0.5F, pos.getX() + 0.5F, pos.getY() + 4.5F, pos.getZ() - 0.8F, pos.getX() - 0.8F, pos.getY() + 5.5F, pos.getZ() + 0.5F, 1, 0F, (byte) 10, TeslaCoilTopTileEntity.COLOR_CODES[(int) (world.getTotalWorldTime() % 3)]);
				RenderUtil.addArc(world.provider.getDimension(), pos.getX() - 0.8F, pos.getY() + 4.5F, pos.getZ() + 0.5F, pos.getX() + 0.5F, pos.getY() + 4.5F, pos.getZ() + 1.8F, pos.getX() - 0.8F, pos.getY() + 5.5F, pos.getZ() + 0.5F, 1, 0F, (byte) 10, TeslaCoilTopTileEntity.COLOR_CODES[(int) (world.getTotalWorldTime() % 3)]);
				RenderUtil.addArc(world.provider.getDimension(), pos.getX() + 1.8F, pos.getY() + 4.5F, pos.getZ() + 0.5F, pos.getX() + 0.5F, pos.getY() + 4.5F, pos.getZ() - 0.8F, pos.getX() + 1.8F, pos.getY() + 5.5F, pos.getZ() + 0.5F, 1, 0F, (byte) 10, TeslaCoilTopTileEntity.COLOR_CODES[(int) (world.getTotalWorldTime() % 3)]);
				RenderUtil.addArc(world.provider.getDimension(), pos.getX() + 1.8F, pos.getY() + 4.5F, pos.getZ() + 0.5F, pos.getX() + 0.5F, pos.getY() + 4.5F, pos.getZ() + 1.8F, pos.getX() + 1.8F, pos.getY() + 5.5F, pos.getZ() + 0.5F, 1, 0F, (byte) 10, TeslaCoilTopTileEntity.COLOR_CODES[(int) (world.getTotalWorldTime() % 3)]);
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		fe = nbt.getInteger("fe");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("fe", fe);
		return nbt;
	}

	private ElecHandler feHandler = new ElecHandler();
	private IAdvancedRedstoneHandler redsHandler = (boolean measure) -> measure ? 15D * (double) AtmosChargeSavedData.getCharge(world) / (double) AtmosChargeSavedData.CAPACITY : 0;

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		return (cap == CapabilityEnergy.ENERGY && side != EnumFacing.UP) || (cap == Capabilities.ADVANCED_REDSTONE_CAPABILITY && (side == null || side.getAxis() != EnumFacing.Axis.Y)) || super.hasCapability(cap, side);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == CapabilityEnergy.ENERGY && side != EnumFacing.UP){
			return (T) feHandler;
		}
		if((cap == Capabilities.ADVANCED_REDSTONE_CAPABILITY && (side == null || side.getAxis() != EnumFacing.Axis.Y))){
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
