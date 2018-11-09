package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import com.Da_Technomancer.essentials.shared.IAxisHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class StampMillTileEntity extends InventoryTE{

	public static final int TIME_LIMIT = 100;
	public static final double REQUIRED = 800;
	private static final double PROGRESS_PER_RADIAN = 20D;//Energy to consume per radian the internal gear turns
	private double progress = 0;
	private int timer = 0;

	public StampMillTileEntity(){
		super(2);
	}

	@Override
	protected boolean useRotary(){
		return true;
	}

	@Override
	protected AxleHandler createAxleHandler(){
		return new ThroughAxleHandler();
	}

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, @Nullable EnumFacing side, float hitX, float hitY, float hitZ){
		chat.add("Progress: " + (int) (progress) + "/" + (int) REQUIRED);
		super.addInfo(chat, player, side, hitX, hitY, hitZ);
	}

	@Override
	public void update(){
		super.update();
		if(!world.isRemote){
			IBlockState state = world.getBlockState(pos);

			if(state.getBlock() != ModBlocks.stampMill){
				return;
			}

			double progChange = Math.min(Math.abs(motData[1]), Math.min(REQUIRED - progress, PROGRESS_PER_RADIAN * Math.abs(motData[0]) / 20D));
			motData[1] -= Math.signum(motData[1]) * progChange;
			if(inventory[1].isEmpty() && !inventory[0].isEmpty()){
				progress += progChange;
				if(++timer >= TIME_LIMIT || progress >= REQUIRED){
					timer = 0;
					if(progress >= REQUIRED){
						progress = 0;
						//TODO possibly add a sound effect
						ItemStack produced = RecipeHolder.stampMillRecipes.get(inventory[0]);
						produced = produced.isEmpty() ? inventory[0].splitStack(1) : produced.copy();
						inventory[1] = produced;
					}else{
						inventory[1] = inventory[0].splitStack(1);
					}
				}
				markDirty();
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setDouble("prog", progress);
		nbt.setInteger("timer", timer);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		progress = nbt.getDouble("prog");
		timer = nbt.getInteger("timer");
	}

	private final ItemHandler itemHandler = new ItemHandler(null);

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (T) itemHandler;
		}

		IBlockState state = world.getBlockState(pos);
		if(state.getBlock() == ModBlocks.stampMill && cap == Capabilities.AXLE_HANDLER_CAPABILITY && (side == null || side.getAxis() == state.getValue(Properties.HORIZ_AXIS))){
			return (T) axleHandler;
		}

		return super.getCapability(cap, side);
	}

	private class ThroughAxleHandler extends AngleAxleHandler{

		@Override
		public void propogate(IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius){
			//If true, this has already been checked.
			if(key == updateKey || masterIn.addToList(this)){
				return;
			}

			rotRatio = rotRatioIn == 0 ? 1 : rotRatioIn;
			updateKey = key;
			connected = true;

			IBlockState state = world.getBlockState(pos);
			if(state.getBlock() != ModBlocks.stampMill){
				return;
			}
			EnumFacing.Axis ax = state.getValue(Properties.HORIZ_AXIS);
			for(EnumFacing.AxisDirection dir : EnumFacing.AxisDirection.values()){
				EnumFacing side = EnumFacing.getFacingFromAxis(dir, ax);
				TileEntity te = world.getTileEntity(pos.offset(side));
				if(te != null){
					if(te.hasCapability(Capabilities.AXIS_HANDLER_CAPABILITY, side.getOpposite())){
						te.getCapability(Capabilities.AXIS_HANDLER_CAPABILITY, side.getOpposite()).trigger(masterIn, key);
					}
					if(te.hasCapability(Capabilities.SLAVE_AXIS_HANDLER_CAPABILITY, side.getOpposite())){
						masterIn.addAxisToList(te.getCapability(Capabilities.SLAVE_AXIS_HANDLER_CAPABILITY, side.getOpposite()), side.getOpposite());
					}
					if(te.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, side.getOpposite())){
						te.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, side.getOpposite()).propogate(masterIn, key, rotRatioIn, lastRadius);
					}
				}
			}
		}
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction){
		return index == 1;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return index == 0 && !RecipeHolder.stampMillRecipes.get(stack).isEmpty();
	}

	@Override
	public int getField(int id){
		if(id == getFieldCount() - 2){
			return timer;
		}else if(id == getFieldCount() - 1){
			return (int) Math.round(progress);
		}else{
			return super.getField(id);
		}
	}

	@Override
	public void setField(int id, int value){
		super.setField(id, value);

		if(id == getFieldCount() - 2){
			timer = value;
		}else if(id == getFieldCount() - 1){
			progress = value;
		}
	}

	@Override
	public int getFieldCount(){
		return super.getFieldCount() + 2;
	}

	@Override
	public String getName(){
		return "container.stamp_mill";
	}

	@Override
	public double getMoInertia(){
		return 200;
	}

	private static final AxisAlignedBB RENDER_BOX = new AxisAlignedBB(0, 0, 0, 1, 2, 1);

	@Override
	public AxisAlignedBB getRenderBoundingBox(){
		return RENDER_BOX.offset(pos);
	}
}
