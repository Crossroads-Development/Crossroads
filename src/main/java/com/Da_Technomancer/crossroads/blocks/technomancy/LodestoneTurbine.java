package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public class LodestoneTurbine extends BaseEntityBlock{

	public LodestoneTurbine(){
		super(CRBlocks.getMetalProperty());
		String name = "lodestone_turbine";
		CRBlocks.queueForRegister(name, this);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new LodestoneTurbineTileEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, LodestoneTurbineTileEntity.TYPE);
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(Component.translatable("tt.crossroads.lodestone_turbine.desc", CRConfig.lodestoneTurbinePower.get()));
		tooltip.add(Component.translatable("tt.crossroads.lodestone_turbine.limit", LodestoneTurbineTileEntity.MAX_SPEED));
		tooltip.add(Component.translatable("tt.crossroads.boilerplate.inertia", LodestoneTurbineTileEntity.INERTIA));
		tooltip.add(Component.translatable("tt.crossroads.lodestone_turbine.quip").setStyle(MiscUtil.TT_QUIP));
	}
}
