package com.Da_Technomancer.crossroads.API.effects.mechArm;

import com.Da_Technomancer.crossroads.entity.EntityArmRidable;
import com.Da_Technomancer.crossroads.tileentities.technomancy.MechanicalArmTileEntity;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class MechArmPickupOneFromInvEffect implements IMechArmEffect{

	@Override
	public boolean onTriggered(World world, BlockPos pos, double posX, double posY, double posZ, Direction side, EntityArmRidable ent, MechanicalArmTileEntity arm){
		TileEntity te = world.getTileEntity(pos);
		Boolean holdingStack = null;
		if(ent.getPassengers().isEmpty()){
			holdingStack = false;
		}else if(ent.getPassengers().get(0) instanceof ItemEntity){
			holdingStack = true;
		}
		if(te == null || holdingStack == null || !te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)){
			return false;
		}
		IItemHandler handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);

		ItemStack heldStack = holdingStack ? ((ItemEntity) ent.getPassengers().get(0)).getItem().copy() : ItemStack.EMPTY;
		int startSize = heldStack.getCount();

		for(int i = 0; i < handler.getSlots(); i++){
			ItemStack invStack = handler.getStackInSlot(i);
			if(heldStack.isEmpty() || heldStack.getMaxStackSize() > heldStack.getCount()){
				if(!invStack.isEmpty() && (heldStack.isEmpty() || (ItemStack.areItemsEqual(heldStack, invStack) && ItemStack.areItemStackTagsEqual(heldStack, invStack)))){
					ItemStack extracted = handler.extractItem(i, 1, false);
					int change = extracted.getCount();
					if(heldStack.isEmpty()){
						heldStack = extracted;
					}else{
						heldStack.grow(change);
					}
					if(change == 1){
						break;
					}
				}
			}else{
				break;
			}
		}
		if(!heldStack.isEmpty()){
			if(holdingStack){
				((ItemEntity) ent.getPassengers().get(0)).setItem(heldStack);
			}else{
				ItemEntity heldItemEnt = new ItemEntity(world, posX, posY, posZ, heldStack);
				heldItemEnt.startRiding(ent, true);
				world.addEntity(heldItemEnt);
			}
		}

		return heldStack.getCount() > startSize;
	}

	@Override
	public boolean useSideModifier(){
		return true;
	}
}