package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.technomancy.LodestoneTurbineTileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;
import java.util.List;

public class LodestoneTurbine extends ContainerBlock{

	public LodestoneTurbine(){
		super(CRBlocks.getMetalProperty());
		String name = "lodestone_turbine";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Nullable
	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn){
		return new LodestoneTurbineTileEntity();
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.lodestone_turbine.desc", CRConfig.lodestoneTurbinePower.get()));
		tooltip.add(new TranslationTextComponent("tt.crossroads.lodestone_turbine.limit", LodestoneTurbineTileEntity.MAX_SPEED));
		tooltip.add(new TranslationTextComponent("tt.crossroads.boilerplate.inertia", LodestoneTurbineTileEntity.INERTIA));
		tooltip.add(new TranslationTextComponent("tt.crossroads.lodestone_turbine.quip").setStyle(MiscUtil.TT_QUIP));
	}
}
