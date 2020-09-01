package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.beams.*;
import com.Da_Technomancer.crossroads.tileentities.electric.DynamoTileEntity;
import com.Da_Technomancer.crossroads.tileentities.electric.TeslaCoilTopTileEntity;
import com.Da_Technomancer.crossroads.tileentities.fluid.RotaryPumpTileEntity;
import com.Da_Technomancer.crossroads.tileentities.heat.HeatingCrucibleTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.*;
import com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms.MechanismTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.*;
import com.Da_Technomancer.essentials.render.LinkLineRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class CRRendererRegistry{

	public static void registerBlockRenderer(){

		ClientRegistry.bindTileEntityRenderer(MechanismTileEntity.type, MechanismRenderer::new);
		reg(CRBlocks.rotaryPump);
		ClientRegistry.bindTileEntityRenderer(RotaryPumpTileEntity.type, RotaryPumpRenderer::new);
		reg(CRBlocks.steamTurbine);
		ClientRegistry.bindTileEntityRenderer(SteamTurbineTileEntity.type, SteamTurbineRenderer::new);
		ClientRegistry.bindTileEntityRenderer(LargeGearMasterTileEntity.teType, LargeGearRenderer::new);
		ClientRegistry.bindTileEntityRenderer(RotaryDrillTileEntity.type, RotaryDrillRenderer::new);
		ClientRegistry.bindTileEntityRenderer(BeamExtractorTileEntity.type, BeamRenderer::new);
		ClientRegistry.bindTileEntityRenderer(QuartzStabilizerTileEntity.type, BeamRenderer::new);
		ClientRegistry.bindTileEntityRenderer(CrystallinePrismTileEntity.type, BeamRenderer::new);
		ClientRegistry.bindTileEntityRenderer(BeamReflectorTileEntity.type, BeamRenderer::new);
		ClientRegistry.bindTileEntityRenderer(LensFrameTileEntity.type, LensFrameRenderer::new);
		ClientRegistry.bindTileEntityRenderer(BeamSiphonTileEntity.type, BeamRenderer::new);
		ClientRegistry.bindTileEntityRenderer(BeamRedirectorTileEntity.type, BeamRenderer::new);
		ClientRegistry.bindTileEntityRenderer(BeamSplitterTileEntity.type, BeamRenderer::new);
		ClientRegistry.bindTileEntityRenderer(BeaconHarnessTileEntity.type, BeaconHarnessRenderer::new);
		ClientRegistry.bindTileEntityRenderer(HamsterWheelTileEntity.type, HamsterWheelRenderer::new);
		ClientRegistry.bindTileEntityRenderer(GatewayFrameTileEntity.type, GatewayFrameRenderer::new);
//		ClientRegistry.bindTileEntityRenderer(MechanicalArmTileEntity.type, MechanicalArmRenderer::new);
		ClientRegistry.bindTileEntityRenderer(HeatingCrucibleTileEntity.type, HeatingCrucibleRenderer::new);
		ClientRegistry.bindTileEntityRenderer(DynamoTileEntity.type, DynamoRenderer::new);
		ClientRegistry.bindTileEntityRenderer(LodestoneDynamoTileEntity.type, DynamoRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ClockworkStabilizerTileEntity.type, BeamRenderer::new);
		ClientRegistry.bindTileEntityRenderer(WindTurbineTileEntity.type, WindTurbineRenderer::new);
		ClientRegistry.bindTileEntityRenderer(StampMillTileEntity.type, StampMillRenderer::new);
		ClientRegistry.bindTileEntityRenderer(FluxNodeTileEntity.type, FluxNodeRenderer::new);
		ClientRegistry.bindTileEntityRenderer(TeslaCoilTopTileEntity.type, LinkLineRenderer::new);
//		ClientRegistry.bindTileEntityRenderer(RedstoneTransmitterTileEntity.type, LinkLineRenderer<RedstoneTransmitterTileEntity>::new);
		//Flux machines (all have link lines)
		ClientRegistry.bindTileEntityRenderer(ChronoHarnessTileEntity.type, ChronoHarnessRenderer::new);
		ClientRegistry.bindTileEntityRenderer(TemporalAcceleratorTileEntity.type, TemporalAcceleratorRenderer::new);
		ClientRegistry.bindTileEntityRenderer(CopshowiumCreationChamberTileEntity.type, LinkLineRenderer::new);
		ClientRegistry.bindTileEntityRenderer(FluxSinkTileEntity.type, FluxSinkRenderer::new);
	}

	private static void reg(Block block){
		Minecraft.getInstance().getItemRenderer().getItemModelMesher().register(block.asItem(), new ModelResourceLocation(block.getRegistryName(), "inventory"));
	}
}