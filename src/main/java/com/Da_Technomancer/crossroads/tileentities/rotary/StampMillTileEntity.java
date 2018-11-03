package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class StampMillTileEntity extends InventoryTE{

	private static final int TIME_LIMIT = 100;
	public static final double REQUIRED = 800;
	private static final double PROGRESS_PER_RADIAN = 20D;//Energy to consume per radian the internal gear turns
	private double progress = 0;
	private int timer = 0;

	public StampMillTileEntity(){
		super(2);
	}
	
	@Override
	protected boolean useHeat(){
		return false;
	}

	@Override
	protected boolean useRotary(){
		return true;
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

			if(!inventory[1].isEmpty()){
				//Try to eject output
				EnumFacing dir = state.getValue(Properties.HORIZ_FACING);
				TileEntity offsetTE = world.getTileEntity(pos.offset(dir));
				if(offsetTE != null && offsetTE.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite())){
					IItemHandler outHandler = offsetTE.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite());
					for(int i = 0; i < outHandler.getSlots(); i++){
						ItemStack outStack = outHandler.insertItem(i, inventory[1], false);
						if(outStack.getCount() < inventory[1].getCount()){
							markDirty();
							break;
						}
					}
				}else{
					EntityItem ent = new EntityItem(world, pos.offset(dir).getX() + .5D, pos.offset(dir).getY(), pos.offset(dir).getZ() + .5D, inventory[1]);
					ent.motionX = 0;
					ent.motionZ = 0;
					world.spawnEntity(ent);
					inventory[1] = ItemStack.EMPTY;
					markDirty();
				}
			}else if(!inventory[0].isEmpty()){
				double progChange = Math.min(Math.abs(motData[1]), Math.min(REQUIRED - progress, PROGRESS_PER_RADIAN * Math.abs(motData[0]) / 20D));
				if((int) (3D * progress / REQUIRED) != (int) (3D * (progress + progChange) / REQUIRED)){
					world.setBlockState(pos, state.withProperty(Properties.TEXTURE_4, (int) (3D * (progress + progChange) / REQUIRED)), 18);
				}
				motData[1] -= Math.signum(motData[1]) * progChange;
				progress += progChange;



				if(++timer >= TIME_LIMIT){
					timer = 0;
					if(progress >= REQUIRED){
						progress = 0;
						//TODO sound effect
						world.setBlockState(pos, state.withProperty(Properties.TEXTURE_4, 0), 18);
						ItemStack produced = RecipeHolder.stampMillRecipes.get(inventory[0]);
						produced = produced.isEmpty() ? inventory[0].splitStack(1) : produced;
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
		if(state.getBlock() == ModBlocks.stampMill && cap == Capabilities.AXLE_HANDLER_CAPABILITY && side == state.getValue(Properties.HORIZ_FACING).rotateY()){
			return (T) axleHandler;
		}

		return super.getCapability(cap, side);
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
}
