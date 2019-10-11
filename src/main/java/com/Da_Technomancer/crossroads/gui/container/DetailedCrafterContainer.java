package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.crafting.ComponentCraftingStack;
import com.Da_Technomancer.crossroads.items.crafting.ItemRecipePredicate;
import com.Da_Technomancer.crossroads.items.crafting.TagCraftingStack;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvents;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.function.Predicate;

public class DetailedCrafterContainer extends Container{

	private CraftingInventory inInv = new CraftingInventory(this, 3, 3);
	private CraftResultInventory outInv = new CraftResultInventory();
	private PlayerInventory playerInv;
	private final World world;
	private final BlockPos pos;
	private final boolean fake;

	public DetailedCrafterContainer(PlayerInventory playerInv, BlockPos pos, boolean fake){
		this.world = playerInv.player.world;
		this.pos = pos;
		this.playerInv = playerInv;
		this.fake = fake;

		// input 0-8
		for(int x = 0; x < 3; x++){
			for(int y = 0; y < 3; y++){
				addSlot(new Slot(inInv, (x * 3) + y, 30 + y * 18, 17 + x * 18));
			}
		}

		// output 9
		addSlot(new SlotCraftingFlexible(playerInv.player, inInv, outInv, 0, 124, 35));

		// Player Inventory, Slots 9-35, Slot IDs 10-36
		for(int y = 0; y < 3; ++y){
			for(int x = 0; x < 9; ++x){
				addSlot(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
			}
		}

		// Player Inventory, Slot 0-8, Slot IDs 37-46
		for(int x = 0; x < 9; ++x){
			addSlot(new Slot(playerInv, x, 8 + x * 18, 142));
		}
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn){
		return fake || world.getBlockState(pos).getBlock() == CrossroadsBlocks.detailedCrafter && playerIn.getDistanceSq((pos.getX()) + .5D, (pos.getY()) + .5D, (pos.getZ()) + .5D) <= 64;
	}

	@Override
	public void onCraftMatrixChanged(IInventory inventoryIn){
		ItemStack out;
		CompoundNBT nbt = world.isRemote ? StoreNBTToClient.clientPlayerTag : MiscUtil.getPlayerTag(playerInv.player);
		if(!world.isRemote && !nbt.contains("path")){
			nbt.put("path", new CompoundNBT());
		}

		if(nbt.getCompound("path").getBoolean("technomancy")){
			IRecipe recipe = findMatchingSpecialRecipe(inInv, world, RecipeHolder.technomancyRecipes);
			out = recipe == null ? ItemStack.EMPTY : recipe.getCraftingResult(inInv);
			if(out != ItemStack.EMPTY){
				outInv.setInventorySlotContents(0, out);
				return;
			}
		}else if(passesTechnomancyCriteria(nbt.getCompound("elements"), inInv) && CRConfig.technomancy.get() && (nbt.getBoolean("multiplayer") ? CRConfig.allowAllServer.get() || !nbt.getCompound("path").getBoolean("alchemy") : CRConfig.allowAllSingle.get() || !nbt.getCompound("path").getBoolean("alchemy"))){
			nbt.getCompound("path").putBoolean("technomancy", true);
			if(!world.isRemote){
				StoreNBTToClient.syncNBTToClient((ServerPlayerEntity) playerInv.player, false);
			}else{
				playUnlockSound();
			}

			for(int i = 0; i < 9; i++){
				inInv.decrStackSize(i, 1);
			}
		}

		if(nbt.getCompound("path").getBoolean("alchemy")){
			IRecipe recipe = findMatchingSpecialRecipe(inInv, world, RecipeHolder.alchemyRecipes);
			out = recipe == null ? ItemStack.EMPTY : recipe.getCraftingResult(inInv);
			if(out != ItemStack.EMPTY){
				outInv.setInventorySlotContents(0, out);
				return;
			}
		}else if(passesAlchemyCriteria(nbt.getCompound("elements"), inInv) && CRConfig.alchemy.get() && (nbt.getBoolean("multiplayer") ? CRConfig.allowAllServer.get() || !nbt.getCompound("path").getBoolean("technomancy") : CRConfig.allowAllSingle.get() || !nbt.getCompound("path").getBoolean("technomancy"))){
			nbt.getCompound("path").putBoolean("alchemy", true);
			if(!world.isRemote){
				StoreNBTToClient.syncNBTToClient((ServerPlayerEntity) playerInv.player, false);
			}else{
				playUnlockSound();
			}

			for(int i = 0; i < 9; i++){
				inInv.decrStackSize(i, 1);
			}
		}
		
		IRecipe recipe = CraftingManager.findMatchingRecipe(inInv, world);
		outInv.setInventorySlotContents(0, recipe == null ? ItemStack.EMPTY : recipe.getCraftingResult(inInv));
	}

	private void playUnlockSound(){
		world.playSound(playerInv.player, pos, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 2, 0);
	}

	private static final Predicate<ItemStack> fillerMats = new TagCraftingStack("gemDiamond");
	private static final Predicate<ItemStack> technomancyKey = new ComponentCraftingStack("gear");
	private static final Predicate<ItemStack> alchemyKey = new ItemRecipePredicate(Items.GLASS_BOTTLE, 0);

	private static boolean passesTechnomancyCriteria(CompoundNBT elementTag, CraftingInventory craftingInv){
		//In order to unlock technomancy, the player needs to have discovered time
		if(elementTag.contains(EnumBeamAlignments.TIME.name())){
			for(int i = 0; i < 3; i++){
				for(int j = 0; j < 3; j++){
					if(i != 1 && j != 1 && !fillerMats.test(craftingInv.getStackInRowAndColumn(i, j))){
						return false;
					}
				}
			}
			return technomancyKey.test(craftingInv.getStackInRowAndColumn(1, 1));
		}

		return false;
	}

	private static boolean passesAlchemyCriteria(CompoundNBT elementTag, CraftingInventory craftingInv){
		//In order to unlock alchemy, the player needs to have discovered all elements other than void and time. (Discovering void and/or time doesn't hurt)
		for(EnumBeamAlignments element : EnumBeamAlignments.values()){
			if(element != EnumBeamAlignments.TIME && element != EnumBeamAlignments.VOID && element != EnumBeamAlignments.NO_MATCH && !elementTag.getBoolean(element.name())){
				return false;
			}
		}

		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 3; j++){
				if(i != 1 && j != 1 && !fillerMats.test(craftingInv.getStackInRowAndColumn(i, j))){
					return false;
				}
			}
		}
		return alchemyKey.test(craftingInv.getStackInRowAndColumn(1, 1));
	}
	
	@Nullable
	private static IRecipe findMatchingSpecialRecipe(CraftingInventory craftInv, World world, ArrayList<IRecipe> recipes){
		for(IRecipe recipe : recipes){
			if(recipe.matches(craftInv, world)){
				return recipe;
			}
		}
		return null;
	}

	@Override
	public void onContainerClosed(PlayerEntity playerIn){
		super.onContainerClosed(playerIn);



		if(!world.isRemote){
			clearContainer(playerIn, world, inInv);
		}
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index){
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);

		if(slot != null && slot.getHasStack()){
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if(index == 9){
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

			if(index == 9){
				playerIn.dropItem(itemstack2, false);
			}
		}

		return itemstack;
	}

	@Override
	public boolean canMergeSlot(ItemStack stack, Slot slotIn){
		return slotIn.inventory != outInv && super.canMergeSlot(stack, slotIn);
	}

	private static class SlotCraftingFlexible extends Slot{
		/** The craft matrix inventory linked to this result slot. */
		private final CraftingInventory craftMatrix;
		/** The player that is using the GUI where this slot resides. */
		private final PlayerEntity player;
		/** The number of items that have been crafted so far. Gets passed to ItemStack.onCrafting before being reset. */
		private int amountCrafted;

		public SlotCraftingFlexible(PlayerEntity player, CraftingInventory craftingInventory, IInventory inventoryIn, int slotIndex, int xPosition, int yPosition)
		{
			super(inventoryIn, slotIndex, xPosition, yPosition);
			this.player = player;
			this.craftMatrix = craftingInventory;
		}

		/** Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel. */
		@Override
		public boolean isItemValid(ItemStack stack){
			return false;
		}

		/** Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
		 * stack. */
		@Override
		public ItemStack decrStackSize(int amount){
			if(getHasStack()){
				amountCrafted += Math.min(amount, getStack().getCount());
			}

			return super.decrStackSize(amount);
		}

		/** the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. Typically increases an
		 * internal count then calls onCrafting(item). */
		@Override
		protected void onCrafting(ItemStack stack, int amount){
			amountCrafted += amount;
			onCrafting(stack);
		}

		@Override
		protected void onSwapCraft(int p_190900_1_){
			amountCrafted += p_190900_1_;
		}

		/** the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. */
		@Override
		protected void onCrafting(ItemStack stack){
			if(amountCrafted > 0){
				stack.onCrafting(player.world, player, amountCrafted);
				net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerCraftingEvent(player, stack, craftMatrix);
			}

			amountCrafted = 0;

			CraftResultInventory inventorycraftresult = (CraftResultInventory)this.inventory;
			IRecipe irecipe = inventorycraftresult.getRecipeUsed();

			if (irecipe != null && !irecipe.isDynamic()){
				player.unlockRecipes(Lists.newArrayList(irecipe));
				inventorycraftresult.setRecipeUsed(null);
			}
		}

		@Override
		public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack){
			onCrafting(stack);
			if(!player.world.isRemote && !MiscUtil.getPlayerTag(player).contains("path")){
				MiscUtil.getPlayerTag(player).put("path", new CompoundNBT());
			}
			CompoundNBT nbt = player.world.isRemote ? StoreNBTToClient.clientPlayerTag.getCompound("path") : MiscUtil.getPlayerTag(player).getCompound("path");
			IRecipe recipe = null;
			if(nbt.getBoolean("technomancy")){
				recipe = findMatchingSpecialRecipe(craftMatrix, player.world, RecipeHolder.technomancyRecipes);
			}
			if(recipe == null && nbt.getBoolean("alchemy")){
				recipe = findMatchingSpecialRecipe(craftMatrix, player.world, RecipeHolder.alchemyRecipes);
			}
			NonNullList<ItemStack> nonnulllist;

			if(recipe == null){
				net.minecraftforge.common.ForgeHooks.setCraftingPlayer(thePlayer);
				nonnulllist = CraftingManager.getRemainingItems(craftMatrix, thePlayer.world);
				net.minecraftforge.common.ForgeHooks.setCraftingPlayer(null);
			}else{
				nonnulllist = recipe.getRemainingItems(craftMatrix);
			}

			for(int i = 0; i < nonnulllist.size(); ++i){
				ItemStack itemstack = craftMatrix.getStackInSlot(i);
				ItemStack itemstack1 = nonnulllist.get(i);

				if(!itemstack.isEmpty()){
					craftMatrix.decrStackSize(i, 1);
					itemstack = craftMatrix.getStackInSlot(i);
				}

				if(!itemstack1.isEmpty()){
					if(itemstack.isEmpty()){
						craftMatrix.setInventorySlotContents(i, itemstack1);
					}else if(ItemStack.areItemsEqual(itemstack, itemstack1) && ItemStack.areItemStackTagsEqual(itemstack, itemstack1)){
						itemstack1.grow(itemstack.getCount());
						craftMatrix.setInventorySlotContents(i, itemstack1);
					}else if(!player.inventory.addItemStackToInventory(itemstack1)){
						player.dropItem(itemstack1, false);
					}
				}
			}

			return stack;
		}
	}
}
