package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.alchemy.FlowLimiterTileEntity;
import com.Da_Technomancer.essentials.ESConfig;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class FlowLimiter extends ContainerBlock{

	private static final VoxelShape[] SHAPES = new VoxelShape[3];

	static{
		SHAPES[0] = makeCuboidShape(0, 4, 4, 16, 12, 12);
		SHAPES[1] = makeCuboidShape(4, 0, 4, 12, 16, 12);
		SHAPES[2] = makeCuboidShape(4, 4, 0, 12, 12, 16);
	}

	private final boolean crystal;

	public FlowLimiter(boolean crystal){
		super(CRBlocks.GLASS_PROPERTY);
		this.crystal = crystal;
		String name = (crystal ? "crystal_" : "") + "flow_limiter";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new FlowLimiterTileEntity(!crystal);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

//	@Override
//	@OnlyIn(Dist.CLIENT)
//	public BlockRenderLayer getRenderLayer(){
//		return BlockRenderLayer.TRANSLUCENT;
//	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(ESConfig.isWrench(playerIn.getHeldItem(hand))){
			if(!worldIn.isRemote){
				if(playerIn.isSneaking()){
					TileEntity te = worldIn.getTileEntity(pos);
					if(te instanceof FlowLimiterTileEntity){
						((FlowLimiterTileEntity) te).cycleLimit((ServerPlayerEntity) playerIn);
					}
				}else{
					worldIn.setBlockState(pos, state.func_235896_a_(ESProperties.FACING));
					TileEntity te = worldIn.getTileEntity(pos);
					if(te instanceof FlowLimiterTileEntity){
						((FlowLimiterTileEntity) te).wrench();
					}
				}
			}
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
		builder.add(ESProperties.FACING);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		return SHAPES[state.get(ESProperties.FACING).getAxis().ordinal()];
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context){
		return getDefaultState().with(ESProperties.FACING, context.getNearestLookingDirection());
	}
}
