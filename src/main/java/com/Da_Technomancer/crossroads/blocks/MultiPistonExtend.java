package com.Da_Technomancer.crossroads.blocks;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.items.ModItems;

import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class MultiPistonExtend extends Block{

	private final boolean sticky;
	
	protected MultiPistonExtend(boolean sticky){
		super(Material.PISTON);
		this.sticky = sticky;
		String name = "multiPistonExtend" + (sticky ? "Sticky" : "");
		setUnlocalizedName(name);
		setRegistryName(name);
		setHardness(0.5F);
		setCreativeTab(ModItems.tabCrossroads);
		GameRegistry.register(this);
		setDefaultState(this.blockState.getBaseState().withProperty(Properties.FACING, EnumFacing.NORTH).withProperty(Properties.HEAD, false));
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state){
		if(world.isRemote){
			return;
		}
		if(!state.getValue(Properties.HEAD)){
			if(world.getBlockState(pos.offset(state.getValue(Properties.FACING))).getBlock() == this){
				world.setBlockState(pos.offset(state.getValue(Properties.FACING)), Blocks.AIR.getDefaultState());
			}
		}
		if(world.getBlockState(pos.offset(state.getValue(Properties.FACING).getOpposite())).getBlock() == this){
			world.setBlockState(pos.offset(state.getValue(Properties.FACING).getOpposite()), Blocks.AIR.getDefaultState());
		}else if(world.getBlockState(pos.offset(state.getValue(Properties.FACING).getOpposite())).getBlock() == (sticky ? ModBlocks.multiPistonSticky : ModBlocks.multiPiston)){
			((MultiPistonBase) world.getBlockState(pos.offset(state.getValue(Properties.FACING).getOpposite())).getBlock()).safeBreak(world, pos.offset(state.getValue(Properties.FACING).getOpposite()));
		}
	}
	
	@Override
	public int damageDropped(IBlockState state){
		return 0;
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, new IProperty[] {Properties.FACING, Properties.HEAD});
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return this.getDefaultState().withProperty(Properties.HEAD, (meta & 8) == 8).withProperty(Properties.FACING, EnumFacing.getFront(meta & 7));
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(Properties.FACING).getIndex() + (state.getValue(Properties.HEAD) ? 8 : 0);
	}
	
	@Override
	public EnumPushReaction getMobilityFlag(IBlockState state){
		return EnumPushReaction.BLOCK;
	}
}