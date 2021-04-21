package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class LightCluster extends Block{

	private static final VoxelShape SHAPE = box(6.4, 6.4, 6.4, 9.6, 9.6, 9.6);

	public LightCluster(){
		super(Properties.of(Material.DECORATION).strength(0).sound(SoundType.GLASS).noCollission().noDrops().lightLevel(state -> 15));
		String name = "light_cluster";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
		registerDefaultState(defaultBlockState().setValue(ESProperties.COLOR, DyeColor.WHITE));
	}

	@Override
	public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_){
		return VoxelShapes.empty();
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state){
		return PushReaction.DESTROY;
	}

	@Override
	public boolean canBeReplaced(BlockState p_196253_1_, BlockItemUseContext p_196253_2_){
		return true;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.boilerplate.beam_permeable"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.light_cluster.decor"));
	}

	@Override
	public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_){
		return SHAPE;
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder){
		builder.add(ESProperties.COLOR);
	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		ItemStack heldItem = playerIn.getItemInHand(hand);
		DyeColor col = DyeColor.getColor(heldItem);
		if(col != null){
			worldIn.setBlock(pos, state.setValue(ESProperties.COLOR, col),  2);
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}
}
