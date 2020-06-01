package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.integration.curios.CurioHelper;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.LeydenJar;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.tileentities.electric.TeslaCoilTopTileEntity;
import com.google.common.collect.Multimap;
import net.minecraft.client.util.ITooltipFlag;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class TeslaRay extends Item{

	private static final int FE_USE = 1000;
	private static final int RANGE = 12;
	private static final int RADIUS = 5;
	private static final float DAMAGE = 7;

	public TeslaRay(){
		super(new Properties().group(CRItems.TAB_CROSSROADS).maxStackSize(1));
		String name = "tesla_ray";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack){
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);

		if(slot == EquipmentSlotType.MAINHAND){
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -2D, AttributeModifier.Operation.ADDITION));
		}

		return multimap;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.tesla_ray.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.tesla_ray.leyden"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.tesla_ray.quip").setStyle(MiscUtil.TT_QUIP));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand hand){
		float scale = playerIn.getCooledAttackStrength(0.5F);

		if(worldIn.isRemote){
			playerIn.resetCooldown();
			return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getHeldItem(hand));
		}

		ItemStack leyden = CurioHelper.getEquipped(CRItems.leydenJar, playerIn);
		if(hand == Hand.MAIN_HAND && !leyden.isEmpty() && LeydenJar.getCharge(leyden) >= FE_USE){
			//Stores attack targets, in order
			ArrayList<LivingEntity> targets = new ArrayList<>(4);

			//Populate and damage targets
			//The first target is found in a conical area with the vertex at the player
			List<LivingEntity> entities = worldIn.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(playerIn.getPosX(), playerIn.getPosY() + playerIn.getEyeHeight(), playerIn.getPosZ(), playerIn.getPosX(), playerIn.getPosY() + playerIn.getEyeHeight(), playerIn.getPosZ()).grow(RANGE), EntityPredicates.IS_ALIVE);
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
				return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getHeldItem(hand));
			}

			LeydenJar.setCharge(leyden, LeydenJar.getCharge(leyden) - FE_USE);
//			playerIn.setHeldItem(Hand.OFF_HAND, leydenStack);

			targets.add(closest);
			closest.attackEntityFrom(DamageSource.LIGHTNING_BOLT, DAMAGE * scale);


			//Only chains if attack is fully charged. Additional targets are found in a cubical area from previous ones
			if(scale >= 0.99F){//Check attack meter is charged
				//An arbitrary limit of 32 targets exists to prevent glitchy infinite chaining behaviour- which could occur under exceptional circumstances
				for(int i = 0; i < 32; i++){
					entities = worldIn.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(targets.get(i).getPosX(), targets.get(i).getPosY(), targets.get(i).getPosZ(), targets.get(i).getPosX(), targets.get(i).getPosY(), targets.get(i).getPosZ()).grow(RADIUS - i), EntityPredicates.IS_ALIVE);
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

				CRRenderUtil.addArc(playerIn.world, start, end, 1, 0, TeslaCoilTopTileEntity.COLOR_CODES[(int) (Math.random() * 3D)]);
			}

			return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getHeldItem(hand));
		}else{
			return new ActionResult<>(ActionResultType.FAIL, playerIn.getHeldItem(hand));
		}
	}
}
