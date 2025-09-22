package com.Da_Technomancer.crossroads.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class RedstoneCrystal extends Block{

	public RedstoneCrystal(){
		super(CRBlocks.getGlassProperty().strength(0.3F));
		String name = "redstone_crystal";
		CRBlocks.queueForRegister(name, this);
	}

	@Override
	protected MapCodec<? extends Block> codec(){
		return CRBlocks.REDSTONE_CRYSTAL_TYPE.value();
	}

	@Override
	public boolean isSignalSource(BlockState state){
		return true;
	}

	@Override
	public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction side){
		return 15;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag){
		tooltip.add(Component.translatable("tt.crossroads.redstone_crystal.drops"));
		tooltip.add(Component.translatable("tt.crossroads.redstone_crystal.power"));
	}
}
