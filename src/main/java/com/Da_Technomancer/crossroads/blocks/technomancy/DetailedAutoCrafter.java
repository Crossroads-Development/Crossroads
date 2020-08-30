package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.technomancy.DetailedAutoCrafterTileEntity;
import com.Da_Technomancer.essentials.blocks.AutoCrafter;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;

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
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new DetailedAutoCrafterTileEntity();
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add(new TranslationTextComponent("tt.crossroads.detailed_auto_crafter.basic"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.detailed_auto_crafter.sigil"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.detailed_auto_crafter.quip").func_240703_c_(MiscUtil.TT_QUIP));
	}
}
