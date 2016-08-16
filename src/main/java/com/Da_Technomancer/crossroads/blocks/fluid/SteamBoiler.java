package com.Da_Technomancer.crossroads.blocks.fluid;

import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.fluid.SteamBoilerTileEntity;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class SteamBoiler extends BlockContainer{

	public SteamBoiler(){
		super(Material.IRON);
		setUnlocalizedName("steamBoiler");
		setRegistryName("steamBoiler");
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this).setRegistryName("steamBoiler"));
		this.setCreativeTab(ModItems.tabCrossroads);
		this.setHardness(3);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new SteamBoilerTileEntity();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}
}
