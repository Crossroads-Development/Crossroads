package com.Da_Technomancer.crossroads.blocks.witchcraft;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.templates.InventoryTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import com.Da_Technomancer.crossroads.gui.container.HydroponicsTroughContainer;
import com.Da_Technomancer.essentials.api.BlockUtil;
import com.Da_Technomancer.essentials.api.IFluidCapable;
import com.Da_Technomancer.essentials.api.IItemCapable;
import com.Da_Technomancer.essentials.api.redstone.RedstoneUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.TallFlowerBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class HydroponicsTroughTileEntity extends InventoryTE implements IFluidCapable, IItemCapable{

	public static final BlockEntityType<HydroponicsTroughTileEntity> TYPE = CRTileEntity.createType(HydroponicsTroughTileEntity::new, CRBlocks.hydroponicsTrough);

	private static boolean fallbackRecipesInitialized = false;
	/* Flagging a risk with this implementation:
	 * For actual mechanics, only server-side value matters here
	 * But client-side will control what is shown in JEI
	 * Everything with non-deterministic products should be using a subclass that doesn't show products to JEI,
	 * but if we somehow get a different list between server-and-client side, there's currently no mechanism to rectify the discrepancy
	 */
	private static final ArrayList<IHydroponicsRec> fallbackRecipes = new ArrayList<>();

	public static synchronized List<IHydroponicsRec> getFallbackRecipes(){
		if(!fallbackRecipesInitialized){
			fallbackRecipesInitialized = true;
			for(Item item : BuiltInRegistries.ITEM){
				// Build an explicit list of every item for which a fallback recipe exists
				if(item instanceof BlockItem bItem){
					Block block = bItem.getBlock();
					switch(block){
						case CropBlock crop -> {
							fallbackRecipes.add(new HydroponicsRecLootTable(bItem, crop));
						}
						case FlowerBlock flowerBlock -> {
							fallbackRecipes.add(new HydroponicsRecFixed(Ingredient.of(item), true, 2, List.of(new ItemStack(item))));
						}
						case TallFlowerBlock tallFlowerBlock -> {
							fallbackRecipes.add(new HydroponicsRecFixed(Ingredient.of(item), true, 2, List.of(new ItemStack(item))));
						}
						default -> {

						}
					}
				}
			}
		}
		return fallbackRecipes;
	}

	private static final int CAPACITY = 8000;
	public static final int SOLUTION_DRAIN_INTERVAL = 4;

	private int progress = 0;
	private IHydroponicsRec recipeCache = null;
	private final IItemHandler itemHandler = new ItemHandler();

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
		IHydroponicsRec crop = getCrop(inventory[0]);
		if(crop != null){
			boolean needsLight = crop.needsLight();
			return !needsLight || MiscUtil.getLight(level, worldPosition) >= 9 ? CRConfig.hydroponicsMult.get() : 0;
		}
		return 0;
	}

	@Nullable
	private IHydroponicsRec getCrop(ItemStack seeds){
		if(recipeCache != null && recipeCache.getIngredient().test(seeds)){
			return recipeCache;
		}

		recipeCache = level.getRecipeManager().getAllRecipesFor(CRRecipes.HYDROPONIC_TROUGH_TYPE).stream().map(RecipeHolder::value).filter(rec -> rec.isEnabled() && rec.getIngredient().test(seeds)).findAny().orElse(null);
		if(recipeCache == null){
			for(IHydroponicsRec fallbackRec : getFallbackRecipes()){
				if(fallbackRec.getIngredient().test(seeds)){
					recipeCache = fallbackRec;
					break;
				}
			}
		}

		return recipeCache;
	}

	@Override
	public void serverTick(){
		super.serverTick();
		//Creation of products is driven by random ticks - see performGrowth

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
		IHydroponicsRec product = getCrop(inventory[0]);
		if(product == null){
			return 0;
		}else{
			//Because the maximum progress can vary based on crop type, we get the progress as the percentage complete
			int maxProg = product.getGrowthStages();
			return 100 * progress / maxProg;
		}
	}

	public void performGrowth(){
		if(!level.isClientSide()){
			IHydroponicsRec product = getCrop(inventory[0]);
			if(product == null){
				progress = 0;
			}else{
				int maxProg = product.getGrowthStages();
				progress += getGrowthMult();
				while(progress >= maxProg){
					progress -= maxProg;
					//Produce drops
					//We make a list of copies of the itemstacks; we modify these stacks, so we need to copy.
					List<ItemStack> drops = product.getOutputs((ServerLevel) level, worldPosition).stream().map(ItemStack::copy).toList();
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
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		progress = nbt.getInt("progress");
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		nbt.putInt("progress", progress);
	}

	@Nullable
	@Override
	public IItemHandler getItemHandler(Direction direction){
		if(direction != Direction.UP){
			return itemHandler;
		}
		return null;
	}

	@Override
	@Nullable
	public IFluidHandler getFluidHandler(Direction dir){
		if(dir != Direction.UP){
			return globalFluidHandler;
		}
		return null;
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

	public static interface IHydroponicsRec{

		Ingredient getIngredient();

		boolean needsLight();

		int getGrowthStages();

		/**
		 * Get the actual outputs of the recipe. Can be randomized with each call.
		 * @param world World
		 * @param pos Position
		 * @return Actual outputs
		 */
		@Nonnull
		default List<ItemStack> getOutputs(ServerLevel world, BlockPos pos){
			List<ItemStack> outputs = getJeiOutputs();
			assert outputs != null;
			return outputs;
		}

		/**
		 * Gets the outputs to display in JEI. Should not vary between calls. If null, JEI will display a message that recipe outputs are randomized
		 * @return List of outputs to be shown in JEI, or null
		 */
		@Nullable
		List<ItemStack> getJeiOutputs();
	}

	public static record HydroponicsRecLootTable(Ingredient ing, boolean needsLight, int growthStages, BlockState lootTableBlock) implements IHydroponicsRec{

		public HydroponicsRecLootTable(Item seed, CropBlock crop){
			this(Ingredient.of(seed), true, crop.getMaxAge(), crop.getStateForAge(crop.getMaxAge()));
		}

		@Override
		public Ingredient getIngredient(){
			return ing;
		}

		@Override
		public int getGrowthStages(){
			return growthStages;
		}

		@Nonnull
		@Override
		public List<ItemStack> getOutputs(ServerLevel world, BlockPos pos){
			return lootTableBlock.getDrops(new LootParams.Builder(world).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withParameter(LootContextParams.TOOL, new ItemStack(Items.IRON_HOE)));
		}

		@Nullable
		@Override
		public List<ItemStack> getJeiOutputs(){
			return null;//Not currently supported
		}
	}


	public static record HydroponicsRecFixed(Ingredient ing, boolean needsLight, int growthStages, List<ItemStack> output) implements IHydroponicsRec{

		@Override
		public Ingredient getIngredient(){
			return ing;
		}

		@Override
		public int getGrowthStages(){
			return growthStages;
		}

		@Override
		public List<ItemStack> getJeiOutputs(){
			return output;
		}
	}
}
