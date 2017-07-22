package com.Da_Technomancer.crossroads.blocks;

import org.apache.commons.lang3.tuple.Pair;

import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.crafting.ModCrafting;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

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
		setCreativeTab(ModItems.tabCrossroads);
		setHardness(hardness);
		if(sound != null){
			setSoundType(sound);
		}
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this);
		if(oreDict != null){
			ModCrafting.toRegisterOreDict.add(Pair.of(this, new String[] {oreDict}));
		}
	}
}
