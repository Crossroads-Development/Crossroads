package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.integration.curios.CurioHelper;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.LeydenJar;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.tileentities.electric.TeslaCoilTopTileEntity;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class TeslaRay extends Item{

	private static final int FE_USE = 1000;
	private static final int RANGE = 12;
	private static final int RADIUS = 5;
	private static final float DAMAGE = 7;

	private final Multimap<Attribute, AttributeModifier> attributeModifiers;

	public TeslaRay(){
		super(new Properties().tab(CRItems.TAB_CROSSROADS).stacksTo(1));
		String name = "tesla_ray";
		setRegistryName(name);
		CRItems.toRegister.add(this);

		//Attributes
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -2D, AttributeModifier.Operation.ADDITION));
		attributeModifiers = builder.build();
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack){
		return slot == EquipmentSlot.MAINHAND ? attributeModifiers : super.getAttributeModifiers(slot, stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(new TranslatableComponent("tt.crossroads.tesla_ray.desc"));
		tooltip.add(new TranslatableComponent("tt.crossroads.tesla_ray.leyden"));
		tooltip.add(new TranslatableComponent("tt.crossroads.tesla_ray.quip").setStyle(MiscUtil.TT_QUIP));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand){
		float scale = playerIn.getAttackStrengthScale(0.5F);

		if(worldIn.isClientSide){
			playerIn.resetAttackStrengthTicker();
			return new InteractionResultHolder<>(InteractionResult.SUCCESS, playerIn.getItemInHand(hand));
		}

		ItemStack leyden = CurioHelper.getEquipped(CRItems.leydenJar, playerIn);
		if(hand == InteractionHand.MAIN_HAND && !leyden.isEmpty() && LeydenJar.getCharge(leyden) >= FE_USE){
			//Stores attack targets, in order
			ArrayList<LivingEntity> targets = new ArrayList<>(4);

			//Populate and damage targets
			//The first target is found in a conical area with the vertex at the player
			List<LivingEntity> entities = worldIn.getEntitiesOfClass(LivingEntity.class, new AABB(playerIn.getX(), playerIn.getY() + playerIn.getEyeHeight(), playerIn.getZ(), playerIn.getX(), playerIn.getY() + playerIn.getEyeHeight(), playerIn.getZ()).inflate(RANGE), EntitySelector.ENTITY_STILL_ALIVE);
			Predicate<LivingEntity> cannotTarget = (LivingEntity e) -> targets.contains(e) || e == playerIn || e instanceof ServerPlayer && !playerIn.canHarmPlayer((Player) e);

			//Removes entities from the list if they aren't in the conical region in the direction the player is looking, and checks PVP rules
			Vec3 look = playerIn.getLookAngle();
			Vec3 playPos = playerIn.getEyePosition(0);
			entities.removeIf((LivingEntity e) -> {Vec3 ePos = e.position().subtract(playPos); return ePos.cross(look).lengthSqr() > RADIUS * RADIUS || ePos.dot(look) > RANGE || ePos.dot(look) < 0 || cannotTarget.test(e);});

			double minDist = Integer.MAX_VALUE;
			LivingEntity closest = null;
			for(LivingEntity e : entities){
				if(e.position().distanceToSqr(playPos) < minDist){
					minDist = e.position().distanceToSqr(playPos);
					closest = e;
				}
			}

			if(closest == null){
				return new InteractionResultHolder<>(InteractionResult.SUCCESS, playerIn.getItemInHand(hand));
			}

			LeydenJar.setCharge(leyden, LeydenJar.getCharge(leyden) - FE_USE);
//			playerIn.setHeldItem(Hand.OFF_HAND, leydenStack);

			targets.add(closest);
			attackEntity(closest, scale);

			//Only chains if attack is fully charged. Additional targets are found in a cubical area from previous ones
			if(scale >= 0.99F){//Check attack meter is charged
				//An arbitrary limit of 32 targets exists to prevent glitchy infinite chaining behaviour- which could occur under exceptional circumstances
				for(int i = 0; i < 32; i++){
					entities = worldIn.getEntitiesOfClass(LivingEntity.class, new AABB(targets.get(i).getX(), targets.get(i).getY(), targets.get(i).getZ(), targets.get(i).getX(), targets.get(i).getY(), targets.get(i).getZ()).inflate(RADIUS - i), EntitySelector.ENTITY_STILL_ALIVE);
					entities.removeIf(cannotTarget);
					if(entities.isEmpty()){
						break;
					}

					targets.add(entities.get((int) (Math.random() * (double) entities.size())));
					attackEntity(targets.get(i + 1), scale);
				}
			}

			//Render the electric arcs. The player is added to targets for simplification, despite not taking damage
			targets.add(0, playerIn);
			for(int i = 0; i < targets.size() - 1; i++){
				Vec3 start = targets.get(i).position();
				if(i == 0){
					double angleOffset = 30D * (playerIn.getMainArm() == HumanoidArm.LEFT ? -1D : 1D);
					start = start.add(-Math.sin(Math.toRadians(playerIn.getYRot() + angleOffset)) * 0.4F, 0.8D, Math.cos(Math.toRadians(playerIn.getYRot() + angleOffset)) * 0.4F);
				}
				Vec3 end = targets.get(i + 1).getEyePosition(0);

				CRRenderUtil.addArc(playerIn.level, start, end, 1, 0, TeslaCoilTopTileEntity.COLOR_CODES[(int) (Math.random() * 3D)]);
			}

			return new InteractionResultHolder<>(InteractionResult.SUCCESS, playerIn.getItemInHand(hand));
		}else{
			return new InteractionResultHolder<>(InteractionResult.FAIL, playerIn.getItemInHand(hand));
		}
	}

	private void attackEntity(LivingEntity entity, float scale){
		if(scale >= 0.99F){
			//We want to apply lightning effects (ex. pig->pig zombie, creeper->charged creeper, etc) if this is fully charged
			MiscUtil.attackWithLightning(entity, DAMAGE, null);
		}else{
			entity.hurt(DamageSource.LIGHTNING_BOLT, DAMAGE * scale);
		}
	}
}
