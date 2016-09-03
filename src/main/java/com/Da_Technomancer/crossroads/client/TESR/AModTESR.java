package com.Da_Technomancer.crossroads.client.TESR;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.enums.GearTypes;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.tileentities.fluid.RotaryPumpTileEntity;
import com.Da_Technomancer.crossroads.tileentities.fluid.SteamTurbineTileEntity;
import com.Da_Technomancer.crossroads.tileentities.magic.ArcaneExtractorTileEntity;
import com.Da_Technomancer.crossroads.tileentities.magic.ArcaneReflectorTileEntity;
import com.Da_Technomancer.crossroads.tileentities.magic.CrystallinePrismTileEntity;
import com.Da_Technomancer.crossroads.tileentities.magic.LensHolderTileEntity;
import com.Da_Technomancer.crossroads.tileentities.magic.QuartzStabilizerTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearMasterTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.RotaryDrillTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.SidedGearHolderTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.ToggleGearTileEntity;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.client.registry.ClientRegistry;

/** Before anyone asks, the A at the beginning of the name is so I can easily find it in the list of classes.*/
public class AModTESR{

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
		
		reg(ModBlocks.arcaneExtractor);
		ClientRegistry.bindTileEntitySpecialRenderer(ArcaneExtractorTileEntity.class, new BeamRenderer());
		
		reg(ModBlocks.smallQuartzStabilizer);
		reg(ModBlocks.largeQuartzStabilizer);
		ClientRegistry.bindTileEntitySpecialRenderer(QuartzStabilizerTileEntity.class, new BeamRenderer());
		
		reg(ModBlocks.crystallinePrism);
		ClientRegistry.bindTileEntitySpecialRenderer(CrystallinePrismTileEntity.class, new BeamRenderer());
		
		reg(ModBlocks.arcaneReflector);
		ClientRegistry.bindTileEntitySpecialRenderer(ArcaneReflectorTileEntity.class, new BeamRenderer());
		
		reg(ModBlocks.lensHolder);
		ClientRegistry.bindTileEntitySpecialRenderer(LensHolderTileEntity.class, new BeamRenderer());
	}

	public static void reg(Block block){
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), 0, new ModelResourceLocation(Main.MODID + ":" + block.getUnlocalizedName().substring(5), "inventory"));
	}

}