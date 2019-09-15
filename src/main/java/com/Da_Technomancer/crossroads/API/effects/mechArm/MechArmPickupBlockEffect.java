package com.Da_Technomancer.crossroads.API.effects.mechArm;

import com.Da_Technomancer.crossroads.entity.EntityArmRidable;
import com.Da_Technomancer.crossroads.tileentities.technomancy.MechanicalArmTileEntity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MechArmPickupBlockEffect implements IMechArmEffect{

	@Override
	public boolean onTriggered(World world, BlockPos pos, double posX, double posY, double posZ, Direction side, EntityArmRidable ent, MechanicalArmTileEntity te){
		BlockState state = world.getBlockState(pos);
		NonNullList<ItemStack> drops = NonNullList.create();
		state.getBlock().getDrops(drops, world, pos, state, 0);
		if(!drops.isEmpty()){
			if(ent.getPassengers().size() == 0){
				ItemEntity heldStackEnt = new ItemEntity(world, posX, posY, posZ, drops.get(0));
				heldStackEnt.startRiding(ent, true);
				world.spawnEntity(heldStackEnt);
				drops.remove(0);
			}else{
				if(ent.getPassengers().get(0) instanceof ItemEntity){
					ItemStack heldStack = ((ItemEntity) ent.getPassengers().get(0)).getItem();
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