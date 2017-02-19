package com.Da_Technomancer.crossroads.gui.container;

import java.awt.Color;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.enums.MagicElements;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendElementNBTToClient;
import com.Da_Technomancer.crossroads.items.ModItems;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ColorChartContainer extends Container{

	private int index = 0;
	private static final int xCENTER = 142;
	private static final int yCENTER = 142;
	private static final int VAR_PER_ITEM = 2;
	private static final int RADIUS = 256 / VAR_PER_ITEM;
	private final World world;

	public ColorChartContainer(EntityPlayer player, World world, BlockPos pos){
		if(!world.isRemote){
			ModPackets.network.sendTo(new SendElementNBTToClient(MiscOp.getPlayerTag(player).getCompoundTag("elements")), (EntityPlayerMP) player);
		}
		this.world = world;
		ChartInventory inv = new ChartInventory();
		for(int i = 0; i < 32; i++){
			for(int j = 0; j < 32; j++){
				if(Math.pow(RADIUS - (16 * i), 2) + Math.pow(RADIUS - (16 * j), 2) <= RADIUS * RADIUS){
					addSlotToContainer(new Slot(inv, index++, xCENTER + RADIUS - (16 * i), yCENTER + RADIUS - (16 * j)){
						@Override
						public boolean isItemValid(@Nullable ItemStack stack){
							return false;
						}
					});
				}
			}
		}

	}

	@Nullable
	private static Color getColor(int x, int y){
		return Color.getHSBColor(((float) (Math.atan2(y - yCENTER, x - xCENTER) / (2D * Math.PI))), (float) Math.min(Math.sqrt(Math.pow(x - xCENTER, 2) + Math.pow(y - yCENTER, 2)) / RADIUS, 1), 1);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn){
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int fromSlot){
		return null;
	}

	private class ChartInventory implements IInventory{

		@Override
		public String getName(){
			return "Color Chart";
		}

		@Override
		public boolean hasCustomName(){
			return false;
		}

		@Override
		public ITextComponent getDisplayName(){
			return new TextComponentString(getName());
		}

		@Override
		public int getSizeInventory(){
			return inventorySlots.size();
		}

		@Override
		public ItemStack getStackInSlot(int index){
			if(index >= inventorySlots.size()){
				return null;
			}
			if(!world.isRemote){
				return null;
			}
			ItemStack item = new ItemStack(SendElementNBTToClient.elementNBT.hasKey(MagicElements.getElement(getColor(inventorySlots.get(index).xDisplayPosition, inventorySlots.get(index).yDisplayPosition)).name()) ? ModItems.invisItem : Item.getItemFromBlock(Blocks.BARRIER), 1);
			NBTTagCompound nbt = new NBTTagCompound();
			NBTTagList nbtlist = new NBTTagList();
			Color col = getColor(inventorySlots.get(index).xDisplayPosition, inventorySlots.get(index).yDisplayPosition);
			nbtlist.appendTag(new NBTTagString("R: " + col.getRed() + ", G: " + col.getGreen() + ", B: " + col.getBlue()));
			nbt.setTag("Lore", nbtlist);
			item.setTagInfo("display", nbt);
			item.setStackDisplayName(SendElementNBTToClient.elementNBT.hasKey(MagicElements.getElement(getColor(inventorySlots.get(index).xDisplayPosition, inventorySlots.get(index).yDisplayPosition)).name()) ? MagicElements.getElement(getColor(inventorySlots.get(index).xDisplayPosition, inventorySlots.get(index).yDisplayPosition)).name() : "UNDISCOVERED");

			return item;
		}

		@Override
		public ItemStack decrStackSize(int index, int count){
			return null;
		}

		@Override
		public ItemStack removeStackFromSlot(int index){
			return null;
		}

		@Override
		public void setInventorySlotContents(int index, ItemStack stack){

		}

		@Override
		public int getInventoryStackLimit(){
			return 1;
		}

		@Override
		public void markDirty(){

		}

		@Override
		public boolean isUseableByPlayer(EntityPlayer player){
			return true;
		}

		@Override
		public void openInventory(EntityPlayer player){

		}

		@Override
		public void closeInventory(EntityPlayer player){

		}

		@Override
		public boolean isItemValidForSlot(int index, ItemStack stack){
			return false;
		}

		@Override
		public int getField(int id){
			return 0;
		}

		@Override
		public void setField(int id, int value){

		}

		@Override
		public int getFieldCount(){
			return 0;
		}

		@Override
		public void clear(){

		}
	}
}
