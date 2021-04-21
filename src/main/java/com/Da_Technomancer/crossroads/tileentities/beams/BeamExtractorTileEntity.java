package com.Da_Technomancer.crossroads.tileentities.beams;

import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.templates.BeamRenderTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.recipes.BeamExtractRec;
import com.Da_Technomancer.crossroads.gui.container.BeamExtractorContainer;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.technomancy.BeamCage;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import io.netty.buffer.Unpooled;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

@ObjectHolder(Crossroads.MODID)
public class BeamExtractorTileEntity extends BeamRenderTE implements IInventory, INamedContainerProvider{

	@ObjectHolder("beam_extractor")
	public static TileEntityType<BeamExtractorTileEntity> type = null;

	private ItemStack inv = ItemStack.EMPTY;
	private Direction facing = null;

	//Used for multi-cycle output
	//Will always be EMPTY and 0 for single cycle fuels
	private BeamUnit output = BeamUnit.EMPTY;
	private int timeRemaining = 0;//Ticks remaining, including this one, to emit fuel
	private int timeLimit = 0;//Used for UI, total number of cycles on fuel type

	public BeamExtractorTileEntity(){
		super(type);
	}

	private Direction getFacing(){
		if(facing == null){
			BlockState s = getBlockState();
			if(s.hasProperty(ESProperties.FACING)){
				facing = s.getValue(ESProperties.FACING);
			}else{
				return Direction.DOWN;
			}
		}

		return facing;
	}

	public int getProgress(){
		//For UI, as a percentage
		if(timeLimit == 0){
			return 0;
		}
		return 100 * timeRemaining / timeLimit;
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt){
		super.save(nbt);
		if(!inv.isEmpty()){
			nbt.put("inv", inv.save(new CompoundNBT()));
		}
		output.writeToNBT("output", nbt);
		nbt.putInt("remain", timeRemaining);
		nbt.putInt("time_limit", timeLimit);

		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt){
		super.load(state, nbt);
		inv = nbt.contains("inv") ? ItemStack.of(nbt.getCompound("inv")) : ItemStack.EMPTY;
		output = BeamUnit.readFromNBT("output", nbt);
		timeRemaining = nbt.getInt("remain");
		timeLimit = nbt.getInt("time_limit");
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		itemOpt.invalidate();
	}

	@Override
	public void clearCache(){
		super.clearCache();
		facing = null;
		itemOpt.invalidate();
		itemOpt = LazyOptional.of(ItemHandler::new);
	}

	private LazyOptional<IItemHandler> itemOpt = LazyOptional.of(ItemHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != getFacing()){
			return (LazyOptional<T>) itemOpt;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public int getContainerSize(){
		return 1;
	}

	@Override
	public boolean isEmpty(){
		return inv.isEmpty();
	}

	@Override
	public ItemStack getItem(int index){
		return index == 0 ? inv : ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItem(int index, int count){
		if(index == 0){
			setChanged();
			return inv.split(count);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index){
		if(index != 0){
			return ItemStack.EMPTY;
		}
		setChanged();
		ItemStack held = inv;
		inv = ItemStack.EMPTY;
		return held;
	}

	@Override
	public void setItem(int index, ItemStack stack){
		if(index == 0){
			inv = stack;
			setChanged();
		}
	}

	@Override
	public boolean stillValid(PlayerEntity player){
		return level.getBlockEntity(worldPosition) == this && player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		return index == 0 && (level.getRecipeManager().getRecipeFor(CRRecipes.BEAM_EXTRACT_TYPE, new Inventory(stack), level).isPresent() || stack.getItem() == CRItems.beamCage);
	}

	@Override
	public void clearContent(){
		inv = ItemStack.EMPTY;
		setChanged();
	}

	@Override
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.beam_extractor");
	}

	@Nullable
	@Override
	public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity){
		return new BeamExtractorContainer(i, playerInventory, new PacketBuffer(Unpooled.buffer()).writeBlockPos(worldPosition));
	}

	@Override
	protected void doEmit(BeamUnit toEmit){
		Direction dir = getFacing();

		//If we have a multi-cycle fuel being used, continue to emit that rather than consuming more fuel
		if(!output.isEmpty() && timeRemaining > 0){
			if(--timeRemaining == 0){
				output = BeamUnit.EMPTY;
				timeLimit = 0;
				timeRemaining = 0;
				consumeFuel();
			}
			setChanged();
		}else{
			consumeFuel();
		}

		if(beamer[dir.get3DDataValue()].emit(output, level)){
			refreshBeam(dir.get3DDataValue());
		}
	}

	private void consumeFuel(){
		if(!inv.isEmpty() && !level.hasNeighborSignal(worldPosition)){//Consume fuel; Can be disabled with a redstone signal
			Optional<BeamExtractRec> recOpt = level.getRecipeManager().getRecipeFor(CRRecipes.BEAM_EXTRACT_TYPE, this, level);
			if(recOpt.isPresent()){
				BeamExtractRec rec = recOpt.get();
				output = rec.getOutput();
				inv.shrink(1);
				timeLimit = rec.getDuration();
				timeRemaining = timeLimit;
				output = rec.getOutput();
				setChanged();
			}else if(inv.getItem() == CRItems.beamCage){
				//Beam cages are emitted in their entirety in one pulse
				//The empty cage remains in the extractor
				output = BeamCage.getStored(inv);
				BeamCage.storeBeam(inv, BeamUnit.EMPTY);
				setChanged();
			}
		}else{
			timeRemaining = 0;
			timeLimit = 0;
			output = BeamUnit.EMPTY;
		}
	}

	@Override
	protected boolean[] inputSides(){
		return new boolean[6];//All false
	}

	@Override
	protected boolean[] outputSides(){
		boolean[] out = new boolean[6];
		out[getFacing().get3DDataValue()] = true;
		return out;
	}

	private class ItemHandler implements IItemHandler{

		@Override
		public int getSlots(){
			return 1;
		}

		@Override
		public ItemStack getStackInSlot(int slot){
			return slot == 0 ? inv : ItemStack.EMPTY;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
			if(!canPlaceItem(0, stack) || stack.isEmpty() || inv.getCount() >= getSlotLimit(0) || !inv.isEmpty() && !inv.sameItem(stack)){
				return stack;
			}

			int moved = Math.min(stack.getMaxStackSize() - inv.getCount(), stack.getCount());

			if(!simulate){
				if(inv.isEmpty()){
					inv = stack.copy();
					inv.setCount(moved);
				}else{
					inv.grow(moved);
				}
				setChanged();
			}

			ItemStack output = stack.copy();
			output.shrink(moved);
			return output;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate){
			//We can only extract empty beam cages via automation
			if(slot == 0 && inv.getItem() == CRItems.beamCage && BeamCage.getStored(inv).isEmpty()){
				int moved = Math.min(amount, inv.getCount());
				if(simulate){
					ItemStack out = inv.copy();
					out.setCount(moved);
					return out;
				}
				setChanged();
				return inv.split(moved);
			}
			return ItemStack.EMPTY;
		}

		@Override
		public int getSlotLimit(int slot){
			return 64;
		}

		@Override
		public boolean isItemValid(int slot, @Nonnull ItemStack stack){
			return canPlaceItem(slot, stack);
		}
	}
}
