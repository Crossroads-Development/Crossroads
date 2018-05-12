package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.technomancy.ArcSinAxisTileEntity;

import com.Da_Technomancer.essentials.EssentialsConfig;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
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
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ArcSinAxis extends BlockContainer{
	
	public ArcSinAxis(){
		super(Material.IRON);
		String name = "arcsin_axis";
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setHardness(3);
		setSoundType(SoundType.METAL);
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this);
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		return getDefaultState().withProperty(Properties.FACING, (placer == null) ? EnumFacing.NORTH : placer.getHorizontalFacing().getOpposite());
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState blockstate){
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof ArcSinAxisTileEntity){
			((ArcSinAxisTileEntity) te).disconnect();
		}
		super.breakBlock(world, pos, blockstate);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(EssentialsConfig.isWrench(playerIn.getHeldItem(hand), worldIn.isRemote)){
			if(!worldIn.isRemote){
				TileEntity te = worldIn.getTileEntity(pos);
				if(te instanceof ArcSinAxisTileEntity){
					((ArcSinAxisTileEntity) te).disconnect();
				}
				worldIn.setBlockState(pos, state.withProperty(Properties.FACING, state.getValue(Properties.FACING).rotateY()));
			}
			return true;
		}
		return false;
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, new IProperty[] {Properties.FACING});
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		EnumFacing facing = EnumFacing.getFront(meta);
		return this.getDefaultState().withProperty(Properties.FACING, facing);
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(Properties.FACING).getIndex();
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new ArcSinAxisTileEntity(EnumFacing.getFront(meta));

	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot){
		return state.withProperty(Properties.FACING, rot.rotate(state.getValue(Properties.FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn){
		return state.withRotation(mirrorIn.toRotation(state.getValue(Properties.FACING)));
	}

}
