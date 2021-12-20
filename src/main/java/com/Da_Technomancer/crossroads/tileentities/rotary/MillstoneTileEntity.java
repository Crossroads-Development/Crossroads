package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.recipes.MillRec;
import com.Da_Technomancer.crossroads.gui.container.MillstoneContainer;
import com.Da_Technomancer.essentials.blocks.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
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
	public static BlockEntityType<MillstoneTileEntity> TYPE = null;

	private double progress = 0;
	public static final double REQUIRED = 400;
	public static final double POWER_PER_SPEED = 2;
	public static final double INERTIA = 200D;

	public MillstoneTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 4);
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		chat.add(new TranslatableComponent("tt.crossroads.boilerplate.progress", (int) progress, (int) REQUIRED));
		super.addInfo(chat, player, hit);
	}

	public int getProgress(){
		return (int) Math.min(REQUIRED, progress);
	}

	private void createOutput(ItemStack[] outputs){
		if(canFit(outputs)){
			progress = 0;
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
			setChanged();
		}
	}

	private boolean canFit(ItemStack[] outputs){
		//The millstone is literally the first machine added to Crossroads (called the grindstone at the time)
		//Which is why the code for this block is so weird- it was written when I had no idea what I was doing
		//Unlike now, where I have no idea what I was thinking

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
	public void serverTick(){
		super.serverTick();
		if(inventory[0].isEmpty()){
			progress = 0;
		}else{
			Optional<MillRec> recOpt = level.getRecipeManager().getRecipeFor(CRRecipes.MILL_TYPE, this, level);
			if(recOpt.isPresent()){
				double used = POWER_PER_SPEED * Math.abs(axleHandler.getSpeed());
				used = Math.min(Math.abs(axleHandler.getEnergy()), Math.min(REQUIRED - progress, used));
				progress += used;
				axleHandler.addEnergy(-used, false);

				if(progress >= REQUIRED){
					createOutput(recOpt.get().getOutputs());
				}
			}else{
				progress = 0;
			}
		}
	}

	private final LazyOptional<IItemHandler> itemOpt = LazyOptional.of(ItemHandler::new);

	@Override
	public void setRemoved(){
		super.setRemoved();
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
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return index > 0 && index < 4;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		return index == 0 && level.getRecipeManager().getRecipeFor(CRRecipes.MILL_TYPE, new SimpleContainer(stack), level).isPresent();
	}

	@Override
	public double getMoInertia(){
		return INERTIA;
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		nbt.putDouble("prog", progress);
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		progress = nbt.getDouble("prog");
	}

	@Override
	public Component getDisplayName(){
		return new TranslatableComponent("container.millstone");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player){
		return new MillstoneContainer(id, playerInv, createContainerBuf());
	}
}
