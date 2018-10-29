package com.Da_Technomancer.crossroads.blocks.magic;

import com.Da_Technomancer.crossroads.API.templates.BeamRenderTE;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.magic.BeamSplitterTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BeamSplitter extends BlockContainer{

	public BeamSplitter(){
		super(Material.ROCK);
		String name = "beam_splitter";
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setHardness(3);
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new BeamSplitterTileEntity();
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack){
		neighborChanged(state, world, pos, this, pos);
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state){
		TileEntity te = worldIn.getTileEntity(pos);
		if(te instanceof BeamRenderTE){
			((BeamRenderTE) te).refresh();
		}
		super.breakBlock(worldIn, pos, state);
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		int i = Math.max(worldIn.getRedstonePower(pos.down(), EnumFacing.DOWN), Math.max(worldIn.getRedstonePower(pos.up(), EnumFacing.UP), Math.max(worldIn.getRedstonePower(pos.east(), EnumFacing.EAST), Math.max(worldIn.getRedstonePower(pos.west(), EnumFacing.WEST), Math.max(worldIn.getRedstonePower(pos.north(), EnumFacing.NORTH), worldIn.getRedstonePower(pos.south(), EnumFacing.SOUTH))))));
		i = Math.min(15, i);
		((BeamSplitterTileEntity) worldIn.getTileEntity(pos)).setRedstone(i);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}
}
