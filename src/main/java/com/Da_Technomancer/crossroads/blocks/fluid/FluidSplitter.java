package com.Da_Technomancer.crossroads.blocks.fluid;

import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.tileentities.fluid.FluidSplitterTileEntity;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FluidSplitter extends ContainerBlock{

	public FluidSplitter(){
		super(Material.IRON);
		String name = "fluid_splitter";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CRItems.TAB_CROSSROADS);
		setHardness(3);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new FluidSplitterTileEntity();
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		neighborChanged(null, world, pos, null, null);
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		int i = Math.max(worldIn.getRedstonePower(pos.down(), Direction.DOWN), Math.max(worldIn.getRedstonePower(pos.up(), Direction.UP), Math.max(worldIn.getRedstonePower(pos.east(), Direction.EAST), Math.max(worldIn.getRedstonePower(pos.west(), Direction.WEST), Math.max(worldIn.getRedstonePower(pos.north(), Direction.NORTH), worldIn.getRedstonePower(pos.south(), Direction.SOUTH))))));
		i = Math.min(i, 15);
		TileEntity te = worldIn.getTileEntity(pos);
		if(te instanceof FluidSplitterTileEntity && ((FluidSplitterTileEntity) te).redstone != i){
			((FluidSplitterTileEntity) te).redstone = i;
		}
	}
}
