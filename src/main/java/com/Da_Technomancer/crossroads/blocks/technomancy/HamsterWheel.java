package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.tileentities.technomancy.HamsterWheelTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class HamsterWheel extends ContainerBlock{

	private static final AxisAlignedBB[] BB = new AxisAlignedBB[]{new AxisAlignedBB(0, 0, .5D, 1, 1, 1), new AxisAlignedBB(0, 0, 0, .5D, 1, 1), new AxisAlignedBB(0, 0, 0, 1, 1, .5D), new AxisAlignedBB(.5D, 0, 0, 1, 1, 1)};

	public HamsterWheel(){
		super(Material.IRON);
		String name = "hamster_wheel";
		setTranslationKey(name);
		setHardness(2);
		setRegistryName(name);
		setCreativeTab(CRItems.TAB_CROSSROADS);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new HamsterWheelTileEntity();
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(EssentialsConfig.isWrench(playerIn.getHeldItem(hand), worldIn.isRemote)){
			if(!worldIn.isRemote){
				worldIn.setBlockState(pos, state.with(Properties.HORIZ_FACING, state.get(Properties.HORIZ_FACING).rotateY()));
			}
			return true;
		}
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos){
		return BB[Math.max(state.get(Properties.HORIZ_FACING).getHorizontalIndex(), 0)];
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, BlockRayTraceResult hit, int meta, LivingEntity placer, Hand hand){
		return getDefaultState().with(Properties.HORIZ_FACING, placer.getHorizontalFacing());
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, Properties.HORIZ_FACING);
	}

	@Override
	public BlockState getStateFromMeta(int meta){
		return getDefaultState().with(Properties.HORIZ_FACING, Direction.byIndex(meta));
	}

	@Override
	public int getMetaFromState(BlockState state){
		return state.get(Properties.HORIZ_FACING).getIndex();
	}

	@Override
	public void addCollisionBoxToList(BlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> list, @Nullable Entity entityIn, boolean p_185477_7_){
		addCollisionBoxToList(pos, entityBox, list, BB[Math.max(state.get(Properties.HORIZ_FACING).getHorizontalIndex(), 0)]);
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
	public boolean isSideSolid(BlockState base_state, IBlockAccess world, BlockPos pos, Direction side){
		return side == world.getBlockState(pos).get(Properties.HORIZ_FACING);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add("Produces: 2J/t");
		tooltip.add("Does it need batteries?");
	}
}
