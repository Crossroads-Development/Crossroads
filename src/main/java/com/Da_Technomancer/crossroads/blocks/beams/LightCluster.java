package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.API.beams.IBeamTransparent;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CrossroadsItems;
import net.minecraft.block.*;
import net.minecraft.block.material.PushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class LightCluster extends Block implements IBeamTransparent{

	public LightCluster(){
		super(Material.CIRCUITS);
		String name = "light_cluster";
		setRegistryName(name);
		setTranslationKey(name);
		setHardness(0);
		setCreativeTab(CrossroadsItems.TAB_CROSSROADS);
		setSoundType(SoundType.GLASS);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
		setDefaultState(getDefaultState().with(Properties.COLOR, DyeColor.WHITE));
	}

	@Override
	public boolean isOpaqueCube(BlockState state){
		return false;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face){
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public PushReaction getPushReaction(BlockState state){
		return PushReaction.DESTROY;
	}

	@Override
	public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos){
		return true;
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, BlockState state, int fortune){

	}

	@Override
	public boolean isFullCube(BlockState state){
		return false;
	}

	@Override
	public int getLightValue(BlockState state, IBlockAccess world, BlockPos pos){
		return 15;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn){
		tooltip.add("Permeable to beams");
		tooltip.add("Safe for decoration, can be dyed");
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(BlockState blockState, IBlockAccess worldIn, BlockPos pos){
		return NULL_AABB;
	}

	private static final AxisAlignedBB BB = new AxisAlignedBB(0.4, 0.4, 0.4, 0.6, 0.6, 0.6);

	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos){
		return BB;
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, Properties.COLOR);
	}

	@Override
	public int getMetaFromState(BlockState state){
		return state.get(Properties.COLOR).getMetadata();
	}

	@Override
	public BlockState getStateFromMeta(int meta){
		return getDefaultState().with(Properties.COLOR, DyeColor.byMetadata(meta));
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		ItemStack heldItem = playerIn.getHeldItem(hand);
		if(heldItem.getItem() == Items.DYE){
			worldIn.setBlockState(pos, state.with(Properties.COLOR, DyeColor.byDyeDamage(heldItem.getMetadata())),  2);
			return true;
		}
		return false;
	}
}
