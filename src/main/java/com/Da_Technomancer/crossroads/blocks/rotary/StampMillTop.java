package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.gui.GuiHandler;
import com.Da_Technomancer.essentials.EssentialsConfig;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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
		ModBlocks.toRegister.add(this);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(EssentialsConfig.isWrench(playerIn.getHeldItem(hand), worldIn.isRemote)){
			if(!worldIn.isRemote){
				worldIn.setBlockState(pos, state.cycleProperty(Properties.HORIZ_AXIS));
				IBlockState lowerState = worldIn.getBlockState(pos.down());
				if(lowerState.getBlock() == ModBlocks.stampMill){
					worldIn.setBlockState(pos, state.withProperty(Properties.HORIZ_AXIS, worldIn.getBlockState(pos).getValue(Properties.HORIZ_AXIS)));
				}
			}
			return true;
		}

		if(!worldIn.isRemote && worldIn.getBlockState(pos.down()).getBlock() == ModBlocks.stampMill){
			playerIn.openGui(Main.instance, GuiHandler.STAMP_MILL_GUI, worldIn, pos.getX(), pos.getY() - 1, pos.getZ());
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player){
		return new ItemStack(ModBlocks.stampMill, 1);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune){
		return null;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face){
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		if(!(worldIn.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock() instanceof StampMill)){
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
		}
	}

	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state){
		return false;
	}
	
	@Override
	public EnumPushReaction getPushReaction(IBlockState state){
		return EnumPushReaction.BLOCK;
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, Properties.HORIZ_AXIS);
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState().withProperty(Properties.HORIZ_AXIS, meta == 0 ? EnumFacing.Axis.X : EnumFacing.Axis.Z);
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(Properties.HORIZ_AXIS) == EnumFacing.Axis.X ? 0 : 1;
	}
}
