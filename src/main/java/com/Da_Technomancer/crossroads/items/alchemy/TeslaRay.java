package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.items.LeydenJar;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.render.RenderUtil;
import com.Da_Technomancer.crossroads.tileentities.electric.TeslaCoilTopTileEntity;
import com.google.common.collect.Multimap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class TeslaRay extends Item{

	private static final int FE_USE = 1000;
	private static final int RANGE = 8;
	private static final int RADIUS = 4;
	private static final float DAMAGE = 6;

	public TeslaRay(){
		String name = "tesla_ray";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setMaxStackSize(1);
		ModItems.toRegister.add(this);
		ModItems.itemAddQue(this);
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack){
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);

		if (slot == EntityEquipmentSlot.MAINHAND){
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -3D, 0));
		}

		return multimap;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand){
		float scale = playerIn.getCooledAttackStrength(0.5F);

		if(worldIn.isRemote){
			playerIn.resetCooldown();
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand));
		}

		if(hand == EnumHand.MAIN_HAND && LeydenJar.getCharge(playerIn.getHeldItemOffhand()) >= FE_USE){
			//Stores attack targets, in order
			ArrayList<EntityLivingBase> targets = new ArrayList<EntityLivingBase>(4);

			//Populate and damage targets
			//The first target is found in a conical area with the vertex at the player
			List<EntityLivingBase> entities = worldIn.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(playerIn.posX, playerIn.posY + playerIn.getEyeHeight(), playerIn.posZ, playerIn.posX, playerIn.posY + playerIn.getEyeHeight(), playerIn.posZ).grow(RANGE), EntitySelectors.IS_ALIVE);
			Predicate<EntityLivingBase> cannotTarget = (EntityLivingBase e) -> targets.contains(e) || e == playerIn || e instanceof  EntityPlayerMP && !playerIn.canAttackPlayer((EntityPlayer) e);

			//Removes entities from the list if they aren't in the conical region in the direction the player is looking, and checks PVP rules
			Vec3d look = playerIn.getLookVec();
			Vec3d playPos = playerIn.getPositionEyes(0);
			entities.removeIf((EntityLivingBase e) -> {Vec3d ePos = e.getPositionVector().subtract(playPos); return ePos.crossProduct(look).lengthSquared() > RADIUS * RADIUS || ePos.dotProduct(look) > RANGE || ePos.dotProduct(look) < 0 || cannotTarget.test(e);});

			double minDist = Integer.MAX_VALUE;
			EntityLivingBase closest = null;
			for(EntityLivingBase e : entities){
				if(e.getPositionVector().squareDistanceTo(playPos) < minDist){
					minDist = e.getPositionVector().squareDistanceTo(playPos);
					closest = e;
				}
			}

			if(closest == null){
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand));
			}

			ItemStack leydenStack = playerIn.getHeldItemOffhand();
			LeydenJar.setCharge(leydenStack, LeydenJar.getCharge(leydenStack) - FE_USE);
			playerIn.setHeldItem(EnumHand.OFF_HAND, leydenStack);

			targets.add(closest);
			closest.attackEntityFrom(DamageSource.LIGHTNING_BOLT, DAMAGE * scale);


			//Only chains if attack is fully charged. Additional targets are found in a cubical area from previous ones
			if(scale >= 0.99F){
				for(int i = 0; i < 3; i++){
					entities = worldIn.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(targets.get(i).posX, targets.get(i).posY, targets.get(i).posZ, targets.get(i).posX, targets.get(i).posY, targets.get(i).posZ).grow(RADIUS - i), EntitySelectors.IS_ALIVE);
					entities.removeIf(cannotTarget);
					if(entities.isEmpty()){
						break;
					}

					targets.add(entities.get((int) (Math.random() * (double) entities.size())));
					targets.get(i + 1).attackEntityFrom(DamageSource.LIGHTNING_BOLT, DAMAGE * scale);
				}
			}

			//Render the electric arcs. The player is added to targets for simplification, despite not taking damage
			targets.add(0, playerIn);
			for(int i = 0; i < targets.size() - 1; i++){
				Vec3d start = targets.get(i).getPositionVector();
				if(i == 0){
					double angleOffset = 30D * (playerIn.getPrimaryHand() == EnumHandSide.LEFT ? -1D : 1D);
					start = start.add(-Math.sin(Math.toRadians(playerIn.rotationYaw + angleOffset)) * 0.4F, 0.8D, Math.cos(Math.toRadians(playerIn.rotationYaw + angleOffset)) * 0.4F);
				}
				Vec3d end = targets.get(i + 1).getPositionEyes(0);

				RenderUtil.addArc(playerIn.world.provider.getDimension(), start, end, 1, 0, TeslaCoilTopTileEntity.COLOR_CODES[(int) (Math.random() * 3D)]);
			}

			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand));
		}else{
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, playerIn.getHeldItem(hand));
		}
	}
}
