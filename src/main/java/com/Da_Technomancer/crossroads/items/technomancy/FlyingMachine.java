package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.entity.EntityFlyingMachine;
import com.Da_Technomancer.crossroads.items.CrossroadsItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FlyingMachine extends Item{

	public FlyingMachine(){
		String name = "flying_machine";
		maxStackSize = 1;
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CrossroadsItems.TAB_CROSSROADS);
		CrossroadsItems.toRegister.add(this);
		CrossroadsItems.itemAddQue(this);
	}

	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn){
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		Vec3d vec3d = new Vec3d(playerIn.posX, playerIn.posY + playerIn.getEyeHeight(), playerIn.posZ);
		RayTraceResult ray = worldIn.rayTraceBlocks(vec3d, vec3d.add(playerIn.getLookVec().scale(5)), false);

		if(ray == null || ray.typeOfHit != RayTraceResult.Type.BLOCK){
			return new ActionResult<ItemStack>(ActionResultType.PASS, itemstack);
		}else{
			EntityFlyingMachine mach = new EntityFlyingMachine(worldIn);
			mach.setPosition(ray.hitVec.x, ray.hitVec.y, ray.hitVec.z);
			if(!worldIn.isRemote){
				worldIn.spawnEntity(mach);
			}

			if(!playerIn.capabilities.isCreativeMode){
				itemstack.shrink(1);
			}
			return new ActionResult<ItemStack>(ActionResultType.SUCCESS, itemstack);
		}
	}
}
