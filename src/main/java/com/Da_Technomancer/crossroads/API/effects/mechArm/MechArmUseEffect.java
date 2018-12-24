package com.Da_Technomancer.crossroads.API.effects.mechArm;

import java.util.Random;
import java.util.UUID;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.entity.EntityArmRidable;
import com.Da_Technomancer.crossroads.tileentities.technomancy.MechanicalArmTileEntity;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

public class MechArmUseEffect implements IMechArmEffect{

	private static final Random ID_GEN = new Random();
	
	@Override
	public boolean onTriggered(World world, BlockPos pos, double posX, double posY, double posZ, EnumFacing side, EntityArmRidable ent, MechanicalArmTileEntity te){
		if(!ent.getPassengers().isEmpty() && ent.getPassengers().get(0) instanceof EntityItem){
			EntityItem itemEnt = (EntityItem) ent.getPassengers().get(0);
			ItemStack heldStack = itemEnt.getItem();
			int oldSize = heldStack.getCount();
			boolean itemBlock = heldStack.getItem() instanceof ItemBlock;
			FakePlayer user = FakePlayerFactory.get((WorldServer) world, new GameProfile(new UUID(ID_GEN.nextLong(), ID_GEN.nextLong()), Main.MODID + "-arm_use_effect-" + world.provider.getDimension()));
			user.setHeldItem(EnumHand.MAIN_HAND, heldStack);
			user.setPositionAndRotation(posX, posY, posZ, side.getHorizontalAngle(), 0);
			user.eyeHeight = 0;
			Vec3d posVec = new Vec3d(posX, posY, posZ);
			EnumActionResult result = EnumActionResult.PASS;
			if(heldStack.getItem() instanceof ItemBlock){
				//Blocks can be placed in midair by this device. 
				boolean offsetPos = !world.getBlockState(pos.offset(side)).getBlock().isReplaceable(world, pos.offset(side));
				result = heldStack.onItemUse(user, world, offsetPos ? pos.offset(side) : pos, EnumHand.MAIN_HAND, side.getOpposite(), side == EnumFacing.WEST ? 1 : 0, side == EnumFacing.DOWN ? 1 : 0, side == EnumFacing.NORTH ? 1 : 0);
			}else{
				RayTraceResult trace = world.rayTraceBlocks(posVec, posVec.add(new Vec3d(side.getDirectionVec())), false, false, true);
				if(trace != null && trace.typeOfHit == RayTraceResult.Type.BLOCK){
					Vec3d hitVec = trace.hitVec.subtract(new Vec3d(trace.getBlockPos()));
					result = heldStack.onItemUse(user, world, trace.getBlockPos(), EnumHand.MAIN_HAND, trace.sideHit, (float) hitVec.x, (float) hitVec.y, (float) hitVec.z);
				}
			}
			if(result == EnumActionResult.PASS){
				ActionResult<ItemStack> actionResult = heldStack.useItemRightClick(world, user, EnumHand.MAIN_HAND);
				result = actionResult.getType();
				itemEnt.setItem(actionResult.getResult());
			}

			return result == EnumActionResult.SUCCESS && (!itemBlock || itemEnt.getItem().getCount() != oldSize);
		}
		return false;
	}

	@Override
	public boolean useSideModifier(){
		return true;
	}
}