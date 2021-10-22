package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.render.HopperHawkShoulderRenderer;
import com.Da_Technomancer.crossroads.render.TechnomancyElytraRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.registries.IForgeRegistry;

public final class CREntities{

	@OnlyIn(Dist.CLIENT)
	public static void clientInit(EntityRenderersEvent.RegisterRenderers e){
//		e.registerEntityRenderer(EntityBullet.class, (EntityRendererManager manager) -> (new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer())));
//		e.registerEntityRenderer(EntityArmRidable.class, RenderEmpty::new);
		e.registerEntityRenderer(EntityShell.type, ThrownItemRenderer::new);
		e.registerEntityRenderer(EntityNitro.type, ThrownItemRenderer::new);
		e.registerEntityRenderer(EntityFlyingMachine.type, RenderFlyingMachine::new);
		e.registerEntityRenderer(EntityFlameCore.type, RenderFlameCoreEntity::new);
		e.registerEntityRenderer(EntityGhostMarker.type, RenderEmpty::new);
		e.registerEntityRenderer(EntityHopperHawk.type, RenderHopperHawk::new);
	}

	public static void init(IForgeRegistry<EntityType<?>> reg){
		registerEnt(reg, EntityType.Builder.of(EntityFlameCore::new, MobCategory.MISC).fireImmune().noSummon().setShouldReceiveVelocityUpdates(false).sized(1, 1), "flame_core");
		registerEnt(reg, EntityType.Builder.<EntityShell>of(EntityShell::new, MobCategory.MISC).fireImmune().setTrackingRange(64).setUpdateInterval(5).sized(.25F, .25F), "shell");
		registerEnt(reg, EntityType.Builder.<EntityNitro>of(EntityNitro::new, MobCategory.MISC).setTrackingRange(64).setUpdateInterval(5), "nitro");
		registerEnt(reg, EntityType.Builder.<EntityGhostMarker>of(EntityGhostMarker::new, MobCategory.MISC).noSummon().setTrackingRange(64).setUpdateInterval(20).fireImmune().setShouldReceiveVelocityUpdates(false), "ghost_marker");
		registerEnt(reg, EntityType.Builder.of(EntityFlyingMachine::new, MobCategory.MISC).sized(1F, 1.3F).setTrackingRange(64).setUpdateInterval(1), "flying_machine");
		registerEnt(reg, EntityType.Builder.of(EntityHopperHawk::new, MobCategory.CREATURE).sized(0.5F, 0.9F).clientTrackingRange(8), "hopper_hawk");
	}

	public static <T extends Entity> EntityType<T> createType(EntityType.Builder<T> builder, String name){
		EntityType<T> type = builder.build(name);
		type.setRegistryName(name);
		return type;
	}

	private static <T extends Entity> void registerEnt(IForgeRegistry<EntityType<?>> reg, EntityType.Builder<T> builder, String name){
		EntityType<T> type = createType(builder, name);
		reg.register(type);
	}

	@OnlyIn(Dist.CLIENT)
	public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions e){
		e.registerLayerDefinition(RenderHopperHawk.HOPPER_HAWK_MODEL_LAYER, ModelHopperHawk::createBodyLayer);
	}

	@OnlyIn(Dist.CLIENT)
	public static void attachLayerRenderers(EntityRenderersEvent.AddLayers e){
		EntityModelSet modelSet = e.getEntityModels();

		//Add the technomancy armor elytra render layer to every entity that can render an elytra
		EntityRenderDispatcher manager = Minecraft.getInstance().getEntityRenderDispatcher();
		for(EntityRenderer<?> entityRenderer : manager.renderers.values()){
			if(entityRenderer instanceof HumanoidMobRenderer || entityRenderer instanceof ArmorStandRenderer){
				LivingEntityRenderer<?, ?> livingRenderer = (LivingEntityRenderer<?, ?>) entityRenderer;
				livingRenderer.addLayer(new TechnomancyElytraRenderer(livingRenderer, modelSet));
			}
		}
		//Player renderers are stored separately from the main renderer map
		for(EntityRenderer<? extends Player> skinRenderer : manager.getSkinMap().values()){
			if(skinRenderer instanceof PlayerRenderer playerRenderer){
				playerRenderer.addLayer(new TechnomancyElytraRenderer<>(playerRenderer, modelSet));
				playerRenderer.addLayer(new HopperHawkShoulderRenderer<>(playerRenderer, modelSet));
			}
		}
	}
}
