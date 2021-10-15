package com.Da_Technomancer.crossroads.entity;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IForgeRegistry;

public final class CREntities{

	@OnlyIn(Dist.CLIENT)
	public static void clientInit(){
//		EntityRenderers.register(EntityBullet.class, (EntityRendererManager manager) -> (new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer())));
//		EntityRenderers.register(EntityArmRidable.class, RenderEmpty::new);
		EntityRenderers.register(EntityShell.type, ThrownItemRenderer::new);
		EntityRenderers.register(EntityNitro.type, ThrownItemRenderer::new);
		EntityRenderers.register(EntityFlyingMachine.type, RenderFlyingMachine::new);
		EntityRenderers.register(EntityFlameCore.type, RenderFlameCoreEntity::new);
		EntityRenderers.register(EntityGhostMarker.type, RenderEmpty::new);
		EntityRenderers.register(EntityHopperHawk.type, RenderHopperHawk::new);
	}

	public static void init(IForgeRegistry<EntityType<?>> reg){
		registerEnt(reg, EntityType.Builder.of(EntityFlameCore::new, MobCategory.MISC).fireImmune().noSummon().setShouldReceiveVelocityUpdates(false).sized(1, 1), "flame_core");
		registerEnt(reg, EntityType.Builder.<EntityShell>of(EntityShell::new, MobCategory.MISC).fireImmune().setTrackingRange(64).setUpdateInterval(5).sized(.25F, .25F), "shell");
		registerEnt(reg, EntityType.Builder.<EntityNitro>of(EntityNitro::new, MobCategory.MISC).setTrackingRange(64).setUpdateInterval(5), "nitro");
		registerEnt(reg, EntityType.Builder.<EntityGhostMarker>of(EntityGhostMarker::new, MobCategory.MISC).noSummon().setTrackingRange(64).setUpdateInterval(20).fireImmune().setShouldReceiveVelocityUpdates(false), "ghost_marker");
		registerEnt(reg, EntityType.Builder.of(EntityFlyingMachine::new, MobCategory.MISC).sized(1F, 1.3F).setTrackingRange(64).setUpdateInterval(1), "flying_machine");
		reg.register(EntityHopperHawk.type);
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
}
