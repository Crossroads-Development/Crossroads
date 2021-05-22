package com.Da_Technomancer.crossroads.items.witchcraft;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.item.BlockNamedItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WheezewortSeeds extends BlockNamedItem{

	private static final IDispenseItemBehavior PLANTING_DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior(){

		@Override
		public ItemStack execute(IBlockSource source, ItemStack stack){
			//Can be planted by dispenser
			BlockState dispenserState = source.getBlockState();
			if(dispenserState.hasProperty(DispenserBlock.FACING)){
				BlockPos plantPos = source.getPos().relative(dispenserState.getValue(DispenserBlock.FACING));
				BlockPos groundPos = plantPos.below();
				World world = source.getLevel();
				if(world.getBlockState(plantPos).canBeReplacedByLeaves(world, plantPos) && CRBlocks.wheezewort.canSustainPlant(world.getBlockState(groundPos), world, groundPos, Direction.UP, CRBlocks.wheezewort)){
					world.setBlockAndUpdate(plantPos, CRBlocks.wheezewort.defaultBlockState());
					stack.shrink(1);
					return stack;
				}
			}

			return stack;
		}
	};

	public WheezewortSeeds(){
		super(CRBlocks.wheezewort, new Item.Properties().tab(CRItems.TAB_CROSSROADS));
		setRegistryName("wheezewort_seeds");
		CRItems.toRegister.add(this);
		DispenserBlock.registerBehavior(this, PLANTING_DISPENSER_BEHAVIOR);
	}
}
