package com.Da_Technomancer.crossroads.API.effects.mechArm;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.entity.EntityArmRidable;
import com.Da_Technomancer.crossroads.tileentities.technomancy.MechanicalArmTileEntity;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class MechArmAttackEffect implements IMechArmEffect{

	private static final Random ID_GEN = new Random();
	private static final int ATTACK_RANGE = 3;

	private static final Field lastSwingField;

	static{
		Field holder = null;
		try{
			for(Field f : EntityLivingBase.class.getDeclaredFields()){
				if("field_184617_aD".equals(f.getName()) || "ticksSinceLastSwing".equals(f.getName())){
					holder = f;
					holder.setAccessible(true);
					break;
				}
			}
		}catch(Exception e){
			Main.logger.error("Something went wrong getting the player cooldown field. Disabling relevant features (mechanical arm attacking)");
			Main.logger.catching(e);
		}
		lastSwingField = holder;
	}

	@Override
	public boolean onTriggered(World world, BlockPos pos, double posX, double posY, double posZ, EnumFacing side, EntityArmRidable ent, MechanicalArmTileEntity te){
		FakePlayer user = FakePlayerFactory.get((WorldServer) world, new GameProfile(new UUID(ID_GEN.nextLong(), ID_GEN.nextLong()), Main.MODID + "-arm_attack_effect-" + world.provider.getDimension()));
		user.setPositionAndRotation(posX, posY, posZ, side.getHorizontalAngle(), 0);
		user.eyeHeight = 0;

		EntityItem itemEnt = null;
		if(!ent.getPassengers().isEmpty() && ent.getPassengers().get(0) instanceof EntityItem){
			itemEnt = (EntityItem) ent.getPassengers().get(0);
			user.setHeldItem(EnumHand.MAIN_HAND, itemEnt.getItem());
		}

		AxisAlignedBB targetBB;

		if(side.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE){
			targetBB = new AxisAlignedBB(posX - 0.5D + ATTACK_RANGE * side.getXOffset(), posY - 0.5D + ATTACK_RANGE * side.getYOffset(), posZ - 0.5D + ATTACK_RANGE * side.getZOffset(), posX + 0.5D, posY + 0.5D, posZ + 0.5D);
		}else{
			targetBB = new AxisAlignedBB(posX - 0.5D, posY - 0.5D, posZ - 0.5D, posX + 0.5D + ATTACK_RANGE * side.getXOffset(), posY + 0.5D + ATTACK_RANGE * side.getYOffset(), posZ + 0.5D + ATTACK_RANGE * side.getZOffset());
		}

		List<Entity> targets = world.getEntitiesWithinAABBExcludingEntity(ent, targetBB);
		if(itemEnt != null){
			targets.remove(itemEnt);
		}

		if(!targets.isEmpty()){
			if(itemEnt != null){
				user.getAttributeMap().applyAttributeModifiers(itemEnt.getItem().getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
				try{
					if(lastSwingField != null){
						lastSwingField.setInt(user, Integer.MAX_VALUE);//We need the attack cooldown to be full, as otherwise we deal 0 damage, and even when using FakePlayers there's no way to get at the field without reflection
					}
				}catch(IllegalAccessException e){
					Main.logger.catching(e);
				}
			}
			user.attackTargetEntityWithCurrentItem(targets.get(0));
			if(itemEnt != null){
				itemEnt.setItem(user.getHeldItem(EnumHand.MAIN_HAND));
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean useSideModifier(){
		return true;
	}
}