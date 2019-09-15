package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CrossroadsItems;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearMasterTileEntity;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class LargeGearMaster extends ContainerBlock{

	private static final AxisAlignedBB[] BB = new AxisAlignedBB[] {new AxisAlignedBB(0D, 0D, 0D, 1D, .125D, 1D), new AxisAlignedBB(0D, 0.875D, 0D, 1D, 1D, 1D), new AxisAlignedBB(0D, 0D, 0D, 1D, 1D, .125D), new AxisAlignedBB(0D, 0D, 0.875D, 1D, 1D, 1D), new AxisAlignedBB(0D, 0D, 0D, .125D, 1D, 1D), new AxisAlignedBB(0.875D, 0D, 0D, 1D, 1D, 1D)};

	public LargeGearMaster(){
		super(Material.IRON);
		String name = "large_gear_master";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CrossroadsItems.TAB_CROSSROADS);
		setHardness(3);
		setSoundType(SoundType.METAL);
		CrossroadsBlocks.toRegister.add(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new LargeGearMasterTileEntity();
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face){
		return face == state.get(EssentialsProperties.FACING) ? BlockFaceShape.SOLID : face == state.get(EssentialsProperties.FACING).getOpposite() ? BlockFaceShape.CENTER_SMALL : BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean isSideSolid(BlockState base_state, IBlockAccess world, BlockPos pos, Direction side){
		return side.getAxis() == base_state.get(EssentialsProperties.FACING).getAxis();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, World world, BlockPos pos, PlayerEntity player){
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof LargeGearMasterTileEntity){
			return new ItemStack(GearFactory.gearTypes.get(((LargeGearMasterTileEntity) te).getMember()).getLargeGear(), 1);
		}
		return ItemStack.EMPTY;
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
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, BlockState state, int fortune){
		List<ItemStack> drops = new ArrayList<ItemStack>();
		LargeGearMasterTileEntity te = (LargeGearMasterTileEntity) world.getTileEntity(pos);
		if(te != null && te.getMember() != null){
			drops.add(new ItemStack(GearFactory.gearTypes.get(te.getMember()).getLargeGear(), 1));
		}
		return drops;
	}

	@Override
	public boolean removedByPlayer(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, boolean canHarvest){
		if(canHarvest && worldIn.getTileEntity(pos) instanceof LargeGearMasterTileEntity){
			((LargeGearMasterTileEntity) worldIn.getTileEntity(pos)).breakGroup(state.get(EssentialsProperties.FACING), true);
		}
		return super.removedByPlayer(state, worldIn, pos, player, canHarvest);
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, BlockState state){
		if(worldIn.getTileEntity(pos) instanceof LargeGearMasterTileEntity){
			((LargeGearMasterTileEntity) worldIn.getTileEntity(pos)).breakGroup(state.get(EssentialsProperties.FACING), false);
		}
		super.breakBlock(worldIn, pos, state);
	}
}
