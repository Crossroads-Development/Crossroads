package com.Da_Technomancer.crossroads.blocks;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.WorldBuffer;
import com.Da_Technomancer.crossroads.items.ModItems;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**Notable differences from a normal piston include:
 * 16 block head range, distance controlled by signal strength,
 * No quasi-connectivity,
 * Redstone can be placed on top of the piston,
 * Hit box does not change when extended,
 * Piston extension and retraction is instant, no 2-tick delay or rendering of block movement.
 * Does not work with slime blocks, breaks them (May change)
 */
public class MultiPistonBase extends Block{
	
	private final boolean sticky;

	protected MultiPistonBase(boolean sticky){
		super(Material.PISTON);
		String name = "multiPiston" + (sticky ? "Sticky" : "");
		setUnlocalizedName(name);
		setRegistryName(name);
		this.sticky = sticky;
		setHardness(0.5F);
		setCreativeTab(ModItems.tabCrossroads);
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this).setRegistryName(name));
		setDefaultState(this.blockState.getBaseState().withProperty(Properties.FACING, EnumFacing.NORTH));
	}
	
	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		return this.getDefaultState().withProperty(Properties.FACING, BlockPistonBase.getFacingFromEntity(pos, placer));
	}

	private void checkRedstone(World worldIn, BlockPos pos, EnumFacing dir){
		int i = Math.max(worldIn.getRedstonePower(pos.down(), EnumFacing.DOWN), Math.max(worldIn.getRedstonePower(pos.up(), EnumFacing.UP), Math.max(worldIn.getRedstonePower(pos.east(), EnumFacing.EAST), Math.max(worldIn.getRedstonePower(pos.west(), EnumFacing.WEST), Math.max(worldIn.getRedstonePower(pos.north(), EnumFacing.NORTH), worldIn.getRedstonePower(pos.south(), EnumFacing.SOUTH))))));
		if(getExtension(worldIn, pos, dir) !=  i){
			setExtension(worldIn, pos, dir, i);
		}
	}
	
	private int getExtension(World worldIn, BlockPos pos, EnumFacing dir){
		final Block GOAL = sticky ? ModBlocks.multiPistonExtendSticky : ModBlocks.multiPistonExtend;
		for(int i = 1; i <= 16; i++){
			if(worldIn.getBlockState(pos.offset(dir, i)).getBlock() != GOAL || worldIn.getBlockState(pos.offset(dir, i)).getValue(Properties.FACING) != dir || worldIn.getBlockState(pos.offset(dir, i)).getValue(Properties.HEAD)){
				return i - 1;
			}
		}
		return 16;
	}
	
	private void setExtension(World worldIn, BlockPos pos, EnumFacing dir, int distance){
		int prev;
		if((prev = getExtension(worldIn, pos, dir)) == distance){
			return;
		}
		
		final WorldBuffer world = new WorldBuffer(worldIn);
		final Block GOAL = sticky ? ModBlocks.multiPistonExtendSticky : ModBlocks.multiPistonExtend;
		for(int i = 1; i <= prev; i++){
			if(world.getBlockState(pos.offset(dir, i)).getBlock() == GOAL && worldIn.getBlockState(pos.offset(dir, i)).getValue(Properties.FACING) == dir){
				world.addChange(pos, Blocks.AIR.getDefaultState());
			}
			if(i == prev && world.getBlockState(pos.offset(dir, prev + 1)).getMobilityFlag() == EnumPushReaction.NORMAL){
				world.addChange(pos.offset(dir, 1), world.getBlockState(pos.offset(dir, prev + 1)));
				world.addChange(pos.offset(dir, prev + 1), Blocks.AIR.getDefaultState());
			}
		}

		final int PUSH_LIMIT = 12;
		if(distance == 0){
			world.doChanges();
			return;
		}
		
		for(int i = 1; i <= distance; i++){
			
			//TODO PUSHING
		}
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state){
		setExtension(world, pos, state.getValue(Properties.FACING), 0);
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn){
		checkRedstone(worldIn, pos, state.getValue(Properties.FACING));
	}
	
	@Override
	public int damageDropped(IBlockState state){
		return 0;
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, new IProperty[] {Properties.FACING});
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return this.getDefaultState().withProperty(Properties.FACING, EnumFacing.getFront(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(Properties.FACING).getIndex();
	}
}