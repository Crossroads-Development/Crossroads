package com.Da_Technomancer.crossroads.API;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

/**Tile entities should only use ISidedInventory if they really need to (GUI for example). Otherwise just use Capabilities.
 * 
 */
public abstract class AbstractInventory extends TileEntity implements ISidedInventory{

	@Override
	public boolean hasCustomName(){
		return false;
	}

	@Override
	public ITextComponent getDisplayName(){
		return new TextComponentTranslation(this.getName());
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player){
		return this.worldObj.getTileEntity(this.getPos()) == this && player.getDistanceSq(this.pos.add(0.5, 0.5, 0.5)) <= 64;
	}

	@Override
	public void openInventory(EntityPlayer player){

	}

	@Override
	public void closeInventory(EntityPlayer player){

	}
}
