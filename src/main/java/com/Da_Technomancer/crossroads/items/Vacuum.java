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
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Vacuum extends Item{

	private final static int range = 5;
	private final static double angle = Math.cos((Math.PI) / 4F);

	public Vacuum(){
		String name = "vacuum";
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		this.setCreativeTab(ModItems.tabCrossroads);
		this.maxStackSize = 1;
		this.setMaxDamage(1200);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World worldIn, EntityPlayer playerIn, EnumHand hand){
		ArrayList<Entity> entities = (ArrayList<Entity>) worldIn.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(playerIn.posX - range, playerIn.posY - range, playerIn.posZ - range, playerIn.posX + range, playerIn.posY + range, playerIn.posZ + range), EntitySelectors.IS_ALIVE);

		entities = areValid(entities, playerIn);

		for(Entity ent : entities){
			Vec3d motVec = playerIn.getPositionVector().subtract(ent.getPositionVector()).normalize();
			ent.addVelocity(motVec.xCoord, motVec.yCoord, motVec.zCoord);
		}

		stack.damageItem(1, playerIn);

		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
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
