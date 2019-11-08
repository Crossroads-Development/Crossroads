package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.MillstoneContainer;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import com.Da_Technomancer.crossroads.items.crafting.recipes.MillRec;
import com.Da_Technomancer.essentials.blocks.BlockUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Optional;

@ObjectHolder(Crossroads.MODID)
public class MillstoneTileEntity extends InventoryTE{

	@ObjectHolder("millstone")
	private static TileEntityType<MillstoneTileEntity> type = null;

	private double progress = 0;
	public IntReferenceHolder progRef = IntReferenceHolder.single();
	public static final double REQUIRED = 400;
	public static final double PEAK_SPEED = 5D;
	public static final double POWER = 10D;
	public static final double INERTIA = 200D;

	public MillstoneTileEntity(){
		super(type, 4);
	}

	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		chat.add(new TranslationTextComponent("tt.crossroads.boilerplate.progress", (int) progress, (int) REQUIRED));
		super.addInfo(chat, player, hit);
	}

	private void runMachine(){
		if(progress == REQUIRED){
			return;
		}
		double used = POWER * RotaryUtil.findEfficiency(motData[0], 0.2D, PEAK_SPEED);
		progress = Math.min(progress + used, REQUIRED);
		progRef.set((int) progress);
		axleHandler.addEnergy(-used, false, false);
	}

	private void createOutput(ItemStack[] outputs){
		if(canFit(outputs)){
			progress = 0;
			progRef.set((int) progress);
			inventory[0].shrink(1);

			for(ItemStack stack : outputs){
				int remain = stack.getCount();
				//Try to fill slots that already contain this item first
				for(int slot = 1; slot < 4; slot++){
					if(remain > 0 && BlockUtil.sameItem(inventory[slot], stack)){
						int stored = stack.getMaxStackSize() - inventory[slot].getCount();

						inventory[slot].grow(Math.min(stored, remain));
						remain -= stored;
					}
				}

				//No matching slots- use an empty slot
				for(int slot = 1; slot < 4; slot++){
					if(remain <= 0){
						break;
					}

					if(inventory[slot].isEmpty()){
						inventory[slot] = stack.copy();
						inventory[slot].setCount(Math.min(stack.getMaxStackSize(), remain));
						remain -= Math.min(stack.getMaxStackSize(), remain);
					}
				}
			}
			markDirty();
		}
	}

	private boolean canFit(ItemStack[] outputs){
		boolean viable = true;

		ArrayList<Integer> locked = new ArrayList<>();

		for(ItemStack stack : outputs){

			int remain = stack.getCount();
			for(int slot : new int[] {1, 2, 3}){
				if(!locked.contains(slot) && BlockUtil.sameItem(inventory[slot], stack)){
					remain -= stack.getMaxStackSize() - inventory[slot].getCount();
				}
			}

			for(int slot : new int[] {1, 2, 3}){
				if(!locked.contains(slot) && remain > 0 && inventory[slot].isEmpty()){
					remain -= stack.getMaxStackSize();
					locked.add(slot);
				}
			}

			if(remain > 0){
				viable = false;
				break;
			}
		}

		return viable;
	}

	@Override
	protected boolean useRotary(){
		return true;
	}

	@Override
	public void tick(){
		super.tick();
		if(!world.isRemote){
			if(!inventory[0].isEmpty()){
				Optional<MillRec> recOpt = world.getRecipeManager().getRecipe(RecipeHolder.MILL_TYPE, this, world);
				if(!recOpt.isPresent()){
					progress = 0;
					progRef.set((int) progress);
					return;
				}
				runMachine();
				if(progress == REQUIRED){
					createOutput(recOpt.get().getOutputs());
				}
			}else{
				progress = 0;
				progRef.set((int) progress);
			}
		}
	}

	private final LazyOptional<IItemHandler> itemOpt = LazyOptional.of(ItemHandler::new);

	@Override
	public void remove(){
		super.remove();
		itemOpt.invalidate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (LazyOptional<T>) itemOpt;
		}
		if(cap == Capabilities.AXLE_CAPABILITY && side == Direction.UP){
			return (LazyOptional<T>) axleOpt;
		}

		return super.getCapability(cap, side);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction){
		return index > 0 && index < 4;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return index == 0 && world.getRecipeManager().getRecipe(RecipeHolder.MILL_TYPE, this, world).isPresent();
	}

	@Override
	public double getMoInertia(){
		return INERTIA;
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putDouble("prog", progress);
		return nbt;
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		progress = nbt.getDouble("prog");
		progRef.set((int) progress);
	}

	@Override
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.millstone");
	}

	@Nullable
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player){
		return new MillstoneContainer(id, playerInv, createContainerBuf());
	}
}
