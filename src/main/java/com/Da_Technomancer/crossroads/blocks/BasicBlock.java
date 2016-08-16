package com.Da_Technomancer.crossroads.blocks;

import java.util.Random;

import com.Da_Technomancer.crossroads.items.ModItems;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

public class BasicBlock extends Block{

	private Item drop;
	private Boolean allowFort;
	private int dropCount;

	// This class can define any block without any special properties or
	// TileEntity attached.

	public BasicBlock(String unlocName){
		this(unlocName, Material.IRON);
	}

	public BasicBlock(String unlocName, Material mat){
		this(unlocName, mat, -2, (String) null);
	}

	public BasicBlock(String unlocName, Material mat, int mineLevel, String tool){
		this(unlocName, mat, mineLevel, tool, 1.5F);
	}

	public BasicBlock(String unlocName, Material mat, int mineLevel, String tool, float hardness){
		this(unlocName, mat, mineLevel, tool, hardness, (Item) null);
	}

	public BasicBlock(String unlocName, Material mat, int mineLevel, String tool, float hardness, Item drop){
		this(unlocName, mat, mineLevel, tool, hardness, drop, (String) null);
	}

	public BasicBlock(String unlocName, Material mat, int mineLevel, String tool, float hardness, Item drop, String oreDict){
		this(unlocName, mat, mineLevel, tool, hardness, drop, oreDict, false);
	}

	public BasicBlock(String unlocName, Material mat, int mineLevel, String tool, float hardness, Item drop, String oreDict, Boolean allowFort){
		this(unlocName, mat, mineLevel, tool, hardness, drop, oreDict, false, 1);
	}

	public BasicBlock(String unlocName, Material mat, int mineLevel, String tool, float hardness, Item drop, String oreDict, Boolean allowFort, int dropCount){
		super(mat);
		if(tool != null && mineLevel != -2)
			this.setHarvestLevel(tool, mineLevel);
		this.drop = drop;
		this.dropCount = dropCount;
		this.allowFort = allowFort;
		setUnlocalizedName(unlocName);
		setRegistryName(unlocName);
		this.setCreativeTab(ModItems.tabCrossroads);
		this.setHardness(hardness);
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this).setRegistryName(unlocName));
		if(oreDict != null)
			OreDictionary.registerOre(oreDict, this);
		ModBlocks.blockAddQue(this);
	}

	@Override
	public Item getItemDropped(IBlockState blockstate, Random random, int fortune){
		return (drop != null) ? this.drop : Item.getItemFromBlock(this);
	}

	@Override
	public int quantityDropped(IBlockState blockstate, int fortune, Random random){
		return (allowFort) ? (dropCount + random.nextInt(fortune + 1)) : dropCount;
	}

}
