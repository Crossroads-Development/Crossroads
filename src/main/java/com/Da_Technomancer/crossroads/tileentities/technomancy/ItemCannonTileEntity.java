package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.CircuitUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.ambient.sounds.CRSounds;
import com.Da_Technomancer.essentials.blocks.redstone.IRedstoneHandler;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

@ObjectHolder(Crossroads.MODID)
public class ItemCannonTileEntity extends AbstractCannonTileEntity{

	@ObjectHolder("item_cannon")
	public static BlockEntityType<ItemCannonTileEntity> TYPE = null;

	public ItemStack inventory = ItemStack.EMPTY;
	private static final float MAX_LAUNCH_POWER = 4;

	public ItemCannonTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		super.addInfo(chat, player, hit);
		if(inventory.isEmpty()){
			chat.add(new TranslatableComponent("tt.crossroads.item_cannon.inventory.empty", launchSpeed()));
		}else{
			chat.add(new TranslatableComponent("tt.crossroads.item_cannon.inventory", inventory, launchSpeed()));
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
				ItemEntity ent = new ItemEntity(level, itemPos.x, itemPos.y, itemPos.z, inventory);
				ent.setDeltaMovement(aimed.scale(force));
				level.addFreshEntity(ent);
				inventory = ItemStack.EMPTY;
				//Play sound
				CRSounds.playSoundServer(level, worldPosition, CRSounds.ITEM_CANNON, SoundSource.BLOCKS, 1F, 1F);
			}
		}
	}

	@Override
	public AABB getRenderBoundingBox(){
		return new AABB(worldPosition.offset(-3, -3, -3), worldPosition.offset(4, 4, 4));
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		redsHandler.read(nbt);
		if(nbt.contains("inv")){
			inventory = ItemStack.of(nbt.getCompound("inv"));
		}
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		redsHandler.write(nbt);
		if(!inventory.isEmpty()){
			CompoundTag stackTag = new CompoundTag();
			inventory.save(stackTag);
			nbt.put("inv", stackTag);
		}
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		itemOpt.invalidate();
		redsOpt.invalidate();
	}

	private final LazyOptional<IItemHandler> itemOpt = LazyOptional.of(InventoryHandler::new);
	public final CircuitUtil.InputCircHandler redsHandler = new CircuitUtil.InputCircHandler();
	private final LazyOptional<IRedstoneHandler> redsOpt = CircuitUtil.makeBaseCircuitOptional(this, redsHandler, 0);

	@Nonnull
	@Override
	@SuppressWarnings("unchecked")
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side){
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (LazyOptional<T>) itemOpt;
		}
		if(cap == RedstoneUtil.REDSTONE_CAPABILITY){
			return (LazyOptional<T>) redsOpt;
		}

		return super.getCapability(cap, side);
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
			if(slot != 0 || stack.isEmpty() || !inventory.isEmpty() && (!inventory.sameItem(stack) || !ItemStack.tagMatches(inventory, stack))){
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
