package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.ambient.sounds.CRSounds;
import com.Da_Technomancer.crossroads.api.CircuitUtil;
import com.Da_Technomancer.crossroads.api.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.entity.EntityShell;
import com.Da_Technomancer.crossroads.items.alchemy.Shell;
import com.Da_Technomancer.essentials.api.BlockUtil;
import com.Da_Technomancer.essentials.api.IItemCapable;
import com.Da_Technomancer.essentials.api.redstone.IRedstoneCapable;
import com.Da_Technomancer.essentials.api.redstone.IRedstoneHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

public class ItemCannonTileEntity extends AbstractCannonTileEntity implements IItemCapable, IRedstoneCapable{

	public static final BlockEntityType<ItemCannonTileEntity> TYPE = CRTileEntity.createType(ItemCannonTileEntity::new, CRBlocks.itemCannon);

	public ItemStack inventory = ItemStack.EMPTY;
	private static final float MAX_LAUNCH_POWER = 4;

	private final IItemHandler itemHandler = new InventoryHandler();
	public final CircuitUtil.InputCircHandler redsHandler = new CircuitUtil.InputCircHandler();
	private final IRedstoneHandler redsOpt = CircuitUtil.makeBaseCircuitOptional(this, redsHandler, 0);

	public ItemCannonTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		super.addInfo(chat, player, hit);
		if(inventory.isEmpty()){
			chat.add(Component.translatable("tt.crossroads.item_cannon.inventory.empty", launchSpeed()));
		}else{
			chat.add(Component.translatable("tt.crossroads.item_cannon.inventory", inventory.toString(), launchSpeed()));
		}
	}

	private float launchSpeed(){
		return Math.min(MAX_LAUNCH_POWER, Math.max(0, CircuitUtil.combineRedsSources(redsHandler)));
	}

	@Override
	public void serverTick(){
		super.serverTick();

		//Fire once per redstone tick
		if(level.getGameTime() % 2 == 0 && !inventory.isEmpty()){
			float force = launchSpeed();
			if(force > 0){
				//Launch item
				Vec3 itemPos = Vec3.atCenterOf(worldPosition);
				Vec3 aimed = getAimedVec();
				itemPos = itemPos.add(aimed.scale(2));//Offset the item start position to ensure it clears the base

				if(inventory.getItem() instanceof Shell shellItem){
					ReagentMap contents = shellItem.getReagents(inventory);
					EntityShell shellEnt = new EntityShell(level, contents, inventory);
					shellEnt.setPos(itemPos.x, itemPos.y, itemPos.z);
					shellEnt.setDeltaMovement(aimed.scale(force));
					level.addFreshEntity(shellEnt);
				}else{
					ItemEntity ent = new ItemEntity(level, itemPos.x, itemPos.y, itemPos.z, inventory);
					ent.setDeltaMovement(aimed.scale(force));
					level.addFreshEntity(ent);
				}
				inventory = ItemStack.EMPTY;
				//Play sound
				CRSounds.playSoundServer(level, worldPosition, CRSounds.ITEM_CANNON, SoundSource.BLOCKS, 1F, 1F);
			}
		}
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		redsHandler.read(nbt);
		if(nbt.contains("inv")){
			inventory = BlockUtil.nbtToItemStack(nbt.getCompound("inv"), registries);
		}
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		redsHandler.write(nbt);
		if(!inventory.isEmpty()){
			nbt.put("inv", BlockUtil.stackToNBT(inventory, pRegistries));
		}
	}

	@Nullable
	@Override
	public IItemHandler getItemHandler(Direction direction){
		return itemHandler;
	}

	@Nullable
	@Override
	public IRedstoneHandler getRedstoneHandler(Direction direction){
		return redsOpt;
	}

	private class InventoryHandler implements IItemHandler{

		@Override
		public int getSlots(){
			return 1;
		}

		@Override
		public ItemStack getStackInSlot(int slot){
			return slot == 0 ? inventory : ItemStack.EMPTY;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
			if(slot != 0 || stack.isEmpty() || !inventory.isEmpty() && !BlockUtil.sameItem(stack, inventory)){
				return stack;
			}

			int moved = Math.min(stack.getCount(), stack.getMaxStackSize() - inventory.getCount());

			if(!simulate && moved != 0){
				if(inventory.isEmpty()){
					inventory = stack.copy();
					inventory.setCount(moved);
				}else{
					inventory.grow(moved);
				}
				setChanged();
			}

			ItemStack remain = stack.copy();
			remain.shrink(moved);
			return remain;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate){
			return ItemStack.EMPTY;
		}

		@Override
		public int getSlotLimit(int slot){
			return 64;
		}

		@Override
		public boolean isItemValid(int slot, @Nonnull ItemStack stack){
			return true;
		}
	}
}
