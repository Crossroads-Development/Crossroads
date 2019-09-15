package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.gui.GuiHandler;
import com.Da_Technomancer.essentials.EssentialsConfig;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.PushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class StampMillTop extends Block{

	public StampMillTop(){
		super(Material.WOOD);
		String name = "stamp_mill_top";
		setTranslationKey(name);
		setRegistryName(name);
		setHardness(1);
		setSoundType(SoundType.METAL);
		CrossroadsBlocks.toRegister.add(this);
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(EssentialsConfig.isWrench(playerIn.getHeldItem(hand), worldIn.isRemote)){
			if(!worldIn.isRemote){
				worldIn.setBlockState(pos, state.cycle(Properties.HORIZ_AXIS));
				BlockState lowerState = worldIn.getBlockState(pos.down());
				if(lowerState.getBlock() == CrossroadsBlocks.stampMill){
					worldIn.setBlockState(pos, state.with(Properties.HORIZ_AXIS, worldIn.getBlockState(pos).get(Properties.HORIZ_AXIS)));
				}
			}
			return true;
		}

		if(!worldIn.isRemote && worldIn.getBlockState(pos.down()).getBlock() == CrossroadsBlocks.stampMill){
			playerIn.openGui(Crossroads.instance, GuiHandler.STAMP_MILL_GUI, worldIn, pos.getX(), pos.getY() - 1, pos.getZ());
		}
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, World world, BlockPos pos, PlayerEntity player){
		return new ItemStack(CrossroadsBlocks.stampMill, 1);
	}

	@Override
	public Item getItemDropped(BlockState state, Random rand, int fortune){
		return null;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face){
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		if(!(worldIn.getBlockState(pos.offset(Direction.DOWN)).getBlock() instanceof StampMill)){
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
		}
	}

	@Override
	public boolean isOpaqueCube(BlockState state){
		return false;
	}

	@Override
	public boolean isFullCube(BlockState state){
		return false;
	}
	
	@Override
	public PushReaction getPushReaction(BlockState state){
		return PushReaction.BLOCK;
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, Properties.HORIZ_AXIS);
	}

	@Override
	public BlockState getStateFromMeta(int meta){
		return getDefaultState().with(Properties.HORIZ_AXIS, meta == 0 ? Direction.Axis.X : Direction.Axis.Z);
	}

	@Override
	public int getMetaFromState(BlockState state){
		return state.get(Properties.HORIZ_AXIS) == Direction.Axis.X ? 0 : 1;
	}
}
