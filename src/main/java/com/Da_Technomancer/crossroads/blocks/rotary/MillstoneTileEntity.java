package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.api.rotary.IAxleCapable;
import com.Da_Technomancer.crossroads.api.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.api.templates.InventoryTE;
import com.Da_Technomancer.crossroads.api.witchcraft.IPerishable;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.MillRec;
import com.Da_Technomancer.crossroads.gui.container.MillstoneContainer;
import com.Da_Technomancer.essentials.api.BlockUtil;
import com.Da_Technomancer.essentials.api.IItemCapable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MillstoneTileEntity extends InventoryTE implements IAxleCapable, IItemCapable{

	public static final BlockEntityType<MillstoneTileEntity> TYPE = CRTileEntity.createType(MillstoneTileEntity::new, CRBlocks.millstone);

	private double progress = 0;
	public static final double REQUIRED = 400;
	public static final double POWER_PER_SPEED = 2;
	public static final double INERTIA = 200D;

	public MillstoneTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 4);
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		chat.add(Component.translatable("tt.crossroads.boilerplate.progress", (int) progress, (int) REQUIRED));
		super.addInfo(chat, player, hit);
	}

	public int getProgress(){
		return (int) Math.min(REQUIRED, progress);
	}

	private void createOutput(List<ItemStack> outputs){
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

	private boolean canFit(List<ItemStack> outputs){
		//The millstone is literally the first machine added to Crossroads (called the grindstone at the time)
		//Which is why the code for this block is so weird- it was written when I had no idea what I was doing
		//Unlike now, where I have no idea what I was thinking

		boolean viable = true;

		ArrayList<Integer> locked = new ArrayList<>();

		for(ItemStack stack : outputs){

			int remain = stack.getCount();
			for(int slot = 1; slot < 4; slot++){
				if(!locked.contains(slot) && BlockUtil.sameItem(inventory[slot], stack)){
					remain -= stack.getMaxStackSize() - inventory[slot].getCount();
				}
			}

			for(int slot = 1; slot < 4; slot++){
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
	public void serverTick(){
		super.serverTick();
		if(inventory[0].isEmpty()){
			progress = 0;
		}else{
			Optional<RecipeHolder<MillRec>> recOpt = level.getRecipeManager().getRecipeFor(CRRecipes.MILL_TYPE, this, level);
			if(recOpt.isPresent()){
				double used = POWER_PER_SPEED * Math.abs(axleHandler.getSpeed());
				used = Math.min(Math.abs(axleHandler.getEnergy()), Math.min(REQUIRED - progress, used));
				progress += used;
				axleHandler.addEnergy(-used, false);

				if(progress >= REQUIRED){
					List<ItemStack> outputs = recOpt.get().value().getOutputs();
					for(ItemStack output : outputs){
						//Millstone can handle recipes with perishable outputs
						IPerishable.getAndInitSpoilTime(output, level);
					}
					createOutput(outputs);
				}
			}else{
				progress = 0;
			}
		}
	}

	@Override
	@Nullable
	public IAxleHandler getAxleHandler(Direction dir){
		if(dir == Direction.UP){
			return axleHandler;
		}
		return null;
	}

	@Nullable
	@Override
	public IItemHandler getItemHandler(Direction direction){
		return itemHandler;
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return index > 0 && index < 4;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		return index == 0 && level.getRecipeManager().getRecipeFor(CRRecipes.MILL_TYPE, new SingleRecipeInput(stack), level).isPresent();
	}

	@Override
	public double getMoInertia(){
		return INERTIA;
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		nbt.putDouble("prog", progress);
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		progress = nbt.getDouble("prog");
	}

	@Override
	public Component getDisplayName(){
		return Component.translatable("container.millstone");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player){
		return new MillstoneContainer(id, playerInv, createContainerBuf());
	}
}
