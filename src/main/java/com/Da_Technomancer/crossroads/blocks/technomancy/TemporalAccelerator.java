package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.api.ConfigUtil;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class TemporalAccelerator extends BaseEntityBlock{

	private static final VoxelShape[] SHAPES = new VoxelShape[6];
	static{
		SHAPES[0] = Shapes.or(box(0, 0, 0, 16, 4, 16), box(4, 4, 4, 12, 8, 12));
		SHAPES[1] = Shapes.or(box(0, 12, 0, 16, 16, 16), box(4, 8, 4, 12, 12, 12));
		SHAPES[2] = Shapes.or(box(0, 0, 0, 16, 16, 4), box(4, 4, 4, 12, 12, 8));
		SHAPES[3] = Shapes.or(box(0, 0, 12, 16, 16, 16), box(4, 4, 8, 12, 12, 12));
		SHAPES[4] = Shapes.or(box(0, 0, 0, 4, 16, 16), box(4, 4, 4, 8, 12, 12));
		SHAPES[5] = Shapes.or(box(12, 0, 0, 16, 16, 16), box(8, 4, 4, 12, 12, 12));
	}

	public TemporalAccelerator(){
		super(CRBlocks.getMetalProperty());
		String name = "temporal_accelerator";
		CRBlocks.queueForRegister(name, this);
		registerDefaultState(defaultBlockState().setValue(CRProperties.ACCELERATOR_TARGET, Mode.BOTH));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(CRProperties.FACING, CRProperties.ACCELERATOR_TARGET);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return SHAPES[state.getValue(CRProperties.FACING).get3DDataValue()];
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context){
		return defaultBlockState().setValue(CRProperties.FACING, context.getNearestLookingDirection().getOpposite());
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		ItemStack held = playerIn.getItemInHand(hand);
		//Linking with a linking tool
		if(FluxUtil.handleFluxLinking(worldIn, pos, held, playerIn).shouldSwing()){
			return InteractionResult.SUCCESS;
		}else if(ConfigUtil.isWrench(held)){
			if(playerIn.isShiftKeyDown()){
				//Sneak clicking- change mode
				state = state.cycle(CRProperties.ACCELERATOR_TARGET);
				worldIn.setBlockAndUpdate(pos, state);
				if(worldIn.isClientSide){
					Mode newMode = state.getValue(CRProperties.ACCELERATOR_TARGET);
					MiscUtil.displayMessage(playerIn, Component.translatable("tt.crossroads.time_accel.new_mode", MiscUtil.localize(newMode.getLocalizationName())));
					if(!CRConfig.teTimeAccel.get() && newMode.accelerateTileEntities){
						MiscUtil.displayMessage(playerIn, Component.translatable("tt.crossroads.time_accel.config").setStyle(Style.EMPTY.applyFormat(ChatFormatting.RED)));
					}
				}
			}else{
				//Rotate this machine
				worldIn.setBlockAndUpdate(pos, state.cycle(CRProperties.FACING));
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new TemporalAcceleratorTileEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, TemporalAcceleratorTileEntity.TYPE);
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(Component.translatable("tt.crossroads.time_accel.desc", TemporalAcceleratorTileEntity.SIZE));
		tooltip.add(Component.translatable("tt.crossroads.time_accel.beam"));
		tooltip.add(Component.translatable("tt.crossroads.time_accel.wrench"));
		tooltip.add(Component.translatable("tt.crossroads.time_accel.flux"));
	}

	public enum Mode implements StringRepresentable{

		ENTITIES(true, false, false),
		BLOCKS(false, true, true),
		BOTH(true, true, true);

		public final boolean accelerateEntities;
		public final boolean accelerateTileEntities;
		public final boolean accelerateBlockTicks;

		Mode(boolean entity, boolean te, boolean blockTicks){
			accelerateEntities = entity;
			accelerateTileEntities = te;
			accelerateBlockTicks = blockTicks;
		}

		@Override
		public String toString(){
			return name().toLowerCase(Locale.US);
		}

		@Override
		public String getSerializedName(){
			return toString();
		}

		public String getLocalizationName(){
			return "tt.crossroads.time_accel.mode." + toString();
		}
	}
}
