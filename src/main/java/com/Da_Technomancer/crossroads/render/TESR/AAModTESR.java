package com.Da_Technomancer.crossroads.render.TESR;

import ca.weblite.objc.Client;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.blocks.technomancy.CopshowiumCreationChamber;
import com.Da_Technomancer.crossroads.tileentities.RedstoneTransmitterTileEntity;
import com.Da_Technomancer.crossroads.tileentities.alchemy.TeslaCoilTopTileEntity;
import com.Da_Technomancer.crossroads.tileentities.beams.*;
import com.Da_Technomancer.crossroads.tileentities.fluid.RotaryPumpTileEntity;
import com.Da_Technomancer.crossroads.tileentities.heat.HeatingCrucibleTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.*;
import com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms.MechanismTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.*;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.client.registry.ClientRegistry;

/** Before anyone asks, the AA at the beginning of the name is so I can easily find it in the list of classes.*/
public class AAModTESR{

	public static void registerBlockRenderer(){

		ClientRegistry.bindTileEntitySpecialRenderer(MechanismTileEntity.class, new MechanismRenderer());
		reg(ModBlocks.rotaryPump);
		ClientRegistry.bindTileEntitySpecialRenderer(RotaryPumpTileEntity.class, new RotaryPumpRenderer());
		reg(ModBlocks.steamTurbine);
		ClientRegistry.bindTileEntitySpecialRenderer(SteamTurbineTileEntity.class, new SteamTurbineRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(LargeGearMasterTileEntity.class, new LargeGearRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(RotaryDrillTileEntity.class, new RotaryDrillRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(BeamExtractorTileEntity.class, new BeamRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(QuartzStabilizerTileEntity.class, new BeamRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(CrystallinePrismTileEntity.class, new BeamRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(BeamReflectorTileEntity.class, new BeamRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(LensFrameTileEntity.class, new LensFrameRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(BeamSiphonTileEntity.class, new BeamRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(BeamRedirectorTileEntity.class, new BeamRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(BeaconHarnessTileEntity.class, new BeaconHarnessRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(HamsterWheelTileEntity.class, new HamsterWheelRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(GatewayFrameTileEntity.class, new GatewayFrameRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(MechanicalArmTileEntity.class, new MechanicalArmRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(HeatingCrucibleTileEntity.class, new HeatingCrucibleRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(DynamoTileEntity.class, new DynamoRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(PrototypingTableTileEntity.class, new PrototypingTableRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(ClockworkStabilizerTileEntity.class, new BeamRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(PrototypeTileEntity.class, new BeamRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(WindTurbineTileEntity.class, new WindTurbineRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(StampMillTileEntity.class, new StampMillRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(FluxNodeTileEntity.class, new FluxNodeRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TeslaCoilTopTileEntity.class, new LinkLineRenderer<TeslaCoilTopTileEntity>());
		ClientRegistry.bindTileEntitySpecialRenderer(CopshowiumCreationChamberTileEntity.class, new LinkLineRenderer<CopshowiumCreationChamberTileEntity>());
		ClientRegistry.bindTileEntitySpecialRenderer(TemporalAcceleratorTileEntity.class, new LinkLineRenderer<TemporalAcceleratorTileEntity>());
		ClientRegistry.bindTileEntitySpecialRenderer(RedstoneTransmitterTileEntity.class, new LinkLineRenderer<RedstoneTransmitterTileEntity>());
	}

	private static void reg(Block block){
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName().toString(), "inventory"));
	}

}