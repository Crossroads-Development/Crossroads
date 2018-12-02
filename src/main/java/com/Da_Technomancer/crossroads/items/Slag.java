package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.items.crafting.ModCrafting;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

public class Slag extends Item{


	private static final IBehaviorDispenseItem SLAG_DISPENSER_BEHAVIOR = new Bootstrap.BehaviorDispenseOptional(){
		protected ItemStack dispenseStack(IBlockSource source, ItemStack stack){
			World world = source.getWorld();
			BlockPos blockpos = source.getBlockPos().offset((EnumFacing) source.getBlockState().getValue(BlockDispenser.FACING));

			if(ItemDye.applyBonemeal(stack, world, blockpos)){
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
		setCreativeTab(ModItems.TAB_CROSSROADS);
		ModItems.toRegister.add(this);
		ModItems.itemAddQue(this);
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, SLAG_DISPENSER_BEHAVIOR);
		ModCrafting.toRegisterOreDict.add(Pair.of(this, new String[] {"itemSlag"}));
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		if(ItemDye.applyBonemeal(player.getHeldItem(hand), worldIn, pos, player, hand)){
			if(!worldIn.isRemote){
				worldIn.playEvent(2005, pos, 0);
			}
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.FAIL;
	}
}
