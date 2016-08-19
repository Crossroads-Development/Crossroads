package com.Da_Technomancer.crossroads.client.TESR;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.enums.GearTypes;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.tileentities.fluid.RotaryPumpTileEntity;
import com.Da_Technomancer.crossroads.tileentities.fluid.SteamTurbineTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearMasterTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.RotaryDrillTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.SidedGearHolderTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.ToggleGearTileEntity;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class RegisterBlockRenderer{

	public static void registerBlockRenderer(){

		reg(ModBlocks.sidedGearHolder);
		ClientRegistry.bindTileEntitySpecialRenderer(SidedGearHolderTileEntity.class, new SidedGearHolderRenderer());

		reg(ModBlocks.rotaryPump);
		ClientRegistry.bindTileEntitySpecialRenderer(RotaryPumpTileEntity.class, new RotaryPumpRenderer());

		reg(ModBlocks.steamTurbine);
		ClientRegistry.bindTileEntitySpecialRenderer(SteamTurbineTileEntity.class, new SteamTurbineRenderer());

		reg(ModBlocks.largeGearMaster);
		ClientRegistry.bindTileEntitySpecialRenderer(LargeGearMasterTileEntity.class, new LargeGearRenderer());

		reg(ModBlocks.rotaryDrill);
		ClientRegistry.bindTileEntitySpecialRenderer(RotaryDrillTileEntity.class, new RotaryDrillRenderer());
		
		for(GearTypes typ : GearTypes.values()){
			reg(GearFactory.toggleGears.get(typ));
		}
		ClientRegistry.bindTileEntitySpecialRenderer(ToggleGearTileEntity.class, new ToggleGearRenderer());
	}

	public static void reg(Block block){
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), 0, new ModelResourceLocation(Main.MODID + ":" + block.getUnlocalizedName().substring(5), "inventory"));
	}

}