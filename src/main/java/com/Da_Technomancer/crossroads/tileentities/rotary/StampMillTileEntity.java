package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.CrossroadsProperties;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.CrossroadsConfig;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
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

	public float renderAngle(float partialTicks){
		float prev = axleHandler.getAngle(partialTicks - 1F);
		float cur = axleHandler.getAngle(partialTicks);
		return Math.signum(cur - prev) * cur;
	}

	@Override
	public void addInfo(ArrayList<String> chat, PlayerEntity player, @Nullable Direction side, BlockRayTraceResult hit){
		chat.add("Progress: " + (int) (progress) + "/" + (int) REQUIRED);
		super.addInfo(chat, player, side, hitX, hitY, hitZ);
	}

	@Override
	public void tick(){
		super.tick();
		if(!world.isRemote){
			BlockState state = world.getBlockState(pos);

			if(state.getBlock() != CrossroadsBlocks.stampMill){
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
						if(produced.isEmpty()){
							produced = inventory[0].copy();
							produced.setCount(1);
						}else{
							produced = produced.copy();
						}
						inventory[0].shrink(1);
						inventory[1] = produced;
					}else{
						inventory[1] = inventory[0].split(1);
						progress -= REQUIRED * CrossroadsConfig.stampMillDamping.get() / 100;
						if(progress < 0){
							progress = 0;
						}
					}
				}
				markDirty();
			}
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putDouble("prog", progress);
		nbt.putInt("timer", timer);
		return nbt;
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		progress = nbt.getDouble("prog");
		timer = nbt.getInt("timer");
	}

	private final ItemHandler itemHandler = new ItemHandler(null);

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, Direction side){
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (T) itemHandler;
		}

		BlockState state = world.getBlockState(pos);
		if(state.getBlock() == CrossroadsBlocks.stampMill && cap == Capabilities.AXLE_CAPABILITY && (side == null || side.getAxis() == state.get(CrossroadsProperties.HORIZ_AXIS))){
			return (T) axleHandler;
		}

		return super.getCapability(cap, side);
	}

	private class ThroughAxleHandler extends AngleAxleHandler{

		@Override
		public void propogate(IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius, boolean renderOffset){
			//If true, this has already been checked.
			if(key == updateKey || masterIn.addToList(this)){
				return;
			}

			rotRatio = rotRatioIn == 0 ? 1 : rotRatioIn;
			updateKey = key;
			axis = masterIn;

			BlockState state = world.getBlockState(pos);
			if(state.getBlock() != CrossroadsBlocks.stampMill){
				return;
			}
			Direction.Axis ax = state.get(CrossroadsProperties.HORIZ_AXIS);
			for(Direction.AxisDirection dir : Direction.AxisDirection.values()){
				Direction side = Direction.getFacingFromAxis(dir, ax);
				TileEntity te = world.getTileEntity(pos.offset(side));
				if(te != null){
					if(te.hasCapability(Capabilities.AXIS_CAPABILITY, side.getOpposite())){
						te.getCapability(Capabilities.AXIS_CAPABILITY, side.getOpposite()).trigger(masterIn, key);
					}
					if(te.hasCapability(Capabilities.SLAVE_AXIS_CAPABILITY, side.getOpposite())){
						masterIn.addAxisToList(te.getCapability(Capabilities.SLAVE_AXIS_CAPABILITY, side.getOpposite()), side.getOpposite());
					}
					if(te.hasCapability(Capabilities.AXLE_CAPABILITY, side.getOpposite())){
						te.getCapability(Capabilities.AXLE_CAPABILITY, side.getOpposite()).propogate(masterIn, key, rotRatioIn, lastRadius, renderOffset);
					}
				}
			}
		}
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction){
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
