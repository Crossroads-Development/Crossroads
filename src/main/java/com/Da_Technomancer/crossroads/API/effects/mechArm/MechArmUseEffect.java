package com.Da_Technomancer.crossroads.API.effects.mechArm;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.entity.EntityArmRidable;
import com.Da_Technomancer.crossroads.tileentities.technomancy.MechanicalArmTileEntity;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

public class MechArmUseEffect implements IMechArmEffect{

	@Override
	public boolean onTriggered(World world, BlockPos pos, double posX, double posY, double posZ, EnumFacing side, EntityArmRidable ent, MechanicalArmTileEntity te){
		if(!ent.getPassengers().isEmpty() && ent.getPassengers().get(0) instanceof EntityItem){
			EntityItem itemEnt = (EntityItem) ent.getPassengers().get(0);
			FakePlayer user = FakePlayerFactory.get((WorldServer) world, new GameProfile(null, Main.MODID + "-arm_use_effect-" + world.provider.getDimension()));
			EnumActionResult result = itemEnt.getEntityItem().onItemUse(user, world, pos.offset(side.getOpposite()), EnumHand.MAIN_HAND, side, side.getFrontOffsetX(), side.getFrontOffsetY(), side.getFrontOffsetZ());
			return result == EnumActionResult.SUCCESS;
		}
		return false;
	}
}