package com.Da_Technomancer.crossroads.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;

public class Vacuum extends Item{

	private static final int RANGE = 5;
	private static final double ANGLE = Math.cos(Math.PI / 4F);//Pre-calc cosine for speed

	protected Vacuum(){
		super(new Properties().group(CRItems.TAB_CROSSROADS).maxStackSize(1).defaultMaxDamage(1200));
		String name = "vacuum";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Override
	public Rarity getRarity(ItemStack stack){
		return CRItems.BOBO_RARITY;
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

		playerIn.getHeldItem(hand).damageItem(1, playerIn, p -> p.sendBreakAnimation(hand));

		return ActionResult.newResult(ActionResultType.SUCCESS, playerIn.getHeldItem(hand));
	}
}
