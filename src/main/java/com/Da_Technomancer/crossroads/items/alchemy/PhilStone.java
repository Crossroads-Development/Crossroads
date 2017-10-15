package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.API.IInfoDevice;
import com.Da_Technomancer.crossroads.items.ModItems;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class PhilStone extends Item implements IInfoDevice{

	public PhilStone(){
		String name = "phil_stone";
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModItems.tabCrossroads);
		ModItems.toRegister.add(this);
		ModItems.itemAddQue(this);
	}

	/**
	 * Called by the default implemetation of EntityItem's onUpdate method, allowing for cleaner
	 * control over the update of the item without having to write a subclass.
	 *
	 * @param entityItem The entity Item
	 * @return Return true to skip any further update code.
	 */
	public boolean onEntityItemUpdate(EntityItem entityItem){
		if(entityItem.onGround){
			AxisAlignedBB entityBox = entityItem.getEntityBoundingBox();
			entityItem.world.setBlockState(new BlockPos(entityBox.maxX, entityBox.minY - 1D, entityBox.maxZ), Blocks.AIR.getDefaultState());
			entityItem.world.setBlockState(new BlockPos(entityBox.maxX, entityBox.minY - 1D, entityBox.minZ), Blocks.AIR.getDefaultState());
			entityItem.world.setBlockState(new BlockPos(entityBox.minX, entityBox.minY - 1D, entityBox.maxZ), Blocks.AIR.getDefaultState());
			entityItem.world.setBlockState(new BlockPos(entityBox.minX, entityBox.minY - 1D, entityBox.minZ), Blocks.AIR.getDefaultState());
		}
		return false;
	}
}
