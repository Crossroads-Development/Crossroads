package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.technomancy.GatewayFrameTileEntity;
import com.Da_Technomancer.essentials.ESConfig;
import com.Da_Technomancer.essentials.blocks.redstone.IReadable;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
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

public class GatewayFrame extends ContainerBlock implements IReadable{

	public GatewayFrame(){
		super(CRBlocks.METAL_PROPERTY);
		String name = "gateway_frame";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
		setDefaultState(getDefaultState().with(CRProperties.ACTIVE, false));
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new GatewayFrameTileEntity();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		//If this is formed into a multiblock, we let the TESR on the top handle all rendering
		return state.get(CRProperties.ACTIVE) ? BlockRenderType.ENTITYBLOCK_ANIMATED : BlockRenderType.MODEL;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
		builder.add(CRProperties.ACTIVE);//ACTIVE is whether this is formed into a multiblock
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray){
		ItemStack held = player.getHeldItem(hand);
		if(state.get(CRProperties.ACTIVE)){
			//Handle linking if this is the top block
			return FluxUtil.handleFluxLinking(world, pos, held, player);
		}else if(ESConfig.isWrench(held)){
			//Attempt to form the multiblock
			TileEntity te = world.getTileEntity(pos);
			if(te instanceof GatewayFrameTileEntity){
				((GatewayFrameTileEntity) te).assemble();
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.PASS;
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving){
		TileEntity te = world.getTileEntity(pos);
		if(newState.getBlock() != state.getBlock() && te instanceof GatewayFrameTileEntity){
			((GatewayFrameTileEntity) te).dismantle();//Shutdown the multiblock
		}
		super.onReplaced(state, world, pos, newState, isMoving);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag flag){
		tooltip.add(new TranslationTextComponent("tt.crossroads.gateway.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.gateway.dial"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.gateway.proc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.gateway.flux", GatewayFrameTileEntity.FLUX_PER_CYCLE));
		tooltip.add(new TranslationTextComponent("tt.crossroads.boilerplate.inertia", GatewayFrameTileEntity.INERTIA));
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state){
		return state.get(CRProperties.ACTIVE);
	}

	@Override
	public int getComparatorInputOverride(BlockState state, World world, BlockPos pos){
		return RedstoneUtil.clampToVanilla(read(world, pos, state));
	}

	@Override
	public float read(World world, BlockPos pos, BlockState state){
		//Read the number of entries in the dialed address [0-4]
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof GatewayFrameTileEntity){
			EnumBeamAlignments[] chev = ((GatewayFrameTileEntity) te).chevrons;
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
	public PushReaction getPushReaction(BlockState state){
		return PushReaction.BLOCK;//Some mods make TileEntities piston moveable. That would be really bad for this block
	}
}
