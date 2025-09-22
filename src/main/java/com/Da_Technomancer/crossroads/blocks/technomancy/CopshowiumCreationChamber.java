package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import com.Da_Technomancer.essentials.api.redstone.IReadable;
import com.Da_Technomancer.essentials.api.redstone.RedstoneUtil;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
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
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.List;

public class CopshowiumCreationChamber extends BaseEntityBlock implements IReadable{

	public CopshowiumCreationChamber(){
		super(CRBlocks.getMetalProperty());
		String name = "copshowium_creation_chamber";
		CRBlocks.queueForRegister(name, this);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new CopshowiumCreationChamberTileEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, CopshowiumCreationChamberTileEntity.TYPE);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec(){
		return CRBlocks.COPSHOWIUM_CREATION_CHAMBER_TYPE.value();
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}

	@Override
	public ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		BlockEntity te;
		if(FluxUtil.handleFluxLinking(worldIn, pos, playerIn.getItemInHand(hand), playerIn).indicateItemUse()){
			return ItemInteractionResult.sidedSuccess(worldIn.isClientSide);
		}else if(!worldIn.isClientSide && (te = worldIn.getBlockEntity(pos)) instanceof MenuProvider){
			((ServerPlayer) playerIn).openMenu((MenuProvider) te, pos);
		}
		return ItemInteractionResult.sidedSuccess(worldIn.isClientSide);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag){
		tooltip.add(Component.translatable("tt.crossroads.ccc.desc"));
//		tooltip.add(new TranslationTextComponent("tt.crossroads.ccc.mult", CRConfig.copsPerLiq.get()));
		tooltip.add(Component.translatable("tt.crossroads.ccc.flux", CopshowiumCreationChamberTileEntity.FLUX_PER_INGOT));
		tooltip.add(Component.translatable("tt.crossroads.ccc.io"));
		if(CRConfig.allowOverflow.get()){
			tooltip.add(Component.translatable("tt.crossroads.ccc.limit"));//Describe bursting
		}
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState p_149740_1_){
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos){
		return RedstoneUtil.clampToVanilla(read(world, pos, state));
	}

	@Override
	public float read(Level world, BlockPos pos, BlockState state){
		BlockEntity te = world.getBlockEntity(pos);
		return te instanceof CopshowiumCreationChamberTileEntity ? ((CopshowiumCreationChamberTileEntity) te).getRedstone() : 0;
	}
}
