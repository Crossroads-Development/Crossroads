package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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

import javax.annotation.Nullable;
import java.util.List;

public class StirlingEngine extends BaseEntityBlock{

	public StirlingEngine(){
		super(CRBlocks.getMetalProperty());
		String name = "stirling_engine";
		CRBlocks.queueForRegister(name, this);
		registerDefaultState(defaultBlockState().setValue(CRProperties.POWER_LEVEL_5, 2));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new StirlingEngineTileEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, StirlingEngineTileEntity.TYPE);
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(CRProperties.POWER_LEVEL_5);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag advanced){
		tooltip.add(Component.translatable("tt.crossroads.stirling_engine.desc", CRConfig.jouleWorth.get() * StirlingEngineTileEntity.MAX_TEMPERATURE_DIFFERANCE / CRConfig.stirlingConversion.get() / StirlingEngineTileEntity.EFFICIENCY_MULTIPLIER));
		tooltip.add(Component.translatable("tt.crossroads.stirling_engine.rate", StirlingEngineTileEntity.HEAT_INTERVAL));
		tooltip.add(Component.translatable("tt.crossroads.stirling_engine.power", CRConfig.formatVal(CRConfig.stirlingConversion.get())));
		tooltip.add(Component.translatable("tt.crossroads.stirling_engine.limit", CRConfig.stirlingSpeedLimit.get()));
		tooltip.add(Component.translatable("tt.crossroads.boilerplate.inertia", StirlingEngineTileEntity.INERTIA));
	}
}
