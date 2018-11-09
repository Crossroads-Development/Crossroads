package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.*;
import com.Da_Technomancer.crossroads.API.alchemy.*;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendLooseArcToClient;
import com.Da_Technomancer.crossroads.API.redstone.IAdvancedRedstoneHandler;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.blocks.alchemy.AtmosCharger;
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
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class AtmosChargerTileEntity extends TileEntity implements ITickable, IInfoTE{

	private static final double VOLTUS_CAPACITY = 100;
	private static final int FE_CAPACITY = 20_000;
	private double voltusAmount = 0;
	private int fe = 0;
	private boolean extractMode;
	private int renderTimer;

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, @Nullable EnumFacing side, float hitX, float hitY, float hitZ){
		chat.add(MiscUtil.betterRound(voltusAmount, 2) + "/" + VOLTUS_CAPACITY + " Voltus");
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
				world.playSound(null, pos.getX() + 0.5F, pos.getY() + 2F, pos.getZ() + 0.5F, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 0.25F, -5F);

				NBTTagCompound nbt = new NBTTagCompound();
				new LooseArcRenderable(pos.getX() - 0.8F, pos.getY() + 5.5F, pos.getZ() + 0.5F, pos.getX() + 0.5F, pos.getY() + 5.5F, pos.getZ() - 0.8F, pos.getX() - 0.8F, pos.getY() + 4.5F, pos.getZ() + 0.5F, pos.getX() + 0.5F, pos.getY() + 4.5F, pos.getZ() - 0.8F, 1, 0F, 0.4F, (byte) 10, TeslaCoilTileEntity.COLOR_CODES[(int) (world.getTotalWorldTime() % 3)]).saveToNBT(nbt);
				ModPackets.network.sendToAllAround(new SendLooseArcToClient(nbt), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));

				nbt = new NBTTagCompound();
				new LooseArcRenderable(pos.getX() - 0.8F, pos.getY() + 5.5F, pos.getZ() + 0.5F, pos.getX() + 0.5F, pos.getY() + 5.5F, pos.getZ() + 1.8F, pos.getX() - 0.8F, pos.getY() + 4.5F, pos.getZ() + 0.5F, pos.getX() + 0.5F, pos.getY() + 4.5F, pos.getZ() + 1.8F, 1, 0F, 0.4F, (byte) 10, TeslaCoilTileEntity.COLOR_CODES[(int) (world.getTotalWorldTime() % 3)]).saveToNBT(nbt);
				ModPackets.network.sendToAllAround(new SendLooseArcToClient(nbt), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));

				nbt = new NBTTagCompound();
				new LooseArcRenderable(pos.getX() + 1.8F, pos.getY() + 5.5F, pos.getZ() + 0.5F, pos.getX() + 0.5F, pos.getY() + 5.5F, pos.getZ() - 0.8F, pos.getX() + 1.8F, pos.getY() + 4.5F, pos.getZ() + 0.5F, pos.getX() + 0.5F, pos.getY() + 4.5F, pos.getZ() - 0.8F, 1, 0F, 0.4F, (byte) 10, TeslaCoilTileEntity.COLOR_CODES[(int) (world.getTotalWorldTime() % 3)]).saveToNBT(nbt);
				ModPackets.network.sendToAllAround(new SendLooseArcToClient(nbt), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));

				nbt = new NBTTagCompound();
				new LooseArcRenderable(pos.getX() + 1.8F, pos.getY() + 5.5F, pos.getZ() + 0.5F, pos.getX() + 0.5F, pos.getY() + 5.5F, pos.getZ() + 1.8F, pos.getX() + 1.8F, pos.getY() + 4.5F, pos.getZ() + 0.5F, pos.getX() + 0.5F, pos.getY() + 4.5F, pos.getZ() + 1.8F, 1, 0F, 0.4F, (byte) 10, TeslaCoilTileEntity.COLOR_CODES[(int) (world.getTotalWorldTime() % 3)]).saveToNBT(nbt);
				ModPackets.network.sendToAllAround(new SendLooseArcToClient(nbt), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
			}
		}else{
			int oldCharge = AtmosChargeSavedData.getCharge(world);
			int op = Math.min(fe / 1000, (AtmosChargeSavedData.CAPACITY - oldCharge) / 1000);
			int voltOp = Math.min((int) (voltusAmount / (ModConfig.getConfigDouble(ModConfig.voltusUsage, false))), (AtmosChargeSavedData.CAPACITY - oldCharge - op * 1000) / 1000);
			if(op <= 0 && voltOp <= 0){
				return;
			}
			fe -= op * 1000;
			voltusAmount -= voltOp * ModConfig.getConfigDouble(ModConfig.voltusUsage, false);
			AtmosChargeSavedData.setCharge(world, oldCharge + op * 1000 + voltOp * 1000);
			markDirty();
			if(renderTimer <= 0){
				renderTimer = 10;
				world.playSound(null, pos.getX() + 0.5F, pos.getY() + 2F, pos.getZ() + 0.5F, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 0.1F, 0F);

				NBTTagCompound nbt = new NBTTagCompound();
				new LooseArcRenderable(pos.getX() - 0.8F, pos.getY() + 4.5F, pos.getZ() + 0.5F, pos.getX() + 0.5F, pos.getY() + 4.5F, pos.getZ() - 0.8F, pos.getX() - 0.8F, pos.getY() + 5.5F, pos.getZ() + 0.5F, pos.getX() + 0.5F, pos.getY() + 5.5F, pos.getZ() - 0.8F, 1, 0F, 0.4F, (byte) 10, TeslaCoilTileEntity.COLOR_CODES[(int) (world.getTotalWorldTime() % 3)]).saveToNBT(nbt);
				ModPackets.network.sendToAllAround(new SendLooseArcToClient(nbt), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));

				nbt = new NBTTagCompound();
				new LooseArcRenderable(pos.getX() - 0.8F, pos.getY() + 4.5F, pos.getZ() + 0.5F, pos.getX() + 0.5F, pos.getY() + 4.5F, pos.getZ() + 1.8F, pos.getX() - 0.8F, pos.getY() + 5.5F, pos.getZ() + 0.5F, pos.getX() + 0.5F, pos.getY() + 5.5F, pos.getZ() + 1.8F, 1, 0F, 0.4F, (byte) 10, TeslaCoilTileEntity.COLOR_CODES[(int) (world.getTotalWorldTime() % 3)]).saveToNBT(nbt);
				ModPackets.network.sendToAllAround(new SendLooseArcToClient(nbt), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));

				nbt = new NBTTagCompound();
				new LooseArcRenderable(pos.getX() + 1.8F, pos.getY() + 4.5F, pos.getZ() + 0.5F, pos.getX() + 0.5F, pos.getY() + 4.5F, pos.getZ() - 0.8F, pos.getX() + 1.8F, pos.getY() + 5.5F, pos.getZ() + 0.5F, pos.getX() + 0.5F, pos.getY() + 5.5F, pos.getZ() - 0.8F, 1, 0F, 0.4F, (byte) 10, TeslaCoilTileEntity.COLOR_CODES[(int) (world.getTotalWorldTime() % 3)]).saveToNBT(nbt);
				ModPackets.network.sendToAllAround(new SendLooseArcToClient(nbt), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));

				nbt = new NBTTagCompound();
				new LooseArcRenderable(pos.getX() + 1.8F, pos.getY() + 4.5F, pos.getZ() + 0.5F, pos.getX() + 0.5F, pos.getY() + 4.5F, pos.getZ() + 1.8F, pos.getX() + 1.8F, pos.getY() + 5.5F, pos.getZ() + 0.5F, pos.getX() + 0.5F, pos.getY() + 5.5F, pos.getZ() + 1.8F, 1, 0F, 0.4F, (byte) 10, TeslaCoilTileEntity.COLOR_CODES[(int) (world.getTotalWorldTime() % 3)]).saveToNBT(nbt);
				ModPackets.network.sendToAllAround(new SendLooseArcToClient(nbt), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		voltusAmount = nbt.getDouble("voltus");
		fe = nbt.getInteger("fe");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setDouble("voltus", voltusAmount);
		nbt.setInteger("fe", fe);
		return nbt;
	}

	private IChemicalHandler handler = new AlchHandler();
	private ElecHandler feHandler = new ElecHandler();
	private IAdvancedRedstoneHandler redsHandler = (boolean measure) -> measure ? 15D * (double) AtmosChargeSavedData.getCharge(world) / (double) AtmosChargeSavedData.CAPACITY : 0;

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		return (cap == Capabilities.CHEMICAL_HANDLER_CAPABILITY && (side == null || side == EnumFacing.DOWN)) || (cap == CapabilityEnergy.ENERGY && side != EnumFacing.UP) || (cap == Capabilities.ADVANCED_REDSTONE_HANDLER_CAPABILITY && (side == null || side.getAxis() != EnumFacing.Axis.Y)) || super.hasCapability(cap, side);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.CHEMICAL_HANDLER_CAPABILITY && (side == null || side == EnumFacing.DOWN)){
			return (T) handler;
		}
		if(cap == CapabilityEnergy.ENERGY && side != EnumFacing.UP){
			return (T) feHandler;
		}
		if((cap == Capabilities.ADVANCED_REDSTONE_HANDLER_CAPABILITY && (side == null || side.getAxis() != EnumFacing.Axis.Y))){
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

	private class AlchHandler implements IChemicalHandler{

		@Override
		public EnumTransferMode getMode(EnumFacing side){
			return side == EnumFacing.DOWN ? EnumTransferMode.INPUT : EnumTransferMode.NONE;
		}

		@Override
		public EnumContainerType getChannel(EnumFacing side){
			return EnumContainerType.CRYSTAL;
		}

		@Override
		public double getContent(){
			return voltusAmount;
		}

		@Override
		public double getTransferCapacity(){
			return VOLTUS_CAPACITY;
		}

		@Override
		public double getHeat(){
			return 0;
		}

		@Override
		public void setHeat(double heatIn){

		}

		@Override
		public boolean insertReagents(ReagentStack[] reag, EnumFacing side, IChemicalHandler caller, boolean ignorePhase){
			//Only allows insertion of voltus
			if(voltusAmount >= VOLTUS_CAPACITY || reag[36] == null){
				return false;
			}

			ReagentStack r = reag[36];
			double moved = Math.min(reag[36].getAmount(), VOLTUS_CAPACITY - voltusAmount);
			voltusAmount += moved;
			if(r.increaseAmount(-moved) <= 0){
				reag[36] = null;
			}
			if(caller != null){
				caller.addHeat(-moved * (caller.getTemp() + 273D));
			}
			markDirty();
			return true;
		}

		@Override
		public double getContent(int type){
			return type == 36 ? voltusAmount : 0;
		}
	}
}
