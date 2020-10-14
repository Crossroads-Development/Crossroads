package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.rotary.StirlingEngineTileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class StirlingEngine extends ContainerBlock{

	public StirlingEngine(){
		super(CRBlocks.METAL_PROPERTY);
		String name = "stirling_engine";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new StirlingEngineTileEntity();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add(new TranslationTextComponent("tt.crossroads.stirling_engine.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.stirling_engine.rate", StirlingEngineTileEntity.RATE));
		tooltip.add(new TranslationTextComponent("tt.crossroads.stirling_engine.power", CRConfig.formatVal(CRConfig.stirlingMultiplier.get())));
		tooltip.add(new TranslationTextComponent("tt.crossroads.stirling_engine.limit", CRConfig.stirlingSpeedLimit.get()));
		tooltip.add(new TranslationTextComponent("tt.crossroads.boilerplate.inertia", StirlingEngineTileEntity.INERTIA));
	}
}
