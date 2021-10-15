package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import com.Da_Technomancer.crossroads.gui.container.SteamerContainer;
import com.Da_Technomancer.essentials.blocks.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;

@ObjectHolder(Crossroads.MODID)
public class SteamerTileEntity extends InventoryTE{

	@ObjectHolder("steamer")
	public static BlockEntityType<SteamerTileEntity> TYPE = null;

	public static final int FLUID_USE = 200;//Steam per tick
	public static final int REQUIRED = 50;//Number of processing ticks

	private int progress = 0;

	public SteamerTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 2);
		fluidProps[0] = new TankProperty(10_000, true, false, CRFluids.STEAM::contains);
		fluidProps[1] = new TankProperty(10_000, false, true);
		initFluidManagers();
	}

	@Override
	public int fluidTanks(){
		return 2;
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		chat.add(new TranslatableComponent("tt.crossroads.boilerplate.progress", progress, REQUIRED));
		super.addInfo(chat, player, hit);
	}

	public int getProgress(){
		return progress;
	}

	@Override
	public void serverTick(){
		super.serverTick();

		SmokingRecipe rec;
		if(!inventory[0].isEmpty() && (rec = level.getRecipeManager().getRecipeFor(RecipeType.SMOKING, this, level).orElse(null)) != null && (inventory[1].isEmpty() || BlockUtil.sameItem(rec.getResultItem(), inventory[1]) && inventory[1].getCount() < inventory[1].getMaxStackSize())){
			//Check fluids
			if(fluids[0].getAmount() >= FLUID_USE && fluidProps[1].capacity - fluids[1].getAmount() >= FLUID_USE){
				if(fluids[1].isEmpty()){
					fluids[1] = new FluidStack(CRFluids.distilledWater.still, FLUID_USE);
				}else{
					fluids[1].grow(FLUID_USE);
				}

				fluids[0].shrink(FLUID_USE);

				if(++progress >= REQUIRED){
					progress = 0;
					if(inventory[1].isEmpty()){
						inventory[1] = rec.assemble(this);
					}else{
						inventory[1].grow(1);
					}
					inventory[0].shrink(1);
				}

				setChanged();
			}
		}else{
			progress = 0;
		}
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		return index == 0 && !stack.isEmpty() && level.getRecipeManager().getRecipeFor(RecipeType.SMOKING, new SimpleContainer(stack), level).isPresent();
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return index == 1;
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		steamOpt.invalidate();
		waterOpt.invalidate();
	}

	private final LazyOptional<IFluidHandler> steamOpt = LazyOptional.of(() -> new FluidHandler(0));
	private final LazyOptional<IFluidHandler> waterOpt = LazyOptional.of(() -> new FluidHandler(1));

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){

		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			if(side == Direction.UP || side == Direction.DOWN){
				return (LazyOptional<T>) waterOpt;
			}else if(side != null){
				return (LazyOptional<T>) steamOpt;
			}
		}

		return super.getCapability(cap, side);
	}

	@Override
	public Component getDisplayName(){
		return new TranslatableComponent("container.steamer");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player playerEntity){
		return new SteamerContainer(id, playerInventory, createContainerBuf());
	}
}
