package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.API.beams.IBeamTransparent;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class LightCluster extends Block implements IBeamTransparent{

	public LightCluster(){
		super(Material.GLASS);
		String name = "light_cluster";
		setRegistryName(name);
		setTranslationKey(name);
		setHardness(0);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setSoundType(SoundType.GLASS);
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}

	@Override
	public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos){
		return true;
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune){

	}

	@Override
	public boolean isFullCube(IBlockState state){
		return false;
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos){
		return 15;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn){
		tooltip.add("Permeable to beams");
		tooltip.add("Safe for decoration");
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos){
		return NULL_AABB;
	}

	private static final AxisAlignedBB BB = new AxisAlignedBB(0.4, 0.4, 0.4, 0.6, 0.6, 0.6);

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
		return BB;
	}
}
