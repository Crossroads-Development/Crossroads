package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public class PermeableGlass extends Block{

	public PermeableGlass(){
		super(CRBlocks.getGlassProperty().noOcclusion());
		String name = "permeable_glass";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos){
		return 1.0F;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos){
		return true;
	}

	@Override
	public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext selection) {
		return Shapes.empty();
	}

	@Override
	public boolean skipRendering(BlockState state, BlockState otherState, Direction dir) {
		return otherState.getBlock() == this || super.skipRendering(state, otherState, dir);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter player, List<Component> tooltip, TooltipFlag advanced){
		tooltip.add(new TranslatableComponent("tt.crossroads.boilerplate.beam_permeable"));
		tooltip.add(new TranslatableComponent("tt.crossroads.boilerplate.decor"));
	}
}
