package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.recipes.OreCleanserRec;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import com.Da_Technomancer.crossroads.gui.container.OreCleanserContainer;
import com.Da_Technomancer.essentials.blocks.BlockUtil;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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

import com.Da_Technomancer.crossroads.API.templates.InventoryTE.ItemHandler;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE.FluidHandler;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE.TankProperty;

@ObjectHolder(Crossroads.MODID)
public class OreCleanserTileEntity extends InventoryTE{

	@ObjectHolder("ore_cleanser")
	private static BlockEntityType<OreCleanserTileEntity> type = null;

	public static final int WATER_USE = 250;

	private int progress = 0;//Out of 50

	public OreCleanserTileEntity(BlockPos pos, BlockState state){
		super(type, 2);
		fluidProps[0] = new TankProperty(1_000, true, false, CRFluids.STEAM::contains);//Steam
		fluidProps[1] = new TankProperty(1_000, false, true);//Dirty Water
		initFluidManagers();
	}

	@Override
	public int fluidTanks(){
		return 2;
	}

	public int getProgress(){
		return Math.min(progress, 50);
	}

	@Override
	public void tick(){
		super.tick();

		if(level.isClientSide){
			return;
		}

		if(fluids[0].getAmount() >= WATER_USE && fluidProps[1].capacity - fluids[1].getAmount() >= WATER_USE && !inventory[0].isEmpty()){
			Optional<OreCleanserRec> rec = level.getRecipeManager().getRecipeFor(CRRecipes.ORE_CLEANSER_TYPE, this, level);

			ItemStack created;
			if(!rec.isPresent()){
				created = inventory[0].copy();
				created.setCount(1);
			}else{
				created = rec.get().assemble(this).copy();
			}

			if(!inventory[1].isEmpty() && (inventory[1].getMaxStackSize() - inventory[1].getCount() < created.getCount() || !BlockUtil.sameItem(created, inventory[1]))){
				return;
			}

			progress++;
			setChanged();
			if(progress < 50){
				return;
			}

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
		}
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		progress = nbt.getInt("prog");
	}

	@Override
	public CompoundTag save(CompoundTag nbt){
		super.save(nbt);
		nbt.putInt("prog", progress);
		return nbt;
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
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
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return index == 1;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		return index == 0 && level.getRecipeManager().getRecipeFor(CRRecipes.ORE_CLEANSER_TYPE, new SimpleContainer(stack), level).isPresent();
	}

	@Override
	public Component getDisplayName(){
		return new TranslatableComponent("container.ore_cleanser");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player){
		return new OreCleanserContainer(id, playerInv, createContainerBuf());
	}
}
