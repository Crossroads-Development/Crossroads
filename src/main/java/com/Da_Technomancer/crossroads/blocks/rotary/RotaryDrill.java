package com.Da_Technomancer.crossroads.blocks.rotary;

import java.util.List;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.rotary.RotaryDrillTileEntity;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class RotaryDrill extends BlockContainer{

	private static final AxisAlignedBB X = new AxisAlignedBB(0, .375, .375, 1, .625, .625);
	private static final AxisAlignedBB Y = new AxisAlignedBB(.375, 0, .375, .625, 1, .625);
	private static final AxisAlignedBB Z = new AxisAlignedBB(.375, .375, 0, .625, .625, 1);
	
	public RotaryDrill(){
		super(Material.IRON);
		String name = "rotaryDrill";
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this).setRegistryName(name));
		setCreativeTab(ModItems.tabCrossroads);
		setHardness(3);
		setSoundType(SoundType.METAL);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new RotaryDrillTileEntity();
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		EnumFacing enumfacing = (placer == null) ? EnumFacing.NORTH : EnumFacing.getDirectionFromEntityLiving(pos, placer);
		return getDefaultState().withProperty(Properties.FACING, enumfacing);
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
		return state.getValue(Properties.FACING).getAxis() == Axis.X ? X : state.getValue(Properties.FACING).getAxis() == Axis.Z ? Z : Y;
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity, boolean somethingOrOtherProbably){
		addCollisionBoxToList(pos, mask, list, state.getValue(Properties.FACING).getAxis() == Axis.X ? X : state.getValue(Properties.FACING).getAxis() == Axis.Z ? Z : Y);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}

	@Override
	public boolean isSideSolid(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side){
		return side.getOpposite() == state.getValue(Properties.FACING);
	}

	@Override
	public boolean isFullCube(IBlockState state){
		return false;
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
		EnumFacing facing = EnumFacing.getFront(meta);
		return this.getDefaultState().withProperty(Properties.FACING, facing);
	}

	@Override
	public int getMetaFromState(IBlockState state){
		EnumFacing facing = state.getValue(Properties.FACING);
		int facingbits = facing.getIndex();
		return facingbits;
	}

}
