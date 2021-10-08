package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.rotary.StirlingEngineTileEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class StirlingEngine extends BaseEntityBlock{

	public StirlingEngine(){
		super(CRBlocks.getMetalProperty());
		String name = "stirling_engine";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
		registerDefaultState(defaultBlockState().setValue(CRProperties.RATE_SIGNED, 2));
	}

	@Override
	public BlockEntity newBlockEntity(BlockGetter worldIn){
		return new StirlingEngineTileEntity();
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(CRProperties.RATE_SIGNED);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag advanced){
		tooltip.add(new TranslatableComponent("tt.crossroads.stirling_engine.desc", CRConfig.jouleWorth.get() * StirlingEngineTileEntity.MAX_TEMPERATURE_DIFFERANCE / CRConfig.stirlingConversion.get() / StirlingEngineTileEntity.EFFICIENCY_MULTIPLIER));
		tooltip.add(new TranslatableComponent("tt.crossroads.stirling_engine.rate", StirlingEngineTileEntity.HEAT_INTERVAL));
		tooltip.add(new TranslatableComponent("tt.crossroads.stirling_engine.power", CRConfig.formatVal(CRConfig.stirlingConversion.get())));
		tooltip.add(new TranslatableComponent("tt.crossroads.stirling_engine.limit", CRConfig.stirlingSpeedLimit.get()));
		tooltip.add(new TranslatableComponent("tt.crossroads.boilerplate.inertia", StirlingEngineTileEntity.INERTIA));
	}
}
