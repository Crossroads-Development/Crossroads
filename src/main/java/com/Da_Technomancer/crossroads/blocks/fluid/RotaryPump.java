package com.Da_Technomancer.crossroads.blocks.fluid;

import java.util.List;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.tileentities.fluid.RotaryPumpTileEntity;

import net.minecraft.block.*;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RotaryPump extends ContainerBlock{
	
	public RotaryPump(){
		super(Material.IRON);
		String name = "rotary_pump";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CRItems.TAB_CROSSROADS);
		setHardness(3);
		setSoundType(SoundType.METAL);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new RotaryPumpTileEntity();
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face){
		return face == Direction.UP ? BlockFaceShape.MIDDLE_POLE_THICK : BlockFaceShape.UNDEFINED;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer(){
		return BlockRenderLayer.CUTOUT;
	}
	
	@Override
	public boolean isFullCube(BlockState state){
		return false;
	}

	@Override
	public boolean isOpaqueCube(BlockState state){
		return false;
	}

	@Override
	public boolean isSideSolid(BlockState state, IBlockAccess worldIn, BlockPos pos, Direction side){
		return side == Direction.UP;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add("I: 80");
		tooltip.add(String.format("Consumes: Up to %1$fJ/t while running", RotaryPumpTileEntity.MAX_POWER));
	}
}
