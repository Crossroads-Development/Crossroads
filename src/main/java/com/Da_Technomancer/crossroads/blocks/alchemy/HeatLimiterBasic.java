package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.alchemy.HeatLimiterBasicTileEntity;
import com.Da_Technomancer.essentials.ESConfig;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class HeatLimiterBasic extends BaseEntityBlock{

	public HeatLimiterBasic(){
		super(CRBlocks.getRockProperty());
		String name = "heat_limiter_basic";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
		registerDefaultState(defaultBlockState().setValue(CRProperties.ACTIVE, false));
	}

	@Override
	public BlockEntity newBlockEntity(BlockGetter worldIn){
		return new HeatLimiterBasicTileEntity();
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(CRProperties.ACTIVE, ESProperties.FACING);
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		if(!worldIn.isClientSide){
			BlockEntity te;
			if(ESConfig.isWrench(playerIn.getItemInHand(hand))){
				if(playerIn.isShiftKeyDown()){
					worldIn.setBlockAndUpdate(pos, state.cycle(CRProperties.ACTIVE));
				}else{
					worldIn.setBlockAndUpdate(pos, state.cycle(ESProperties.FACING));
				}
			}else if((te = worldIn.getBlockEntity(pos)) instanceof HeatLimiterBasicTileEntity){
				NetworkHooks.openGui((ServerPlayer) playerIn, (MenuProvider) te, buf -> {buf.writeFloat(((HeatLimiterBasicTileEntity) te).setting); buf.writeUtf(((HeatLimiterBasicTileEntity) te).expression); buf.writeBlockPos(pos);});
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context){
		return defaultBlockState().setValue(ESProperties.FACING, context.getNearestLookingDirection());
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(new TranslatableComponent("tt.crossroads.heat_limiter.desc_cable"));
		tooltip.add(new TranslatableComponent("tt.crossroads.heat_limiter.desc_purpose"));
		tooltip.add(new TranslatableComponent("tt.crossroads.heat_limiter.desc_mode"));
		tooltip.add(new TranslatableComponent("tt.crossroads.heat_limiter.desc_ui"));
	}
}
