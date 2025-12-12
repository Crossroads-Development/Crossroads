package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class Cavorite extends Block{

	public Cavorite(){
		super(CRBlocks.getRockProperty());
		String name = "block_cavorite";
		CRBlocks.queueForRegister(name, this);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag){
		tooltip.add(Component.translatable("tt.crossroads.cavorite"));
		tooltip.add(Component.translatable("tt.crossroads.decoration"));
	}

	@Override
	protected MapCodec<? extends Block> codec(){
		return CRBlocks.CAVORITE_TYPE.value();
	}
}
