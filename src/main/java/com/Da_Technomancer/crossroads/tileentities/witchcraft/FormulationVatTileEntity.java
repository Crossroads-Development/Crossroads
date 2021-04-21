package com.Da_Technomancer.crossroads.tileentities.witchcraft;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.recipes.FormulationVatRec;
import com.Da_Technomancer.crossroads.gui.container.FormulationVatContainer;
import com.Da_Technomancer.essentials.blocks.BlockUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
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
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.Optional;

@ObjectHolder(Crossroads.MODID)
public class FormulationVatTileEntity extends InventoryTE{

	@ObjectHolder("formulation_vat")
	public static TileEntityType<FormulationVatTileEntity> type = null;

	public static final int[] TEMP_TIERS = {0, 50, 75, 90, 100};
	public static final double[] SPEED_MULT = {0.25D, 1, 2, 4, 0};
	public static final int[] HEAT_DRAIN = {0, 1, 2, 4, 0};
	public static final int REQUIRED = 1000;
	private double progress = 0;

	public FormulationVatTileEntity(){
		super(type, 1);
		fluidProps[0] = new TankProperty(4_000, true, false);
		fluidProps[1] = new TankProperty(4_000, false, true);
		initFluidManagers();
	}

	@Override
	protected int fluidTanks(){
		return 2;
	}

	@Override
	protected boolean useHeat(){
		return true;
	}

	public FluidStack getInputFluid(){
		return fluids[0];
	}

	public int getProgess(){
		return (int) progress;
	}

	@Override
	public void tick(){
		super.tick();

		if(level.isClientSide){
			return;
		}

		FormulationVatRec rec = null;

		if(!inventory[0].isEmpty() && !fluids[0].isEmpty()){
			Optional<FormulationVatRec> recOpt = level.getRecipeManager().getRecipeFor(CRRecipes.FORMULATION_VAT_TYPE, this, level);
			if(recOpt.isPresent()){
				rec = recOpt.get();
				if(rec.getInput().getAmount() > fluids[0].getAmount() || (!fluids[1].isEmpty() && !BlockUtil.sameFluid(rec.getOutput(), fluids[1])) || rec.getOutput().getAmount() > fluidProps[1].capacity - fluids[1].getAmount()){
					//Ensure that there is sufficient fluid to craft, and we can fit the output
					rec = null;
				}
			}
		}

		if(rec == null){
			progress = 0;
		}

		//Actually advance the progress and consume heat
		int tier = HeatUtil.getHeatTier(temp, TEMP_TIERS);
		if(tier >= 0){
			temp -= HEAT_DRAIN[tier];

			if(rec != null){
				progress += SPEED_MULT[tier];
				if(progress >= REQUIRED){
					FluidStack created = rec.getOutput();
					progress = 0;
					if(fluids[1].isEmpty()){
						fluids[1] = created.copy();
					}else{
						fluids[1].grow(created.getAmount());
					}
					inventory[0].shrink(1);
					fluids[0].shrink(rec.getInput().getAmount());
				}
			}

			setChanged();
		}
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt){
		super.load(state, nbt);
		progress = nbt.getDouble("prog");
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt){
		super.save(nbt);
		nbt.putDouble("prog", progress);
		return nbt;
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		itemOpt.invalidate();
	}

	private final LazyOptional<IItemHandler> itemOpt = LazyOptional.of(ItemHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return (LazyOptional<T>) globalFluidOpt;
		}

		if(capability == Capabilities.HEAT_CAPABILITY){
			return (LazyOptional<T>) heatOpt;
		}

		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (LazyOptional<T>) itemOpt;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return false;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		return index == 0 && level.getRecipeManager().getAllRecipesFor(CRRecipes.FORMULATION_VAT_TYPE).stream().anyMatch(rec -> rec.getIngredients().get(0).test(stack));
	}

	@Override
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.crossroads.formulation_vat");
	}

	@Nullable
	@Override
	public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity playerEntity){
		return new FormulationVatContainer(id, playerInventory, createContainerBuf());
	}
}
