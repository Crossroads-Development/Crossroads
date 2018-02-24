package com.Da_Technomancer.crossroads.items;

import java.util.ArrayList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Vacuum extends Item{

	private final static int range = 5;
	private final static double angle = Math.cos((Math.PI) / 4F);

	public Vacuum(){
		String name = "vacuum";
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		maxStackSize = 1;
		setMaxDamage(1200);
		ModItems.toRegister.add(this);
		ModItems.itemAddQue(this);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand){
		ArrayList<Entity> entities = (ArrayList<Entity>) worldIn.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(playerIn.posX - range, playerIn.posY - range, playerIn.posZ - range, playerIn.posX + range, playerIn.posY + range, playerIn.posZ + range), EntitySelectors.IS_ALIVE);

		entities = areValid(entities, playerIn);

		for(Entity ent : entities){
			Vec3d motVec = playerIn.getPositionVector().subtract(ent.getPositionVector()).normalize();
			ent.addVelocity(motVec.x, motVec.y, motVec.z);
		}

		playerIn.getHeldItem(hand).damageItem(1, playerIn);

		return ActionResult.newResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand));
	}

	private static ArrayList<Entity> areValid(ArrayList<Entity> listIn, EntityPlayer player){
		if(listIn == null){
			return null;
		}

		ArrayList<Entity> listOut = new ArrayList<Entity>();

		Vec3d look = player.getLookVec().scale(range);
		Vec3d playPos = player.getPositionVector();

		for(Entity ent : listIn){
			Vec3d ePos = ent.getPositionVector().subtract(playPos);

			if(ePos.dotProduct(look) / ePos.lengthVector() / look.lengthVector() > angle && ePos.lengthVector() < range){
				listOut.add(ent);
			}
		}

		return listOut;
	}
}
