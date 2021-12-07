package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.ESConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

public class StampMillTop extends Block{

	private static final VoxelShape[] SHAPES = new VoxelShape[2];

	static{
		SHAPES[0] = Shapes.or(box(0, 15, 3, 16, 16, 11), box(0, 0, 3, 1, 15, 11), box(15, 0, 3, 16, 15, 11));
		SHAPES[1] = Shapes.or(box(3, 15, 0, 11, 16, 16), box(3, 0, 0, 11, 15, 1), box(3, 0, 15, 11, 15, 16));
	}

	public StampMillTop(){
		super(Properties.of(Material.WOOD).strength(1).sound(SoundType.METAL));
		String name = "stamp_mill_top";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		//Not added to queue to prevent registering item form
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return SHAPES[state.getValue(CRProperties.HORIZ_AXIS) == Direction.Axis.X ? 0 : 1];
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		if(ESConfig.isWrench(playerIn.getItemInHand(hand))){
			if(!worldIn.isClientSide){
				worldIn.setBlockAndUpdate(pos, state.cycle(CRProperties.HORIZ_AXIS));
				BlockState lowerState = worldIn.getBlockState(pos.below());
				if(lowerState.getBlock() == CRBlocks.stampMill){
					worldIn.setBlockAndUpdate(pos.below(), lowerState.cycle(CRProperties.HORIZ_AXIS));
				}
			}
			return InteractionResult.SUCCESS;
		}

		BlockEntity te;
		if(!worldIn.isClientSide && (te = worldIn.getBlockEntity(pos.below())) instanceof MenuProvider){
			NetworkHooks.openGui((ServerPlayer) playerIn, (MenuProvider) te, pos.below());
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player){
		return new ItemStack(CRBlocks.stampMill, 1);
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
		if(!(worldIn.getBlockState(pos.relative(Direction.DOWN)).getBlock() instanceof StampMill)){
			worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
		}
	}
	
	@Override
	public PushReaction getPistonPushReaction(BlockState state){
		return PushReaction.BLOCK;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(CRProperties.HORIZ_AXIS);
	}
}
