package com.Da_Technomancer.crossroads.gui.container;

import java.util.ArrayList;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.magic.EnumMagicElements;
import com.Da_Technomancer.crossroads.API.packets.StoreNBTToClient;
import com.Da_Technomancer.crossroads.API.rotary.GearTypes;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class DetailedCrafterContainer extends Container{

	private InventoryCrafting inInv = new InventoryCrafting(this, 3, 3);
	private InventoryCraftResult outInv = new InventoryCraftResult();
	private InventoryPlayer playerInv;
	private final World world;
	private final BlockPos pos;

	public DetailedCrafterContainer(InventoryPlayer playerInv, BlockPos pos){
		this.world = playerInv.player.world;
		this.pos = pos;
		this.playerInv = playerInv;

		// input 0-8
		for(int x = 0; x < 3; x++){
			for(int y = 0; y < 3; y++){
				addSlotToContainer(new Slot(inInv, (x * 3) + y, 30 + y * 18, 17 + x * 18));
			}
		}

		// output 9
		addSlotToContainer(new SlotCraftingFlexible(playerInv.player, inInv, outInv, 0, 124, 35));

		// Player Inventory, Slots 9-35, Slot IDs 10-36
		for(int y = 0; y < 3; ++y){
			for(int x = 0; x < 9; ++x){
				addSlotToContainer(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
			}
		}

		// Player Inventory, Slot 0-8, Slot IDs 37-46
		for(int x = 0; x < 9; ++x){
			addSlotToContainer(new Slot(playerInv, x, 8 + x * 18, 142));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn){
		return world.getBlockState(pos).getBlock() == ModBlocks.detailedCrafter && playerIn.getDistanceSq((pos.getX()) + .5D, (pos.getY()) + .5D, (pos.getZ()) + .5D) <= 64;
	}

	private static final IRecipe UNLOCK_TECHNOMANCY = new ShapelessOreRecipe(null, new ItemStack(GearFactory.BASIC_GEARS.get(GearTypes.COPSHOWIUM), 1), "gearBronze", "gearBronze", "gearBronze", "gearBronze", "gearBronze", "gearBronze", "gearBronze", "gearBronze", "gearBronze");
	private static final IRecipe UNLOCK_ALCHEMY = new ShapelessOreRecipe(null, new ItemStack(Items.POTIONITEM, 1), Items.GLASS_BOTTLE, Items.GLASS_BOTTLE, Items.GLASS_BOTTLE, Items.GLASS_BOTTLE, Items.GLASS_BOTTLE, Items.GLASS_BOTTLE, Items.GLASS_BOTTLE, Items.GLASS_BOTTLE, Items.GLASS_BOTTLE);
	
	@Override
	public void onCraftMatrixChanged(IInventory inventoryIn){
		ItemStack out = ItemStack.EMPTY;
		NBTTagCompound nbt = world.isRemote ? StoreNBTToClient.clientPlayerTag : MiscOp.getPlayerTag(playerInv.player);
		if(!world.isRemote && !nbt.hasKey("path")){
			nbt.setTag("path", new NBTTagCompound());
		}
		
		if(nbt.getCompoundTag("path").getBoolean("technomancy")){
			IRecipe recipe = findMatchingSpecialRecipe(inInv, world, RecipeHolder.technomancyRecipes);
			out = recipe == null ? ItemStack.EMPTY : recipe.getCraftingResult(inInv);
			if(out != ItemStack.EMPTY){
				outInv.setInventorySlotContents(0, out);
				return;
			}
		}else if(UNLOCK_TECHNOMANCY.matches(inInv, world) && nbt.getCompoundTag("elements").hasKey(EnumMagicElements.TIME.name()) && ModConfig.getConfigBool(ModConfig.technomancy, world.isRemote) && (nbt.getBoolean("multiplayer") ? ModConfig.getConfigBool(ModConfig.allowAllServer, world.isRemote) || !nbt.getCompoundTag("path").getBoolean("alchemy") : ModConfig.getConfigBool(ModConfig.allowAllSingle, world.isRemote) || !nbt.getCompoundTag("path").getBoolean("alchemy"))){
			for(int i = 0; i < 9; i++){
				inInv.decrStackSize(i, 1);
			}
			nbt.getCompoundTag("path").setBoolean("technomancy", true);
			if(!world.isRemote){
				StoreNBTToClient.syncNBTToClient((EntityPlayerMP) playerInv.player, false);
			}
		}
		
		if(nbt.getCompoundTag("path").getBoolean("alchemy")){
			IRecipe recipe = findMatchingSpecialRecipe(inInv, world, RecipeHolder.alchemyRecipes);
			out = recipe == null ? ItemStack.EMPTY : recipe.getCraftingResult(inInv);
			if(out != ItemStack.EMPTY){
				outInv.setInventorySlotContents(0, out);
				return;
			}
		}else if(UNLOCK_ALCHEMY.matches(inInv, world) && passesAlchemyCriteria(nbt.getCompoundTag("elements")) && ModConfig.getConfigBool(ModConfig.alchemy, world.isRemote) && (nbt.getBoolean("multiplayer") ? ModConfig.getConfigBool(ModConfig.allowAllServer, world.isRemote) || !nbt.getCompoundTag("path").getBoolean("technomancy") : ModConfig.getConfigBool(ModConfig.allowAllSingle, world.isRemote) || !nbt.getCompoundTag("path").getBoolean("technomancy"))){
			for(int i = 0; i < 9; i++){
				inInv.decrStackSize(i, 1);
			}
			nbt.getCompoundTag("path").setBoolean("alchemy", true);
			if(!world.isRemote){
				StoreNBTToClient.syncNBTToClient((EntityPlayerMP) playerInv.player, false);
			}
		}
		
		IRecipe recipe = CraftingManager.findMatchingRecipe(inInv, world);
		outInv.setInventorySlotContents(0, recipe == null ? ItemStack.EMPTY : recipe.getCraftingResult(inInv));
	}

	private static boolean passesAlchemyCriteria(NBTTagCompound elementTag){
		//In order to unlock alchemy, the player needs to have discovered all elements other than void and time. (Discovering void and/or time doesn't hurt)
		for(EnumMagicElements element : EnumMagicElements.values()){
			if(element != EnumMagicElements.TIME && element != EnumMagicElements.VOID && element != EnumMagicElements.NO_MATCH && !elementTag.getBoolean(element.name())){
				return false;
			}
		}
		
		return true;
	}
	
	@Nullable
	private static IRecipe findMatchingSpecialRecipe(InventoryCrafting craftInv, World world, ArrayList<IRecipe> recipes){
		for(IRecipe recipe : recipes){
			if(recipe.matches(craftInv, world)){
				return recipe;
			}
		}
		return null;
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn){
		super.onContainerClosed(playerIn);

		if(!world.isRemote){
			for(int i = 0; i < 9; ++i){
				ItemStack itemstack = inInv.removeStackFromSlot(i);

				if(!itemstack.isEmpty()){
					playerIn.dropItem(itemstack, false);
				}
			}
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index){
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
		private final InventoryCrafting craftMatrix;
		/** The player that is using the GUI where this slot resides. */
		private final EntityPlayer player;
		/** The number of items that have been crafted so far. Gets passed to ItemStack.onCrafting before being reset. */
		private int amountCrafted;

		public SlotCraftingFlexible(EntityPlayer player, InventoryCrafting craftingInventory, IInventory inventoryIn, int slotIndex, int xPosition, int yPosition)
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

			InventoryCraftResult inventorycraftresult = (InventoryCraftResult)this.inventory;
			IRecipe irecipe = inventorycraftresult.getRecipeUsed();

			if (irecipe != null && !irecipe.isDynamic()){
				player.unlockRecipes(Lists.newArrayList(irecipe));
				inventorycraftresult.setRecipeUsed(null);
			}
		}

		@Override
		public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack){
			onCrafting(stack);
			if(!player.world.isRemote && !MiscOp.getPlayerTag(player).hasKey("path")){
				MiscOp.getPlayerTag(player).setTag("path", new NBTTagCompound());
			}
			NBTTagCompound nbt = player.world.isRemote ? StoreNBTToClient.clientPlayerTag.getCompoundTag("path") : MiscOp.getPlayerTag(player).getCompoundTag("path");
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
