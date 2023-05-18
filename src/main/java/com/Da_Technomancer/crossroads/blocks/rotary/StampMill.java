package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.CircuitUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.api.ConfigUtil;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import com.Da_Technomancer.essentials.api.redstone.IReadable;
import com.Da_Technomancer.essentials.api.redstone.RedstoneUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class StampMill extends BaseEntityBlock implements IReadable{

	private static final VoxelShape[] SHAPES = new VoxelShape[2];

	static{
		VoxelShape base = box(0, 0, 0, 16, 4, 16);
		SHAPES[0] = Shapes.or(base, box(0, 4, 0, 1, 16, 16), box(15, 4, 0, 16, 16, 16));
		SHAPES[1] = Shapes.or(base, box(0, 4, 0, 16, 16, 1), box(0, 4, 15, 16, 16, 16));
	}

	public StampMill(){
		super(Properties.of(Material.WOOD).strength(1).sound(SoundType.METAL).noOcclusion());
		String name = "stamp_mill";
		CRBlocks.toRegister.put(name, this);
		CRBlocks.blockAddQue(name, this);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return SHAPES[state.getValue(CRProperties.HORIZ_AXIS) == Direction.Axis.X ? 0 : 1];
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		BlockEntity te;
		if(ConfigUtil.isWrench(playerIn.getItemInHand(hand))){
			if(!worldIn.isClientSide){
				worldIn.setBlockAndUpdate(pos, state.cycle(CRProperties.HORIZ_AXIS));
				BlockState upState = worldIn.getBlockState(pos.above());
				if(upState.getBlock() instanceof StampMillTop){
					worldIn.setBlockAndUpdate(pos.above(), upState.cycle(CRProperties.HORIZ_AXIS));
				}
			}
		}else if(!worldIn.isClientSide && (te = worldIn.getBlockEntity(pos)) instanceof MenuProvider){
			NetworkHooks.openScreen((ServerPlayer) playerIn, (MenuProvider) te, pos);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new StampMillTileEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, StampMillTileEntity.TYPE);
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}

	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving){
		Containers.dropContents(world, pos, (StampMillTileEntity) world.getBlockEntity(pos));
		super.onRemove(state, world, pos, newState, isMoving);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context){
		return defaultBlockState().setValue(CRProperties.HORIZ_AXIS, context.getHorizontalDirection().getClockWise().getAxis());
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(CRProperties.HORIZ_AXIS);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag advanced){
		tooltip.add(Component.translatable("tt.crossroads.stamp_mill.desc", StampMillTileEntity.REQUIRED / StampMillTileEntity.TIME_LIMIT / StampMillTileEntity.PROGRESS_PER_RADIAN * 20));
		tooltip.add(Component.translatable("tt.crossroads.stamp_mill.power", StampMillTileEntity.PROGRESS_PER_RADIAN));
		tooltip.add(Component.translatable("tt.crossroads.boilerplate.inertia", StampMillTileEntity.INERTIA));
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos){
		return super.canSurvive(state, worldIn, pos) && worldIn.getBlockState(pos.above()).isAir();
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
		if(!worldIn.isClientSide && !(worldIn.getBlockState(pos.relative(Direction.UP)).getBlock() instanceof StampMillTop)){
			worldIn.destroyBlock(pos, true);
		}
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		world.setBlockAndUpdate(pos.relative(Direction.UP), CRBlocks.stampMillTop.defaultBlockState().setValue(CRProperties.HORIZ_AXIS, state.getValue(CRProperties.HORIZ_AXIS)));
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state){
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level worldIn, BlockPos pos){
		return RedstoneUtil.clampToVanilla(read(worldIn, pos, state));
	}

	@Override
	public float read(Level world, BlockPos pos, BlockState state){
		BlockEntity te = world.getBlockEntity(pos);
		if(te instanceof Container){
			return CircuitUtil.getRedstoneFromSlots((Container) te, 0);
		}else{
			return 0;
		}
	}
}
