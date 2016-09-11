package com.Da_Technomancer.crossroads.blocks;

import com.Da_Technomancer.crossroads.items.ModItems;

import net.minecraft.block.BlockBreakable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class GlowGlass extends BlockBreakable{

	protected GlowGlass(){
		super(Material.GLASS, false);
		setSoundType(SoundType.GLASS);
		String name = "glowGlass";
		setUnlocalizedName(name);
		setRegistryName(name);
		this.setCreativeTab(ModItems.tabCrossroads);
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this).setRegistryName(name));
		OreDictionary.registerOre("glass", this);
	}
	
	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos){
		return 15;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer(){
		return BlockRenderLayer.CUTOUT;
	}
}
