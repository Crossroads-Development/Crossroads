package com.Da_Technomancer.crossroads.blocks.heat;

import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.heat.HeatingCrucibleTileEntity;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class HeatingCrucible extends BlockContainer{

	public static final PropertyInteger PROPERTYFULLNESS = PropertyInteger.create("fullness", 0, 3);
	// public static final PropertyBool DISPLAYMOLTEN =
	// PropertyBool.create("molten");
	/**
	 * 0 = copper, 1 = molten copper, 2 = cobble, 3 = lava
	 * 
	 */
	public static final PropertyInteger TEXTURE = PropertyInteger.create("text", 0, 3);

	public HeatingCrucible(){
		super(Material.ROCK);
		setUnlocalizedName("heatingCrucible");
		setRegistryName("heatingCrucible");
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this).setRegistryName("heatingCrucible"));
		this.setCreativeTab(ModItems.tabCrossroads);
		this.setHardness(3);
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		return this.getDefaultState().withProperty(PROPERTYFULLNESS, 0).withProperty(TEXTURE, 0);
	}

	@Override
	public int damageDropped(IBlockState state){
		return 0;
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, new IProperty[] {PROPERTYFULLNESS, TEXTURE});
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return this.getDefaultState().withProperty(PROPERTYFULLNESS, meta & 3).withProperty(TEXTURE, (meta & 12) / 4);
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(PROPERTYFULLNESS) + (state.getValue(TEXTURE) * 4);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new HeatingCrucibleTileEntity();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState blockstate){
		HeatingCrucibleTileEntity te = (HeatingCrucibleTileEntity) world.getTileEntity(pos);
		InventoryHelper.dropInventoryItems(world, pos, te);
		super.breakBlock(world, pos, blockstate);
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
	public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side){
		return false;
	}
}
