package com.Da_Technomancer.crossroads.blocks.fluid;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.tileentities.fluid.FluidVoidTileEntity;
import net.minecraft.block.*;
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

public class FluidVoid extends ContainerBlock{

	public FluidVoid(){
		super(Block.Properties.create(Material.IRON).hardnessAndResistance(0.6F).sound(SoundType.METAL));
		String name = "fluid_void";
		setRegistryName(name);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new FluidVoidTileEntity();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add(new TranslationTextComponent("tt.crossroads.fluid_void.quip").setStyle(MiscUtil.TT_QUIP));
	}
}
