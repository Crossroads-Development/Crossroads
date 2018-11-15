package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.API.templates.BeamBlock;
import com.Da_Technomancer.crossroads.tileentities.beams.BeaconHarnessTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class BeaconHarness extends BeamBlock{

	public BeaconHarness(){
		super("beacon_harness", Material.GLASS);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new BeaconHarnessTileEntity();
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		if(worldIn.isBlockPowered(pos) && worldIn.getTileEntity(pos) instanceof BeaconHarnessTileEntity){
			((BeaconHarnessTileEntity) worldIn.getTileEntity(pos)).trigger();
		}
	}
	@Override
	public int getLightOpacity(IBlockState state){
		return 15;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		tooltip.add("It's balanced because it requires nether stars.");
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		return getDefaultState();
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this);
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState();
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return 0;
	}
}
