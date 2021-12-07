package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.entity.EntityFlyingMachine;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class FlyingMachine extends Item{

	private static final DispenseItemBehavior SPAWN_DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior(){

		@Override
		public ItemStack execute(BlockSource source, ItemStack stack){
			ServerLevel world = source.getLevel();
			EntityFlyingMachine.type.spawn(world, stack, null, source.getPos(), MobSpawnType.SPAWN_EGG, true, false);			stack.shrink(1);
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
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn){
		ItemStack itemstack = playerIn.getItemInHand(handIn);
		Vec3 vec3d = new Vec3(playerIn.getX(), playerIn.getY() + playerIn.getEyeHeight(), playerIn.getZ());
		HitResult ray = worldIn.clip(new ClipContext(vec3d, vec3d.add(playerIn.getLookAngle().scale(5)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, playerIn));

		if(ray.getType() != HitResult.Type.BLOCK){
			return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
		}else{
			if(!worldIn.isClientSide){
				EntityFlyingMachine.type.spawn((ServerLevel) worldIn, itemstack, playerIn, new BlockPos(ray.getLocation()), MobSpawnType.SPAWN_EGG, true, false);
			}
			if(!playerIn.isCreative()){
				itemstack.shrink(1);
			}
			return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
		}
	}
}
