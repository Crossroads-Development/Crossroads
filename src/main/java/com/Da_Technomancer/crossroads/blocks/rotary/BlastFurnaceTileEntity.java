package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.templates.InventoryTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.crafting.BlastFurnaceRec;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.gui.container.BlastFurnaceContainer;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.essentials.api.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Optional;

public class BlastFurnaceTileEntity extends InventoryTE{

	public static final BlockEntityType<BlastFurnaceTileEntity> TYPE = CRTileEntity.createType(BlastFurnaceTileEntity::new, CRBlocks.blastFurnace);

	public static final int CARBON_LIMIT = 32;
	public static final double POWER = 5;
	public static final double REQUIRED_SPD = 2.5;
	public static final int REQUIRED_PRG = 40;
	public static final double INERTIA = 200;

	private static final TagKey<Item> CARBON_SOURCES = CraftingUtil.getTagKey(ForgeRegistries.Keys.ITEMS, new ResourceLocation(Crossroads.MODID, "blast_furnace_carbon"));

	private int carbon = 0;
	private int progress = 0;

	public BlastFurnaceTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 3);//0: Input; 1: Carbon; 2: Slag
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
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		chat.add(Component.translatable("tt.crossroads.boilerplate.progress", progress, REQUIRED_PRG));
		chat.add(Component.translatable("tt.crossroads.blast_furnace.carbon", carbon));
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
			if(worldState.getValue(CRProperties.ACTIVE) != active){
				level.setBlockAndUpdate(worldPosition, worldState.setValue(CRProperties.ACTIVE, active));
			}
		}
	}

	@Override
	public void serverTick(){
		super.serverTick();

		int carbonAvailable = getCarbonValue(inventory[1]);
		if(carbon < CARBON_LIMIT && carbonAvailable != 0 && carbonAvailable + carbon <= CARBON_LIMIT){
			carbon += carbonAvailable;
			inventory[1].shrink(1);
			setChanged();
		}

		if(Math.abs(axleHandler.getSpeed()) < REQUIRED_SPD){
			progress = 0;
			updateWorldState(false);
			return;
		}

		Optional<BlastFurnaceRec> recOpt = level.getRecipeManager().getRecipeFor(CRRecipes.BLAST_FURNACE_TYPE, this, level);
		if(!recOpt.isPresent()){
			progress = 0;
			updateWorldState(false);
			return;
		}
		BlastFurnaceRec recipe = recOpt.get();
		if(carbon < recipe.getSlag() || inventory[2].getCount() + recipe.getSlag() > CRItems.slag.getMaxStackSize(inventory[2]) || (!fluids[0].isEmpty() && (!BlockUtil.sameFluid(recipe.getOutput(), fluids[0]) || fluidProps[0].capacity < fluids[0].getAmount() + recipe.getOutput().getAmount()))){
			//The fluid and slag outputs need to fit, and we need enough carbon
			progress = 0;
			updateWorldState(false);
			return;
		}

		progress++;
		axleHandler.addEnergy(-POWER, false);
		updateWorldState(true);
		setChanged();

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
		if(!stack.isEmpty() && CraftingUtil.tagContains(CARBON_SOURCES, stack.getItem())){
			return 16;
		}

		return 0;
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return index == 2;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		return (index == 0 && level.getRecipeManager().getRecipeFor(CRRecipes.BLAST_FURNACE_TYPE, new SimpleContainer(stack), level).isPresent()) || (index == 1 && getCarbonValue(stack) != 0);
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		nbt.putInt("prog", progress);
		nbt.putInt("carbon", carbon);
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		progress = nbt.getInt("prog");
		carbon = nbt.getInt("carbon");
	}

	@Override
	public Component getDisplayName(){
		return Component.translatable("container.ind_blast_furnace");
	}

	private final LazyOptional<IItemHandler> itemOpt = LazyOptional.of(ItemHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == ForgeCapabilities.ITEM_HANDLER){
			return (LazyOptional<T>) itemOpt;
		}
		if(cap == Capabilities.AXLE_CAPABILITY && (side == Direction.UP || side == null)){
			return (LazyOptional<T>) axleOpt;
		}
		if(cap == ForgeCapabilities.FLUID_HANDLER){
			return (LazyOptional<T>) globalFluidOpt;
		}

		return super.getCapability(cap, side);
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player){
		return new BlastFurnaceContainer(id, playerInv, createContainerBuf());
	}
}
