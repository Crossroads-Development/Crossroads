package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.blocks.AutoCrafter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public class DetailedAutoCrafter extends AutoCrafter{

	public DetailedAutoCrafter(){
		super("detailed_auto_crafter");
		String name = "detailed_auto_crafter";
		CRBlocks.toRegister.put(name, this);
		CRBlocks.blockAddQue(name, this);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new DetailedAutoCrafterTileEntity(pos, state);
	}

//	Not a ticking tile entity
//	@Nullable
//	@Override
//	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
//		return ITickableTileEntity.createTicker(type, DetailedAutoCrafterTileEntity.TYPE);
//	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag advanced){
		tooltip.add(Component.translatable("tt.crossroads.detailed_auto_crafter.basic"));
		tooltip.add(Component.translatable("tt.crossroads.detailed_auto_crafter.sigil"));
		tooltip.add(Component.translatable("tt.crossroads.detailed_auto_crafter.quip").setStyle(MiscUtil.TT_QUIP));
	}
}
