package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.API.templates.BeamBlock;
import com.Da_Technomancer.crossroads.tileentities.beams.BeamRedirectorTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
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
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new BeamRedirectorTileEntity();
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		neighborChanged(state, world, pos, this, pos, false);
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
		TileEntity te = worldIn.getTileEntity(pos);
		if(te instanceof BeamRedirectorTileEntity){
			boolean hasRedstone = worldIn.getRedstonePower(pos.east(), Direction.EAST) != 0 || worldIn.getRedstonePower(pos.west(), Direction.WEST) != 0 || worldIn.getRedstonePower(pos.north(), Direction.NORTH) != 0 || worldIn.getRedstonePower(pos.south(), Direction.SOUTH) != 0 || worldIn.getRedstonePower(pos.down(), Direction.DOWN) != 0 || worldIn.getRedstonePower(pos.up(), Direction.UP) != 0;
			((BeamRedirectorTileEntity) te).setRedstone(hasRedstone);
		}
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.beam_redirector.desc"));
	}
}
