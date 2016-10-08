package com.Da_Technomancer.crossroads;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.fluids.ModFluids;
import com.Da_Technomancer.crossroads.gui.GuiHandler;
import com.Da_Technomancer.crossroads.integration.ModIntegration;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.crafting.ModCrafting;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.items.itemSets.HeatCableFactory;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetUp;
import com.Da_Technomancer.crossroads.tileentities.ModTileEntity;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy{

	protected void preInit(FMLPreInitializationEvent e){
		Capabilities.register();
		ModConfig.init(e);
		OreSetUp.init();
		ModBlocks.init();
		ModItems.init();
		ModFluids.init();
		GearFactory.init();
		HeatCableFactory.init();
		ModTileEntity.init();
		ModPackets.preInit();
	}

	protected void init(FMLInitializationEvent e){
		ModCrafting.initCrafting();
		NetworkRegistry.INSTANCE.registerGuiHandler(Main.instance, new GuiHandler());
		MinecraftForge.EVENT_BUS.register(new EventHandlerCommon());

		ModIntegration.init();

		ModConfig.config.save();
	}

	protected void postInit(FMLPostInitializationEvent e){

	}
}
