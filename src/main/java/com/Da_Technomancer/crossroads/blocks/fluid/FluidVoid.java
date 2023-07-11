package com.Da_Technomancer.crossroads.blocks.fluid;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.templates.ICustomItemBlock;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import javax.annotation.Nullable;
import java.util.List;

public class FluidVoid extends BaseEntityBlock implements ICustomItemBlock{

	public FluidVoid(){
		super(BlockBehaviour.Properties.of(Material.SPONGE).strength(0.6F).sound(SoundType.GRASS));
		String name = "fluid_void";
		CRBlocks.queueForRegister(name, this);
	}

	@Override
	public BlockItem createItemBlock(){
		return new BlockItem(this, CRItems.baseItemProperties().rarity(CRItems.BOBO_RARITY));
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
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag advanced){
		tooltip.add(Component.translatable("tt.crossroads.fluid_void.quip").setStyle(MiscUtil.TT_QUIP));
	}
}
