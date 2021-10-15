package com.Da_Technomancer.crossroads.blocks.heat;

import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.API.heat.HeatInsulators;
import com.Da_Technomancer.crossroads.tileentities.heat.RedstoneHeatCableTileEntity;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.Da_Technomancer.essentials.blocks.redstone.IReadable;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import com.Da_Technomancer.essentials.tileentities.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class RedstoneHeatCable extends HeatCable implements IReadable{

	public RedstoneHeatCable(HeatInsulators insulator){
		super(insulator, "redstone_heat_cable_" + insulator.toString().toLowerCase());
		registerDefaultState(defaultBlockState().setValue(ESProperties.REDSTONE_BOOL, false));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		if(state.getValue(ESProperties.REDSTONE_BOOL)){
			return super.getShape(state, worldIn, pos, context);
		}else{
			return SHAPES[0];//Core only
		}
	}

	@Override
	protected boolean evaluate(EnumTransferMode value, BlockState state, @Nullable IConduitTE<EnumTransferMode> te){
		return super.evaluate(value, state, te) && state.getValue(ESProperties.REDSTONE_BOOL);
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		neighborChanged(state, world, pos, this, pos, false);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		super.createBlockStateDefinition(builder);
		builder.add(ESProperties.REDSTONE_BOOL);
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
		if(worldIn.hasNeighborSignal(pos)){
			if(!state.getValue(ESProperties.REDSTONE_BOOL)){
				worldIn.setBlockAndUpdate(pos, state.setValue(ESProperties.REDSTONE_BOOL, true));
				worldIn.updateNeighbourForOutputSignal(pos, this);
			}
		}else if(state.getValue(ESProperties.REDSTONE_BOOL)){
			worldIn.setBlockAndUpdate(pos, state.setValue(ESProperties.REDSTONE_BOOL, false));
			worldIn.updateNeighbourForOutputSignal(pos, this);
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
		return super.getStateForPlacement(context).setValue(ESProperties.REDSTONE_BOOL, context.getLevel().hasNeighborSignal(context.getClickedPos()));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag advanced){
		super.appendHoverText(stack, world, tooltip, advanced);

		tooltip.add(new TranslatableComponent("tt.crossroads.redstone_heat_cable.reader"));

		/*
		tooltip.add("");

		tooltip.add("Comparators read: ");
		for(int i = 0; i < 5; i++){
			StringBuilder line = new StringBuilder(50);
			for(int j = 3*i + 1; j <= 3*i + 3; j++){
				line.append("  ").append(j).append(" above ").append(Integer.toString((int) Math.round(HeatUtil.toCelcius(j * HeatUtil.toKelvin(insulator.getLimit()) / 16D)))).append("°C ");
			}
			tooltip.add(line.toString());
		}
		*/
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
