package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.API.templates.BeamBlock;
import com.Da_Technomancer.crossroads.tileentities.beams.BeamSplitterTileEntity;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneDiodeBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BeamSplitter extends BeamBlock{

	public BeamSplitter(){
		super("beam_splitter");
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new BeamSplitterTileEntity();
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		neighborChanged(state, world, pos, this, pos, false);
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
		TileEntity te = worldIn.getTileEntity(pos);

		if(te instanceof BeamSplitterTileEntity){
			//Simple optimization- if the block update is just signal strength changing, we don't need to rebuild connections
			if(blockIn != Blocks.REDSTONE_WIRE && !(blockIn instanceof RedstoneDiodeBlock)){
				((BeamSplitterTileEntity) te).buildConnections();
			}
			((BeamSplitterTileEntity) te).setRedstone(Math.round(RedstoneUtil.getRedstoneAtPos(worldIn, pos)));
		}
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.beam_splitter.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.boilerplate.circuit"));
	}
}
