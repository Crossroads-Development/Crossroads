package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import com.Da_Technomancer.crossroads.gui.container.OreCleanserContainer;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import com.Da_Technomancer.crossroads.items.crafting.recipes.OreCleanserRec;
import com.Da_Technomancer.essentials.blocks.BlockUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.Optional;

@ObjectHolder(Crossroads.MODID)
public class OreCleanserTileEntity extends InventoryTE{

	@ObjectHolder("ore_cleanser")
	private static TileEntityType<OreCleanserTileEntity> type = null;

	public static final int WATER_USE = 250;

	public IntReferenceHolder progRef = IntReferenceHolder.single();
	private int progress = 0;//Out of 50

	public OreCleanserTileEntity(){
		super(type, 2);
		fluidProps[0] = new TankProperty(1_000, true, false, (Fluid f) -> f == CRFluids.steam.still);//Steam
		fluidProps[1] = new TankProperty(1_000, false, true);//Dirty Water
	}

	@Override
	public int fluidTanks(){
		return 2;
	}

	@Override
	public void tick(){
		super.tick();

		if(world.isRemote){
			return;
		}

		if(fluids[0].getAmount() >= WATER_USE && fluidProps[1].capacity - fluids[1].getAmount() >= WATER_USE && !inventory[0].isEmpty()){
			Optional<OreCleanserRec> rec = world.getRecipeManager().getRecipe(RecipeHolder.ORE_CLEANSER_TYPE, this, world);

			ItemStack created;
			if(!rec.isPresent()){
				created = inventory[0].copy();
				created.setCount(1);
			}else{
				created = rec.get().getCraftingResult(this).copy();
			}

			if(!inventory[1].isEmpty() && (inventory[1].getMaxStackSize() - inventory[1].getCount() < created.getCount() || !BlockUtil.sameItem(created, inventory[1]))){
				return;
			}

			progress++;
			markDirty();
			if(progress < 50){
				progRef.set(progress);
				return;
			}

			progress = 0;
			progRef.set(progress);

			fluids[0].shrink(WATER_USE);

			if(fluids[1].isEmpty()){
				fluids[1] = new FluidStack(CRFluids.dirtyWater.still, WATER_USE);
			}else{
				fluids[1].grow(WATER_USE);
			}

			inventory[0].shrink(1);
			if(inventory[1].isEmpty()){
				inventory[1] = created;
			}else{
				inventory[1].grow(created.getCount());
			}
		}else{
			progress = 0;
			progRef.set(progress);
		}
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		progress = nbt.getInt("prog");
		progRef.set(progress);
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putInt("prog", progress);
		return nbt;
	}

	@Override
	public void remove(){
		super.remove();
		itemOpt.invalidate();
		inOpt.invalidate();
		outOpt.invalidate();
	}

	private final LazyOptional<IItemHandler> itemOpt = LazyOptional.of(ItemHandler::new);
	private final LazyOptional<IFluidHandler> inOpt = LazyOptional.of(() -> new FluidHandler(0));
	private final LazyOptional<IFluidHandler> outOpt = LazyOptional.of(() -> new FluidHandler(1));

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return (LazyOptional<T>) (facing == null ? globalFluidOpt : facing == Direction.UP ? outOpt : inOpt);
		}

		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (LazyOptional<T>) itemOpt;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction){
		return index == 1;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return index == 0 && world.getRecipeManager().getRecipe(RecipeHolder.ORE_CLEANSER_TYPE, new Inventory(stack), world).isPresent();
	}

	@Override
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.ore_cleanser");
	}

	@Nullable
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player){
		return new OreCleanserContainer(id, playerInv, createContainerBuf());
	}
}
