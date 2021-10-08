package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.technomancy.DetailedAutoCrafterTileEntity;
import com.Da_Technomancer.essentials.blocks.AutoCrafter;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.BlockGetter;

import javax.annotation.Nullable;
import java.util.List;

public class DetailedAutoCrafter extends AutoCrafter{

	public DetailedAutoCrafter(){
		super("detailed_auto_crafter");
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockGetter worldIn){
		return new DetailedAutoCrafterTileEntity();
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag advanced){
		tooltip.add(new TranslatableComponent("tt.crossroads.detailed_auto_crafter.basic"));
		tooltip.add(new TranslatableComponent("tt.crossroads.detailed_auto_crafter.sigil"));
		tooltip.add(new TranslatableComponent("tt.crossroads.detailed_auto_crafter.quip").setStyle(MiscUtil.TT_QUIP));
	}
}
