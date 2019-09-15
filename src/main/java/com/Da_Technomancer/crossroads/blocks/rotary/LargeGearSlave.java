package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CrossroadsItems;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearMasterTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearSlaveTileEntity;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class LargeGearSlave extends ContainerBlock{

	private static final AxisAlignedBB[] BB = new AxisAlignedBB[] {new AxisAlignedBB(0D, 0D, 0D, 1D, .125D, 1D), new AxisAlignedBB(0D, 0.875D, 0D, 1D, 1D, 1D), new AxisAlignedBB(0D, 0D, 0D, 1D, 1D, .125D), new AxisAlignedBB(0D, 0D, 0.875D, 1D, 1D, 1D), new AxisAlignedBB(0D, 0D, 0D, .125D, 1D, 1D), new AxisAlignedBB(0.875D, 0D, 0D, 1D, 1D, 1D)};

	public LargeGearSlave(){
		super(Material.IRON);
		String name = "large_gear_slave";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CrossroadsItems.TAB_CROSSROADS);
		setHardness(3);
		setSoundType(SoundType.METAL);
		CrossroadsBlocks.toRegister.add(this);
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, EssentialsProperties.FACING);
	}
	
	@Override
	public BlockState getStateFromMeta(int meta){
		return getDefaultState().with(EssentialsProperties.FACING, Direction.byIndex(meta));
	}

	@Override
	public int getMetaFromState(BlockState state){
		return state.get(EssentialsProperties.FACING).getIndex();
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos){
		return BB[state.get(EssentialsProperties.FACING).getIndex()];
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new LargeGearSlaveTileEntity();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.INVISIBLE;
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		RotaryUtil.increaseMasterKey(true);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face){
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, World world, BlockPos pos, PlayerEntity player){
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof LargeGearSlaveTileEntity && ((LargeGearSlaveTileEntity) te).masterPos != null){
			te = world.getTileEntity(pos.add(((LargeGearSlaveTileEntity) te).masterPos));
			if(te instanceof LargeGearMasterTileEntity){
				return new ItemStack(GearFactory.gearTypes.get(((LargeGearMasterTileEntity) (te)).getMember()).getLargeGear(), 1);
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean shouldSideBeRendered(BlockState blockState, IBlockAccess blockAccess, BlockPos pos, Direction side){
		return false;
	}

	@Override
	public boolean isFullCube(BlockState state){
		return false;
	}

	@Override
	public boolean isOpaqueCube(BlockState state){
		return false;
	}

	@Override
	public boolean removedByPlayer(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, boolean canHarvest){
		if(canHarvest && worldIn.getTileEntity(pos) instanceof LargeGearSlaveTileEntity){
			((LargeGearSlaveTileEntity) worldIn.getTileEntity(pos)).passBreak(state.get(EssentialsProperties.FACING), true);
		}
		return super.removedByPlayer(state, worldIn, pos, player, canHarvest);
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, BlockState state){
		if(worldIn.getTileEntity(pos) instanceof LargeGearSlaveTileEntity){
			((LargeGearSlaveTileEntity) worldIn.getTileEntity(pos)).passBreak(state.get(EssentialsProperties.FACING), false);
		}
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public Item getItemDropped(BlockState state, Random rand, int fortune){
		return null;
	}
}
