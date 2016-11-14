package com.Da_Technomancer.crossroads.blocks;

import java.util.List;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.BrazierTileEntity;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

public class BlockBrazier extends BlockContainer{

	private static final AxisAlignedBB BB = new AxisAlignedBB(0, 0, 0, 1, .875D, 1);

	protected BlockBrazier(){
		super(Material.IRON);
		String name = "brazier";
		setUnlocalizedName(name);
		setHardness(2);
		setRegistryName(name);
		this.setCreativeTab(ModItems.tabCrossroads);
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this).setRegistryName(name));
		OreDictionary.registerOre(name, this);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new BrazierTileEntity();
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
		return BB;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos){
		IBlockState other = world.getBlockState(pos);
		if(other.getBlock() != this){
			return other.getLightValue(world, pos);
		}
		return state.getValue(Properties.LIGHT) ? 15 : 0;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ){
		if(!worldIn.isRemote && heldItem != null){
			heldItem = ((BrazierTileEntity) worldIn.getTileEntity(pos)).addFuel(heldItem);
		}

		return false;
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		return this.getDefaultState().withProperty(Properties.LIGHT, false);
	}

	@Override
	public int damageDropped(IBlockState state){
		return 0;
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, new IProperty[] {Properties.LIGHT});
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return this.getDefaultState().withProperty(Properties.LIGHT, meta == 1);
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(Properties.LIGHT) ? 1 : 0;
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity){
		addCollisionBoxToList(pos, mask, list, BB);
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
	public void breakBlock(World world, BlockPos pos, IBlockState blockstate){
		BrazierTileEntity te = (BrazierTileEntity) world.getTileEntity(pos);
		if(te.getStackInSlot(0) != null && te.getStackInSlot(0).stackSize > 1){
			ItemStack stack = te.getStackInSlot(0);
			--stack.stackSize;
			InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
		}
		super.breakBlock(world, pos, blockstate);
	}

	@Override
	public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side){
		return false;
	}

}
