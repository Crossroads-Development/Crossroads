package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.EssentialsConfig;
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
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class StampMillTop extends Block{

	public StampMillTop(){
		super(Properties.create(Material.WOOD).hardnessAndResistance(1).sound(SoundType.METAL));
		String name = "stamp_mill_top";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		//Not added to queue to prevent registering item form
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		//TODO
		return super.getShape(state, worldIn, pos, context);
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(EssentialsConfig.isWrench(playerIn.getHeldItem(hand))){
			if(!worldIn.isRemote){
				worldIn.setBlockState(pos, state.cycle(CRProperties.HORIZ_AXIS));
				BlockState lowerState = worldIn.getBlockState(pos.down());
				if(lowerState.getBlock() == CRBlocks.stampMill){
					worldIn.setBlockState(pos, state.with(CRProperties.HORIZ_AXIS, worldIn.getBlockState(pos).get(CRProperties.HORIZ_AXIS)));
				}
			}
			return true;
		}

		TileEntity te;
		if(!worldIn.isRemote && (te = worldIn.getTileEntity(pos.down())) instanceof INamedContainerProvider){
			NetworkHooks.openGui((ServerPlayerEntity) playerIn, (INamedContainerProvider) te, pos.down());
		}
		return true;
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
