package com.Da_Technomancer.crossroads.blocks.rotary;

import java.util.List;

import com.Da_Technomancer.crossroads.CommonProxy;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.rotary.AxleTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class Axle extends BlockContainer{

	public Axle(){
		super(Material.IRON);
		String name = "axle";
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this).setRegistryName(name));
		this.setCreativeTab(ModItems.tabCrossroads);
		this.setHardness(3);
		setSoundType(SoundType.METAL);
		OreDictionary.registerOre("stickIron", this);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new AxleTileEntity();
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced){
		tooltip.add("Mass: 125");
		tooltip.add("I: .25");
	}
	
	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		CommonProxy.masterKey++;
		return this.getDefaultState().withProperty(Properties.AXIS, BlockPistonBase.getFacingFromEntity(pos, placer).getAxis());
	}
	
	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, new IProperty[] {Properties.AXIS});
	}
	
	@Override
	public int damageDropped(IBlockState state){
		return 0;
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState().withProperty(Properties.AXIS, EnumFacing.getFront(2 * meta).getAxis());
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn){
		if(worldIn.isRemote){
			return;
		}
		CommonProxy.masterKey++;
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(Properties.AXIS) == EnumFacing.Axis.Y ? 0 : (state.getValue(Properties.AXIS) == EnumFacing.Axis.Z ? 1 : 2);
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
		return side.getAxis() == world.getBlockState(pos).getValue(Properties.AXIS);
	}

	private static final AxisAlignedBB XBOX = new AxisAlignedBB(0, .4375D, .4375D, 1, .5625D, .5625D);
	private static final AxisAlignedBB YBOX = new AxisAlignedBB(.4375D, 0, .4375D, .5625D, 1, .5625D);
	private static final AxisAlignedBB ZBOX = new AxisAlignedBB(.4375D, .4375D, 0, .5625D, .5625D, 1);
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
		switch(state.getValue(Properties.AXIS)){
			case X:
				return XBOX;
			case Y:
				return YBOX;
			default:
				return ZBOX;
		}
	}
}