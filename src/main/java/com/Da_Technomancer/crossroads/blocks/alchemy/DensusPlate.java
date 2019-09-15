package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CrossroadsItems;
import com.Da_Technomancer.crossroads.tileentities.alchemy.DensusPlateTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

public class DensusPlate extends ContainerBlock{

	private static final AxisAlignedBB BBNORTH = new AxisAlignedBB(0, 0, 0.5D, 1, 1, 1);
	private static final AxisAlignedBB BBSOUTH = new AxisAlignedBB(0, 0, 0, 1, 1, 0.5D);
	private static final AxisAlignedBB BBWEST = new AxisAlignedBB(0.5D, 0, 0, 1, 1, 1);
	private static final AxisAlignedBB BBEAST = new AxisAlignedBB(0, 0, 0, 0.5D, 1, 1);
	private static final AxisAlignedBB BBUP = new AxisAlignedBB(0, 0, 0, 1, 0.5D, 1);
	private static final AxisAlignedBB BBDOWN = new AxisAlignedBB(0, 0.5D, 0, 1, 1, 1);

	public DensusPlate(boolean anti){
		super(Material.ROCK);
		String name = anti ? "anti_densus_plate" : "densus_plate";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CrossroadsItems.TAB_CROSSROADS);
		setHardness(3);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(EssentialsConfig.isWrench(playerIn.getHeldItem(hand), worldIn.isRemote)){
			if(!worldIn.isRemote){
				worldIn.setBlockState(pos, state.cycle(EssentialsProperties.FACING));
			}
			return true;
		}
		return false;
	}

	@Override
	public BlockState getStateForPlacement(World worldIn, BlockPos pos, Direction blockFaceClickedOn, BlockRayTraceResult hit, int meta, LivingEntity placer){
		return getDefaultState().with(EssentialsProperties.FACING, (placer == null) ? Direction.NORTH : Direction.getDirectionFromEntityLiving(pos, placer));
	}

	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos){
		switch(state.get(EssentialsProperties.FACING)){
			case EAST:
				return BBEAST;
			case SOUTH:
				return BBSOUTH;
			case WEST:
				return BBWEST;
			case NORTH:
				return BBNORTH;
			case UP:
				return BBUP;
			default:
				return BBDOWN;
		}
	}

	@Override
	public void addCollisionBoxToList(BlockState state, World worldIn, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity, boolean thingamijiger){
		addCollisionBoxToList(pos, mask, list, getBoundingBox(state, worldIn, pos));
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, EssentialsProperties.FACING);
	}

	@Override
	public BlockState getStateFromMeta(int meta){
		return getDefaultState().with(EssentialsProperties.FACING, Direction.byIndex(meta));
	}

	@Override
	public int getMetaFromState(BlockState state){
		return state.get(EssentialsProperties.FACING).getIndex();
	}

	@Override
	public boolean isOpaqueCube(BlockState state){
		return false;
	}

	@Override
	public boolean isFullCube(BlockState state){
		return false;
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new DensusPlateTileEntity();
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face){
		return face == state.get(EssentialsProperties.FACING).getOpposite() ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
	}
}
