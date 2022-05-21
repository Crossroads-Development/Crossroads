package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.tileentities.beams.*;
import com.Da_Technomancer.crossroads.tileentities.electric.DynamoTileEntity;
import com.Da_Technomancer.crossroads.tileentities.electric.TeslaCoilTopTileEntity;
import com.Da_Technomancer.crossroads.tileentities.fluid.RotaryPumpTileEntity;
import com.Da_Technomancer.crossroads.tileentities.heat.HeatingCrucibleTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.*;
import com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms.MechanismTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.*;
import com.Da_Technomancer.crossroads.tileentities.witchcraft.AutoInjectorTileEntity;
import com.Da_Technomancer.crossroads.tileentities.witchcraft.BloodCentrifugeTileEntity;
import com.Da_Technomancer.crossroads.tileentities.witchcraft.CultivatorVatTileEntity;
import com.Da_Technomancer.essentials.render.LinkLineRenderer;
import net.minecraftforge.client.event.EntityRenderersEvent;

public class CRRendererRegistry{

	public static void registerBlockRenderer(EntityRenderersEvent.RegisterRenderers e){
		e.registerBlockEntityRenderer(MechanismTileEntity.TYPE, MechanismRenderer::new);
//		reg(CRBlocks.rotaryPump);
		e.registerBlockEntityRenderer(RotaryPumpTileEntity.TYPE, RotaryPumpRenderer::new);
//		reg(CRBlocks.steamTurbine);
		e.registerBlockEntityRenderer(SteamTurbineTileEntity.TYPE, SteamTurbineRenderer::new);
		e.registerBlockEntityRenderer(LargeGearMasterTileEntity.TYPE, LargeGearRenderer::new);
		e.registerBlockEntityRenderer(RotaryDrillTileEntity.TYPE, RotaryDrillRenderer::new);
		e.registerBlockEntityRenderer(BeamExtractorTileEntity.TYPE, BeamRenderer::new);
		e.registerBlockEntityRenderer(QuartzStabilizerTileEntity.TYPE, BeamRenderer::new);
		e.registerBlockEntityRenderer(CrystallinePrismTileEntity.TYPE, BeamRenderer::new);
		e.registerBlockEntityRenderer(BeamReflectorTileEntity.TYPE, BeamRenderer::new);
		e.registerBlockEntityRenderer(LensFrameTileEntity.TYPE, LensFrameRenderer::new);
		e.registerBlockEntityRenderer(BeamSiphonTileEntity.TYPE, BeamRenderer::new);
		e.registerBlockEntityRenderer(BeamRedirectorTileEntity.TYPE, BeamRenderer::new);
		e.registerBlockEntityRenderer(BeamSplitterTileEntity.TYPE, BeamRenderer::new);
		e.registerBlockEntityRenderer(BeaconHarnessTileEntity.TYPE, BeaconHarnessRenderer::new);
		e.registerBlockEntityRenderer(HamsterWheelTileEntity.TYPE, HamsterWheelRenderer::new);
		e.registerBlockEntityRenderer(GatewayControllerTileEntity.TYPE, GatewayControllerRenderer::new);
		e.registerBlockEntityRenderer(GatewayControllerDestinationTileEntity.TYPE, GatewayControllerDestinationRenderer::new);
//		e.registerBlockEntityRenderer(MechanicalArmTileEntity.type, MechanicalArmRenderer::new);
		e.registerBlockEntityRenderer(HeatingCrucibleTileEntity.TYPE, HeatingCrucibleRenderer::new);
		e.registerBlockEntityRenderer(DynamoTileEntity.TYPE, DynamoRenderer::new);
		e.registerBlockEntityRenderer(LodestoneDynamoTileEntity.TYPE, DynamoRenderer::new);
		e.registerBlockEntityRenderer(ClockworkStabilizerTileEntity.TYPE, BeamRenderer::new);
		e.registerBlockEntityRenderer(WindTurbineTileEntity.TYPE, WindTurbineRenderer::new);
		e.registerBlockEntityRenderer(StampMillTileEntity.TYPE, StampMillRenderer::new);
		e.registerBlockEntityRenderer(FluxNodeTileEntity.TYPE, FluxNodeRenderer::new);
		e.registerBlockEntityRenderer(TeslaCoilTopTileEntity.TYPE, LinkLineRenderer::new);
//		e.registerBlockEntityRenderer(RedstoneTransmitterTileEntity.type, LinkLineRenderer<RedstoneTransmitterTileEntity>::new);
		//Flux machines (all have link lines)
		e.registerBlockEntityRenderer(ChronoHarnessTileEntity.TYPE, ChronoHarnessRenderer::new);
		e.registerBlockEntityRenderer(TemporalAcceleratorTileEntity.TYPE, TemporalAcceleratorRenderer::new);
		e.registerBlockEntityRenderer(ChunkAcceleratorTileEntity.TYPE, ChunkAcceleratorRenderer::new);
		e.registerBlockEntityRenderer(CopshowiumCreationChamberTileEntity.TYPE, EntropyRenderer::new);
		e.registerBlockEntityRenderer(FluxSinkTileEntity.TYPE, FluxSinkRenderer::new);
		e.registerBlockEntityRenderer(BeamCannonTileEntity.TYPE, BeamCannonRenderer::new);
		e.registerBlockEntityRenderer(AutoInjectorTileEntity.TYPE, AutoInjectorRenderer::new);
		e.registerBlockEntityRenderer(CultivatorVatTileEntity.TYPE, CultivatorVatRenderer::new);
		e.registerBlockEntityRenderer(BloodCentrifugeTileEntity.TYPE, BloodCentrifugeRenderer::new);
		e.registerBlockEntityRenderer(BeamExtractorCreativeTileEntity.TYPE, BeamRenderer::new);
		e.registerBlockEntityRenderer(ItemCannonTileEntity.TYPE, ItemCannonRenderer::new);
	}

//	This doesn't work anymore, and any existing blocks were switched to JSON models for the item form
//	private static void reg(Block block){
//		//Registers the item form of a block to use the block's TESR
//		Minecraft.getInstance().getItemRenderer().getItemModelShaper().register(block.asItem(), new ModelResourceLocation(block.getRegistryName(), "inventory"));
//	}

}