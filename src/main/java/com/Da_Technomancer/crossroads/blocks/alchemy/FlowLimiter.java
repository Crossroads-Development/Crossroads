package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.alchemy.FlowLimiterTileEntity;
import com.Da_Technomancer.essentials.ESConfig;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.Da_Technomancer.essentials.tileentities.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class FlowLimiter extends BaseEntityBlock{

	private static final VoxelShape[] SHAPES = new VoxelShape[3];

	static{
		SHAPES[0] = box(0, 4, 4, 16, 12, 12);
		SHAPES[1] = box(4, 0, 4, 12, 16, 12);
		SHAPES[2] = box(4, 4, 0, 12, 12, 16);
	}

	private final boolean crystal;

	public FlowLimiter(boolean crystal){
		super(CRBlocks.getGlassProperty());
		this.crystal = crystal;
		String name = (crystal ? "crystal_" : "") + "flow_limiter";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new FlowLimiterTileEntity(pos, state, !crystal);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, FlowLimiterTileEntity.TYPE);
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}

//	@Override
//	@OnlyIn(Dist.CLIENT)
//	public BlockRenderLayer getRenderLayer(){
//		return BlockRenderLayer.TRANSLUCENT;
//	}
	
	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		if(ESConfig.isWrench(playerIn.getItemInHand(hand))){
			if(!worldIn.isClientSide){
				if(playerIn.isShiftKeyDown()){
					BlockEntity te = worldIn.getBlockEntity(pos);
					if(te instanceof FlowLimiterTileEntity){
						((FlowLimiterTileEntity) te).cycleLimit((ServerPlayer) playerIn);
					}
				}else{
					worldIn.setBlockAndUpdate(pos, state.cycle(ESProperties.FACING));
					BlockEntity te = worldIn.getBlockEntity(pos);
					if(te instanceof FlowLimiterTileEntity){
						((FlowLimiterTileEntity) te).wrench();
					}
				}
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(ESProperties.FACING);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return SHAPES[state.getValue(ESProperties.FACING).getAxis().ordinal()];
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context){
		return defaultBlockState().setValue(ESProperties.FACING, context.getNearestLookingDirection());
	}
}
