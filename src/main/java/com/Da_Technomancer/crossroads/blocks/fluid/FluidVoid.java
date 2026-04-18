package com.Da_Technomancer.crossroads.blocks.fluid;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

import java.util.List;

public class FluidVoid extends BaseEntityBlock{

	public FluidVoid(){
		super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).sound(SoundType.GRASS).strength(0.6F).sound(SoundType.GRASS));
		String name = "fluid_void";
		CRBlocks.queueForRegister(name, this);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new FluidVoidTileEntity(pos, state);
	}

//	Not a ticking TE
//	@Nullable
//	@Override
//	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
//		return ITickableTileEntity.createTicker(type, FluidVoidTileEntity.TYPE);
//	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec(){
		return CRBlocks.FLUID_VOID_TYPE.value();
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag){
		tooltip.add(Component.translatable("tt.crossroads.fluid_void.desc"));
		tooltip.add(Component.translatable("tt.crossroads.fluid_void.quip").setStyle(MiscUtil.TT_QUIP));
	}
}
