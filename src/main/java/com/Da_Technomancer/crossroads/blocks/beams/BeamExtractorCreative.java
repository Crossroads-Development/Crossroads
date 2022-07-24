package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.api.templates.BeamBlock;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class BeamExtractorCreative extends BeamBlock{

	public BeamExtractorCreative(){
		super("beam_extractor_creative", CRBlocks.getRockProperty());
		CRBlocks.blockAddQue("beam_extractor_creative", this, new Item.Properties().tab(CRItems.TAB_CROSSROADS).rarity(CRItems.CREATIVE_RARITY));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new BeamExtractorCreativeTileEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, BeamExtractorCreativeTileEntity.TYPE);
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		if(!super.use(state, worldIn, pos, playerIn, hand, hit).shouldSwing() && !worldIn.isClientSide){
			if(worldIn.getBlockEntity(pos) instanceof BeamExtractorCreativeTileEntity menuTE){
				NetworkHooks.openScreen((ServerPlayer) playerIn, menuTE, buf -> {
					buf.writeVarIntArray(menuTE.output.getValues());
					buf.writeUtf(menuTE.expression[0]); buf.writeUtf(menuTE.expression[1]);
					buf.writeUtf(menuTE.expression[2]); buf.writeUtf(menuTE.expression[3]);
					buf.writeBlockPos(pos);
				});
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag advanced){
		tooltip.add(Component.translatable("tt.crossroads.boilerplate.creative"));
		tooltip.add(Component.translatable("tt.crossroads.beam_extractor_creative.desc"));
		tooltip.add(Component.translatable("tt.crossroads.beam_extractor_creative.redstone"));
	}
}
