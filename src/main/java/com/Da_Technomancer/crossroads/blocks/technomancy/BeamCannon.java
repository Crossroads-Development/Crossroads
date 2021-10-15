package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.technomancy.BeamCannonTileEntity;
import com.Da_Technomancer.essentials.ESConfig;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.Da_Technomancer.essentials.tileentities.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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

public class BeamCannon extends BaseEntityBlock{

	private static final VoxelShape[] SHAPES = new VoxelShape[6];
	static{
		SHAPES[0] = Shapes.or(box(0, 7, 0, 16, 16, 16));
		SHAPES[1] = Shapes.or(box(0, 0, 0, 16, 9, 16));
		SHAPES[2] = Shapes.or(box(0, 0, 7, 16, 16, 16));
		SHAPES[3] = Shapes.or(box(0, 0, 0, 16, 16, 9));
		SHAPES[4] = Shapes.or(box(7, 0, 0, 16, 16, 16));
		SHAPES[5] = Shapes.or(box(0, 0, 0, 9, 16, 16));
	}

	public BeamCannon(){
		super(CRBlocks.getMetalProperty());
		String name = "beam_cannon";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
		registerDefaultState(defaultBlockState());
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(ESProperties.FACING);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return SHAPES[state.getValue(ESProperties.FACING).get3DDataValue()];
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context){
		return defaultBlockState().setValue(ESProperties.FACING, context.getClickedFace());
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		ItemStack held = playerIn.getItemInHand(hand);
		if(ESConfig.isWrench(held)){
			if(playerIn.isShiftKeyDown()){
				//Sneak clicking- lock/unlock
				BlockEntity te = worldIn.getBlockEntity(pos);
				if(te instanceof BeamCannonTileEntity){
					((BeamCannonTileEntity) te).updateLock(playerIn);
				}
				return InteractionResult.SUCCESS;
			}else{
				//Rotate this machine
				worldIn.setBlockAndUpdate(pos, state.cycle(ESProperties.FACING));
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new BeamCannonTileEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, BeamCannonTileEntity.TYPE);
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(new TranslatableComponent("tt.crossroads.beam_cannon.desc"));
		tooltip.add(new TranslatableComponent("tt.crossroads.beam_cannon.angle"));
		tooltip.add(new TranslatableComponent("tt.crossroads.beam_cannon.lockable"));
		tooltip.add(new TranslatableComponent("tt.crossroads.boilerplate.inertia", BeamCannonTileEntity.INERTIA));
		tooltip.add(new TranslatableComponent("tt.crossroads.beam_cannon.quip").setStyle(MiscUtil.TT_QUIP));
	}
}
