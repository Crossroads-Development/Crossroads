package com.Da_Technomancer.crossroads.tileentities.witchcraft;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.witchcraft.ICultivatable;
import com.Da_Technomancer.crossroads.API.witchcraft.IPerishable;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.witchcraft.CultivatorVat;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import com.Da_Technomancer.crossroads.gui.container.CultivatorVatContainer;
import com.Da_Technomancer.essentials.blocks.BlockUtil;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;

@ObjectHolder(Crossroads.MODID)
public class CultivatorVatTileEntity extends AbstractNutrientEnvironmentTileEntity{

	@ObjectHolder("cultivator_vat")
	public static TileEntityType<CultivatorVatTileEntity> type = null;
	public static final int REQUIRED_PROGRESS = 100;

	private int progress = 0;
	/**
	 * This is the trade which is currently being performed
	 * ie all the ingredients are present, and it is the trade for the target
	 */
	private ICultivatable.CultivationTrade activeTrade = null;
	/**
	 * Cache of the trade for the item in the target slot
	 *
	 * Ideally, for total certainty, this should be recalculated every tick
	 * However, that is fairly expensive, so the value is cached and re-verified each tick instead
	 * However, the verification is not totally foolproof- it can register a false positive when the target item changes
	 * When the target item is replaced, we need to manually wipe the cache
	 */
	private ICultivatable.CultivationTrade targetTrade = null;

	public CultivatorVatTileEntity(){
		super(type, 4, new int[] {0}, 0);
		//Index 0: Target, also an input for ICultivatable items
		//Index 1: Input 1
		//Index 2: Input 2
		//Index 3: Output

		fluidProps[0] = new TankProperty(1_000, true, false, f -> f == CRFluids.nutrientSolution.still);
		initFluidManagers();
	}

	@Override
	protected int fluidTanks(){
		return 1;
	}

	@Override
	public void tick(){
		super.tick();

		if(level.isClientSide){
			return;
		}

		updateBlockstate();

		//Preserving the target item is handled in the superclass

		//If the target item is expired, attempt to eject it to the output
		Item targetItem = inventory[0].getItem();
		if(targetItem instanceof IPerishable && ((IPerishable) targetItem).isSpoiled(inventory[0], level)){
			if(inventory[3].isEmpty()){
				inventory[3] = inventory[0];
				inventory[0] = ItemStack.EMPTY;
				setChanged();
			}
		}

		verifyTradeCache();
		//Only run if we have fluid and there isn't a redstone signal
		if(activeTrade != null && RedstoneUtil.getRedstoneAtPos(level, worldPosition) == 0 && !fluids[0].isEmpty()){
			progress += 1;
			if(progress >= REQUIRED_PROGRESS){
				progress = 0;
				//Produce output
				if(inventory[3].isEmpty()){
					inventory[3] = activeTrade.created.copy();
					if(inventory[3].getItem() instanceof IPerishable){
						//Initializes the spoil time
						((IPerishable) inventory[3].getItem()).getSpoilTime(inventory[3], level);
					}
				}else{
					inventory[3].grow(activeTrade.created.getCount());
				}
				//Consume input
				consumeIngredient(activeTrade.ingr1);
				consumeIngredient(activeTrade.ingr2);
			}
			setChanged();
		}
	}

	private void consumeIngredient(ItemStack ingredient){
		int toConsume = ingredient.getCount();
		for(int i = 1; i <= 2; i++){
			if(toConsume <= 0){
				return;
			}
			ItemStack inputItem = inventory[i];
			if(BlockUtil.sameItem(ingredient, inputItem)){
				int used = Math.min(toConsume, inputItem.getCount());
				inputItem.shrink(used);
				toConsume -= used;
			}
		}
	}

	private void verifyTradeCache(){
		Item targetItem = inventory[0].getItem();
		if(!(targetItem instanceof ICultivatable)){
			//No target item, therefore no trade possible
			targetTrade = null;
			activeTrade = null;
			progress = 0;
			return;
		}

		if(targetTrade == null || !((ICultivatable) targetItem).isTradeValid(inventory[0], targetTrade, level)){
			//Validate the target cache
			targetTrade = ((ICultivatable) targetItem).getCultivationTrade(inventory[0], level);
		}

		if(activeTrade == null){
			activeTrade = targetTrade;

			if(activeTrade == null){
				//Still null, no trade possible
				progress = 0;
				return;
			}
		}

		//Verify that the current trade matches the inputs, and we have space for the output
		//The ingredients need to be present in sufficient quantity; they don't have to match exactly (we can have excess)

		//Check output
		ItemStack output = activeTrade.created;
		if(!inventory[3].isEmpty() && !output.isEmpty() && (!BlockUtil.sameItem(output, inventory[3]) || output.getCount() + inventory[3].getCount() >= output.getMaxStackSize())){
			//Output doesn't fit
			activeTrade = null;
			return;
		}

		//Check inputs
		//Order is irrelevant
		ItemStack req1 = activeTrade.ingr1;
		ItemStack req2 = activeTrade.ingr2;

		//Known issue: we don't check for or account for the possibility that req1 and req2 are the same item when checking quantities
		//But this case should never occur anyway
		if(!req1.isEmpty() && (BlockUtil.sameItem(req1, inventory[1]) ? inventory[1].getCount() : 0) + (BlockUtil.sameItem(req1, inventory[2]) ? inventory[2].getCount() : 0) < req1.getCount()){
			//Insufficient quantity/spoilage of input 1
			activeTrade = null;
			return;
		}

		if(!req2.isEmpty() && (BlockUtil.sameItem(req2, inventory[1]) ? inventory[1].getCount() : 0) + (BlockUtil.sameItem(req2, inventory[2]) ? inventory[2].getCount() : 0) < req2.getCount()){
			//Insufficient quantity/spoilage of input 2
			activeTrade = null;
			return;
		}
	}

	private void updateBlockstate(){
		boolean active = !fluids[0].isEmpty();
		int contents = 0;
		if(!inventory[0].isEmpty()){
			//TODO: When the relevant item is added, correct this
			if(inventory[0].getItem() == null){
				contents = 2;//Brain
			}else{
				contents = 1;//Anything else (generic)
			}
		}
		BlockState state = getBlockState();
		if(state.getBlock() instanceof CultivatorVat && (state.getValue(CRProperties.ACTIVE) != active || state.getValue(CRProperties.CONTENTS) != contents)){
			state = state.setValue(CRProperties.ACTIVE, active).setValue(CRProperties.CONTENTS, contents);
			level.setBlock(worldPosition, state, 2);
		}
	}

	public int getProgress(){
		return progress;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt){
		super.load(state, nbt);
		progress = nbt.getInt("prog");
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt){
		super.save(nbt);
		nbt.putInt("prog", progress);
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

		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (LazyOptional<T>) itemOpt;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public void setItem(int index, ItemStack stack){
		if(index == 0 && !BlockUtil.sameItem(stack, inventory[0])){
			//Wipe the target item cache, reset the progress
			targetTrade = null;
			progress = 0;
		}
		super.setItem(index, stack);
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		if(!super.canPlaceItem(index, stack)){
			return false;
		}
		if(index == 0){
			//Target slot; only accept cultivatables
			return stack.getItem() instanceof ICultivatable;
		}

		//There are additional restrictions on the input slots to prevent automation clogging up the inputs
		if(index == 1 || index == 2){
			//Nothing that can go into the target slot can go into the input
			if(canPlaceItem(0, stack)){
				return false;
			}
			//If the item is already present in the other input slot, don't allow it into this one (unless it is already present)
			ItemStack otherSlot = inventory[index == 1 ? 2 : 1];
			return BlockUtil.sameItem(inventory[index], stack) || !BlockUtil.sameItem(otherSlot, stack);
		}

		return false;
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction dir){
		//Items can be removed from the output
		return index == 3;
	}

	@Override
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.crossroads.cultivator_vat");
	}

	@Nullable
	@Override
	public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player){
		return new CultivatorVatContainer(id, playerInventory, createContainerBuf());
	}
}
