package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.gui.container.ColorChartContainer;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class ColorChart extends Block{

	private static final VoxelShape[] SHAPES = new VoxelShape[6];

	static{
		SHAPES[0] = box(0, 15, 0, 16, 16, 16);;
		SHAPES[1] = box(0, 0, 0, 16, 1, 16);;
		SHAPES[2] = box(0, 0, 15, 16, 16, 16);
		SHAPES[3] = box(0, 0, 0, 16, 16, 1);
		SHAPES[4] = box(15, 0, 0, 16, 16, 16);
		SHAPES[5] = box(0, 0, 0, 1, 16, 16);
	}

	public ColorChart(){
		super(Properties.of(Material.WOOD).strength(3));
		String name = "color_chart";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		if(!worldIn.isClientSide){
			NetworkHooks.openGui((ServerPlayer) playerIn, new MenuProvider(){
				@Override
				public Component getDisplayName(){
					return new TranslatableComponent("container.color_chart");
				}

				@Override
				public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity){
					return new ColorChartContainer(i, playerInventory, null);
				}
			});
		}
		return InteractionResult.SUCCESS;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context){
		return defaultBlockState().setValue(ESProperties.FACING, context.getNearestLookingDirection().getOpposite());
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context){
		return SHAPES[state.getValue(ESProperties.FACING).get3DDataValue()];
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(ESProperties.FACING);
	}
}
