package com.Da_Technomancer.crossroads.tileentities.witchcraft;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import com.Da_Technomancer.crossroads.gui.container.HydroponicsTroughContainer;
import com.Da_Technomancer.essentials.blocks.BlockUtil;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class HydroponicsTroughTileEntity extends InventoryTE{

	@ObjectHolder(Crossroads.MODID + ":hydroponics_trough")
	public static TileEntityType<HydroponicsTroughTileEntity> type = null;

	private static final int CAPACITY = 4000;
	public static final int SOLUTION_DRAIN = 1;

	/**
	 * Stores all the crop types that can be made in the Hydroponics Trough
	 * Does not stores seeds that plants subclasses of CropsBlock, FlowerBlock, or TallFlowerBlock- those are handled automatically
	 * However, if a CropsBlock/FlowerBlock/TallFlowerBlock seed is added to this map, it will override the default behaviour
	 *
	 * Format: {key: Seed item; value: [needsLight, growthStages, [products]]}
	 * Other mods can modify this map; If they do, make sure the keyset is the same on the client and server side (values are irrelevant on the client)
	 */
	public static final HashMap<Item, Triple<Boolean, Integer, ItemStack[]>> CROPS = new HashMap<>();

	static{
		CROPS.put(Items.CACTUS, Triple.of(true, 2, new ItemStack[] {new ItemStack(Items.CACTUS)}));
		CROPS.put(Items.SUGAR_CANE, Triple.of(true, 2, new ItemStack[] {new ItemStack(Items.SUGAR_CANE)}));
		CROPS.put(Items.MELON_SEEDS, Triple.of(true, 7, new ItemStack[] {new ItemStack(Items.MELON_SLICE, 4)}));
		CROPS.put(Items.PUMPKIN_SEEDS, Triple.of(true, 7, new ItemStack[] {new ItemStack(Items.PUMPKIN)}));
		CROPS.put(Items.SEA_PICKLE, Triple.of(false, 7, new ItemStack[] {new ItemStack(Items.SEA_PICKLE)}));
		CROPS.put(Items.SWEET_BERRIES, Triple.of(true, 2, new ItemStack[] {new ItemStack(Items.SWEET_BERRIES)}));
		CROPS.put(Items.NETHER_WART, Triple.of(false, 3, new ItemStack[] {new ItemStack(Items.NETHER_WART, 2)}));
		CROPS.put(Items.SEAGRASS, Triple.of(false, 7, new ItemStack[] {new ItemStack(Items.SEAGRASS)}));
		CROPS.put(Items.BROWN_MUSHROOM, Triple.of(false, 5, new ItemStack[] {new ItemStack(Items.BROWN_MUSHROOM)}));
		CROPS.put(Items.RED_MUSHROOM, Triple.of(false, 5, new ItemStack[] {new ItemStack(Items.RED_MUSHROOM)}));
		CROPS.put(Items.CRIMSON_FUNGUS, Triple.of(false, 5, new ItemStack[] {new ItemStack(Items.CRIMSON_FUNGUS)}));
		CROPS.put(Items.WARPED_FUNGUS, Triple.of(false, 5, new ItemStack[] {new ItemStack(Items.WARPED_FUNGUS)}));
		CROPS.put(Items.LILY_PAD, Triple.of(true, 7, new ItemStack[] {new ItemStack(Items.LILY_PAD)}));
	}

	private int progress = 0;

	public HydroponicsTroughTileEntity(){
		super(type, 5);//Slot 0 is the seed
		fluidProps[0] = new TankProperty(CAPACITY, true, false, f -> f == CRFluids.nutrientSolution.still || f == CRFluids.fertilizerSolution.still);
		initFluidManagers();
	}

	@Override
	protected int fluidTanks(){
		return 1;
	}

	private boolean isVenting(){
		return RedstoneUtil.getRedstoneAtPos(level, worldPosition) > 0;
	}

	public boolean canBonemeal(){
		return getCrop(inventory[0]) != null;
	}

	public float getCircuitOutput(){
		int measured = 0;
		for(int i = 1; i < inventory.length; i++){
			measured += inventory[i].getCount();
		}
		return measured;
	}

	private int getGrowthMult(){
		if(fluids[0].getAmount() < SOLUTION_DRAIN){
			return 0;
		}
		Triple<Boolean, Integer, ItemStack[]> crop = getCrop(inventory[0]);
		if(crop != null){
			boolean needsLight = crop.getLeft();
			return !needsLight || MiscUtil.getLight(level, worldPosition) >= 9 ? CRConfig.hydroponicsMult.get() : 0;
		}
		return 0;
	}

	@Nullable
	private Triple<Boolean, Integer, ItemStack[]> getCrop(ItemStack seeds){
		Item item = seeds.getItem();
		Triple<Boolean, Integer, ItemStack[]> mapped = CROPS.get(item);
		if(mapped != null){
			return mapped;
		}
		//Handle seeds for CropsBlock & FlowerBlock
		if(item instanceof BlockItem){
			Block block = ((BlockItem) item).getBlock();
			if(block instanceof CropsBlock){
				CropsBlock crop = (CropsBlock) block;
				if(level.isClientSide()){
					return Triple.of(true, crop.getMaxAge(), new ItemStack[0]);//We can't get the drops on the client, but we don't need to
				}
				List<ItemStack> drops = crop.getStateForAge(crop.getMaxAge()).getDrops(new LootContext.Builder((ServerWorld) level).withParameter(LootParameters.ORIGIN, Vector3d.atCenterOf(worldPosition)).withParameter(LootParameters.TOOL, new ItemStack(Items.IRON_HOE)));
				return Triple.of(true, crop.getMaxAge(), drops.toArray(new ItemStack[0]));
			}
			if(block instanceof FlowerBlock){
				return Triple.of(true, 2, new ItemStack[] {new ItemStack(seeds.getItem())});
			}
			if(block instanceof TallFlowerBlock){
				return Triple.of(true, 2, new ItemStack[] {new ItemStack(seeds.getItem())});
			}
		}
		return null;
	}

	@Override
	public void tick(){
		super.tick();

		if(!level.isClientSide()){
			if(isVenting()){
				fluids[0] = FluidStack.EMPTY;
				setChanged();
			}else{
				if(!inventory[0].isEmpty() && !fluids[0].isEmpty()){
					fluids[0].shrink(SOLUTION_DRAIN);
					setChanged();
				}
			}
			updateBlockstate();
		}
	}

	public void performGrowth(){
		if(!level.isClientSide()){
			Triple<Boolean, Integer, ItemStack[]> product = getCrop(inventory[0]);
			if(product == null){
				progress = 0;
			}else{
				int maxProg = product.getMiddle();
				progress += getGrowthMult();
				while(progress >= maxProg){
					progress -= maxProg;
					//Produce drops
					//We make a list of copies of the itemstacks; we modify these stacks, so we need to copy.
					//We convert to a list simply because it is more convenient when we're using a stream anyway
					List<ItemStack> drops = Arrays.stream(product.getRight()).map(ItemStack::copy).collect(Collectors.toList());
					for(ItemStack drop : drops){
						for(int i = 1; i < inventory.length; i++){//Skip slot 1, which is the seed
							ItemStack current = inventory[i];
							if(BlockUtil.sameItem(current, drop)){
								int moved = Math.min(drop.getCount(), current.getMaxStackSize() - current.getCount());
								current.grow(moved);
								drop.shrink(moved);
							}else if(current.isEmpty()){
								int moved = Math.min(drop.getCount(), drop.getMaxStackSize());
								inventory[i] = drop.copy();
								inventory[i].setCount(moved);
								drop.shrink(moved);
							}
							if(drop.isEmpty()){
								break;
							}
						}
					}
					updateBlockstate();
				}
			}
			setChanged();
		}
	}

	/**
	 * Updates the blockstate in world
	 * Only call this on the virtual server side
	 */
	private void updateBlockstate(){
		int itemState = 0;//0-3 inclusive
		int fluidState = 0;//0-3 inclusive
		for(int i = 1; i < 5; i++){//Skip the first slot, which holds seeds
			if(!inventory[i].isEmpty()){
				itemState += 1;
			}
		}
		if(isVenting()){
			fluidState = 3;//We reserve fullness 3 for venting; when venting, the actual fluid level must be empty
		}else{
			//0-2 inclusive
			fluidState = (int) Math.ceil(2F * fluids[0].getAmount() / CAPACITY);
		}
		BlockState worldState = getBlockState();
		BlockState newState = worldState.setValue(CRProperties.FULLNESS, fluidState).setValue(CRProperties.SOLID_FULLNESS, itemState);
		if(newState != worldState){
			level.setBlock(worldPosition, newState, 18);
		}
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt){
		super.load(state, nbt);
		progress = nbt.getInt("progress");
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt){
		super.save(nbt);
		nbt.putInt("progress", progress);
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
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (LazyOptional<T>) itemOpt;
		}
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return (LazyOptional<T>) globalFluidOpt;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public int getMaxStackSize(int slot){
		return slot == 0 ? 1 : super.getMaxStackSize(slot);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return index != 0 && isVenting();
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		return index == 0 && getCrop(stack) != null;
	}

	@Override
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.crossroads.hydroponics_trough");
	}

	@Nullable
	@Override
	public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity playerEntity){
		return new HydroponicsTroughContainer(id, playerInventory, createContainerBuf());
	}
}
