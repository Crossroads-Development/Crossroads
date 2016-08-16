package com.Da_Technomancer.crossroads.blocks.rotary;

import java.util.Random;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.ServerProxy;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearSlaveTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LargeGearSlave extends BlockContainer{
	
	public LargeGearSlave(){
		super(Material.IRON);
		String name = "largeGearSlave";
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		this.setCreativeTab(ModItems.tabCrossroads);
		this.setHardness(3);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new LargeGearSlaveTileEntity();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.INVISIBLE;
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn){
		if(worldIn.isRemote){
			return;
		}
		ServerProxy.masterKey++;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player){
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side){
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state){
		return false;
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side){
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos){
		return true;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state){

		if(worldIn.getTileEntity(pos) instanceof LargeGearSlaveTileEntity){
			((LargeGearSlaveTileEntity) worldIn.getTileEntity(pos)).passBreak();
		}
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	@Nullable
	public Item getItemDropped(IBlockState state, Random rand, int fortune){
		return null;
	}
}
