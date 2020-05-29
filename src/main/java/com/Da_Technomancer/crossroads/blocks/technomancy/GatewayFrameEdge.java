package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.technomancy.GatewayEdgeTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.GatewayFrameTileEntity;
import com.Da_Technomancer.essentials.blocks.redstone.IReadable;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
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
		super(Properties.create(Material.IRON).hardnessAndResistance(3).sound(SoundType.METAL));
		String name = "gateway_edge";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
		setDefaultState(getDefaultState().with(CRProperties.ACTIVE, false));
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new GatewayEdgeTileEntity();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
		builder.add(CRProperties.ACTIVE);//ACTIVE is whether this is formed into a multiblock
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving){
		TileEntity te = world.getTileEntity(pos);
		if(newState.getBlock() != state.getBlock() && te instanceof GatewayEdgeTileEntity){
			((GatewayEdgeTileEntity) te).dismantle();//Shutdown the multiblock
		}
		super.onReplaced(state, world, pos, newState, isMoving);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag flag){
		tooltip.add(new TranslationTextComponent("tt.crossroads.gateway.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.gateway.dial"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.gateway.proc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.gateway.flux", GatewayFrameTileEntity.FLUX_PER_CYCLE));
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
		if(!state.get(CRProperties.ACTIVE)){
			return 0;
		}
		//Read the number of entries in the dialed address [0-4]
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof GatewayEdgeTileEntity){
			return ((GatewayEdgeTileEntity) te).getCircuitRead();
		}
		return 0;
	}

	@Override
	public PushReaction getPushReaction(BlockState state){
		return PushReaction.BLOCK;//Some mods make TileEntities piston moveable. That would be really bad for this block
	}
}
