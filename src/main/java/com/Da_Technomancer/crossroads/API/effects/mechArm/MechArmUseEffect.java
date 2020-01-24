package com.Da_Technomancer.crossroads.API.effects.mechArm;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.entity.EntityArmRidable;
import com.Da_Technomancer.crossroads.tileentities.technomancy.MechanicalArmTileEntity;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.Random;
import java.util.UUID;

public class MechArmUseEffect implements IMechArmEffect{

	private static final Random ID_GEN = new Random();
	
	@Override
	public boolean onTriggered(World world, BlockPos pos, double posX, double posY, double posZ, Direction side, EntityArmRidable ent, MechanicalArmTileEntity te){
		if(!ent.getPassengers().isEmpty() && ent.getPassengers().get(0) instanceof ItemEntity){
			ItemEntity itemEnt = (ItemEntity) ent.getPassengers().get(0);
			ItemStack heldStack = itemEnt.getItem();
			int oldSize = heldStack.getCount();
			boolean itemBlock = heldStack.getItem() instanceof BlockItem;
			FakePlayer user = FakePlayerFactory.get((ServerWorld) world, new GameProfile(new UUID(ID_GEN.nextLong(), ID_GEN.nextLong()), Crossroads.MODID + "-arm_use_effect-" + world.getDimension().getType().getId()));
			user.setHeldItem(Hand.MAIN_HAND, heldStack);
			user.setPositionAndRotation(posX, posY, posZ, side.getHorizontalAngle(), 0);
//			user.eyeHeight = 0;
			Vec3d posVec = new Vec3d(posX, posY, posZ);
			ActionResultType result = ActionResultType.PASS;
			if(heldStack.getItem() instanceof BlockItem){
				//Blocks can be placed in midair by this device.
				BlockRayTraceResult trace = new BlockRayTraceResult(posVec, side, pos, true);
				BlockItemUseContext context = new BlockItemUseContext(new ItemUseContext(user, Hand.MAIN_HAND, trace));
				if(!world.getBlockState(pos.offset(side)).isReplaceable(context)){
					context = new BlockItemUseContext(new ItemUseContext(user, Hand.MAIN_HAND, new BlockRayTraceResult(posVec.add(new Vec3d(side.getDirectionVec())), side, pos.offset(side), true)));
				}
				result = heldStack.onItemUse(context);
			}else{
				BlockRayTraceResult trace = world.rayTraceBlocks(new RayTraceContext(posVec, posVec.add(new Vec3d(side.getDirectionVec())), RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, user));
				if(trace.getType() == RayTraceResult.Type.BLOCK){
					ItemUseContext context = new ItemUseContext(user, Hand.MAIN_HAND, trace);
					result = heldStack.onItemUse(context);
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