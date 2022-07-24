package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.api.ConfigUtil;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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

public class RotaryDrill extends BaseEntityBlock{

	private static final VoxelShape[] SHAPES = new VoxelShape[6];

	static{
		SHAPES[0] = Shapes.or(box(3, 10, 3, 13, 16, 13), box(5, 4, 5, 11, 10, 11), box(7, 0, 7, 9, 4, 9));
		SHAPES[1] = Shapes.or(box(3, 0, 3, 13, 6, 13), box(5, 6, 5, 11, 12, 11), box(7, 12, 7, 9, 16, 9));
		SHAPES[2] = Shapes.or(box(3, 3, 10, 13, 13, 16), box(5, 5, 4, 11, 11, 10), box(7, 7, 0, 9, 9, 4));
		SHAPES[3] = Shapes.or(box(3, 3, 0, 13, 13, 6), box(5, 5, 6, 11, 11, 12), box(7, 7, 12, 9, 9, 16));
		SHAPES[4] = Shapes.or(box(10, 3, 3, 16, 13, 13), box(4, 5, 5, 10, 11, 11), box(0, 7, 7, 4, 9, 9));
		SHAPES[5] = Shapes.or(box(0, 3, 3, 6, 13, 13), box(6, 5, 5, 12, 11, 11), box(12, 7, 7, 16, 9, 9));
	}

	private final boolean golden;

	public RotaryDrill(boolean golden){
		super(CRBlocks.getMetalProperty());
		this.golden = golden;
		String name = "rotary_drill" + (golden ? "_gold" : "");
		CRBlocks.toRegister.put(name, this);
		CRBlocks.blockAddQue(name, this);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new RotaryDrillTileEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, RotaryDrillTileEntity.TYPE);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context){
		return defaultBlockState().setValue(CRProperties.FACING, context.getNearestLookingDirection());
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return SHAPES[state.getValue(CRProperties.FACING).get3DDataValue()];
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		if(ConfigUtil.isWrench(playerIn.getItemInHand(hand))){
			if(!worldIn.isClientSide){
				worldIn.setBlockAndUpdate(pos, state.cycle(CRProperties.FACING));
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos){
		return true;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(CRProperties.FACING);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag advanced){
		if(golden){
			tooltip.add(Component.translatable("tt.crossroads.drill.desc.gold"));
		}else{
			tooltip.add(Component.translatable("tt.crossroads.drill.desc"));
		}
		tooltip.add(Component.translatable("tt.crossroads.drill.power", golden ? RotaryDrillTileEntity.ENERGY_USE_GOLD : RotaryDrillTileEntity.ENERGY_USE_IRON));
		tooltip.add(Component.translatable("tt.crossroads.boilerplate.inertia", RotaryDrillTileEntity.INERTIA[golden ? 1 : 0]));
		tooltip.add(Component.translatable("tt.crossroads.drill.quip").setStyle(MiscUtil.TT_QUIP));
	}
}
