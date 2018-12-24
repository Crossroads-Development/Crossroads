package com.Da_Technomancer.crossroads.API.effects.mechArm;

import com.Da_Technomancer.crossroads.entity.EntityArmRidable;
import com.Da_Technomancer.crossroads.tileentities.technomancy.MechanicalArmTileEntity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MechArmPickupBlockEffect implements IMechArmEffect{

	@Override
	public boolean onTriggered(World world, BlockPos pos, double posX, double posY, double posZ, EnumFacing side, EntityArmRidable ent, MechanicalArmTileEntity te){
		IBlockState state = world.getBlockState(pos);
		NonNullList<ItemStack> drops = NonNullList.create();
		state.getBlock().getDrops(drops, world, pos, state, 0);
		if(!drops.isEmpty()){
			if(ent.getPassengers().size() == 0){
				EntityItem heldStackEnt = new EntityItem(world, posX, posY, posZ, drops.get(0));
				heldStackEnt.startRiding(ent, true);
				world.spawnEntity(heldStackEnt);
				drops.remove(0);
			}else{
				if(ent.getPassengers().get(0) instanceof EntityItem){
					ItemStack heldStack = ((EntityItem) ent.getPassengers().get(0)).getItem();
					for(ItemStack drop : drops){
						if(heldStack.getMaxStackSize() > heldStack.getCount()){
							if(ItemStack.areItemsEqual(heldStack, drop) && ItemStack.areItemStackTagsEqual(heldStack, drop)){
								int change = Math.min(heldStack.getMaxStackSize() - heldStack.getCount(), drop.getCount());
								heldStack.grow(change);
								drop.shrink(change);
							}
						}else{
							break;
						}
					}
				}
			}
			world.destroyBlock(pos, false);
			for(ItemStack drop : drops){
				//It is safe to pass an empty ItemStack to this method.
				InventoryHelper.spawnItemStack(world, posX, posY, posZ, drop);
			}
			return true;
		}

		return false;
	}

	@Override
	public boolean useSideModifier(){
		return false;
	}
}