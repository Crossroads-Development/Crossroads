package com.Da_Technomancer.crossroads.blocks.alchemy;

import org.apache.commons.lang3.tuple.Pair;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.alchemy.FluidInjectorTileEntity;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FluidInjector extends BlockContainer{

	private static final AxisAlignedBB BB = new AxisAlignedBB(.25D, 0, .25D, .75D, 1, .75D);

	public FluidInjector(){
		super(Material.GLASS);
		String name = "fluid_injector";
		setUnlocalizedName(name);
		setRegistryName(name);
		setHardness(.5F);
		setCreativeTab(ModItems.tabCrossroads);
		setSoundType(SoundType.GLASS);
		ModBlocks.toRegister.add(this);
		Item item = new ItemBlock(this){
			@Override
			public String getUnlocalizedName(ItemStack stack){
				return stack.getMetadata() == 1 ? "tile.fluid_injector_cryst" : "tile.fluid_injector_glass";
			}
			
			@Override
			public int getMetadata(int damage){
				return damage;
			}
		}.setMaxDamage(0).setHasSubtypes(true);
		item.setRegistryName(name);
		ModItems.toRegister.add(item);
		ModItems.toClientRegister.put(Pair.of(item, 0), new ModelResourceLocation(Main.MODID + ":fluid_injector_glass", "inventory"));
		ModItems.toClientRegister.put(Pair.of(item, 1), new ModelResourceLocation(Main.MODID + ":fluid_injector_cryst", "inventory"));
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new FluidInjectorTileEntity((meta & 1) == 0);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer(){
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return (state.getValue(Properties.LIGHT) ? 1 : 0);
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState().withProperty(Properties.LIGHT, (meta & 1) == 1);
	}

	@Override
	public int damageDropped(IBlockState state){
		return state.getValue(Properties.LIGHT) ? 1 : 0;
	}

	@Override
	protected BlockStateContainer createBlockState(){
		//On this device, light is being re-used. True means crystal, false means glass. 
		return new BlockStateContainer(this, new IProperty[] {Properties.LIGHT});
	}

	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
		return BB;
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		return getStateFromMeta(meta);
	}
	
	@Override
	public boolean isFullCube(IBlockState state){
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list){
		list.add(new ItemStack(this, 1, 0));
		list.add(new ItemStack(this, 1, 1));
	}
}
