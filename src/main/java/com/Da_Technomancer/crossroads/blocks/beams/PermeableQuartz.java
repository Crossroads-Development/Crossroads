package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.List;

public class PermeableQuartz extends Block{

	public PermeableQuartz(){
		super(CRBlocks.getRockProperty());
		String name = "permeable_quartz";
		CRBlocks.queueForRegister(name, this);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter player, List<Component> tooltip, TooltipFlag advanced){
		tooltip.add(Component.translatable("tt.crossroads.boilerplate.beam_permeable"));
		tooltip.add(Component.translatable("tt.crossroads.boilerplate.decor"));
	}
}
