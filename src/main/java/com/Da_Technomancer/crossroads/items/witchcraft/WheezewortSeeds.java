package com.Da_Technomancer.crossroads.items.witchcraft;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.TriState;

public class WheezewortSeeds extends ItemNameBlockItem{

	private static final DispenseItemBehavior PLANTING_DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior(){

		@Override
		public ItemStack execute(BlockSource source, ItemStack stack){
			//Can be planted by dispenser
			BlockState dispenserState = source.state();
			if(dispenserState.hasProperty(DispenserBlock.FACING)){
				BlockPos plantPos = source.pos().relative(dispenserState.getValue(DispenserBlock.FACING));
				BlockPos groundPos = plantPos.below();
				Level world = source.level();
				if(world.getBlockState(plantPos).isAir() && CRBlocks.wheezewort.canSustainPlant(world.getBlockState(groundPos), world, groundPos, Direction.UP, CRBlocks.wheezewort.defaultBlockState()) == TriState.TRUE){
					world.setBlockAndUpdate(plantPos, CRBlocks.wheezewort.defaultBlockState());
					stack.shrink(1);
					return stack;
				}
			}

			return stack;
		}
	};

	public WheezewortSeeds(){
		super(CRBlocks.wheezewort, new Item.Properties());
		String name = "wheezewort_seeds";
		CRItems.queueForRegister(name, this);
		DispenserBlock.registerBehavior(this, PLANTING_DISPENSER_BEHAVIOR);
	}
}
