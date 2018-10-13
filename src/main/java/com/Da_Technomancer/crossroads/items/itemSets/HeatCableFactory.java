package com.Da_Technomancer.crossroads.items.itemSets;

import java.util.HashMap;

import com.Da_Technomancer.crossroads.API.heat.HeatInsulators;
import com.Da_Technomancer.crossroads.blocks.heat.HeatCable;
import com.Da_Technomancer.crossroads.blocks.heat.RedstoneHeatCable;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

public final class HeatCableFactory{

	public static final HashMap<HeatInsulators, HeatCable> HEAT_CABLES = new HashMap<HeatInsulators, HeatCable>();
	public static final HashMap<HeatInsulators, RedstoneHeatCable> REDSTONE_HEAT_CABLES = new HashMap<HeatInsulators, RedstoneHeatCable>();

	public static void init(){
		HEAT_CABLES.clear();
		REDSTONE_HEAT_CABLES.clear();
		for(HeatInsulators insul : HeatInsulators.values()){
			HEAT_CABLES.put(insul, new HeatCable(insul));
			REDSTONE_HEAT_CABLES.put(insul, new RedstoneHeatCable(insul));
		}
	}

	public static void clientInit(){
		for(HeatCable cable : HeatCableFactory.HEAT_CABLES.values()){
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(cable), 0, new ModelResourceLocation(cable.getTexture().toString().replaceFirst("blocks/", "").replaceFirst("-copper", "")));
		}

		for(RedstoneHeatCable cable : HeatCableFactory.REDSTONE_HEAT_CABLES.values()){
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(cable), 0, new ModelResourceLocation(cable.getTexture().toString().replaceFirst("blocks/", "").replaceFirst("-copper", "")));
		}
	}
}
