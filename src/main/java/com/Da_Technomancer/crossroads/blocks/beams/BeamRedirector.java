package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.templates.BeamBlock;
import com.Da_Technomancer.crossroads.tileentities.beams.BeamRedirectorTileEntity;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItemUseContext;
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

public class BeamRedirector extends BeamBlock{

	public BeamRedirector(){
		super("beam_redirector");
		registerDefaultState(defaultBlockState().setValue(CRProperties.REDSTONE_BOOL, false));
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn){
		return new BeamRedirectorTileEntity();
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder){
		super.createBlockStateDefinition(builder);
		builder.add(CRProperties.REDSTONE_BOOL);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context){
		return super.getStateForPlacement(context).setValue(ESProperties.REDSTONE_BOOL, context.getLevel().hasNeighborSignal(context.getClickedPos()));
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
		if(worldIn.hasNeighborSignal(pos)){
			if(!state.getValue(ESProperties.REDSTONE_BOOL)){
				worldIn.setBlockAndUpdate(pos, state.setValue(ESProperties.REDSTONE_BOOL, true));
			}
		}else if(state.getValue(ESProperties.REDSTONE_BOOL)){
			worldIn.setBlockAndUpdate(pos, state.setValue(ESProperties.REDSTONE_BOOL, false));
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.beam_redirector.desc"));
	}
}
