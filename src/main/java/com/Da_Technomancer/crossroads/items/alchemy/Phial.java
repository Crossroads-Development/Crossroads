package com.Da_Technomancer.crossroads.items.alchemy;

import org.apache.commons.lang3.tuple.Pair;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.items.ModItems;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Phial extends AbstractPhial{

	private static final ModelResourceLocation LOCAT_GLASS = new ModelResourceLocation(Main.MODID + ":phial_glass", "inventory");
	private static final ModelResourceLocation LOCAT_CRYSTAL = new ModelResourceLocation(Main.MODID + ":phial_crystal", "inventory");
	
	public Phial(){
		String name = "phial";
		maxStackSize = 1;
		hasSubtypes = true;
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModItems.tabCrossroads);
		ModItems.toRegister.add(this);
		ModItems.toClientRegister.put(Pair.of((Item) this, 0), LOCAT_GLASS);
		ModItems.toClientRegister.put(Pair.of((Item) this, 1), LOCAT_CRYSTAL);
	}

	@Override
	public double getCapacity(){
		return 25D;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack){
		return stack.getMetadata() == 1 ? "item.phial_cryst" : "item.phial_glass";
	}
}
