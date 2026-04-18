package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.ambient.sounds.CRSounds;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.templates.ICustomItemBlock;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.List;

public class MaxwellDemon extends BaseEntityBlock implements ICustomItemBlock{

	public MaxwellDemon(){
		super(CRBlocks.getRockProperty());
		String name = "maxwell_demon";
		CRBlocks.queueForRegister(name, this);
	}

	@Override
	public BlockItem createItemBlock(){
		return new BlockItem(this, CRItems.baseItemProperties().rarity(CRItems.BOBO_RARITY));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new MaxwellDemonTileEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, MaxwellDemonTileEntity.TYPE);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec(){
		return CRBlocks.MAXWELL_DEMON_TYPE.value();
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag){
		double rate = CRConfig.demonPower.get();
		tooltip.add(Component.translatable("tt.crossroads.maxwell_demon.top", MaxwellDemonTileEntity.MAX_TEMP, rate));
		tooltip.add(Component.translatable("tt.crossroads.maxwell_demon.bottom", MaxwellDemonTileEntity.MIN_TEMP, rate));
		tooltip.add(Component.translatable("tt.crossroads.maxwell_demon.feed", MaxwellDemonTileEntity.FAT_CONSUMPTION));
		tooltip.add(Component.translatable("tt.crossroads.maxwell_demon.quip").setStyle(MiscUtil.TT_QUIP));
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState pState, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult pHitResult){
		if(stack.is(CRItems.edibleBlob)){
			if(!level.isClientSide && level.getBlockEntity(pos) instanceof MaxwellDemonTileEntity te){
				ItemStack result = te.feedItem(stack);
				if(!result.equals(stack)){
					CRSounds.playSoundServer(level, pos, SoundEvents.DONKEY_EAT, SoundSource.PLAYERS, 1, 1);
				}
				if(!player.isCreative()){
					player.setItemInHand(hand, result);
				}
			}
			return ItemInteractionResult.sidedSuccess(level.isClientSide);
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}
}
