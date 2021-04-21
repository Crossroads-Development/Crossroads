package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.API.technomancy.IGateway;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.technomancy.GatewayControllerTileEntity;
import com.Da_Technomancer.essentials.ESConfig;
import com.Da_Technomancer.essentials.blocks.redstone.IReadable;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class GatewayController extends ContainerBlock implements IReadable{

	public GatewayController(){
		super(CRBlocks.getMetalProperty());
		String name = "gateway_frame";//This registry name is bad, but kept for backwards compatibility
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
		registerDefaultState(defaultBlockState().setValue(CRProperties.ACTIVE, false));
	}

	@Nullable
	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn){
		return new GatewayControllerTileEntity();
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state){
		//If this is formed into a multiblock, we let the TESR on the top handle all rendering
		return state.getValue(CRProperties.ACTIVE) ? BlockRenderType.ENTITYBLOCK_ANIMATED : BlockRenderType.MODEL;
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder){
		builder.add(CRProperties.ACTIVE);//ACTIVE is whether this is formed into a multiblock
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray){
		ItemStack held = player.getItemInHand(hand);
		if(state.getValue(CRProperties.ACTIVE)){
			//Handle linking if this is the top block
			return FluxUtil.handleFluxLinking(world, pos, held, player);
		}else if(ESConfig.isWrench(held)){
			//Attempt to form the multiblock
			TileEntity te = world.getBlockEntity(pos);
			if(te instanceof GatewayControllerTileEntity){
				((GatewayControllerTileEntity) te).assemble(player);
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.PASS;
	}

	@Override
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving){
		TileEntity te = world.getBlockEntity(pos);
		if(newState.getBlock() != state.getBlock() && te instanceof IGateway){
			((IGateway) te).dismantle();//Shutdown the multiblock
		}
		super.onRemove(state, world, pos, newState, isMoving);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag flag){
		tooltip.add(new TranslationTextComponent("tt.crossroads.gateway.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.gateway.dial"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.gateway.proc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.gateway.flux", GatewayControllerTileEntity.FLUX_PER_CYCLE));
		tooltip.add(new TranslationTextComponent("tt.crossroads.boilerplate.inertia", GatewayControllerTileEntity.INERTIA));
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
		//Read the number of entries in the dialed address [0-4]
		TileEntity te = world.getBlockEntity(pos);
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
