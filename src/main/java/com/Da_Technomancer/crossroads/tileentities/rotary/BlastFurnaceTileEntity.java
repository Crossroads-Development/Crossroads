package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.recipes.BlastFurnaceRec;
import com.Da_Technomancer.crossroads.gui.container.BlastFurnaceContainer;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.essentials.blocks.BlockUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Optional;

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

	public BlastFurnaceTileEntity(){
		super(type, 3);//0: Input; 1: Carbon; 2: Slag
		fluidProps[0] = new TankProperty(4_000, false, true);
		initFluidManagers();
	}

	public int getCarbon(){
		return carbon;
	}

	public int getProgress(){
		return progress;
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

	private void updateWorldState(boolean active){
		BlockState worldState = getBlockState();
		if(worldState.getBlock() == CRBlocks.blastFurnace){
			if(worldState.get(CRProperties.ACTIVE) != active){
				world.setBlockState(pos, worldState.with(CRProperties.ACTIVE, active));
			}
		}
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
			inventory[1].shrink(1);
			markDirty();
		}

		if(Math.abs(motData[0]) < REQUIRED_SPD){
			progress = 0;
			updateWorldState(false);
			return;
		}

		Optional<BlastFurnaceRec> recOpt = world.getRecipeManager().getRecipe(CRRecipes.BLAST_FURNACE_TYPE, this, world);
		if(!recOpt.isPresent()){
			progress = 0;
			updateWorldState(false);
			return;
		}
		BlastFurnaceRec recipe = recOpt.get();
		if(carbon < recipe.getSlag() || inventory[2].getCount() + recipe.getSlag() > CRItems.slag.getItemStackLimit(inventory[2]) || (!fluids[0].isEmpty() && (!BlockUtil.sameFluid(recipe.getOutput(), fluids[0]) || fluidProps[0].capacity < fluids[0].getAmount() + recipe.getOutput().getAmount()))){
			//The fluid and slag outputs need to fit, and we need enough carbon
			progress = 0;
			updateWorldState(false);
			return;
		}

		progress++;
		axleHandler.addEnergy(-POWER, false);
		updateWorldState(true);
		markDirty();

		if(progress >= REQUIRED_PRG){
			progress = 0;

			inventory[0].shrink(1);
			carbon -= recipe.getSlag();
			if(inventory[2].isEmpty()){
				inventory[2] = new ItemStack(CRItems.slag, recipe.getSlag());
			}else{
				inventory[2].grow(recipe.getSlag());
			}
			if(fluids[0].isEmpty()){
				fluids[0] = recipe.getOutput().copy();
			}else{
				fluids[0].grow(recipe.getOutput().getAmount());
			}
		}
	}

	private static int getCarbonValue(ItemStack stack){
		if(!stack.isEmpty() && ItemTags.COALS.contains(stack.getItem())){
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
		return (index == 0 && world.getRecipeManager().getRecipe(CRRecipes.BLAST_FURNACE_TYPE, new Inventory(stack), world).isPresent()) || (index == 1 && getCarbonValue(stack) != 0);
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putInt("prog", progress);
		nbt.putInt("carbon", carbon);
		return nbt;
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt){
		super.read(state, nbt);
		progress = nbt.getInt("prog");
		carbon = nbt.getInt("carbon");
	}

	@Override
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.ind_blast_furnace");
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
