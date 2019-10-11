package com.Da_Technomancer.crossroads.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;

public class Vacuum extends Item{

	private static final int RANGE = 5;
	private static final double ANGLE = 1D / Math.sqrt(2D);//Math.cos(Math.PI / 4F);

	public Vacuum(){
		String name = "vacuum";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CRItems.TAB_CROSSROADS);
		maxStackSize = 1;
		setMaxDamage(1200);
		CRItems.toRegister.add(this);
		CRItems.itemAddQue(this);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand hand){
		ArrayList<Entity> entities = (ArrayList<Entity>) worldIn.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(playerIn.posX, playerIn.posY, playerIn.posZ, playerIn.posX, playerIn.posY, playerIn.posZ).grow(RANGE), EntityPredicates.IS_ALIVE);

		//Removes entities from the list if they aren't in the conical region in the direction the player is looking
		Vec3d look = playerIn.getLookVec().scale(RANGE);
		Vec3d playPos = playerIn.getPositionVector();
		entities.removeIf((Entity e) -> {Vec3d ePos = e.getPositionVector().subtract(playPos); return ePos.dotProduct(look) / (ePos.length() * look.length()) <= ANGLE || ePos.length() >= RANGE;});

		for(Entity ent : entities){
			Vec3d motVec = playerIn.getPositionVector().subtract(ent.getPositionVector()).scale(0.25D);
			ent.addVelocity(motVec.x, motVec.y, motVec.z);
		}

		playerIn.getHeldItem(hand).damageItem(1, playerIn);

		return ActionResult.newResult(ActionResultType.SUCCESS, playerIn.getHeldItem(hand));
	}
}
