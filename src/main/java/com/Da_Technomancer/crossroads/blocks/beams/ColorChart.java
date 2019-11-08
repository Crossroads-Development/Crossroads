package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

public class ColorChart extends Block{
	
	private static final AxisAlignedBB BBNORTH = new AxisAlignedBB(0, 0, 0.9375D, 1, 1, 1);
	private static final AxisAlignedBB BBSOUTH = new AxisAlignedBB(0, 0, 0, 1, 1, 0.0625D);
	private static final AxisAlignedBB BBWEST = new AxisAlignedBB(0.9375D, 0, 0, 1, 1, 1);
	private static final AxisAlignedBB BBEAST = new AxisAlignedBB(0, 0, 0, 0.0625D, 1, 1);
	
	public ColorChart(){
		super(Material.WOOD);
		String name = "color_chart";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CRItems.TAB_CROSSROADS);
		setHardness(3);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
	}
	
	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(!worldIn.isRemote){
			playerIn.openGui(Crossroads.instance, GuiHandler.COLOR_CHART_GUI, worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}
	
	@Override
	public BlockState getStateForPlacement(World worldIn, BlockPos pos, Direction blockFaceClickedOn, BlockRayTraceResult hit, int meta, LivingEntity placer){
		return getDefaultState().with(EssentialsProperties.FACING, (placer == null) ? Direction.NORTH : placer.getHorizontalFacing().getOpposite());
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
			default:
				return BBNORTH;
		}
	}
	
	@Override
	public void addCollisionBoxToList(BlockState state, World worldIn, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity, boolean thingamijiger){
		addCollisionBoxToList(pos, mask, list, getBoundingBox(state, worldIn, pos));
	}
	
	@Override
	public int damageDropped(BlockState state){
		return 0;
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, EssentialsProperties.FACING);
	}

	@Override
	public BlockState getStateFromMeta(int meta){
		return getDefaultState().with(EssentialsProperties.FACING, Direction.byIndex(meta == 0 || meta == 1 ? 2 : meta));
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
}
