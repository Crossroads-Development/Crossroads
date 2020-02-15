package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.entity.EntityFlyingMachine;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FlyingMachine extends Item{

	public FlyingMachine(){
		super(new Properties().group(CRItems.TAB_CROSSROADS).maxStackSize(1));
		String name = "flying_machine";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn){
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		Vec3d vec3d = new Vec3d(playerIn.posX, playerIn.posY + playerIn.getEyeHeight(), playerIn.posZ);
		RayTraceResult ray = worldIn.rayTraceBlocks(new RayTraceContext(vec3d, vec3d.add(playerIn.getLookVec().scale(5)), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, playerIn));

		if(ray.getType() != RayTraceResult.Type.BLOCK){
			return new ActionResult<>(ActionResultType.PASS, itemstack);
		}else{
			EntityFlyingMachine mach = EntityFlyingMachine.type.create(worldIn);
			mach.setPosition(ray.getHitVec().x, ray.getHitVec().y, ray.getHitVec().z);
			if(!worldIn.isRemote){
				worldIn.addEntity(mach);
			}

			if(!playerIn.isCreative()){
				itemstack.shrink(1);
			}
			return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
		}
	}
}
