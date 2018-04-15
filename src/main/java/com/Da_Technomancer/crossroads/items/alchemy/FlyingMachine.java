package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.entity.EntityFlyingMachine;
import com.Da_Technomancer.crossroads.items.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FlyingMachine extends Item{

	public FlyingMachine(){
		String name = "flying_machine";
		maxStackSize = 1;
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		ModItems.toRegister.add(this);
		ModItems.itemAddQue(this);
	}

	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn){
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		Vec3d vec3d = new Vec3d(playerIn.posX, playerIn.posY + playerIn.getEyeHeight(), playerIn.posZ);
		RayTraceResult ray = worldIn.rayTraceBlocks(vec3d, vec3d.add(playerIn.getLookVec().scale(5)), false);

		if(ray == null || ray.typeOfHit != RayTraceResult.Type.BLOCK){
			return new ActionResult<ItemStack>(EnumActionResult.PASS, itemstack);
		}else{
			EntityFlyingMachine mach = new EntityFlyingMachine(worldIn);
			mach.setPosition(ray.hitVec.x, ray.hitVec.y, ray.hitVec.z);
			if(!worldIn.isRemote){
				worldIn.spawnEntity(mach);
			}

			if(!playerIn.capabilities.isCreativeMode){
				itemstack.shrink(1);
			}
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
		}
	}
}
