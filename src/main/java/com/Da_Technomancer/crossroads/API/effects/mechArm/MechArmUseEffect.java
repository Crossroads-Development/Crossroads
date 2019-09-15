package com.Da_Technomancer.crossroads.API.effects.mechArm;

import java.util.Random;
import java.util.UUID;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.entity.EntityArmRidable;
import com.Da_Technomancer.crossroads.tileentities.technomancy.MechanicalArmTileEntity;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

public class MechArmUseEffect implements IMechArmEffect{

	private static final Random ID_GEN = new Random();
	
	@Override
	public boolean onTriggered(World world, BlockPos pos, double posX, double posY, double posZ, Direction side, EntityArmRidable ent, MechanicalArmTileEntity te){
		if(!ent.getPassengers().isEmpty() && ent.getPassengers().get(0) instanceof ItemEntity){
			ItemEntity itemEnt = (ItemEntity) ent.getPassengers().get(0);
			ItemStack heldStack = itemEnt.getItem();
			int oldSize = heldStack.getCount();
			boolean itemBlock = heldStack.getItem() instanceof BlockItem;
			FakePlayer user = FakePlayerFactory.get((ServerWorld) world, new GameProfile(new UUID(ID_GEN.nextLong(), ID_GEN.nextLong()), Crossroads.MODID + "-arm_use_effect-" + world.provider.getDimension()));
			user.setHeldItem(Hand.MAIN_HAND, heldStack);
			user.setPositionAndRotation(posX, posY, posZ, side.getHorizontalAngle(), 0);
			user.eyeHeight = 0;
			Vec3d posVec = new Vec3d(posX, posY, posZ);
			ActionResultType result = ActionResultType.PASS;
			if(heldStack.getItem() instanceof BlockItem){
				//Blocks can be placed in midair by this device. 
				boolean offsetPos = !world.getBlockState(pos.offset(side)).getBlock().isReplaceable(world, pos.offset(side));
				result = heldStack.onItemUse(user, world, offsetPos ? pos.offset(side) : pos, Hand.MAIN_HAND, side.getOpposite(), side == Direction.WEST ? 1 : 0, side == Direction.DOWN ? 1 : 0, side == Direction.NORTH ? 1 : 0);
			}else{
				RayTraceResult trace = world.rayTraceBlocks(posVec, posVec.add(new Vec3d(side.getDirectionVec())), false, false, true);
				if(trace != null && trace.typeOfHit == RayTraceResult.Type.BLOCK){
					Vec3d hitVec = trace.hitVec.subtract(new Vec3d(trace.getBlockPos()));
					result = heldStack.onItemUse(user, world, trace.getBlockPos(), Hand.MAIN_HAND, trace.sideHit, (float) hitVec.x, (float) hitVec.y, (float) hitVec.z);
				}
			}
			if(result == ActionResultType.PASS){
				ActionResult<ItemStack> actionResult = heldStack.useItemRightClick(world, user, Hand.MAIN_HAND);
				result = actionResult.getType();
				itemEnt.setItem(actionResult.getResult());
			}

			return result == ActionResultType.SUCCESS && (!itemBlock || itemEnt.getItem().getCount() != oldSize);
		}
		return false;
	}

	@Override
	public boolean useSideModifier(){
		return true;
	}
}