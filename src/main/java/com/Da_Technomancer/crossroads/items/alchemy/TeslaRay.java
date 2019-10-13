package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.LeydenJar;
import com.Da_Technomancer.crossroads.render.RenderUtil;
import com.Da_Technomancer.crossroads.tileentities.electric.TeslaCoilTopTileEntity;
import com.google.common.collect.Multimap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
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
		setCreativeTab(CRItems.TAB_CROSSROADS);
		setMaxStackSize(1);
		CRItems.toRegister.add(this);
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack){
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);

		if (slot == EquipmentSlotType.MAINHAND){
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -3D, 0));
		}

		return multimap;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand hand){
		float scale = playerIn.getCooledAttackStrength(0.5F);

		if(worldIn.isRemote){
			playerIn.resetCooldown();
			return new ActionResult<ItemStack>(ActionResultType.SUCCESS, playerIn.getHeldItem(hand));
		}

		if(hand == Hand.MAIN_HAND && LeydenJar.getCharge(playerIn.getHeldItemOffhand()) >= FE_USE){
			//Stores attack targets, in order
			ArrayList<LivingEntity> targets = new ArrayList<LivingEntity>(4);

			//Populate and damage targets
			//The first target is found in a conical area with the vertex at the player
			List<LivingEntity> entities = worldIn.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(playerIn.posX, playerIn.posY + playerIn.getEyeHeight(), playerIn.posZ, playerIn.posX, playerIn.posY + playerIn.getEyeHeight(), playerIn.posZ).grow(RANGE), EntityPredicates.IS_ALIVE);
			Predicate<LivingEntity> cannotTarget = (LivingEntity e) -> targets.contains(e) || e == playerIn || e instanceof ServerPlayerEntity && !playerIn.canAttackPlayer((PlayerEntity) e);

			//Removes entities from the list if they aren't in the conical region in the direction the player is looking, and checks PVP rules
			Vec3d look = playerIn.getLookVec();
			Vec3d playPos = playerIn.getEyePosition(0);
			entities.removeIf((LivingEntity e) -> {Vec3d ePos = e.getPositionVector().subtract(playPos); return ePos.crossProduct(look).lengthSquared() > RADIUS * RADIUS || ePos.dotProduct(look) > RANGE || ePos.dotProduct(look) < 0 || cannotTarget.test(e);});

			double minDist = Integer.MAX_VALUE;
			LivingEntity closest = null;
			for(LivingEntity e : entities){
				if(e.getPositionVector().squareDistanceTo(playPos) < minDist){
					minDist = e.getPositionVector().squareDistanceTo(playPos);
					closest = e;
				}
			}

			if(closest == null){
				return new ActionResult<ItemStack>(ActionResultType.SUCCESS, playerIn.getHeldItem(hand));
			}

			ItemStack leydenStack = playerIn.getHeldItemOffhand();
			LeydenJar.setCharge(leydenStack, LeydenJar.getCharge(leydenStack) - FE_USE);
			playerIn.setHeldItem(Hand.OFF_HAND, leydenStack);

			targets.add(closest);
			closest.attackEntityFrom(DamageSource.LIGHTNING_BOLT, DAMAGE * scale);


			//Only chains if attack is fully charged. Additional targets are found in a cubical area from previous ones
			if(scale >= 0.99F){
				for(int i = 0; i < 3; i++){
					entities = worldIn.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(targets.get(i).posX, targets.get(i).posY, targets.get(i).posZ, targets.get(i).posX, targets.get(i).posY, targets.get(i).posZ).grow(RADIUS - i), EntityPredicates.IS_ALIVE);
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
					double angleOffset = 30D * (playerIn.getPrimaryHand() == HandSide.LEFT ? -1D : 1D);
					start = start.add(-Math.sin(Math.toRadians(playerIn.rotationYaw + angleOffset)) * 0.4F, 0.8D, Math.cos(Math.toRadians(playerIn.rotationYaw + angleOffset)) * 0.4F);
				}
				Vec3d end = targets.get(i + 1).getEyePosition(0);

				RenderUtil.addArc(playerIn.world.provider.getDimension(), start, end, 1, 0, TeslaCoilTopTileEntity.COLOR_CODES[(int) (Math.random() * 3D)]);
			}

			return new ActionResult<ItemStack>(ActionResultType.SUCCESS, playerIn.getHeldItem(hand));
		}else{
			return new ActionResult<ItemStack>(ActionResultType.FAIL, playerIn.getHeldItem(hand));
		}
	}
}
