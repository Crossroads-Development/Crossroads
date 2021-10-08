package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.templates.BeamBlock;
import com.Da_Technomancer.crossroads.tileentities.technomancy.ClockworkStabilizerTileEntity;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.Da_Technomancer.essentials.blocks.redstone.IReadable;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ClockworkStabilizer extends BeamBlock implements IReadable{

	private static final VoxelShape[] SHAPE = new VoxelShape[6];

	static{
		//Very crude shape to match the angled model- may be worth refining later
		SHAPE[0] = box(4, 0, 4, 12, 16, 12);
		SHAPE[1] = box(4, 0, 4, 12, 16, 12);
		SHAPE[2] = box(4, 4, 0, 12, 12, 16);
		SHAPE[3] = box(4, 4, 0, 12, 12, 16);
		SHAPE[4] = box(0, 4, 0, 16, 12, 12);
		SHAPE[5] = box(0, 4, 0, 16, 12, 12);
	}

	public ClockworkStabilizer(){
		super("clock_stabilizer");
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context){
		return SHAPE[state.getValue(ESProperties.FACING).get3DDataValue()];
	}

	@Override
	public BlockEntity newBlockEntity(BlockGetter worldIn){
		return new ClockworkStabilizerTileEntity();
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state){
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos){
		return RedstoneUtil.clampToVanilla(read(worldIn, pos, blockState));
	}

	@Override
	public float read(Level world, BlockPos pos, BlockState blockState){
		BlockEntity te = world.getBlockEntity(pos);
		return te instanceof ClockworkStabilizerTileEntity ? ((ClockworkStabilizerTileEntity) te).getRedstone() : 0;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag flag){
		tooltip.add(new TranslatableComponent("tt.crossroads.clock_stab.desc", ClockworkStabilizerTileEntity.RATE * 100));
		tooltip.add(new TranslatableComponent("tt.crossroads.clock_stab.circuit"));
	}
}