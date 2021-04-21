package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.entity.EntityFlyingMachine;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class FlyingMachine extends Item{

	private static final IDispenseItemBehavior SPAWN_DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior(){

		@Override
		public ItemStack execute(IBlockSource source, ItemStack stack){
			ServerWorld world = source.getLevel();
			EntityFlyingMachine.type.spawn(world, stack, null, source.getPos(), SpawnReason.SPAWN_EGG, true, false);			stack.shrink(1);
			return stack;
		}
	};

	public FlyingMachine(){
		super(new Properties().tab(CRItems.TAB_CROSSROADS).stacksTo(1));
		String name = "flying_machine";
		setRegistryName(name);
		CRItems.toRegister.add(this);
		DispenserBlock.registerBehavior(this, SPAWN_DISPENSER_BEHAVIOR);
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn){
		ItemStack itemstack = playerIn.getItemInHand(handIn);
		Vector3d vec3d = new Vector3d(playerIn.getX(), playerIn.getY() + playerIn.getEyeHeight(), playerIn.getZ());
		RayTraceResult ray = worldIn.clip(new RayTraceContext(vec3d, vec3d.add(playerIn.getLookAngle().scale(5)), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, playerIn));

		if(ray.getType() != RayTraceResult.Type.BLOCK){
			return new ActionResult<>(ActionResultType.PASS, itemstack);
		}else{
			if(!worldIn.isClientSide){
				EntityFlyingMachine.type.spawn((ServerWorld) worldIn, itemstack, playerIn, new BlockPos(ray.getLocation()), SpawnReason.SPAWN_EGG, true, false);
			}
			if(!playerIn.isCreative()){
				itemstack.shrink(1);
			}
			return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
		}
	}
}
