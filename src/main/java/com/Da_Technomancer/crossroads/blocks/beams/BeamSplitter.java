package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.CircuitUtil;
import com.Da_Technomancer.crossroads.API.templates.BeamBlock;
import com.Da_Technomancer.crossroads.tileentities.beams.BeamSplitterTileEntity;
import com.Da_Technomancer.essentials.blocks.redstone.IWireConnect;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BeamSplitter extends BeamBlock implements IWireConnect{

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
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
		super.fillStateContainer(builder);
		builder.add(CRProperties.POWER_LEVEL);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context){
		int power = RedstoneUtil.getRedstoneAtPos(context.getWorld(), context.getPos());
		return super.getStateForPlacement(context).with(CRProperties.POWER_LEVEL, power >= 15 ? 2 : power == 0 ? 0 : 1);
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
		TileEntity te = worldIn.getTileEntity(pos);

		if(te instanceof BeamSplitterTileEntity){
			BeamSplitterTileEntity bte = (BeamSplitterTileEntity) te;
			CircuitUtil.updateFromWorld(bte.redsHandler, blockIn);
		}
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.beam_splitter.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.boilerplate.circuit"));
	}

	@Override
	public boolean canConnect(Direction side, BlockState state){
		return true;
	}
}
