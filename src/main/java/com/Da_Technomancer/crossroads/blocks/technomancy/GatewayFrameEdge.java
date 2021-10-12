package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.technomancy.IGateway;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.technomancy.GatewayEdgeTileEntity;
import com.Da_Technomancer.essentials.blocks.redstone.IReadable;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class GatewayFrameEdge extends BaseEntityBlock implements IReadable{

	public GatewayFrameEdge(){
		super(CRBlocks.getMetalProperty());
		String name = "gateway_edge";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
		registerDefaultState(defaultBlockState().setValue(CRProperties.ACTIVE, false));
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new GatewayEdgeTileEntity(pos, state);
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(CRProperties.ACTIVE);//ACTIVE is whether this is formed into a multiblock
	}

	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving){
		BlockEntity te = world.getBlockEntity(pos);
		if(newState.getBlock() != state.getBlock() && te instanceof GatewayEdgeTileEntity){
			//Shutdown the multiblock
			BlockPos keyPos;
			if((keyPos = ((GatewayEdgeTileEntity) te).getKey()) != null){
				//The rest of the multiblock asks the head to dismantle
				BlockEntity controllerTe = world.getBlockEntity(pos.offset(keyPos));
				if(controllerTe instanceof IGateway){
					((IGateway) controllerTe).dismantle();
				}
			}
		}
		super.onRemove(state, world, pos, newState, isMoving);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag flag){
		tooltip.add(new TranslatableComponent("tt.crossroads.gateway.frame"));
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
		if(!state.getValue(CRProperties.ACTIVE)){
			return 0;
		}
		//Read the number of entries in the dialed address [0-4]
		BlockEntity te = world.getBlockEntity(pos);
		BlockPos keyPos;
		if(te instanceof GatewayEdgeTileEntity && (keyPos = ((GatewayEdgeTileEntity) te).getKey()) != null){
			keyPos = pos.offset(keyPos);
			BlockState controllerState = world.getBlockState(keyPos);
			if(controllerState.getBlock() instanceof IReadable){
				return ((IReadable) controllerState.getBlock()).read(world, keyPos, controllerState);
			}
		}
		return 0;
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state){
		return PushReaction.BLOCK;//Some mods make TileEntities piston moveable. That would be really bad for this block
	}
}
