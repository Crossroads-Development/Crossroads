package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.EnumPath;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.recipes.DetailedCrafterRec;
import com.Da_Technomancer.essentials.blocks.BlockUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static net.minecraftforge.common.ForgeHooks.setCraftingPlayer;

@ObjectHolder(Crossroads.MODID)
public class DetailedCrafterContainer extends RecipeBookContainer<CraftingInventory>{

	@ObjectHolder("detailed_crafter")
	private static ContainerType<DetailedCrafterContainer> type = null;

	@SuppressWarnings("unchecked")
	private static final ITag<Item>[] unlockKeys = new ITag[3];
	private static final ITag<Item> fillerMats = ItemTags.makeWrapperTag(Crossroads.MODID + ":path_unlock_filler");

	static{
		unlockKeys[0] = ItemTags.makeWrapperTag(Crossroads.MODID + ":technomancy_unlock_key");
		unlockKeys[1] = ItemTags.makeWrapperTag(Crossroads.MODID + ":alchemy_unlock_key");
		unlockKeys[2] = ItemTags.makeWrapperTag(Crossroads.MODID + ":witchcraft_unlock_key");
	}

	private final CraftingInventory inInv = new CraftingInventory(this, 3, 3);
	private final CraftResultInventory outInv = new CraftResultInventory();
	private final World world;
	private final PlayerEntity player;

	private final boolean fake;//True for goggles- used to shortcut canInteractWith checks
	@Nullable
	private final BlockPos pos;//Null if fake, nonnull otherwise- used for canInteractWith

	public DetailedCrafterContainer(int id, PlayerInventory playerInv, PacketBuffer buf){
		super(type, id);
		player = playerInv.player;
		world = player.world;

		fake = buf.readBoolean();
		if(fake){
			pos = null;
		}else{
			pos = buf.readBlockPos();
		}

		//Output 0
		addSlot(new SlotCraftingFlexible(playerInv.player, inInv, outInv, 0, 124, 35));

		//Input 0-8
		for(int i = 0; i < 3; ++i){
			for(int j = 0; j < 3; ++j){
				addSlot(new Slot(inInv, j + i * 3, 30 + j * 18, 17 + i * 18));
			}
		}

		//Player inventory
		for(int k = 0; k < 3; ++k){
			for(int i1 = 0; i1 < 9; ++i1){
				addSlot(new Slot(playerInv, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
			}
		}

		//Player hotbar
		for(int l = 0; l < 9; ++l){
			addSlot(new Slot(playerInv, l, 8 + l * 18, 142));
		}

	}

	@Override
	public void fillStackedContents(RecipeItemHelper helper){
		inInv.fillStackedContents(helper);
	}

	@Override
	public void clear(){
		inInv.clear();
		outInv.clear();
	}

	@Override
	public boolean matches(IRecipe<? super CraftingInventory> recipeIn){
		if(recipeIn instanceof DetailedCrafterRec){
			return ((DetailedCrafterRec) recipeIn).getPath().isUnlocked(player) && recipeIn.matches(inInv, player.world);
		}else{
			return recipeIn.matches(inInv, player.world);
		}
	}

	/**
	 * Called when the container is closed.
	 */
	@Override
	public void onContainerClosed(PlayerEntity playerIn){
		super.onContainerClosed(playerIn);
		clearContainer(playerIn, world, inInv);
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn){
		return fake || pos == null || playerIn.world.getBlockState(pos).getBlock() == CRBlocks.detailedCrafter && playerIn.getDistanceSq((pos.getX()) + .5D, (pos.getY()) + .5D, (pos.getZ()) + .5D) <= 64;
	}

	/**
	 * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
	 * inventory and the other inventory(s).
	 */
	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index){
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);
		if(slot != null && slot.getHasStack()){
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if(index == 0){
				itemstack1.getItem().onCreated(itemstack1, world, playerIn);
				if(!mergeItemStack(itemstack1, 10, 46, true)){
					return ItemStack.EMPTY;
				}

				slot.onSlotChange(itemstack1, itemstack);
			}else if(index >= 10 && index < 37){
				if(!mergeItemStack(itemstack1, 37, 46, false)){
					return ItemStack.EMPTY;
				}
			}else if(index >= 37 && index < 46){
				if(!mergeItemStack(itemstack1, 10, 37, false)){
					return ItemStack.EMPTY;
				}
			}else if(!mergeItemStack(itemstack1, 10, 46, false)){
				return ItemStack.EMPTY;
			}

			if(itemstack1.isEmpty()){
				slot.putStack(ItemStack.EMPTY);
			}else{
				slot.onSlotChanged();
			}

			if(itemstack1.getCount() == itemstack.getCount()){
				return ItemStack.EMPTY;
			}

			ItemStack itemstack2 = slot.onTake(playerIn, itemstack1);
			if(index == 0){
				playerIn.dropItem(itemstack2, false);
			}
		}

		return itemstack;
	}

	/**
	 * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in is
	 * null for the initial slot that was double-clicked.
	 */
	@Override
	public boolean canMergeSlot(ItemStack stack, Slot slotIn){
		return slotIn.inventory != outInv && super.canMergeSlot(stack, slotIn);
	}

	@Override
	public int getOutputSlot(){
		return 0;
	}

	@Override
	public int getWidth(){
		return inInv.getWidth();
	}

	@Override
	public int getHeight(){
		return inInv.getHeight();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int getSize(){
		return 10;
	}

	@Override
	public RecipeBookCategory func_241850_m(){
		return RecipeBookCategory.CRAFTING;
	}

	/**
	 * On the client side, there can be a delay before the client is informed when unlocking a path.
	 * This represents the last path unlocked in this UI by this player on the client, and is wiped every time the ui is re-opened
	 * It exists to prevent unlocking the same path several times on the client side during this delay- a minor visual inventory-desync glitch when unlocking while holding shift
	 */
	private byte lastUnlock = -1;

	@Override
	public void onCraftMatrixChanged(IInventory inventoryIn){
		for(EnumPath path : EnumPath.values()){
			//Check for path unlocking
			if(!path.isUnlocked(player) && path.pathGatePassed(player) && unlockRecipe(path) && (!world.isRemote || lastUnlock != path.getIndex())){
				if(world.isRemote){
					lastUnlock = path.getIndex();
					playUnlockSound();
				}else{
					path.setUnlocked(player, true);
				}
				for(int i = 0; i < 9; i++){
					inInv.decrStackSize(i, 1);
				}
				return;
			}
		}

		if(!world.isRemote){
			ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) player;
			ItemStack itemstack = ItemStack.EMPTY;
			List<DetailedCrafterRec> recipes = world.getRecipeManager().getRecipes(CRRecipes.DETAILED_TYPE, inInv, world);
			//Find a detailed crafter specific recipe first
			Optional<? extends ICraftingRecipe> recipeOpt = recipes.stream().filter(rec -> rec.getPath().isUnlocked(player)).findFirst();
			//If there is no valid detailed crafter recipe, try vanilla crafting
			if(!recipeOpt.isPresent()){
				recipeOpt = world.getRecipeManager().getRecipe(IRecipeType.CRAFTING, inInv, world);
			}

			if(recipeOpt.isPresent()){
				ICraftingRecipe recipe = recipeOpt.get();
				if(outInv.canUseRecipe(world, serverplayerentity, recipe)){
					itemstack = recipe.getCraftingResult(inInv);
				}
			}
			outInv.setInventorySlotContents(0, itemstack);
			serverplayerentity.connection.sendPacket(new SSetSlotPacket(windowId, 0, itemstack));
		}
	}

	/**
	 * Checks if an "unlock recipe" is set
	 * @param path The path to check for
	 * @return Whether the current recipe is the correct one for unlocked the passed path
	 */
	private boolean unlockRecipe(EnumPath path){
		for(int i = 0; i < 9; i++){
			if(i != 4 && !fillerMats.contains(inInv.getStackInSlot(i).getItem())){
				return false;
			}
		}
		return unlockKeys[path.getIndex()].contains(inInv.getStackInSlot(4).getItem());
	}

	private void playUnlockSound(){
		world.playSound(player, player.getPosition(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 2, 0);
	}

	private static class SlotCraftingFlexible extends CraftingResultSlot{

		// The craft matrix inventory linked to this result slot.
		private final CraftingInventory craftMatrix;//We keep a copy because the superclass field is private

		public SlotCraftingFlexible(PlayerEntity player, CraftingInventory craftingInventory, IInventory inventoryIn, int slotIndex, int xPosition, int yPosition){
			super(player, craftingInventory, inventoryIn, slotIndex, xPosition, yPosition);
			craftMatrix = craftingInventory;
		}

		@Override
		public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack){
			onCrafting(stack);
			setCraftingPlayer(thePlayer);
			List<DetailedCrafterRec> recipes = thePlayer.world.getRecipeManager().getRecipes(CRRecipes.DETAILED_TYPE, craftMatrix, thePlayer.world);
			Optional<? extends ICraftingRecipe> recipeOpt = recipes.stream().filter(rec -> rec.getPath().isUnlocked(thePlayer)).findFirst();
			if(!recipeOpt.isPresent()){
				recipeOpt = thePlayer.world.getRecipeManager().getRecipe(IRecipeType.CRAFTING, craftMatrix, thePlayer.world);
			}
			if(recipeOpt.isPresent()){
				//Remove items if there is a matching recipe
				NonNullList<ItemStack> remaining = recipeOpt.get().getRemainingItems(craftMatrix);
				for(int i = 0; i < remaining.size(); i++){
					craftMatrix.decrStackSize(i, 1);//Consume crafting ingredients

					ItemStack remainStack = remaining.get(i);
					ItemStack invStack = craftMatrix.getStackInSlot(i);
					//Return any remaining items (ex. empty buckets)
					if(!remainStack.isEmpty()){
						if(invStack.isEmpty()){
							craftMatrix.setInventorySlotContents(i, remaining.get(i));//Put it back into the crafting slot if it's empty
						}else if(BlockUtil.sameItem(invStack, remainStack)){
							invStack.grow(remainStack.getCount());//Try stacking it into the crafting slot
							craftMatrix.setInventorySlotContents(i, invStack);
						}else if(!thePlayer.inventory.addItemStackToInventory(remainStack)){//Try returning it to the player inventory
							thePlayer.dropItem(remainStack, false);//Drop it as an item into the world
						}
					}
				}
			}

			return stack;
		}
	}
}
