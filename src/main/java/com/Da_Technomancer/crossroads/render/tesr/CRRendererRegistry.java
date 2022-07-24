package com.Da_Technomancer.crossroads.render.tesr;

import com.Da_Technomancer.crossroads.blocks.beams.*;
import com.Da_Technomancer.crossroads.blocks.electric.DynamoTileEntity;
import com.Da_Technomancer.crossroads.blocks.electric.TeslaCoilTopTileEntity;
import com.Da_Technomancer.crossroads.blocks.fluid.RotaryPumpTileEntity;
import com.Da_Technomancer.crossroads.blocks.heat.HeatingCrucibleTileEntity;
import com.Da_Technomancer.crossroads.blocks.rotary.*;
import com.Da_Technomancer.crossroads.blocks.rotary.mechanisms.MechanismTileEntity;
import com.Da_Technomancer.crossroads.blocks.technomancy.*;
import com.Da_Technomancer.crossroads.blocks.witchcraft.AutoInjectorTileEntity;
import com.Da_Technomancer.crossroads.blocks.witchcraft.BloodCentrifugeTileEntity;
import com.Da_Technomancer.crossroads.blocks.witchcraft.CultivatorVatTileEntity;
import com.Da_Technomancer.essentials.render.LinkLineRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;

public class CRRendererRegistry{

	public static void registerBlockRenderer(EntityRenderersEvent.RegisterRenderers e){
		registerTESR(e, MechanismTileEntity.TYPE, MechanismRenderer::new);
		registerTESR(e, RotaryPumpTileEntity.TYPE, RotaryPumpRenderer::new);
		registerTESR(e, SteamTurbineTileEntity.TYPE, SteamTurbineRenderer::new);
		registerTESR(e, LargeGearMasterTileEntity.TYPE, LargeGearRenderer::new);
		registerTESR(e, RotaryDrillTileEntity.TYPE, RotaryDrillRenderer::new);
		registerTESR(e, BeamExtractorTileEntity.TYPE, BeamRenderer::new);
		registerTESR(e, QuartzStabilizerTileEntity.TYPE, BeamRenderer::new);
		registerTESR(e, CrystallinePrismTileEntity.TYPE, BeamRenderer::new);
		registerTESR(e, BeamReflectorTileEntity.TYPE, BeamRenderer::new);
		registerTESR(e, BeamReflectorSensitiveTileEntity.TYPE, BeamRenderer::new);
		registerTESR(e, LensFrameTileEntity.TYPE, LensFrameRenderer::new);
		registerTESR(e, BeamSiphonTileEntity.TYPE, BeamRenderer::new);
		registerTESR(e, BeamRedirectorTileEntity.TYPE, BeamRenderer::new);
		registerTESR(e, BeamSplitterTileEntity.TYPE, BeamRenderer::new);
		registerTESR(e, BeaconHarnessTileEntity.TYPE, BeaconHarnessRenderer::new);
		registerTESR(e, HamsterWheelTileEntity.TYPE, HamsterWheelRenderer::new);
		registerTESR(e, GatewayControllerTileEntity.TYPE, GatewayControllerRenderer::new);
		registerTESR(e, GatewayControllerDestinationTileEntity.TYPE, GatewayControllerDestinationRenderer::new);
		registerTESR(e, HeatingCrucibleTileEntity.TYPE, HeatingCrucibleRenderer::new);
		registerTESR(e, DynamoTileEntity.TYPE, DynamoRenderer::new);
		registerTESR(e, LodestoneDynamoTileEntity.TYPE, DynamoRenderer::new);
		registerTESR(e, ClockworkStabilizerTileEntity.TYPE, BeamRenderer::new);
		registerTESR(e, WindTurbineTileEntity.TYPE, WindTurbineRenderer::new);
		registerTESR(e, StampMillTileEntity.TYPE, StampMillRenderer::new);
		registerTESR(e, FluxNodeTileEntity.TYPE, FluxNodeRenderer::new);
		registerTESR(e, TeslaCoilTopTileEntity.TYPE, LinkLineRenderer::new);
		//Flux machines (all have link lines)
		registerTESR(e, ChronoHarnessTileEntity.TYPE, ChronoHarnessRenderer::new);
		registerTESR(e, TemporalAcceleratorTileEntity.TYPE, TemporalAcceleratorRenderer::new);
		registerTESR(e, ChunkAcceleratorTileEntity.TYPE, ChunkAcceleratorRenderer::new);
		registerTESR(e, CopshowiumCreationChamberTileEntity.TYPE, EntropyRenderer::new);
		registerTESR(e, FluxSinkTileEntity.TYPE, FluxSinkRenderer::new);
		registerTESR(e, BeamCannonTileEntity.TYPE, BeamCannonRenderer::new);
		registerTESR(e, AutoInjectorTileEntity.TYPE, AutoInjectorRenderer::new);
		registerTESR(e, CultivatorVatTileEntity.TYPE, CultivatorVatRenderer::new);
		registerTESR(e, BloodCentrifugeTileEntity.TYPE, BloodCentrifugeRenderer::new);
		registerTESR(e, BeamExtractorCreativeTileEntity.TYPE, BeamRenderer::new);
		registerTESR(e, ItemCannonTileEntity.TYPE, ItemCannonRenderer::new);
	}

	private static <T extends BlockEntity> void registerTESR(EntityRenderersEvent.RegisterRenderers e, BlockEntityType<?> teType, BlockEntityRendererProvider<T> tesrConstructor){
		e.registerBlockEntityRenderer((BlockEntityType<? extends T>) teType, tesrConstructor);
	}
}