package com.Da_Technomancer.crossroads.items.itemSets;

import java.util.HashMap;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.enums.HeatConductors;
import com.Da_Technomancer.crossroads.API.enums.HeatInsulators;
import com.Da_Technomancer.crossroads.blocks.heat.HeatCable;
import com.Da_Technomancer.crossroads.blocks.heat.RedstoneHeatCable;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

public final class HeatCableFactory{

	public static HashMap<HeatConductors, HashMap<HeatInsulators, HeatCable>> cableMap = new HashMap<HeatConductors, HashMap<HeatInsulators, HeatCable>>();
	public static HashMap<HeatConductors, HashMap<HeatInsulators, RedstoneHeatCable>> rCableMap = new HashMap<HeatConductors, HashMap<HeatInsulators, RedstoneHeatCable>>();

	public static void init(){
		for(HeatConductors cond : HeatConductors.values()){
			HashMap<HeatInsulators, HeatCable> map = new HashMap<HeatInsulators, HeatCable>();
			HashMap<HeatInsulators, RedstoneHeatCable> rMap = new HashMap<HeatInsulators, RedstoneHeatCable>();
			for(HeatInsulators insul : HeatInsulators.values()){
				map.put(insul, new HeatCable(cond, insul));
				rMap.put(insul, new RedstoneHeatCable(cond, insul));
			}
			cableMap.put(cond, map);
			rCableMap.put(cond, rMap);
		}
	}
	
	public static void clientInit(){
		ModelResourceLocation cableModel = new ModelResourceLocation(Main.MODID + ":heatCable");
		for(HashMap<HeatInsulators, HeatCable> map : HeatCableFactory.cableMap.values()){
			for(HeatCable cable : map.values()){
				ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(cable), 0, cableModel);
			}
		}
		
		for(HashMap<HeatInsulators, RedstoneHeatCable> map : HeatCableFactory.rCableMap.values()){
			for(RedstoneHeatCable cable : map.values()){
				ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(cable), 0, cableModel);
			}
		}
	}
}
