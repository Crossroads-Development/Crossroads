package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.api.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.api.technomancy.IGateway;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.api.ConfigUtil;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import com.Da_Technomancer.essentials.api.redstone.IReadable;
import com.Da_Technomancer.essentials.api.redstone.RedstoneUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.List;

public class GatewayController extends BaseEntityBlock implements IReadable{

	public GatewayController(){
		super(CRBlocks.getMetalProperty());
		String name = "gateway_frame";//This registry name is bad, but kept for backwards compatibility
		CRBlocks.queueForRegister(name, this);
		registerDefaultState(defaultBlockState().setValue(CRProperties.ACTIVE, false));
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new GatewayControllerTileEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, GatewayControllerTileEntity.TYPE);
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		//If this is formed into a multiblock, we let the TESR on the top handle all rendering
		return state.getValue(CRProperties.ACTIVE) ? RenderShape.ENTITYBLOCK_ANIMATED : RenderShape.MODEL;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(CRProperties.ACTIVE);//ACTIVE is whether this is formed into a multiblock
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult ray){
		ItemStack held = player.getItemInHand(hand);
		if(state.getValue(CRProperties.ACTIVE)){
			//Handle linking if this is the top block
			return FluxUtil.handleFluxLinking(world, pos, held, player);
		}else if(ConfigUtil.isWrench(held)){
			//Attempt to form the multiblock
			BlockEntity te = world.getBlockEntity(pos);
			if(te instanceof GatewayControllerTileEntity){
				((GatewayControllerTileEntity) te).assemble(player);
				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.PASS;
	}

	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving){
		BlockEntity te = world.getBlockEntity(pos);
		if(newState.getBlock() != state.getBlock() && te instanceof IGateway){
			((IGateway) te).dismantle();//Shutdown the multiblock
		}
		super.onRemove(state, world, pos, newState, isMoving);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag flag){
		tooltip.add(Component.translatable("tt.crossroads.gateway.desc"));
		tooltip.add(Component.translatable("tt.crossroads.gateway.dial"));
		tooltip.add(Component.translatable("tt.crossroads.gateway.proc"));
		tooltip.add(Component.translatable("tt.crossroads.gateway.flux", GatewayControllerTileEntity.FLUX_PER_CYCLE));
		tooltip.add(Component.translatable("tt.crossroads.boilerplate.inertia", GatewayControllerTileEntity.INERTIA));
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state){
		return state.getValue(CRProperties.ACTIVE);
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos){
		return RedstoneUtil.clampToVanilla(read(world, pos, state));
	}

	@Override
	public float read(Level world, BlockPos pos, BlockState state){
		//Read the number of entries in the dialed address [0-4]
		BlockEntity te = world.getBlockEntity(pos);
		if(te instanceof GatewayControllerTileEntity){
			EnumBeamAlignments[] chev = ((GatewayControllerTileEntity) te).chevrons;
			for(int i = 0; i < chev.length; i++){
				if(chev[i] == null){
					return i;
				}
			}
			return chev.length;
		}
		return 0;
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state){
		return PushReaction.BLOCK;//Some mods make TileEntities piston moveable. That would be really bad for this block
	}
}
