package com.Da_Technomancer.crossroads.client.bakedModel;

import java.util.HashMap;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.enums.HeatInsulators;
import com.Da_Technomancer.crossroads.blocks.heat.HeatCable;
import com.Da_Technomancer.crossroads.items.itemSets.HeatCableFactory;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

public final class CustomModelLocationMapper {
	
	public static void preInit(){
		
		ModelResourceLocation cableModel = new ModelResourceLocation(Main.MODID + ":heatCable");
		
		for(HashMap<HeatInsulators, HeatCable> map: HeatCableFactory.cableMap.values()){
    		for(HeatCable cable: map.values()){
    			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(cable), 0, cableModel);
    		}
    	}
		
		
	}

}
