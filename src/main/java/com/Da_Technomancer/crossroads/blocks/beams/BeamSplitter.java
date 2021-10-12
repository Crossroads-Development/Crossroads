package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.CircuitUtil;
import com.Da_Technomancer.crossroads.API.templates.BeamBlock;
import com.Da_Technomancer.crossroads.tileentities.beams.BeamSplitterTileEntity;
import com.Da_Technomancer.essentials.blocks.redstone.IWireConnect;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import com.Da_Technomancer.essentials.tileentities.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

import javax.annotation.Nullable;
import java.util.List;

public class BeamSplitter extends BeamBlock implements IWireConnect{

	public BeamSplitter(){
		super("beam_splitter");
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new BeamSplitterTileEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, BeamSplitterTileEntity.TYPE);
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		neighborChanged(state, world, pos, this, pos, false);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		super.createBlockStateDefinition(builder);
		builder.add(CRProperties.POWER_LEVEL);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context){
		int power = RedstoneUtil.getRedstoneAtPos(context.getLevel(), context.getClickedPos());
		return super.getStateForPlacement(context).setValue(CRProperties.POWER_LEVEL, power >= 15 ? 2 : power == 0 ? 0 : 1);
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
		BlockEntity te = worldIn.getBlockEntity(pos);

		if(te instanceof BeamSplitterTileEntity){
			BeamSplitterTileEntity bte = (BeamSplitterTileEntity) te;
			CircuitUtil.updateFromWorld(bte.redsHandler, blockIn);
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(new TranslatableComponent("tt.crossroads.beam_splitter.desc"));
		tooltip.add(new TranslatableComponent("tt.crossroads.boilerplate.circuit"));
	}

	@Override
	public boolean canConnect(Direction side, BlockState state){
		return true;
	}
}
