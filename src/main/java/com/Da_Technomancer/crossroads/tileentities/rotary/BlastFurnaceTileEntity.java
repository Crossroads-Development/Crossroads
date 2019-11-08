package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.BlastFurnaceContainer;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;

@ObjectHolder(Crossroads.MODID)
public class BlastFurnaceTileEntity extends InventoryTE{

	@ObjectHolder("ind_blast_furnace")
	private static TileEntityType<BlastFurnaceTileEntity> type = null;

	public static final int CARBON_LIMIT = 32;
	public static final double POWER = 5;
	public static final double REQUIRED_SPD = 2.5;
	public static final int REQUIRED_PRG = 40;
	public static final double INERTIA = 200;

	private int carbon = 0;
	private int progress = 0;
	public IntReferenceHolder carbRef = IntReferenceHolder.single();
	public IntReferenceHolder progRef = IntReferenceHolder.single();

	public BlastFurnaceTileEntity(){
		super(type, 3);//0: Input; 1: Carbon; 2: Slag
		fluidProps[0] = new TankProperty(4_000, false, true);
	}

	@Override
	protected int fluidTanks(){
		return 1;
	}

	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		chat.add(new TranslationTextComponent("tt.crossroads.boilerplate.progress", progress, REQUIRED_PRG));
		chat.add(new TranslationTextComponent("tt.crossroads.blast_furnace.carbon", carbon));
		super.addInfo(chat, player, hit);
	}

	@Override
	protected boolean useRotary(){
		return true;
	}

	@Override
	public double getMoInertia(){
		return INERTIA;
	}

	@Override
	public void tick(){
		super.tick();

		if(world.isRemote){
			return;
		}

		int carbonAvailable = getCarbonValue(inventory[1]);
		if(carbon < CARBON_LIMIT && carbonAvailable != 0 && carbonAvailable + carbon <= CARBON_LIMIT){
			carbon += carbonAvailable;
			carbRef.set(carbon);
			inventory[1].shrink(1);
			markDirty();
		}

		if(Math.abs(motData[0]) < REQUIRED_SPD){
			progress = 0;
			return;
		}

		//TODO switch to JSON recipes
		Pair<FluidStack, Integer> recipe = RecipeHolder.blastFurnaceRecipes.get(inventory[0]);

		if(recipe == null || carbon < recipe.getRight() || inventory[2].getCount() + recipe.getRight() > CRItems.slag.getItemStackLimit(inventory[2]) || (fluids[0] != null && (recipe.getLeft().getFluid() != fluids[0].getFluid() || fluidProps[0].capacity < fluids[0].getAmount() + recipe.getLeft().getAmount()))){
			progress = 0;
			return;
		}

		progress++;
		axleHandler.addEnergy(-POWER, false, false);
		markDirty();

		if(progress >= REQUIRED_PRG){
			progress = 0;

			inventory[0].shrink(1);
			carbon -= recipe.getRight();
			carbRef.set(carbon);
			if(inventory[2].isEmpty()){
				inventory[2] = new ItemStack(CRItems.slag, recipe.getRight());
			}else{
				inventory[2].grow(recipe.getRight());
			}
			if(fluids[0].isEmpty()){
				fluids[0] = recipe.getLeft().copy();
			}else{
				fluids[0].grow(recipe.getLeft().getAmount());
			}
		}
		progRef.set(progress);
	}

	private static int getCarbonValue(ItemStack stack){
		if(!stack.isEmpty() && stack.getItem() == Items.COAL){
			return 16;
		}

		return 0;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction){
		return index == 2;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return (index == 0 && RecipeHolder.blastFurnaceRecipes.get(stack) != null) || (index == 1 && getCarbonValue(stack) != 0);
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putInt("prog", progress);
		nbt.putInt("carbon", carbon);
		return nbt;
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		progress = nbt.getInt("prog");
		carbon = nbt.getInt("carbon");
		progRef.set(progress);
		carbRef.set(carbon);
	}

	@Override
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.blast_furnace");
	}

	private final LazyOptional<IItemHandler> itemOpt = LazyOptional.of(ItemHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (LazyOptional<T>) itemOpt;
		}
		if(cap == Capabilities.AXLE_CAPABILITY && (side == Direction.UP || side == null)){
			return (LazyOptional<T>) axleOpt;
		}
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return (LazyOptional<T>) globalFluidOpt;
		}

		return super.getCapability(cap, side);
	}

	@Nullable
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player){
		return new BlastFurnaceContainer(id, playerInv, createContainerBuf());
	}
}
