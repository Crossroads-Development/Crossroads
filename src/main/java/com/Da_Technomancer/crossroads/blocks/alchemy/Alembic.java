package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.alchemy.AlembicTileEntity;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class Alembic extends BlockContainer{

	private static final AxisAlignedBB[] BB = {new AxisAlignedBB(0.125D, 0, 0.25D, 0.875D, 1, 1), new AxisAlignedBB(0, 0, 0.125D, 0.75D, 1, 0.875D), new AxisAlignedBB(0.125D, 0, 0, 0.875D, 1, 0.75D), new AxisAlignedBB(0.25D, 0, 0.125D, 1, 1, 0.875D)};

	public Alembic(){
		super(Material.IRON);
		String name = "alembic";
		setUnlocalizedName(name);
		setRegistryName(name);
		setHardness(.5F);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setSoundType(SoundType.METAL);
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new AlembicTileEntity();
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		return getDefaultState().withProperty(Properties.HORIZONTAL_FACING, (placer == null) ? EnumFacing.NORTH : placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(ModConfig.isWrench(playerIn.getHeldItem(hand), worldIn.isRemote)){
			if(!worldIn.isRemote){
				worldIn.setBlockState(pos, state.withProperty(Properties.HORIZONTAL_FACING, state.getValue(Properties.HORIZONTAL_FACING).rotateY()));
			}
			return true;
		}

		if(!worldIn.isRemote){
			TileEntity te = worldIn.getTileEntity(pos);
			if(te instanceof AlembicTileEntity){
				playerIn.setHeldItem(hand, ((AlembicTileEntity) te).rightClickWithItem(playerIn.getHeldItem(hand), playerIn.isSneaking()));
			}
		}
		return true;
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> list, @Nullable Entity entityIn, boolean p_185477_7_){
		addCollisionBoxToList(pos, entityBox, list, BB[state.getValue(Properties.HORIZONTAL_FACING).getHorizontalIndex()]);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
		return BB[state.getValue(Properties.HORIZONTAL_FACING).getHorizontalIndex()];
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(Properties.HORIZONTAL_FACING).getHorizontalIndex();
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState().withProperty(Properties.HORIZONTAL_FACING, EnumFacing.getHorizontal(meta));
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, Properties.HORIZONTAL_FACING);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state){
		return false;
	}
}
