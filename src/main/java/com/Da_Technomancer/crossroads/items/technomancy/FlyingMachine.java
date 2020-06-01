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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FlyingMachine extends Item{

	private static final IDispenseItemBehavior SPAWN_DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior(){

		@Override
		public ItemStack dispenseStack(IBlockSource source, ItemStack stack){
			World world = source.getWorld();
			EntityFlyingMachine.type.spawn(world, stack, null, source.getBlockPos(), SpawnReason.SPAWN_EGG, true, false);			stack.shrink(1);
			return stack;
		}
	};

	public FlyingMachine(){
		super(new Properties().group(CRItems.TAB_CROSSROADS).maxStackSize(1));
		String name = "flying_machine";
		setRegistryName(name);
		CRItems.toRegister.add(this);
		DispenserBlock.registerDispenseBehavior(this, SPAWN_DISPENSER_BEHAVIOR);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn){
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		Vec3d vec3d = new Vec3d(playerIn.getPosX(), playerIn.getPosY() + playerIn.getEyeHeight(), playerIn.getPosZ());
		RayTraceResult ray = worldIn.rayTraceBlocks(new RayTraceContext(vec3d, vec3d.add(playerIn.getLookVec().scale(5)), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, playerIn));

		if(ray.getType() != RayTraceResult.Type.BLOCK){
			return new ActionResult<>(ActionResultType.PASS, itemstack);
		}else{
			EntityFlyingMachine.type.spawn(worldIn, itemstack, playerIn, new BlockPos(ray.getHitVec()), SpawnReason.SPAWN_EGG, true, false);

			if(!playerIn.isCreative()){
				itemstack.shrink(1);
			}
			return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
		}
	}
}
