package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.tileentities.alchemy.FluidInjectorTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FluidInjector extends ContainerBlock{

	private static final AxisAlignedBB BB = new AxisAlignedBB(.25D, 0, .25D, .75D, 1, .75D);

	private final boolean crystal;

	public FluidInjector(boolean crystal){
		super(Material.GLASS);
		this.crystal = crystal;
		String name = (crystal ? "crystal_" : "") + "fluid_injector";
		setTranslationKey(name);
		setRegistryName(name);
		setHardness(.5F);
		setCreativeTab(CRItems.TAB_CROSSROADS);
		setSoundType(SoundType.GLASS);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new FluidInjectorTileEntity(!crystal);
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
	public boolean isOpaqueCube(BlockState state){
		return false;
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos){
		return BB;
	}
	
	@Override
	public boolean isFullCube(BlockState state){
		return false;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face){
		return BlockFaceShape.UNDEFINED;
	}
}
