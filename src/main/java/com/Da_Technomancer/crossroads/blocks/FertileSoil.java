package com.Da_Technomancer.crossroads.blocks;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.google.common.base.Function;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FertileSoil extends Block{

	protected FertileSoil(){
		super(Material.GROUND);
		String name = "fertileSoil";
		setUnlocalizedName(name);
		setRegistryName(name);
		setHardness(2);
		setSoundType(SoundType.GROUND);
		this.setCreativeTab(ModItems.tabCrossroads);
		GameRegistry.register(this);
		GameRegistry.register(new ItemMultiTexture(this, this, new Function<ItemStack, String>(){
			@Override
			@Nullable
			public String apply(@Nullable ItemStack stack){
				return (stack.getMetadata() == 0 ? "wheat" : stack.getMetadata() == 1 ? "potato" : stack.getMetadata() == 2 ? "carrot" : stack.getMetadata() == 3 ? "beet" : stack.getMetadata() == 4 ? "oak" : stack.getMetadata() == 5 ? "birch" : stack.getMetadata() == 6 ? "spruce" : stack.getMetadata() == 7 ? "jungle" : stack.getMetadata() == 8 ? "acacia" : "dark");
			}
		}).setRegistryName(name));
		this.setTickRandomly(true);
		setDefaultState(blockState.getBaseState().withProperty(Properties.PLANT, 0));
	}
	
	@Override
	public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable){
		return true;
	}
	
	@Override
	public boolean isFertile(World world, BlockPos pos){
		return true;
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn){
		if(state.getValue(Properties.PLANT) >= 4){
			updateTick(worldIn, pos, state, new Random());
		}
	}
	
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand){
		if(worldIn.isRemote){
			return;
		}
		
		if(worldIn.isAirBlock(pos.offset(EnumFacing.UP))){
			switch(state.getValue(Properties.PLANT)){
				case 0:
					worldIn.setBlockState(pos.offset(EnumFacing.UP), Blocks.WHEAT.getDefaultState().withProperty(BlockCrops.AGE, 0));
					break;
				case 1:
					worldIn.setBlockState(pos.offset(EnumFacing.UP), Blocks.POTATOES.getDefaultState().withProperty(BlockCrops.AGE, 0));
					break;
				case 2:
					worldIn.setBlockState(pos.offset(EnumFacing.UP), Blocks.CARROTS.getDefaultState().withProperty(BlockCrops.AGE, 0));
					break;
				case 3:
					worldIn.setBlockState(pos.offset(EnumFacing.UP), Blocks.BEETROOTS.getDefaultState().withProperty(BlockCrops.AGE, 0));
					break;
				case 4:
					worldIn.setBlockState(pos.offset(EnumFacing.UP), Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.OAK));
					break;
				case 5:
					worldIn.setBlockState(pos.offset(EnumFacing.UP), Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.BIRCH));
					break;
				case 6:
					worldIn.setBlockState(pos.offset(EnumFacing.UP), Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.SPRUCE));
					break;
				case 7:
					worldIn.setBlockState(pos.offset(EnumFacing.UP), Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.JUNGLE));
					break;
				case 8:
					worldIn.setBlockState(pos.offset(EnumFacing.UP), Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.ACACIA));
					break;
				case 9:
					worldIn.setBlockState(pos.offset(EnumFacing.UP), Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.DARK_OAK));
			}
		}
	}

	@Override
	public int damageDropped(IBlockState state){
		return state.getValue(Properties.PLANT);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list){
		for(int i = 0; i < 10; i++){
			list.add(new ItemStack(itemIn, 1, i));
		}
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, new IProperty[] {Properties.PLANT});
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return this.getDefaultState().withProperty(Properties.PLANT, meta);
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(Properties.PLANT);
	}

}
