package com.Da_Technomancer.crossroads.items.witchcraft;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class WheezewortSeeds extends ItemNameBlockItem{

	private static final DispenseItemBehavior PLANTING_DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior(){

		@Override
		public ItemStack execute(BlockSource source, ItemStack stack){
			//Can be planted by dispenser
			BlockState dispenserState = source.getBlockState();
			if(dispenserState.hasProperty(DispenserBlock.FACING)){
				BlockPos plantPos = source.getPos().relative(dispenserState.getValue(DispenserBlock.FACING));
				BlockPos groundPos = plantPos.below();
				Level world = source.getLevel();
				if(world.getBlockState(plantPos).isAir() && CRBlocks.wheezewort.canSustainPlant(world.getBlockState(groundPos), world, groundPos, Direction.UP, CRBlocks.wheezewort)){
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
