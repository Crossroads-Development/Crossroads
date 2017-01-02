package com.Da_Technomancer.crossroads.blocks;

import com.Da_Technomancer.crossroads.items.ModItems;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

public class BasicBlock extends Block{
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
		this(unlocName, mat, mineLevel, tool, hardness, null);
	}

	public BasicBlock(String unlocName, Material mat, int mineLevel, String tool, float hardness, SoundType sound){
		this(unlocName, mat, mineLevel, tool, hardness, sound, null);
	}
	
	public BasicBlock(String unlocName, Material mat, int mineLevel, String tool, float hardness, SoundType sound, String oreDict){
		super(mat);
		if(tool != null && mineLevel != -2){
			setHarvestLevel(tool, mineLevel);
		}
		setUnlocalizedName(unlocName);
		setRegistryName(unlocName);
		this.setCreativeTab(ModItems.tabCrossroads);
		this.setHardness(hardness);
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this).setRegistryName(unlocName));
		if(sound != null){
			setSoundType(sound);
		}
		if(oreDict != null){
			OreDictionary.registerOre(oreDict, this);
		}
		ModBlocks.blockAddQue(this);
	}

	public BasicBlock setSoundType(SoundType sound){
		return (BasicBlock) super.setSoundType(sound);
	}
}
