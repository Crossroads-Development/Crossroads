package com.Da_Technomancer.crossroads.blocks;

import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.crafting.ModCrafting;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import org.apache.commons.lang3.tuple.Pair;

public class BasicBlock extends Block{

	public BasicBlock(String unlocName, Material mat){
		this(unlocName, mat, 1.5F);
	}

	public BasicBlock(String unlocName, Material mat, float hardness){
		this(unlocName, mat, hardness, null);
	}
	
	public BasicBlock(String unlocName, Material mat, float hardness, String oreDict){
		super(mat);
		setUnlocalizedName(unlocName);
		setRegistryName(unlocName);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setHardness(hardness);
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this);
		if(oreDict != null){
			ModCrafting.toRegisterOreDict.add(Pair.of(this, new String[] {oreDict}));
		}
	}
}
