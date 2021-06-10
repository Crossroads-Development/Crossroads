package com.Da_Technomancer.crossroads.tileentities.witchcraft;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.witchcraft.ICultivatable;
import com.Da_Technomancer.crossroads.API.witchcraft.IPerishable;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.witchcraft.CultivatorVat;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
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
	 *
	 * Ideally, for total certainty, this should be recalculated every tick
	 * However, that is fairly expensive, so the value is cached and re-verified each tick instead
	 * However, the verification is not totally foolproof- it can register a false positive when the target item changes
	 * When the target item is replaced, we need to manually wipe the cache
	 */
	private ICultivatable.CultivationTrade activeTrade = null;

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



		//TODO
	}

	private void verifyTradeCache(){
		if(activeTrade == null){
			Item target = inventory[0].getItem();
			if(target instanceof ICultivatable){
				activeTrade = ((ICultivatable) target).getCultivationTrade(inventory[0], level);
			}
			if(activeTrade == null){
				//Still null, no trade possible
				progress = 0;
				return;
			}
		}
		//Verify that the current trade matches the inputs, and we have space for the output
		//The ingredients need to be present in sufficient quantity; they don't have to match exactly (we can have excess)
		
		//TODO
	}

	private void updateBlockstate(){
		boolean active = !fluids[0].isEmpty();
		int contents = 0;
		if(!inventory[0].isEmpty()){
			//TODO: When the relevant item is added, correct this
			if(inventory[0].getItem() == null){
				contents = 2;//Brain
			}else{
				contents = 1;
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
	public boolean canPlaceItem(int index, ItemStack stack){
		if(!super.canPlaceItem(index, stack)){
			return false;
		}
		if(index == 0){
			//Target slot; only accept cultivatables
			return stack.getItem() instanceof ICultivatable;
		}
		//Accept anything into the inputs, except for items that could go into the target slot
		return (index == 1 || index == 2) && !(stack.getItem() instanceof ICultivatable);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction dir){
		//Items can be removed from the output
		//Spoiled items can be removed from the target slot
		return index == 3 || (index == 0 && inventory[0].getItem() instanceof IPerishable && ((IPerishable) inventory[0].getItem()).isSpoiled(inventory[0], level));
	}

	@Override
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.crossroads.cultivator_vat");
	}

	@Nullable
	@Override
	public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player){
		return null;//TODO
	}
}
