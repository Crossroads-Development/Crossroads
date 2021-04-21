package com.Da_Technomancer.crossroads.items;

import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.dispenser.OptionalDispenseBehavior;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Slag extends Item{

	private static final IDispenseItemBehavior SLAG_DISPENSER_BEHAVIOR = new OptionalDispenseBehavior(){
		@Override
		protected ItemStack execute(IBlockSource source, ItemStack stack){
			World world = source.getLevel();
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
	public ActionResultType useOn(ItemUseContext context){
		if(BoneMealItem.applyBonemeal(context.getItemInHand(), context.getLevel(), context.getClickedPos(), context.getPlayer())){
			if(!context.getLevel().isClientSide){
				context.getLevel().levelEvent(2005, context.getClickedPos(), 0);
			}
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.FAIL;
	}
}
