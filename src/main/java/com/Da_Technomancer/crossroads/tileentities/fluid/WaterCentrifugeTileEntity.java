package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import com.Da_Technomancer.crossroads.gui.container.WaterCentrifugeContainer;
import com.Da_Technomancer.crossroads.items.crafting.CRItemTags;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import com.Da_Technomancer.crossroads.items.crafting.recipes.DirtyWaterRec;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
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
import java.util.List;

@ObjectHolder(Crossroads.MODID)
public class WaterCentrifugeTileEntity extends InventoryTE{

	@ObjectHolder("water_centrifuge")
	private static TileEntityType<WaterCentrifugeTileEntity> type = null;
	
	public static final double TIP_POINT = .5D;
	public static final int INERTIA = 115;
	private static final int BATCH_SIZE = 250;
	private boolean neg;

	public WaterCentrifugeTileEntity(){
		super(type, 1);
		fluidProps[0] = new TankProperty(10_000, true, false, (Fluid f) -> f == Fluids.WATER || f == CRFluids.dirtyWater.still);
		fluidProps[1] = new TankProperty(10_000, false, true, (Fluid f) -> true);
	}

	public boolean isNeg(){
		return neg;
	}

	@Override
	public int fluidTanks(){
		return 2;
	}

	@Override
	public boolean useRotary(){
		return true;
	}

	@Override
	public void tick(){
		super.tick();
		if(world.isRemote){
			return;
		}

		if(Math.abs(motData[0]) >= TIP_POINT && (Math.signum(motData[0]) == -1) == neg){
			neg = !neg;
			if(fluids[0].getAmount() >= BATCH_SIZE){
				boolean dirty = fluids[0].getFluid() != Fluids.WATER;
				ItemStack product = ItemStack.EMPTY;

				if(dirty){//Dirty water crafting
					List<DirtyWaterRec> recipes = world.getRecipeManager().getRecipes(RecipeHolder.DIRTY_WATER_TYPE, this, world);
					//Ideally this value would be precalculated and cached- however, due to the possibility of data pack reloading, this becomes more trouble than it's worth
					//If forge ever implements Forge issue #6260, this would be worth caching
					int totalWeight = recipes.parallelStream().mapToInt(DirtyWaterRec::getWeight).sum();
					int choice = world.rand.nextInt(totalWeight) + 1;

					for(DirtyWaterRec entry : world.getRecipeManager().getRecipes(RecipeHolder.DIRTY_WATER_TYPE, this, world)){
						choice -= entry.getWeight();
						if(choice <= 0){
							product = entry.getCraftingResult(this);
							break;
						}
					}
				}else{//Normal de-salination
					product = new ItemStack(CRItemTags.getTagEntry(CRItemTags.SALT));
				}
				fluids[0].shrink(BATCH_SIZE);
				fluids[1] = new FluidStack(CRFluids.distilledWater.still, Math.min(fluidProps[1].capacity, BATCH_SIZE + fluids[1].getAmount()));
				if(inventory[0].isEmpty() || inventory[0].isItemEqual(product)){
					inventory[0] = new ItemStack(product.getItem(), Math.min(product.getMaxStackSize(), 1 + inventory[0].getCount()));
				}
				markDirty();
			}
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putBoolean("neg", neg);

		return nbt;
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		neg = nbt.getBoolean("neg");
	}

	@Override
	public void remove(){
		super.remove();
		waterOpt.invalidate();
		dWaterOpt.invalidate();
		saltOpt.invalidate();
	}

	@Override
	public void rotate(){
		super.rotate();
		waterOpt.invalidate();
		dWaterOpt.invalidate();
		waterOpt = LazyOptional.of(() -> new FluidHandler(0));
		dWaterOpt = LazyOptional.of(() -> new FluidHandler(1));
	}

	private LazyOptional<IFluidHandler> waterOpt = LazyOptional.of(() -> new FluidHandler(0));
	private LazyOptional<IFluidHandler> dWaterOpt = LazyOptional.of(() -> new FluidHandler(1));
	private final LazyOptional<IItemHandler> saltOpt = LazyOptional.of(ItemHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction facing){
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing.getAxis() != world.getBlockState(pos).get(CRProperties.HORIZ_AXIS)){
			return (LazyOptional<T>) waterOpt;
		}
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing.getAxis() == world.getBlockState(pos).get(CRProperties.HORIZ_AXIS)){
			return (LazyOptional<T>) dWaterOpt;
		}
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (LazyOptional<T>) saltOpt;
		}
		if(cap == Capabilities.AXLE_CAPABILITY && facing == Direction.UP){
			return (LazyOptional<T>) axleOpt;
		}

		return super.getCapability(cap, facing);
	}

	@Override
	public double getMoInertia(){
		return INERTIA;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction){
		return index == 0;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return false;
	}

	@Override
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.water_centrifuge");
	}

	@Nullable
	@Override
	public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity){
		return new WaterCentrifugeContainer(i, playerInventory, createContainerBuf());
	}
}
