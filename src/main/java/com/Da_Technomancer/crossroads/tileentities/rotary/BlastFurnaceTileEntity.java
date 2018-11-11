package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class BlastFurnaceTileEntity extends InventoryTE{

	private int carbon = 0;
	public static final int CARBON_LIMIT = 32;
	private int progress = 0;
	public static final double REQUIRED_SPD = 2;
	public static final int REQUIRED_PRG = 40;

	public BlastFurnaceTileEntity(){
		super(3);//0: Input; 1: Carbon; 2: Slag
		fluidProps[0] = new TankProperty(0, 4_000, false, true);
	}

	@Override
	protected int fluidTanks(){
		return 1;
	}

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, @Nullable EnumFacing side, float hitX, float hitY, float hitZ){
		chat.add("Progress: " + progress + "/" + REQUIRED_PRG);
		chat.add("Carbon: " + carbon);
		super.addInfo(chat, player, side, hitX, hitY, hitZ);
	}

	@Override
	protected boolean useRotary(){
		return true;
	}

	@Override
	public double getMoInertia(){
		return 200;
	}

	@Override
	public void update(){
		super.update();

		if(world.isRemote){
			return;
		}

		int carbonAvailable = getCarbonValue(inventory[1]);
		if(carbon < CARBON_LIMIT && carbonAvailable != 0 && carbonAvailable + carbon <= CARBON_LIMIT){
			carbon += carbonAvailable;
			inventory[1].shrink(1);
			markDirty();
		}

		if(Math.abs(motData[0]) < REQUIRED_SPD){
			progress = 0;
			return;
		}

		Pair<FluidStack, Integer> recipe = RecipeHolder.blastFurnaceRecipes.get(inventory[0]);

		if(recipe == null || carbon < recipe.getRight() || inventory[2].getCount() + recipe.getRight() > ModItems.slag.getItemStackLimit(inventory[2]) || (fluids[0] != null && (recipe.getLeft().getFluid() != fluids[0].getFluid() || fluidProps[0].getCapacity() < fluids[0].amount + recipe.getLeft().amount))){
			progress = 0;
			return;
		}

		progress++;
		axleHandler.addEnergy(-5, false, false);
		markDirty();

		if(progress >= REQUIRED_PRG){
			progress = 0;

			inventory[0].shrink(1);
			carbon -= recipe.getRight();
			if(inventory[2].isEmpty()){
				inventory[2] = new ItemStack(ModItems.slag, recipe.getRight());
			}else{
				inventory[2].grow(recipe.getRight());
			}
			if(fluids[0] == null){
				fluids[0] = recipe.getLeft().copy();
			}else{
				fluids[0].amount += recipe.getLeft().amount;
			}
		}
	}

	private static int getCarbonValue(ItemStack stack){
		if(!stack.isEmpty() && stack.getItem() == Items.COAL){
			return 16;
		}

		return 0;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction){
		return index == 2;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return (index == 0 && RecipeHolder.blastFurnaceRecipes.get(stack) != null) || (index == 1 && getCarbonValue(stack) != 0);
	}

	@Override
	public int getField(int id){
		int fieldCount = getFieldCount();
		if(id == fieldCount - 2){
			return progress;
		}
		if(id == fieldCount - 1){
			return carbon;
		}
		return super.getField(id);
	}

	@Override
	public void setField(int id, int value){
		int fieldCount = getFieldCount();
		if(id == fieldCount - 2){
			progress = value;
		}else if(id == fieldCount - 1){
			carbon = value;
		}else{
			super.setField(id, value);
		}
	}

	@Override
	public int getFieldCount(){
		return 2 + super.getFieldCount();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("prog", progress);
		nbt.setInteger("carbon", carbon);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		progress = nbt.getInteger("prog");
		carbon = nbt.getInteger("carbon");

	}

	@Override
	public String getName(){
		return "container.blast_furnace";
	}

	private final ItemHandler itemHandler = new ItemHandler(null);
	private final FluidHandler fluidHandler = new FluidHandler(0);

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (T) itemHandler;
		}
		if(cap == Capabilities.AXLE_HANDLER_CAPABILITY && side == EnumFacing.UP){
			return (T) axleHandler;
		}
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return (T) fluidHandler;
		}

		return super.getCapability(cap, side);
	}
}
