package com.Da_Technomancer.crossroads.blocks.fluid;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.fluid.WaterCentrifugeTileEntity;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.CapabilityItemHandler;

public class WaterCentrifuge extends BlockContainer{
	
	public WaterCentrifuge(){
		super(Material.IRON);
		String name = "water_centrifuge";
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this).setRegistryName(name));
		this.setCreativeTab(ModItems.tabCrossroads);
		this.setHardness(3);
		setSoundType(SoundType.METAL);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new WaterCentrifugeTileEntity();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(Properties.ORIENT) ? 1 : 0;
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState().withProperty(Properties.ORIENT, meta == 1);
	}
	
	@Override
	public BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, new IProperty[] {Properties.ORIENT});
	}
	
	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		return getDefaultState().withProperty(Properties.ORIENT, (placer == null) ? true : placer.getHorizontalFacing().getAxis() == Axis.X);
	}

	@Override
	public int damageDropped(IBlockState state){
		return 0;
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState blockstate){
		if(world.getTileEntity(pos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).getStackInSlot(0) != null){
			InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), world.getTileEntity(pos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).getStackInSlot(0));
		}
		super.breakBlock(world, pos, blockstate);
	}
}
