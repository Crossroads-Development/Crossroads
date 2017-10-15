package com.Da_Technomancer.crossroads.items.alchemy;

import org.apache.commons.lang3.tuple.Pair;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.items.ModItems;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;

public class FlorenceFlask extends AbstractPhial{

	private static final ModelResourceLocation LOCAT_GLASS = new ModelResourceLocation(Main.MODID + ":florence_flask_glass", "inventory");
	private static final ModelResourceLocation LOCAT_CRYSTAL = new ModelResourceLocation(Main.MODID + ":florence_flask_crystal", "inventory");
	
	public FlorenceFlask(){
		String name = "florence_flask";
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
}
