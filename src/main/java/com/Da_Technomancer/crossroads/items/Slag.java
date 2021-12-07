package com.Da_Technomancer.crossroads.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class Slag extends Item{

	private static final DispenseItemBehavior SLAG_DISPENSER_BEHAVIOR = new OptionalDispenseItemBehavior(){
		@Override
		protected ItemStack execute(BlockSource source, ItemStack stack){
			Level world = source.getLevel();
			BlockPos blockpos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));

			//We currently use the deprecated method because this is what vanilla dispensers currently use
			if(BoneMealItem.growCrop(stack, world, blockpos)){
				if(!world.isClientSide){
					world.levelEvent(2005, blockpos, 0);
				}

				setSuccess(true);//Success
			}else{
				setSuccess(false);//Fail
			}
			return stack;
		}
	};

	protected Slag(){
		super(new Properties().tab(CRItems.TAB_CROSSROADS));
		String name = "slag";
		setRegistryName(name);
		CRItems.toRegister.add(this);
		DispenserBlock.registerBehavior(this, SLAG_DISPENSER_BEHAVIOR);
	}

	@Override
	public InteractionResult useOn(UseOnContext context){
		if(BoneMealItem.applyBonemeal(context.getItemInHand(), context.getLevel(), context.getClickedPos(), context.getPlayer())){
			if(!context.getLevel().isClientSide){
				context.getLevel().levelEvent(2005, context.getClickedPos(), 0);
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.FAIL;
	}
}
