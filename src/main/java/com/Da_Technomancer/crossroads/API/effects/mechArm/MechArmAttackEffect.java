package com.Da_Technomancer.crossroads.API.effects.mechArm;

import com.Da_Technomancer.crossroads.API.CrReflection;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.entity.EntityArmRidable;
import com.Da_Technomancer.crossroads.tileentities.technomancy.MechanicalArmTileEntity;
import com.Da_Technomancer.essentials.ReflectionUtil;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class MechArmAttackEffect implements IMechArmEffect{

	private static final Random ID_GEN = new Random();
	private static final int ATTACK_RANGE = 3;

	private static final Field lastSwingField = ReflectionUtil.reflectField(CrReflection.SWING_TIME);

	@Override
	public boolean onTriggered(World world, BlockPos pos, double posX, double posY, double posZ, Direction side, EntityArmRidable ent, MechanicalArmTileEntity te){
		FakePlayer user = FakePlayerFactory.get((ServerWorld) world, new GameProfile(new UUID(ID_GEN.nextLong(), ID_GEN.nextLong()), Crossroads.MODID + "-arm_attack_effect-" + world.provider.getDimension()));
		user.setPositionAndRotation(posX, posY, posZ, side.getHorizontalAngle(), 0);
		user.eyeHeight = 0;

		ItemEntity itemEnt = null;
		if(!ent.getPassengers().isEmpty() && ent.getPassengers().get(0) instanceof ItemEntity){
			itemEnt = (ItemEntity) ent.getPassengers().get(0);
			user.setHeldItem(Hand.MAIN_HAND, itemEnt.getItem());
		}

		AxisAlignedBB targetBB;

		if(side.getAxisDirection() == Direction.AxisDirection.NEGATIVE){
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
				user.getAttributeMap().applyAttributeModifiers(itemEnt.getItem().getAttributeModifiers(EquipmentSlotType.MAINHAND));
				try{
					if(lastSwingField != null){
						lastSwingField.setInt(user, Integer.MAX_VALUE);//We need the attack cooldown to be full, as otherwise we deal 0 damage, and even when using FakePlayers there's no way to get at the field without reflection
					}
				}catch(IllegalAccessException e){
					Crossroads.logger.catching(e);
				}
			}
			user.attackTargetEntityWithCurrentItem(targets.get(0));
			if(itemEnt != null){
				itemEnt.setItem(user.getHeldItem(Hand.MAIN_HAND));
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