package com.Da_Technomancer.crossroads.blocks.heat;

import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.heat.FluidCoolingChamberTileEntity;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.CapabilityItemHandler;

public class FluidCoolingChamber extends BlockContainer{

	public FluidCoolingChamber(){
		super(Material.IRON);
		String name = "fluidCoolingChamber";
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this).setRegistryName(name));
		this.setCreativeTab(ModItems.tabCrossroads);
		this.setHardness(3);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new FluidCoolingChamberTileEntity();
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState blockstate){
		ItemStack stack = world.getTileEntity(pos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).getStackInSlot(0);
		if(stack != null){
			InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
		}
		super.breakBlock(world, pos, blockstate);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}
}
