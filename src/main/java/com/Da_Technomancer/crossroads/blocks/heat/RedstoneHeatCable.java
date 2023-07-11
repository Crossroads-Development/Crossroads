package com.Da_Technomancer.crossroads.blocks.heat;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.alchemy.EnumTransferMode;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import com.Da_Technomancer.essentials.api.redstone.IReadable;
import com.Da_Technomancer.essentials.api.redstone.RedstoneUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public class RedstoneHeatCable extends HeatCable implements IReadable{

	public RedstoneHeatCable(HeatInsulators insulator){
		super(insulator, "redstone_heat_cable_" + insulator.toString().toLowerCase());
		registerDefaultState(defaultBlockState().setValue(CRProperties.REDSTONE_BOOL, false));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		if(state.getValue(CRProperties.REDSTONE_BOOL)){
			return super.getShape(state, worldIn, pos, context);
		}else{
			return SHAPES[0];//Core only
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		//Invert when sneak-wrenching
		if(playerIn != null && hand != null && playerIn.isCrouching()){
			BlockEntity te = worldIn.getBlockEntity(pos);
			if(te instanceof RedstoneHeatCableTileEntity cableTE){
				boolean inverted = !cableTE.isInverted();
				cableTE.setInverted(inverted);
				if(inverted){
					MiscUtil.displayMessage(playerIn, Component.translatable("tt.crossroads.redstone_heat_cable.wrench.invert"));
				}else{
					MiscUtil.displayMessage(playerIn, Component.translatable("tt.crossroads.redstone_heat_cable.wrench.uninvert"));
				}
				neighborChanged(state, worldIn, pos, this, pos, false);
			}
			return InteractionResult.SUCCESS;
		}
		return super.use(state, worldIn, pos, playerIn, hand, hit);
	}

	@Override
	protected boolean evaluate(EnumTransferMode value, BlockState state, @Nullable IConduitTE<EnumTransferMode> te){
		return super.evaluate(value, state, te) && state.getValue(CRProperties.REDSTONE_BOOL);
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		neighborChanged(state, world, pos, this, pos, false);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		super.createBlockStateDefinition(builder);
		builder.add(CRProperties.REDSTONE_BOOL);
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
		boolean inverted = false;
		if(worldIn.getBlockEntity(pos) instanceof RedstoneHeatCableTileEntity cableTE){
			inverted = cableTE.isInverted();
		}
		boolean isPowered = worldIn.hasNeighborSignal(pos) != inverted;
		boolean statePowered = state.getValue(CRProperties.REDSTONE_BOOL);
		if(isPowered != statePowered){
			worldIn.setBlockAndUpdate(pos, state.setValue(CRProperties.REDSTONE_BOOL, isPowered));
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new RedstoneHeatCableTileEntity(pos, state, insulator);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, RedstoneHeatCableTileEntity.TYPE);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context){
		return super.getStateForPlacement(context).setValue(CRProperties.REDSTONE_BOOL, context.getLevel().hasNeighborSignal(context.getClickedPos()));
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag advanced){
		super.appendHoverText(stack, world, tooltip, advanced);
		tooltip.add(Component.translatable("tt.crossroads.redstone_heat_cable.control"));
		tooltip.add(Component.translatable("tt.crossroads.redstone_heat_cable.reader"));
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state){
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos){
		return RedstoneUtil.clampToVanilla(read(worldIn, pos, blockState));//A rather pointless output- ranging from 0 to 15 degrees C
	}

	@Override
	public float read(Level world, BlockPos pos, BlockState blockState){
		BlockEntity te = world.getBlockEntity(pos);
		if(te instanceof RedstoneHeatCableTileEntity){
			return ((RedstoneHeatCableTileEntity) te).getTemp();
		}
		return 0;
	}
}
