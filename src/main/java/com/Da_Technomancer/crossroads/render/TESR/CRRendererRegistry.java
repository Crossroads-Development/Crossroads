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
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import net.minecraft.client.renderer.entity.player.PlayerRenderer;

public class CRRendererRegistry{

	public static void registerBlockRenderer(){

		ClientRegistry.bindTileEntityRenderer(MechanismTileEntity.type, MechanismRenderer::new);
//		reg(CRBlocks.rotaryPump);
		ClientRegistry.bindTileEntityRenderer(RotaryPumpTileEntity.type, RotaryPumpRenderer::new);
//		reg(CRBlocks.steamTurbine);
		ClientRegistry.bindTileEntityRenderer(SteamTurbineTileEntity.type, SteamTurbineRenderer::new);
		ClientRegistry.bindTileEntityRenderer(LargeGearMasterTileEntity.teType, LargeGearRenderer::new);
		ClientRegistry.bindTileEntityRenderer(RotaryDrillTileEntity.type, RotaryDrillRenderer::new);
		ClientRegistry.bindTileEntityRenderer(BeamExtractorTileEntity.TYPE, BeamRenderer::new);
		ClientRegistry.bindTileEntityRenderer(QuartzStabilizerTileEntity.TYPE, BeamRenderer::new);
		ClientRegistry.bindTileEntityRenderer(CrystallinePrismTileEntity.TYPE, BeamRenderer::new);
		ClientRegistry.bindTileEntityRenderer(BeamReflectorTileEntity.TYPE, BeamRenderer::new);
		ClientRegistry.bindTileEntityRenderer(LensFrameTileEntity.TYPE, LensFrameRenderer::new);
		ClientRegistry.bindTileEntityRenderer(BeamSiphonTileEntity.TYPE, BeamRenderer::new);
		ClientRegistry.bindTileEntityRenderer(BeamRedirectorTileEntity.TYPE, BeamRenderer::new);
		ClientRegistry.bindTileEntityRenderer(BeamSplitterTileEntity.TYPE, BeamRenderer::new);
		ClientRegistry.bindTileEntityRenderer(BeaconHarnessTileEntity.type, BeaconHarnessRenderer::new);
		ClientRegistry.bindTileEntityRenderer(HamsterWheelTileEntity.type, HamsterWheelRenderer::new);
		ClientRegistry.bindTileEntityRenderer(GatewayControllerTileEntity.type, GatewayControllerRenderer::new);
		ClientRegistry.bindTileEntityRenderer(GatewayControllerDestinationTileEntity.type, GatewayControllerDestinationRenderer::new);
//		ClientRegistry.bindTileEntityRenderer(MechanicalArmTileEntity.type, MechanicalArmRenderer::new);
		ClientRegistry.bindTileEntityRenderer(HeatingCrucibleTileEntity.type, HeatingCrucibleRenderer::new);
		ClientRegistry.bindTileEntityRenderer(DynamoTileEntity.TYPE, DynamoRenderer::new);
		ClientRegistry.bindTileEntityRenderer(LodestoneDynamoTileEntity.type, DynamoRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ClockworkStabilizerTileEntity.type, BeamRenderer::new);
		ClientRegistry.bindTileEntityRenderer(WindTurbineTileEntity.type, WindTurbineRenderer::new);
		ClientRegistry.bindTileEntityRenderer(StampMillTileEntity.type, StampMillRenderer::new);
		ClientRegistry.bindTileEntityRenderer(FluxNodeTileEntity.type, FluxNodeRenderer::new);
		ClientRegistry.bindTileEntityRenderer(TeslaCoilTopTileEntity.TYPE, LinkLineRenderer::new);
//		ClientRegistry.bindTileEntityRenderer(RedstoneTransmitterTileEntity.type, LinkLineRenderer<RedstoneTransmitterTileEntity>::new);
		//Flux machines (all have link lines)
		ClientRegistry.bindTileEntityRenderer(ChronoHarnessTileEntity.type, ChronoHarnessRenderer::new);
		ClientRegistry.bindTileEntityRenderer(TemporalAcceleratorTileEntity.type, TemporalAcceleratorRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ChunkAcceleratorTileEntity.type, ChunkAcceleratorRenderer::new);
		ClientRegistry.bindTileEntityRenderer(CopshowiumCreationChamberTileEntity.type, EntropyRenderer::new);
		ClientRegistry.bindTileEntityRenderer(FluxSinkTileEntity.type, FluxSinkRenderer::new);
		ClientRegistry.bindTileEntityRenderer(BeamCannonTileEntity.type, BeamCannonRenderer::new);
		ClientRegistry.bindTileEntityRenderer(AutoInjectorTileEntity.type, AutoInjectorRenderer::new);
		ClientRegistry.bindTileEntityRenderer(CultivatorVatTileEntity.type, CultivatorVatRenderer::new);
		ClientRegistry.bindTileEntityRenderer(BloodCentrifugeTileEntity.type, BloodCentrifugeRenderer::new);
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
		for(PlayerRenderer playerRenderer : manager.getSkinMap().values()){
			playerRenderer.addLayer(new TechnomancyElytraRenderer<>(playerRenderer));
			playerRenderer.addLayer(new HopperHawkShoulderRenderer<>(playerRenderer));
		}
	}
}