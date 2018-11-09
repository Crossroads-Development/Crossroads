package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.technomancy.HamsterWheelTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class HamsterWheel extends BlockContainer{

	private static final AxisAlignedBB[] BB = new AxisAlignedBB[]{new AxisAlignedBB(0, 0, .5D, 1, 1, 1), new AxisAlignedBB(0, 0, 0, .5D, 1, 1), new AxisAlignedBB(0, 0, 0, 1, 1, .5D), new AxisAlignedBB(.5D, 0, 0, 1, 1, 1)};

	public HamsterWheel(){
		super(Material.IRON);
		String name = "hamster_wheel";
		setUnlocalizedName(name);
		setHardness(2);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new HamsterWheelTileEntity();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(EssentialsConfig.isWrench(playerIn.getHeldItem(hand), worldIn.isRemote)){
			if(!worldIn.isRemote){
				worldIn.setBlockState(pos, state.withProperty(Properties.HORIZ_FACING, state.getValue(Properties.HORIZ_FACING).rotateY()));
			}
			return true;
		}
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
		return BB[Math.max(state.getValue(Properties.HORIZ_FACING).getHorizontalIndex(), 0)];
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand){
		return getDefaultState().withProperty(Properties.HORIZ_FACING, placer.getHorizontalFacing());
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, Properties.HORIZ_FACING);
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState().withProperty(Properties.HORIZ_FACING, EnumFacing.getFront(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(Properties.HORIZ_FACING).getIndex();
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> list, @Nullable Entity entityIn, boolean p_185477_7_){
		addCollisionBoxToList(pos, entityBox, list, BB[Math.max(state.getValue(Properties.HORIZ_FACING).getHorizontalIndex(), 0)]);
	}

	@Override
	public boolean isFullCube(IBlockState state){
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side){
		return side == world.getBlockState(pos).getValue(Properties.HORIZ_FACING);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		tooltip.add("Produces: 2J/t");
		tooltip.add("Does it need batteries?");
	}
}
