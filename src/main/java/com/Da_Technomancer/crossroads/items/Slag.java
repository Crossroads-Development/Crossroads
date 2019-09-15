package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.items.crafting.ModCrafting;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.registry.Bootstrap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

public class Slag extends Item{


	private static final IDispenseItemBehavior SLAG_DISPENSER_BEHAVIOR = new Bootstrap.BehaviorDispenseOptional(){
		protected ItemStack dispenseStack(IBlockSource source, ItemStack stack){
			World world = source.getWorld();
			BlockPos blockpos = source.getBlockPos().offset((Direction) source.getBlockState().get(DispenserBlock.FACING));

			if(DyeItem.applyBonemeal(stack, world, blockpos)){
				if(!world.isRemote){
					world.playEvent(2005, blockpos, 0);
				}
				successful = true;
			}else{
				successful = false;
			}
			return stack;
		}
	};

	public Slag(){
		String name = "slag";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CrossroadsItems.TAB_CROSSROADS);
		CrossroadsItems.toRegister.add(this);
		CrossroadsItems.itemAddQue(this);
		DispenserBlock.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, SLAG_DISPENSER_BEHAVIOR);
		ModCrafting.toRegisterOreDict.add(Pair.of(this, new String[] {"itemSlag"}));
	}

	@Override
	public ActionResultType onItemUse(PlayerEntity player, World worldIn, BlockPos pos, Hand hand, Direction facing, BlockRayTraceResult hit){
		if(DyeItem.applyBonemeal(player.getHeldItem(hand), worldIn, pos, player, hand)){
			if(!worldIn.isRemote){
				worldIn.playEvent(2005, pos, 0);
			}
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.FAIL;
	}
}
