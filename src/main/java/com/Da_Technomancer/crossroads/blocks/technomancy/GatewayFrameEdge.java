package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.technomancy.IGateway;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.technomancy.GatewayEdgeTileEntity;
import com.Da_Technomancer.essentials.blocks.redstone.IReadable;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class GatewayFrameEdge extends ContainerBlock implements IReadable{

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
	public TileEntity newBlockEntity(IBlockReader worldIn){
		return new GatewayEdgeTileEntity();
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder){
		builder.add(CRProperties.ACTIVE);//ACTIVE is whether this is formed into a multiblock
	}

	@Override
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving){
		TileEntity te = world.getBlockEntity(pos);
		if(newState.getBlock() != state.getBlock() && te instanceof GatewayEdgeTileEntity){
			//Shutdown the multiblock
			BlockPos keyPos;
			if((keyPos = ((GatewayEdgeTileEntity) te).getKey()) != null){
				//The rest of the multiblock asks the head to dismantle
				TileEntity controllerTe = world.getBlockEntity(pos.offset(keyPos));
				if(controllerTe instanceof IGateway){
					((IGateway) controllerTe).dismantle();
				}
			}
		}
		super.onRemove(state, world, pos, newState, isMoving);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag flag){
		tooltip.add(new TranslationTextComponent("tt.crossroads.gateway.frame"));
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state){
		return state.getValue(CRProperties.ACTIVE);
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, World world, BlockPos pos){
		return RedstoneUtil.clampToVanilla(read(world, pos, state));
	}

	@Override
	public float read(World world, BlockPos pos, BlockState state){
		if(!state.getValue(CRProperties.ACTIVE)){
			return 0;
		}
		//Read the number of entries in the dialed address [0-4]
		TileEntity te = world.getBlockEntity(pos);
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
