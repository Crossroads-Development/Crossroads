package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.render.HopperHawkShoulderRenderer;
import com.Da_Technomancer.crossroads.render.TechnomancyElytraRenderer;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.player.Player;

public class CRRendererRegistry{

	public static void registerBlockRenderer(){

		BlockEntityRenderers.register(MechanismTileEntity.TYPE, MechanismRenderer::new);
//		reg(CRBlocks.rotaryPump);
		BlockEntityRenderers.register(RotaryPumpTileEntity.TYPE, RotaryPumpRenderer::new);
//		reg(CRBlocks.steamTurbine);
		BlockEntityRenderers.register(SteamTurbineTileEntity.TYPE, SteamTurbineRenderer::new);
		BlockEntityRenderers.register(LargeGearMasterTileEntity.TYPE, LargeGearRenderer::new);
		BlockEntityRenderers.register(RotaryDrillTileEntity.TYPE, RotaryDrillRenderer::new);
		BlockEntityRenderers.register(BeamExtractorTileEntity.TYPE, BeamRenderer::new);
		BlockEntityRenderers.register(QuartzStabilizerTileEntity.TYPE, BeamRenderer::new);
		BlockEntityRenderers.register(CrystallinePrismTileEntity.TYPE, BeamRenderer::new);
		BlockEntityRenderers.register(BeamReflectorTileEntity.TYPE, BeamRenderer::new);
		BlockEntityRenderers.register(LensFrameTileEntity.TYPE, LensFrameRenderer::new);
		BlockEntityRenderers.register(BeamSiphonTileEntity.TYPE, BeamRenderer::new);
		BlockEntityRenderers.register(BeamRedirectorTileEntity.TYPE, BeamRenderer::new);
		BlockEntityRenderers.register(BeamSplitterTileEntity.TYPE, BeamRenderer::new);
		BlockEntityRenderers.register(BeaconHarnessTileEntity.TYPE, BeaconHarnessRenderer::new);
		BlockEntityRenderers.register(HamsterWheelTileEntity.TYPE, HamsterWheelRenderer::new);
		BlockEntityRenderers.register(GatewayControllerTileEntity.TYPE, GatewayControllerRenderer::new);
		BlockEntityRenderers.register(GatewayControllerDestinationTileEntity.TYPE, GatewayControllerDestinationRenderer::new);
//		BlockEntityRenderers.register(MechanicalArmTileEntity.type, MechanicalArmRenderer::new);
		BlockEntityRenderers.register(HeatingCrucibleTileEntity.TYPE, HeatingCrucibleRenderer::new);
		BlockEntityRenderers.register(DynamoTileEntity.TYPE, DynamoRenderer::new);
		BlockEntityRenderers.register(LodestoneDynamoTileEntity.TYPE, DynamoRenderer::new);
		BlockEntityRenderers.register(ClockworkStabilizerTileEntity.TYPE, BeamRenderer::new);
		BlockEntityRenderers.register(WindTurbineTileEntity.TYPE, WindTurbineRenderer::new);
		BlockEntityRenderers.register(StampMillTileEntity.TYPE, StampMillRenderer::new);
		BlockEntityRenderers.register(FluxNodeTileEntity.TYPE, FluxNodeRenderer::new);
		BlockEntityRenderers.register(TeslaCoilTopTileEntity.TYPE, LinkLineRenderer::new);
//		BlockEntityRenderers.register(RedstoneTransmitterTileEntity.type, LinkLineRenderer<RedstoneTransmitterTileEntity>::new);
		//Flux machines (all have link lines)
		BlockEntityRenderers.register(ChronoHarnessTileEntity.TYPE, ChronoHarnessRenderer::new);
		BlockEntityRenderers.register(TemporalAcceleratorTileEntity.TYPE, TemporalAcceleratorRenderer::new);
		BlockEntityRenderers.register(ChunkAcceleratorTileEntity.TYPE, ChunkAcceleratorRenderer::new);
		BlockEntityRenderers.register(CopshowiumCreationChamberTileEntity.TYPE, EntropyRenderer::new);
		BlockEntityRenderers.register(FluxSinkTileEntity.TYPE, FluxSinkRenderer::new);
		BlockEntityRenderers.register(BeamCannonTileEntity.TYPE, BeamCannonRenderer::new);
		BlockEntityRenderers.register(AutoInjectorTileEntity.TYPE, AutoInjectorRenderer::new);
		BlockEntityRenderers.register(CultivatorVatTileEntity.TYPE, CultivatorVatRenderer::new);
		BlockEntityRenderers.register(BloodCentrifugeTileEntity.TYPE, BloodCentrifugeRenderer::new);
	}

//	This doesn't work anymore, and any existing blocks were switched to JSON models for the item form
//	private static void reg(Block block){
//		//Registers the item form of a block to use the block's TESR
//		Minecraft.getInstance().getItemRenderer().getItemModelShaper().register(block.asItem(), new ModelResourceLocation(block.getRegistryName(), "inventory"));
//	}

	public static void registerEntityLayerRenderers(){

		//Add the technomancy armor elytra render layer to every entity that can render an elytra
		EntityRenderDispatcher manager = Minecraft.getInstance().getEntityRenderDispatcher();
		for(EntityRenderer<?> entityRenderer : manager.renderers.values()){
			if(entityRenderer instanceof HumanoidMobRenderer || entityRenderer instanceof ArmorStandRenderer){
				LivingEntityRenderer<?, ?> livingRenderer = (LivingEntityRenderer<?, ?>) entityRenderer;
				livingRenderer.addLayer(new TechnomancyElytraRenderer(livingRenderer));
			}
		}
		//Player renderers are stored separately from the main renderer map
		for(EntityRenderer<? extends Player> skinRenderer : manager.getSkinMap().values()){
			if(skinRenderer instanceof PlayerRenderer playerRenderer){
				playerRenderer.addLayer(new TechnomancyElytraRenderer<>(playerRenderer));
				playerRenderer.addLayer(new HopperHawkShoulderRenderer<>(playerRenderer));
			}
		}
	}
}