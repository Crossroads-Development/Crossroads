package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public class LightCluster extends Block{

	private static final VoxelShape SHAPE = box(6.4, 6.4, 6.4, 9.6, 9.6, 9.6);

	public LightCluster(){
		super(Properties.of(Material.DECORATION).strength(0).sound(SoundType.GLASS).noCollission().noLootTable().lightLevel(state -> 15));
		String name = "light_cluster";
		CRBlocks.toRegister.put(name, this);
		CRBlocks.blockAddQue(name, this);
		registerDefaultState(defaultBlockState().setValue(CRProperties.COLOR, DyeColor.WHITE));
	}

	@Override
	public VoxelShape getCollisionShape(BlockState p_220071_1_, BlockGetter p_220071_2_, BlockPos p_220071_3_, CollisionContext p_220071_4_){
		return Shapes.empty();
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state){
		return PushReaction.DESTROY;
	}

	@Override
	public boolean canBeReplaced(BlockState p_196253_1_, BlockPlaceContext p_196253_2_){
		return true;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(Component.translatable("tt.crossroads.boilerplate.beam_permeable"));
		tooltip.add(Component.translatable("tt.crossroads.light_cluster.decor"));
	}

	@Override
	public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_){
		return SHAPE;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(CRProperties.COLOR);
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		ItemStack heldItem = playerIn.getItemInHand(hand);
		DyeColor col = DyeColor.getColor(heldItem);
		if(col != null){
			worldIn.setBlock(pos, state.setValue(CRProperties.COLOR, col),  2);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}
}
