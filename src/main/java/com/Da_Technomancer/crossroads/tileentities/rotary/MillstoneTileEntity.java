package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class MillstoneTileEntity extends InventoryTE{

	private double progress = 0;
	public static final double REQUIRED = 400;

	public MillstoneTileEntity(){
		super(4);
	}

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, @Nullable EnumFacing side, float hitX, float hitY, float hitZ){
		chat.add("Progress: " + (int) (progress) + "/" + (int) REQUIRED);
		super.addInfo(chat, player, side, hitX, hitY, hitZ);
	}

	private void runMachine(){
		if(progress == REQUIRED){
			return;
		}
		double used = 10D * RotaryUtil.findEfficiency(motData[0], 0.2D, 10D);
		progress = Math.min(progress + used, REQUIRED);
		axleHandler.addEnergy(-used, false, false);
	}

	private void createOutput(ItemStack[] outputs){
		if(canFit(outputs)){
			progress = 0;
			inventory[0].shrink(1);

			for(ItemStack stack : outputs){
				int remain = stack.getCount();
				for(int slot = 1; slot < 4; slot++){
					if(remain > 0 && ItemStack.areItemsEqual(inventory[slot], stack)){
						int stored = stack.getMaxStackSize() - inventory[slot].getCount();

						inventory[slot] = new ItemStack(stack.getItem(), inventory[slot].getCount() + Math.min(stored, remain), stack.getMetadata());
						remain -= stored;
					}
				}

				for(int slot = 1; slot < 4; slot++){
					if(remain <= 0){
						break;
					}

					if(inventory[slot].isEmpty()){
						inventory[slot] = new ItemStack(stack.getItem(), Math.min(stack.getMaxStackSize(), remain), stack.getMetadata());
						remain -= Math.min(stack.getMaxStackSize(), remain);
					}
				}
			}
			markDirty();
		}
	}

	private boolean canFit(ItemStack[] outputs){
		boolean viable = true;

		ArrayList<Integer> locked = new ArrayList<Integer>();

		for(ItemStack stack : outputs){

			int remain = stack.getCount();
			for(int slot : new int[] {1, 2, 3}){
				if(!locked.contains(slot) && ItemStack.areItemsEqual(inventory[slot], stack)){
					remain -= stack.getMaxStackSize() - inventory[slot].getCount();

				}
			}

			for(int slot : new int[] {1, 2, 3}){
				if(!locked.contains(slot) && remain > 0 && inventory[slot].isEmpty()){
					remain -= stack.getMaxStackSize();
					locked.add(slot);
				}
			}

			if(remain > 0){
				viable = false;
				break;
			}
		}

		return viable;
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
	public void update(){
		super.update();
		if(!world.isRemote){
			if(!inventory[0].isEmpty()){
				ItemStack[] output = RecipeHolder.millRecipes.get(inventory[0]);//A null result means no recipe exists
				if(output == null){
					progress = 0;
					return;
				}
				runMachine();
				if(progress == REQUIRED){
					createOutput(output);
				}
			}else{
				progress = 0;
			}
		}
	}

	private final ItemHandler itemHandler = new ItemHandler(null);

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (T) itemHandler;
		}
		if(cap == Capabilities.AXLE_HANDLER_CAPABILITY && side == EnumFacing.UP){
			return (T) axleHandler;
		}

		return super.getCapability(cap, side);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction){
		return index > 0 && index < 4;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return index == 0 && RecipeHolder.millRecipes.get(stack) != null;
	}

	@Override
	public int getField(int id){
		return id == 0 ? (int) progress : 0;
	}

	@Override
	public void setField(int id, int value){
		if(id == 0){
			progress = value;
		}
	}

	@Override
	public int getFieldCount(){
		return 1;
	}

	@Override
	public double getMoInertia(){
		return 200;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setDouble("prog", progress);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		progress = nbt.getDouble("prog");
	}

	@Override
	public String getName(){
		return "container.millstone";
	}
}
