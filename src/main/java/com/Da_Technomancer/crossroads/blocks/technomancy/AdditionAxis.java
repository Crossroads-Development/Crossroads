package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.technomancy.AdditionAxisTileEntity;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class AdditionAxis extends BlockContainer{
	
	public AdditionAxis(){
		super(Material.IRON);
		String name = "additionAxis";
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this).setRegistryName(name));
		setCreativeTab(ModItems.tabCrossroads);
		setHardness(3);
		setSoundType(SoundType.METAL);
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		return this.getDefaultState().withProperty(Properties.ORIENT, (placer == null) ? false : placer.getHorizontalFacing().getAxis() == EnumFacing.Axis.Z);
	}

	@Override
	public int damageDropped(IBlockState state){
		return 0;
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, new IProperty[] {Properties.ORIENT});
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return this.getDefaultState().withProperty(Properties.ORIENT, meta == 1);
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(Properties.ORIENT) ? 1 : 0;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new AdditionAxisTileEntity(meta == 1 ? Axis.Z : Axis.X);

	}
	
	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos){
		return true;
	}
	
	@Override
	public boolean isFullCube(IBlockState state){
		return false;
	}
	
	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side){
		return side == EnumFacing.UP || side == EnumFacing.DOWN || side.getAxis() == (world.getBlockState(pos).getValue(Properties.ORIENT) ? EnumFacing.Axis.Z : EnumFacing.Axis.X);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}

}