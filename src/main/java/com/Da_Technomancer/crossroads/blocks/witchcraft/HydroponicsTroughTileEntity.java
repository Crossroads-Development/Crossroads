package com.Da_Technomancer.crossroads.blocks.witchcraft;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.templates.InventoryTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import com.Da_Technomancer.crossroads.gui.container.HydroponicsTroughContainer;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.essentials.api.BlockUtil;
import com.Da_Technomancer.essentials.api.redstone.RedstoneUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.TallFlowerBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class HydroponicsTroughTileEntity extends InventoryTE{

	public static final BlockEntityType<HydroponicsTroughTileEntity> TYPE = CRTileEntity.createType(HydroponicsTroughTileEntity::new, CRBlocks.hydroponicsTrough);

	private static final int CAPACITY = 8000;
	public static final int SOLUTION_DRAIN_INTERVAL = 4;

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
		CROPS.put(Items.KELP, Triple.of(false, 3, new ItemStack[] {new ItemStack(Items.KELP)}));
		CROPS.put(Items.BROWN_MUSHROOM, Triple.of(false, 5, new ItemStack[] {new ItemStack(Items.BROWN_MUSHROOM)}));
		CROPS.put(Items.RED_MUSHROOM, Triple.of(false, 5, new ItemStack[] {new ItemStack(Items.RED_MUSHROOM)}));
		CROPS.put(Items.CRIMSON_FUNGUS, Triple.of(false, 5, new ItemStack[] {new ItemStack(Items.CRIMSON_FUNGUS)}));
		CROPS.put(Items.WARPED_FUNGUS, Triple.of(false, 5, new ItemStack[] {new ItemStack(Items.WARPED_FUNGUS)}));
		CROPS.put(Items.LILY_PAD, Triple.of(true, 7, new ItemStack[] {new ItemStack(Items.LILY_PAD)}));
		CROPS.put(CRBlocks.medicinalMushroom.asItem(), Triple.of(false, 5, new ItemStack[] {new ItemStack(CRBlocks.medicinalMushroom)}));
		CROPS.put(CRBlocks.petrolCactus.asItem(), Triple.of(true, 2, new ItemStack[] {new ItemStack(CRBlocks.petrolCactus)}));
		CROPS.put(CRItems.wheezewortSeeds, Triple.of(false, 15, new ItemStack[] {new ItemStack(CRItems.wheezewortSeeds)}));
	}

	private int progress = 0;

	public HydroponicsTroughTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 5);//Slot 0 is the seed; 1, 2, 3, 4 are output
		fluidProps[0] = new TankProperty(CAPACITY, true, false, f -> f == CRFluids.fertilizerSolution.getStill());
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

	private int getGrowthMult(){
		if(fluids[0].isEmpty()){
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
			if(block instanceof CropBlock crop){
				if(level.isClientSide()){
					return Triple.of(true, crop.getMaxAge(), new ItemStack[0]);//We can't get the drops on the client, but we don't need to
				}
				List<ItemStack> drops = crop.getStateForAge(crop.getMaxAge()).getDrops(new LootParams.Builder((ServerLevel) level).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(worldPosition)).withParameter(LootContextParams.TOOL, new ItemStack(Items.IRON_HOE)));
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
	public void serverTick(){
		super.serverTick();

		if(isVenting()){
			fluids[0] = FluidStack.EMPTY;
			setChanged();
		}else{
			if(!inventory[0].isEmpty() && !fluids[0].isEmpty() && level.getGameTime() % SOLUTION_DRAIN_INTERVAL == 0){
				//Drains 1mB every (drain interval) ticks
				fluids[0].shrink(1);
				setChanged();
			}
		}
		updateBlockstate();
	}

	public int getProgressBar(){
		Triple<Boolean, Integer, ItemStack[]> product = getCrop(inventory[0]);
		if(product == null){
			return 0;
		}else{
			//Because the maximum progress can vary based on crop type, we get the progress as the percentage complete
			int maxProg = product.getMiddle();
			return 100 * progress / maxProg;
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
		for(int i = 1; i < inventory.length; i++){//Skip the first slot, which holds seeds
			if(!inventory[i].isEmpty()){
				itemState += 1;
			}
		}
		itemState = itemState > 2 ? itemState - 1 : itemState;
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
	public void load(CompoundTag nbt){
		super.load(nbt);
		progress = nbt.getInt("progress");
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		nbt.putInt("progress", progress);
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
		if(capability == ForgeCapabilities.ITEM_HANDLER && facing != Direction.UP){
			return (LazyOptional<T>) itemOpt;
		}
		if(capability == ForgeCapabilities.FLUID_HANDLER && facing != Direction.UP){
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
	public Component getDisplayName(){
		return Component.translatable("container.crossroads.hydroponics_trough");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player playerEntity){
		return new HydroponicsTroughContainer(id, playerInventory, createContainerBuf());
	}
}
