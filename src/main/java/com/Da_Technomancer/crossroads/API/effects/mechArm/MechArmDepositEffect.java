package com.Da_Technomancer.crossroads.API.effects.mechArm;

import com.Da_Technomancer.crossroads.entity.EntityArmRidable;
import com.Da_Technomancer.crossroads.tileentities.technomancy.MechanicalArmTileEntity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class MechArmDepositEffect implements IMechArmEffect{

	@Override
	public boolean onTriggered(World world, BlockPos pos, double posX, double posY, double posZ, EnumFacing side, EntityArmRidable ent, MechanicalArmTileEntity arm){
		TileEntity te = world.getTileEntity(pos);
		if(te == null || ent.getPassengers().isEmpty() || !(ent.getPassengers().get(0) instanceof EntityItem) || !te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)){
			return false;
		}
		IItemHandler handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);

		EntityItem itemEnt = ((EntityItem) ent.getPassengers().get(0));
		ItemStack heldStack = itemEnt.getItem();
		int startSize = heldStack.getCount();

		for(int i = 0; i < handler.getSlots(); i++){
			if(heldStack.isEmpty()){
				break;
			}
			heldStack = handler.insertItem(i, heldStack, false);
		}
		itemEnt.setItem(heldStack);

		return heldStack.getCount() < startSize;
	}
}