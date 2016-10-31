package com.Da_Technomancer.crossroads.blocks.magic;

import java.util.List;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.magic.BeamRenderTE;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.magic.LensHolderTileEntity;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LensHolder extends BlockContainer{

	public LensHolder(){
		super(Material.ROCK);
		String name = "lensHolder";
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this).setRegistryName(name));
		this.setCreativeTab(ModItems.tabCrossroads);
		this.setHardness(3);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new LensHolderTileEntity();
	}
	
	@Override
	public boolean hasComparatorInputOverride(IBlockState state){
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos){
		return ((LensHolderTileEntity) worldIn.getTileEntity(pos)).getRedstone();
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer(){
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isFullCube(IBlockState state){
		return false;
	}
	
	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		return this.getDefaultState().withProperty(Properties.ORIENT, (placer == null) ? true : placer.getHorizontalFacing().getAxis() == Axis.X).withProperty(Properties.TEXTURE_6, 0);
	}

	@Override
	public int damageDropped(IBlockState state){
		return 0;
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, new IProperty[] {Properties.ORIENT, Properties.TEXTURE_6});
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return this.getDefaultState().withProperty(Properties.ORIENT, (meta & 1) == 1).withProperty(Properties.TEXTURE_6, (meta & 14) >> 1);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ){
		if(!worldIn.isRemote){
			if(stack == null && state.getValue(Properties.TEXTURE_6) != 0){
				int i = state.getValue(Properties.TEXTURE_6);
				playerIn.setHeldItem(hand, new ItemStack(i == 1 ? Item.getByNameOrId(Main.MODID + ":gemRuby") : i == 2 ? Items.EMERALD : i == 3 ? Items.DIAMOND : i == 4 ? ModItems.pureQuartz : ModItems.luminescentQuartz, 1));
				worldIn.setBlockState(pos, getDefaultState().withProperty(Properties.ORIENT, worldIn.getBlockState(pos).getValue(Properties.ORIENT)).withProperty(Properties.TEXTURE_6, 0));
			}else if(stack != null && state.getValue(Properties.TEXTURE_6) == 0){
				int i = stack.getItem() == Items.DIAMOND ? 3 : stack.getItem() == Items.EMERALD ? 2 : stack.getItem() == ModItems.pureQuartz ? 4 : stack.getItem() == Item.getByNameOrId(Main.MODID + ":gemRuby") ? 1 : 0;
				worldIn.setBlockState(pos, getDefaultState().withProperty(Properties.ORIENT, worldIn.getBlockState(pos).getValue(Properties.ORIENT)).withProperty(Properties.TEXTURE_6, i));
				if(i != 0 && --stack.stackSize <= 0){
					playerIn.setHeldItem(hand, null);
				}
			}
		}
		return true;
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state){
		if(state.getValue(Properties.TEXTURE_6) != 0){
			int i = state.getValue(Properties.TEXTURE_6);
			InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(i == 1 ? Item.getByNameOrId(Main.MODID + ":gemRuby") : i == 2 ? Items.EMERALD : i == 3 ? Items.DIAMOND : i == 4 ? ModItems.pureQuartz : ModItems.luminescentQuartz, 1));
		}
		if(world.getTileEntity(pos) instanceof BeamRenderTE){
			((BeamRenderTE) world.getTileEntity(pos)).refresh();
		}
		super.breakBlock(world, pos, state);
	}
	
	@Override
	public int getMetaFromState(IBlockState state){
		return (state.getValue(Properties.ORIENT) ? 1 : 0) + (state.getValue(Properties.TEXTURE_6) << 1);
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}
	
	private static final AxisAlignedBB BB = new AxisAlignedBB(0, 0, .375D, 1, 1, .625D);
	private static final AxisAlignedBB BBA = new AxisAlignedBB(.375D, 0, 0, .625D, 1, 1);
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
		return state.getValue(Properties.ORIENT) ? BBA : BB;
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity){
		addCollisionBoxToList(pos, mask, list, state.getValue(Properties.ORIENT) ? BBA : BB);
	}
}
