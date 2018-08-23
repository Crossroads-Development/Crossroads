package com.Da_Technomancer.crossroads.blocks.magic;

import com.Da_Technomancer.crossroads.API.magic.BeamRenderTE;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.magic.CrystallinePrismTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CrystallinePrism extends BlockContainer{

	public CrystallinePrism(){
		super(Material.ROCK);
		String name = "crystalline_prism";
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setHardness(3);
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new CrystallinePrismTileEntity();
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(EssentialsConfig.isWrench(playerIn.getHeldItem(hand), worldIn.isRemote)){
			if(!worldIn.isRemote){
				TileEntity te = worldIn.getTileEntity(pos);
				worldIn.setBlockState(pos, state.withProperty(EssentialsProperties.FACING, state.getValue(EssentialsProperties.FACING).rotateY()));
				if(te instanceof BeamRenderTE){
					((BeamRenderTE) te).resetBeamer();
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		return getDefaultState().withProperty(EssentialsProperties.FACING, (placer == null) ? EnumFacing.NORTH : placer.getHorizontalFacing().getOpposite());
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
	public int damageDropped(IBlockState state){
		return 0;
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, new IProperty[] {EssentialsProperties.FACING});
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		EnumFacing facing = EnumFacing.getFront(meta);
		return this.getDefaultState().withProperty(EssentialsProperties.FACING, facing);
	}

	@Override
	public int getMetaFromState(IBlockState state){
		EnumFacing facing = state.getValue(EssentialsProperties.FACING);
		int facingbits = facing.getIndex();
		return facingbits;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}
}
