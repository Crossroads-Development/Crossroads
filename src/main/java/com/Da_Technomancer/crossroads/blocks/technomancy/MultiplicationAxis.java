package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.technomancy.MultiplicationAxisTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class MultiplicationAxis extends BlockContainer{
	
	public MultiplicationAxis(){
		super(Material.IRON);
		String name = "multiplication_axis";
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this).setRegistryName(name));
		setCreativeTab(ModItems.tabCrossroads);
		setHardness(3);
		setSoundType(SoundType.METAL);
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		return getDefaultState().withProperty(Properties.FACING, (placer == null) ? EnumFacing.NORTH : placer.getHorizontalFacing().getOpposite()).withProperty(Properties.REDSTONE_BOOL, false);
	}

	@Override
	public int damageDropped(IBlockState state){
		return 0;
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, new IProperty[] {Properties.FACING, Properties.REDSTONE_BOOL});
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		if(worldIn.isBlockPowered(pos)){
			if(!state.getValue(Properties.REDSTONE_BOOL)){
				worldIn.setBlockState(pos, state.withProperty(Properties.REDSTONE_BOOL, true));
			}
		}else{
			if(state.getValue(Properties.REDSTONE_BOOL)){
				worldIn.setBlockState(pos, state.withProperty(Properties.REDSTONE_BOOL, false));
			}
		}
	}
	
	@Override
	public boolean getWeakChanges(IBlockAccess world, BlockPos pos){
		return true;
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		EnumFacing facing = EnumFacing.getFront(meta & 7);
		return getDefaultState().withProperty(Properties.FACING, facing).withProperty(Properties.REDSTONE_BOOL, (meta & 8) == 8);
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(Properties.FACING).getIndex() + (state.getValue(Properties.REDSTONE_BOOL) ? 8 : 0);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new MultiplicationAxisTileEntity(EnumFacing.getFront(meta));

	}
	
	@Override
	public boolean isFullCube(IBlockState state){
		return false;
	}
	
	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side){
		return side == EnumFacing.UP || side == EnumFacing.DOWN || side.getAxis() == world.getBlockState(pos).getValue(Properties.FACING).getAxis();
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
	
	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos){
		return true;
	}
}
