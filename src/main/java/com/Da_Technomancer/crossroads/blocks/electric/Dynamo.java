package com.Da_Technomancer.crossroads.blocks.electric;

import com.Da_Technomancer.crossroads.CrossroadsConfig;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CrossroadsItems;
import com.Da_Technomancer.crossroads.tileentities.electric.DynamoTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
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

public class Dynamo extends ContainerBlock{

	public Dynamo(){
		super(Material.IRON);
		String name = "dynamo";
		setTranslationKey(name);
		setRegistryName(name);
		setHardness(2);
		setCreativeTab(CrossroadsItems.TAB_CROSSROADS);
		setSoundType(SoundType.METAL);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
	}

	private static final AxisAlignedBB[] BB = new AxisAlignedBB[] {new AxisAlignedBB(0, 0D, 0.25D, 1, 0.5D, 0.75D), new AxisAlignedBB(0.25D, 0D, 0, 0.75D, 0.5D, 1)};

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new DynamoTileEntity();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}
	
	@Override
	public boolean isOpaqueCube(BlockState state){
		return false;
	}
	
	@Override
	public boolean isFullCube(BlockState state){
		return false;
	}
	
	@Override
	public boolean isSideSolid(BlockState base_state, IBlockAccess world, BlockPos pos, Direction side){
		return side == world.getBlockState(pos).get(Properties.HORIZ_FACING);
	}
	
	@Override
	public BlockState getStateForPlacement(World worldIn, BlockPos pos, Direction blockFaceClickedOn, BlockRayTraceResult hit, int meta, LivingEntity placer){
		return getDefaultState().with(Properties.HORIZ_FACING, placer == null ? Direction.EAST : placer.getHorizontalFacing());
	}

	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos){
		return BB[state.get(Properties.HORIZ_FACING).getAxis() == Direction.Axis.X ? 0 : 1];
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(EssentialsConfig.isWrench(playerIn.getHeldItem(hand), worldIn.isRemote)){
			if(!worldIn.isRemote){
				worldIn.setBlockState(pos, state.cycle(Properties.HORIZ_FACING));
			}
			return true;
		}
		return false;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add("I: 200");
		tooltip.add("Produces: " + CrossroadsConfig.electPerJoule.get() + "FE/J");
		tooltip.add("Consumes: 100*(speed^2) J/t");
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, Properties.HORIZ_FACING);
	}

	@Override
	public BlockState getStateFromMeta(int meta){
		return getDefaultState().with(Properties.HORIZ_FACING, Direction.byHorizontalIndex(meta));
	}

	@Override
	public int getMetaFromState(BlockState state){
		return state.get(Properties.HORIZ_FACING).getHorizontalIndex();
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face){
		if(face == state.get(Properties.HORIZ_FACING)){
			return BlockFaceShape.CENTER_SMALL;
		}

		return BlockFaceShape.UNDEFINED;
	}
}
