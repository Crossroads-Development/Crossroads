package com.Da_Technomancer.crossroads;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.dimensions.ModDimensions;
import com.Da_Technomancer.crossroads.entity.ModEntities;
import com.Da_Technomancer.crossroads.fluids.ModFluids;
import com.Da_Technomancer.crossroads.gui.GuiHandler;
import com.Da_Technomancer.crossroads.integration.ModIntegration;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.crafting.ModCrafting;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.items.itemSets.HeatCableFactory;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import com.Da_Technomancer.crossroads.particles.ModParticles;
import com.Da_Technomancer.crossroads.tileentities.ModTileEntity;
import com.Da_Technomancer.crossroads.world.ModWorldGen;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber()
public class CommonProxy{

	protected static final ModWorldGen WORLD_GEN = new ModWorldGen();

	public static int masterKey = 0;

	protected void preInit(FMLPreInitializationEvent e){
		Capabilities.register();
		ModConfig.init(e);
		ModTileEntity.init();
		ModPackets.preInit();
		ModDimensions.init();
		ModEntities.init();
		ModParticles.init();
	}

	protected void init(FMLInitializationEvent e){
		NetworkRegistry.INSTANCE.registerGuiHandler(Main.instance, new GuiHandler());
		MinecraftForge.EVENT_BUS.register(new EventHandlerCommon());

		if(ModConfig.retrogen.getString().isEmpty()){
			GameRegistry.registerWorldGenerator(WORLD_GEN, 0);
		}
		ModIntegration.init();

		ModConfig.config.save();
	}

	protected void postInit(FMLPostInitializationEvent e){
		
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> e){
		IForgeRegistry<Block> registry = e.getRegistry();
		ModBlocks.init();
		HeatCableFactory.init();
		GearFactory.init();
		ModFluids.init();
		OreSetup.init();
		for(Block block : ModBlocks.toRegister){
			registry.register(block);
		}
		ModBlocks.toRegister.clear();
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> e){
		IForgeRegistry<Item> registry = e.getRegistry();
		ModItems.init();
		for(Item item : ModItems.toRegister){
			registry.register(item);
		}
		ModItems.toRegister.clear();
	}

	@SubscribeEvent
	public static void registerRecipes(RegistryEvent.Register<IRecipe> e){
		IForgeRegistry<IRecipe> registry = e.getRegistry();
		ModCrafting.init();
		for(IRecipe recipe : ModCrafting.toRegister){
			if(recipe.getRegistryName() == null){
				ResourceLocation rawLoc = new ResourceLocation(Main.MODID, recipe.getRecipeOutput().getItem().getRegistryName().getResourcePath());
				ResourceLocation adjusted = rawLoc;
				int i = 0;
				while(CraftingManager.REGISTRY.containsKey(adjusted)){
					adjusted = new ResourceLocation(Main.MODID, rawLoc.getResourcePath() + '_' + i);
					i++;
				}
				recipe.setRegistryName(adjusted);
			}
			registry.register(recipe);
		}
		ModCrafting.toRegister.clear();
		
		ModIntegration.preInit();
	}
}
