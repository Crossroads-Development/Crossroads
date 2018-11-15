package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.API.redstone.RedstoneUtil;
import com.Da_Technomancer.crossroads.API.templates.BeamBlock;
import com.Da_Technomancer.crossroads.tileentities.beams.BeamSiphonTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BeamSiphon extends BeamBlock{

	public BeamSiphon(){
		super("beam_siphon");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new BeamSiphonTileEntity();
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack){
		neighborChanged(state, world, pos, this, pos);
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		TileEntity te = worldIn.getTileEntity(pos);
		if(te instanceof BeamSiphonTileEntity){
			((BeamSiphonTileEntity) te).setRedstone((int) Math.round(RedstoneUtil.getPowerAtPos(worldIn, pos)));
		}
	}
}
