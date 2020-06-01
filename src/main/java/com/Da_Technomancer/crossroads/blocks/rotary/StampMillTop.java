package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.ESConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class StampMillTop extends Block{

	private static final VoxelShape[] SHAPES = new VoxelShape[2];

	static{
		SHAPES[0] = VoxelShapes.or(makeCuboidShape(0, 15, 3, 16, 16, 11), makeCuboidShape(0, 0, 3, 1, 15, 11), makeCuboidShape(15, 0, 3, 16, 15, 11));
		SHAPES[1] = VoxelShapes.or(makeCuboidShape(3, 15, 0, 11, 16, 16), makeCuboidShape(3, 0, 0, 11, 15, 1), makeCuboidShape(3, 0, 15, 11, 15, 16));
	}

	public StampMillTop(){
		super(Properties.create(Material.WOOD).hardnessAndResistance(1).sound(SoundType.METAL));
		String name = "stamp_mill_top";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		//Not added to queue to prevent registering item form
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		return SHAPES[state.get(CRProperties.HORIZ_AXIS) == Direction.Axis.X ? 0 : 1];
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(ESConfig.isWrench(playerIn.getHeldItem(hand))){
			if(!worldIn.isRemote){
				worldIn.setBlockState(pos, state.cycle(CRProperties.HORIZ_AXIS));
				BlockState lowerState = worldIn.getBlockState(pos.down());
				if(lowerState.getBlock() == CRBlocks.stampMill){
					worldIn.setBlockState(pos.down(), lowerState.cycle(CRProperties.HORIZ_AXIS));
				}
			}
			return ActionResultType.SUCCESS;
		}

		TileEntity te;
		if(!worldIn.isRemote && (te = worldIn.getTileEntity(pos.down())) instanceof INamedContainerProvider){
			NetworkHooks.openGui((ServerPlayerEntity) playerIn, (INamedContainerProvider) te, pos.down());
		}
		return ActionResultType.SUCCESS;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player){
		return new ItemStack(CRBlocks.stampMill, 1);
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
		if(!(worldIn.getBlockState(pos.offset(Direction.DOWN)).getBlock() instanceof StampMill)){
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
		}
	}
	
	@Override
	public PushReaction getPushReaction(BlockState state){
		return PushReaction.BLOCK;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
		builder.add(CRProperties.HORIZ_AXIS);
	}
}
