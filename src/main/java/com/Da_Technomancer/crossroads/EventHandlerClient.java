package com.Da_Technomancer.crossroads;

import com.Da_Technomancer.crossroads.ambient.particles.CRParticles;
import com.Da_Technomancer.crossroads.ambient.sounds.CRSounds;
import com.Da_Technomancer.crossroads.api.CRMaterialLibrary;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.alchemy.ReagentManager;
import com.Da_Technomancer.crossroads.api.packets.*;
import com.Da_Technomancer.crossroads.api.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.api.render.IVisualEffect;
import com.Da_Technomancer.crossroads.api.technomancy.EnumGoggleLenses;
import com.Da_Technomancer.crossroads.api.witchcraft.EntityTemplate;
import com.Da_Technomancer.crossroads.entity.CREntities;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import com.Da_Technomancer.crossroads.gui.container.CRContainers;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.alchemy.AbstractGlassware;
import com.Da_Technomancer.crossroads.items.item_sets.OreProfileItem;
import com.Da_Technomancer.crossroads.items.technomancy.ArmorGoggles;
import com.Da_Technomancer.crossroads.items.technomancy.ArmorPropellerPack;
import com.Da_Technomancer.crossroads.items.technomancy.BeamUsingItem;
import com.Da_Technomancer.crossroads.items.witchcraft.GeneticSpawnEgg;
import com.Da_Technomancer.crossroads.render.BeamToolOverlay;
import com.Da_Technomancer.crossroads.render.MultiLineMessageOverlay;
import com.Da_Technomancer.crossroads.render.tesr.CRRendererRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class EventHandlerClient{

	@EventBusSubscriber(modid = Crossroads.MODID, value = Dist.CLIENT)
	public static class CRModEventsClient{

		@SubscribeEvent
		@SuppressWarnings("unused")
		public static void registerScreens(RegisterMenuScreensEvent e){
			CRContainers.initClient(e);
		}

		@SuppressWarnings("unused")
		@SubscribeEvent
		public static void registerRenderers(EntityRenderersEvent.RegisterRenderers e){
			CRRendererRegistry.registerBlockRenderer(e);
			CREntities.clientInit(e);
		}

		@SuppressWarnings("unused")
		@SubscribeEvent
		public static void registerEntityRenderingLayers(EntityRenderersEvent.AddLayers e){
			CREntities.attachLayerRenderers(e);
		}

		@SuppressWarnings("unused")
		@SubscribeEvent
		public static void registerLayerLocation(EntityRenderersEvent.RegisterLayerDefinitions e){
			CREntities.registerLayers(e);
		}

		@SuppressWarnings("unused")
		@SubscribeEvent
		public static void registerParticleFactories(RegisterParticleProvidersEvent e){
			CRParticles.clientInit(e);
		}

		@SuppressWarnings("unused")
		@SubscribeEvent
		public static void registerKeyMappings(RegisterKeyMappingsEvent e){
			Keys.init();
			Keys.toRegister.forEach(e::register);
			Keys.toRegister.clear();
		}

		@SuppressWarnings("unused")
		@SubscribeEvent
		public static void registerOverlays(RegisterGuiLayersEvent e){
			e.registerAboveAll(ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "beam_tool_overlay"), new BeamToolOverlay());
			e.registerBelow(VanillaGuiLayers.OVERLAY_MESSAGE, ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "multi_line_overlay"), new MultiLineMessageOverlay());
		}

		@SuppressWarnings("unused")
		@SubscribeEvent
		public static void registerItemColoration(RegisterColorHandlersEvent.Item e){
			//Alchemy containers
			e.register((ItemStack stack, int layer) -> layer == 0 ? AbstractGlassware.getColorRGB(stack) : -1, CRItems.phialGlass, CRItems.florenceFlaskGlass, CRItems.shellGlass, CRItems.phialCrystal, CRItems.florenceFlaskCrystal, CRItems.shellCrystal);

			//Gears and ore processing dusts
			ItemColor oreItemColoring = (ItemStack stack, int tintIndex) -> {
				if(tintIndex == 0){
					return -1;
				}
				CRMaterialLibrary.OreProfile mat = OreProfileItem.getProfile(stack);
				return mat == null ? -1 : mat.getColor().getRGB();
			};
			e.register(oreItemColoring, CRItems.oreGravel, CRItems.oreClump, CRItems.axle, CRItems.smallGear, CRItems.largeGear, CRItems.clutch, CRItems.invClutch, CRItems.toggleGear, CRItems.invToggleGear, CRItems.axleMount);

			//Genetic spawn egg
			ItemColor eggItemColoring = (ItemStack stack, int tintIndex) -> {
				//Lookup the mob's vanilla egg, copy the colors
				//If it doesn't have an egg, fallback to defaults
				if(stack.getItem() instanceof GeneticSpawnEgg){
					EntityTemplate template = GeneticSpawnEgg.getEntityTypeData(stack);
					EntityType<?> type = template.getEntityType();
					if(type != null){
						SpawnEggItem vanillaEgg = DeferredSpawnEggItem.byId(type);
						if(vanillaEgg != null){
							return 0xFF000000 | vanillaEgg.getColor(tintIndex);
						}
					}
				}
				//Fallback to defaults
				//Which are hideous, but that's what you get for not registering spawn eggs
				return tintIndex == 0 ? Color.CYAN.getRGB() : Color.GREEN.getRGB();
			};
			e.register(eggItemColoring, CRItems.geneticSpawnEgg);
		}

		@SubscribeEvent
		@SuppressWarnings("unused")
		public static void registerClientExtensions(RegisterClientExtensionsEvent e){
			CRFluids.initClient(e);
		}
	}

	private static final Random RAND = new Random();

	@SubscribeEvent
	@SuppressWarnings("unused")
	public void drawFieldsAndBeams(RenderLevelStageEvent e){
		if(e.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES){
			return;
		}

		Minecraft game = Minecraft.getInstance();

//		//Goggle entity glowing (Moved to tick event handler)
//		game.getProfiler().startSection(Crossroads.MODNAME + ": Goggle Glowing Application");
//		handleGoggleGlowing(game);
//		game.getProfiler().endSection();

		//IVisualEffects
		if(!AddVisualToClient.effectsToRender.isEmpty()){
			game.getProfiler().push(Crossroads.MODNAME + ": Visual Effects Draw");

			PoseStack matrix = e.getPoseStack();

			matrix.pushPose();
			Vec3 cameraPos = CRRenderUtil.getCameraPos();
			matrix.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);//Translate to 0,0,0 world coords

			ArrayList<IVisualEffect> toRemove = new ArrayList<>();
			MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
			long worldTime = game.level.getGameTime();
			float partialTicks = e.getPartialTick().getGameTimeDeltaTicks();

			for(IVisualEffect effect : AddVisualToClient.effectsToRender){
				matrix.pushPose();

				if(effect.render(matrix, buffer, worldTime, partialTicks, RAND)){
					toRemove.add(effect);
				}

				matrix.popPose();
			}

			AddVisualToClient.effectsToRender.removeAll(toRemove);

			buffer.endBatch();//Due to weirdness surrounding how this event is called, we need to force anything in the buffer to render immediately to prevent something else changing render system settings
			matrix.popPose();

			game.getProfiler().pop();
		}
	}

	private static void handleGoggleGlowing(Minecraft game){
		//Handles glow in the dark entities when wearing goggles
		if(game.level.getGameTime() % 5 == 0){
			ItemStack helmet = Minecraft.getInstance().player.getItemBySlot(EquipmentSlot.HEAD);
			boolean doGlowing = (helmet.getItem() == CRItems.armorGoggles || helmet.getItem() == CRItems.armorGogglesReinforced) && helmet.has(CRItems.GOGGLE_LENSES_DATA) && helmet.get(CRItems.GOGGLE_LENSES_DATA).lenses().getBoolean(EnumGoggleLenses.VOID);
			for(Entity ent : game.level.entitiesForRendering()){
				CompoundTag entNBT = ent.getPersistentData();
				if(entNBT == null){
					Crossroads.logger.info("Found entity with null persistent data! Report to the mod author of the mod that added the entity: %s", MiscUtil.getRegistryName(ent.getType(), BuiltInRegistries.ENTITY_TYPE).toString());
					continue;//Should never be null, but some mods override the entNBT method to return null for some reason
				}

				//The NBT shenanigans is to prevent this purely client side glowing effect from interfering with server-side glowing effects (such as being hit with the glowing arrow) when disabled
				if(!entNBT.contains("cr_glow")){
					ent.setSharedFlag(6, false);//Disable glowing the flag
				}else{
					entNBT.remove("cr_glow");
				}

				if(doGlowing){
					if(ent.getSharedFlag(6)){//Check if currently glowing via the flag
						entNBT.putBoolean("cr_glow", true);
					}else{
						ent.setSharedFlag(6, true);//Set glowing via the flag
					}
				}
			}
		}
	}

//	private static final ResourceLocation MAGIC_BAR_BACKGROUND = gui/magic_info_back.png");
//	private static final ResourceLocation MAGIC_BAR_FOREGROUND = gui/magic_info_front.png");
//	private static final ResourceLocation COLOR_SHEET = ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "textures/block/color_sheet.png");

//	@SubscribeEvent
//	@SuppressWarnings("unused")
//	public void magicUsingItemOverlay(RenderGameOverlayEvent.PostLayer e){
//		if(e.getType() == ElementType.LAYER){
//			Player player = Minecraft.getInstance().player;
//
//			//Beam cage overlay
//			ItemStack cageStack = CurioHelper.getEquipped(CRItems.beamCage, player);
//			ItemStack mainStack = player.getItemInHand(InteractionHand.MAIN_HAND);
//
//			boolean renderToolOverlay = mainStack.getItem() instanceof BeamUsingItem;
//			boolean renderCageOverlay = !cageStack.isEmpty() && (CRConfig.cageMeterOverlay.get() || renderToolOverlay);
//
//			if(renderCageOverlay || renderToolOverlay){
//				//Use the batched renderer instead of the Tesselator
//				MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
//				PoseStack matrix = e.getMatrixStack();
//
//				float barUSt = 8F/39F;
//				float barUEn = 31F/39F;
//				float barVSt = 26F/40F;
//				float barVWid = 2F/40F;
//
//				if(renderCageOverlay){
//					VertexConsumer builder = buffer.getBuffer(CRRenderTypes.BEAM_INFO_TYPE);
//
//					BeamUnit stored = BeamCage.getStored(cageStack);
////					RenderSystem.pushMatrix();
////					RenderSystem.pushLightingAttributes();
////					RenderSystem.enableBlend();
////					RenderSystem.setShaderTexture(0, MAGIC_BAR_BACKGROUND);
////					Tesselator tes = Tesselator.getInstance();
////					BufferBuilder buf = tes.getBuilder();
////					buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
//					builder.addVertex(matrix.last().pose(), 0, 120, -3).setUv(0, 0.5F);
//					builder.addVertex(matrix.last().pose(), 117, 120, -3).setUv(1, 0.5F);
//					builder.addVertex(matrix.last().pose(), 117, 60, -3).setUv(1, 0);
//					builder.addVertex(matrix.last().pose(), 0, 60, -3).setUv(0, 0);
////					buf.addVertex(0, 120, -3).setUv(0, 1);
////					buf.addVertex(117, 120, -3).setUv(1, 1);
////					buf.addVertex(117, 60, -3).setUv(1, 0);
////					buf.addVertex(0, 60, -3).setUv(0, 0);
////					tes.end();
//
////					RenderSystem.setShaderTexture(0, COLOR_SHEET);
////					buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
//					for(int i = 0; i < 4; i++){
//						float fullness = (float) stored.getValues()[i] / BeamCage.CAPACITY;
//						int extension = (int) (72 * fullness);
//						builder.addVertex(matrix.last().pose(), 24, 84 + (9 * i), -2).setUv(barUSt, barVSt + barVWid * (i + 1));
//						builder.addVertex(matrix.last().pose(), 24 + extension, 84 + (9 * i), -2).setUv(barUSt + (barUEn - barUSt) * fullness, barVSt + barVWid * (i + 1));
//						builder.addVertex(matrix.last().pose(), 24 + extension, 78 + (9 * i), -2).setUv(barUSt + (barUEn - barUSt) * fullness, barVSt + barVWid * i);
//						builder.addVertex(matrix.last().pose(), 24, 78 + (9 * i), -2).setUv(barUSt, barVSt + barVWid * i);
////						int[] col = new int[4];
////						col[3] = 255;
////						col[i] = 255;//For void, overrides the alpha. Conveniently not an issue
////						buf.addVertex(24, 84 + (9 * i), -2).setColor(col[0], col[1], col[2], col[3]).setUv(.25F + (((float) i) * .0625F), .0625F);
////						buf.addVertex(24 + extension, 84 + (9 * i), -2).setColor(col[0], col[1], col[2], col[3]).setUv(.3125F + (((float) i) * .0625F), .0625F);
////						buf.addVertex(24 + extension, 78 + (9 * i), -2).setColor(col[0], col[1], col[2], col[3]).setUv(.3125F + (((float) i) * .0625F), 0);
////						buf.addVertex(24, 78 + (9 * i), -2).setColor(col[0], col[1], col[2], col[3]).setUv(.25F + (((float) i) * .0625F), 0);
//					}
////					tes.end();
//
////					RenderSystem.setShaderTexture(0, MAGIC_BAR_FOREGROUND);
////					buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
////					buf.addVertex(0, 120, -1).setUv(0, 1);
////					buf.addVertex(117, 120, -1).setUv(1, 1);
////					buf.addVertex(117, 60, -1).setUv(1, 0);
////					buf.addVertex(0, 60, -1).setUv(0, 0);
////					tes.end();
//
//					//As this is an unbatched environment, we need to manually force the buffer to render before drawing fonts
//					buffer.endBatch();
//
//					Minecraft.getInstance().font.draw(e.getMatrixStack(), cageStack.getHoverName().getString(), 16, 65, Color.DARK_GRAY.getRGB());
////					RenderSystem.setShaderColor(1, 1, 1, 1);
////					RenderSystem.disableAlphaTest();
////					RenderSystem.disableBlend();
////					RenderSystem.popAttributes();
////					RenderSystem.popMatrix();
//				}
//
//				//Beam using item overlay
//				if(renderToolOverlay){
//					VertexConsumer builder = buffer.getBuffer(CRRenderTypes.BEAM_INFO_TYPE);
//
////					RenderSystem.pushMatrix();
////					RenderSystem.pushLightingAttributes();
////					RenderSystem.enableBlend();
////					RenderSystem.setShaderTexture(0, MAGIC_BAR_BACKGROUND);
////					Tesselator tes = Tesselator.getInstance();
////					BufferBuilder buf = tes.getBuilder();
//					builder.addVertex(matrix.last().pose(), 0, 60, -3).setUv(0, 0.5F);
//					builder.addVertex(matrix.last().pose(), 117, 60, -3).setUv(1, 0.5F);
//					builder.addVertex(matrix.last().pose(), 117, 0, -3).setUv(1, 0);
//					builder.addVertex(matrix.last().pose(), 0, 0, -3).setUv(0, 0);
////					buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
////					buf.addVertex(0, 60, -3).setUv(0, 1);
////					buf.addVertex(117, 60, -3).setUv(1, 1);
////					buf.addVertex(117, 0, -3).setUv(1, 0);
////					buf.addVertex(0, 0, -3).setUv(0, 0);
////					tes.end();
//
////					RenderSystem.setShaderTexture(0, COLOR_SHEET);
////					buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
//					byte[] settings = BeamUsingItem.getSetting(mainStack);
//					for(int i = 0; i < 4; i++){
//						float fullness = (float) settings[i] / 8;
//						int extension = (int) (72 * fullness);
//						builder.addVertex(matrix.last().pose(), 24, 24 + (9 * i), -2).setUv(barUSt, barVSt + barVWid * (i + 1));
//						builder.addVertex(matrix.last().pose(), 24 + extension, 24 + (9 * i), -2).setUv(barUSt + (barUEn - barUSt) * fullness, barVSt + barVWid * (i + 1));
//						builder.addVertex(matrix.last().pose(), 24 + extension, 18 + (9 * i), -2).setUv(barUSt + (barUEn - barUSt) * fullness, barVSt + barVWid * i);
//						builder.addVertex(matrix.last().pose(), 24, 18 + (9 * i), -2).setUv(barUSt, barVSt + barVWid * i);
////						int[] col = new int[4];
////						col[3] = 255;
////						col[i] = 255;//For void, overrides the alpha. Conveniently not an issue
////						int extension = 9 * settings[i];
////						buf.addVertex(24, 24 + (9 * i), -2).setColor(col[0], col[1], col[2], col[3]).setUv(.25F + (((float) i) * .0625F), .0625F);
////						buf.addVertex(24 + extension, 24 + (9 * i), -2).setColor(col[0], col[1], col[2], col[3]).setUv(.3125F + (((float) i) * .0625F), .0625F);
////						buf.addVertex(24 + extension, 18 + (9 * i), -2).setColor(col[0], col[1], col[2], col[3]).setUv(.3125F + (((float) i) * .0625F), 0);
////						buf.addVertex(24, 18 + (9 * i), -2).setColor(col[0], col[1], col[2], col[3]).setUv(.25F + (((float) i) * .0625F), 0);
//					}
////					tes.end();
//
////					RenderSystem.setShaderTexture(0, MAGIC_BAR_FOREGROUND);
////					buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
////					buf.addVertex(0, 60, -1).setUv(0, 1);
////					buf.addVertex(117, 60, -1).setUv(1, 1);
////					buf.addVertex(117, 0, -1).setUv(1, 0);
////					buf.addVertex(0, 0, -1).setUv(0, 0);
////					tes.end();
//
//					//As this is an unbatched environment, we need to manually force the buffer to render before drawing fonts
//					buffer.endBatch();
//
//					Minecraft.getInstance().font.draw(e.getMatrixStack(), mainStack.getHoverName().getString(), 16, 5, Color.DARK_GRAY.getRGB());
//
////					RenderSystem.disableAlphaTest();
////					RenderSystem.setShaderColor(1, 1, 1, 1);
////					RenderSystem.disableBlend();
////					RenderSystem.popAttributes();
////					RenderSystem.popMatrix();
//				}
//			}
//		}
//	}

	@SubscribeEvent
	@SuppressWarnings("unused")
	public void dilatePlayerTime(ClientTickEvent.Post e){
		Player player = Minecraft.getInstance().player;
		if(player == null){
			return;
		}

		//Goggle entity glowing
		Minecraft game = Minecraft.getInstance();
		game.getProfiler().push(Crossroads.MODNAME + ": Goggle Glowing Application");
		handleGoggleGlowing(game);
		game.getProfiler().pop();

		//Handle time dilation for players
		if(SendPlayerTickCountToClient.playerTickCount > 0){
			game.getProfiler().push(Crossroads.MODNAME + ": Player time dilation");
			for(int i = 0; i < SendPlayerTickCountToClient.playerTickCount; i++){
				player.tick();
			}
			SendPlayerTickCountToClient.playerTickCount = 0;
			game.getProfiler().pop();
		}
	}

	@SubscribeEvent
	@SuppressWarnings("unused")
	public void keyListener(InputEvent.Key e){
		if(Minecraft.getInstance().screen != null || !Keys.keysInitialized){
			return;//Only accept key hits if the player isn't in a UI
		}
		Player play = Minecraft.getInstance().player;

		ItemStack helmet = play.getItemBySlot(EquipmentSlot.HEAD);
		if(!play.getMainHandItem().isEmpty()){
			int key = Keys.isKeyActiveAndMatch(Keys.controlEnergy, e.getKey(), e.getScanCode()) ? 0 : Keys.isKeyActiveAndMatch(Keys.controlPotential, e.getKey(), e.getScanCode()) ? 1 : Keys.isKeyActiveAndMatch(Keys.controlStability, e.getKey(), e.getScanCode()) ? 2 : Keys.isKeyActiveAndMatch(Keys.controlVoid, e.getKey(), e.getScanCode()) ? 3 : -1;
			ItemStack stack = play.getMainHandItem();
			if(key != -1 && stack.getItem() instanceof BeamUsingItem beamItem){
				beamItem.adjustSetting(Minecraft.getInstance().player, stack, key, !play.isShiftKeyDown());
				return;
			}
		}else if((helmet.getItem() == CRItems.armorGoggles || helmet.getItem() == CRItems.armorGogglesReinforced) && helmet.has(CRItems.GOGGLE_LENSES_DATA)){
			ArmorGoggles.LensesSet lensData = helmet.get(CRItems.GOGGLE_LENSES_DATA);
			for(EnumGoggleLenses lens : EnumGoggleLenses.values()){
				KeyMapping key = Keys.asKeyMapping(lens.getKey());
				if(key != null && key.consumeClick() && key.isDown() && lensData.lenses().containsKey(lens)){
					boolean wasEnabled = lensData.lenses().getBoolean(lens);
					CRSounds.playSoundClientLocal(play.level(), MiscUtil.blockPos(play.getX(), play.getEyeY(), play.getZ()), SoundEvents.SPYGLASS_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
					CRPackets.sendPacketToServer(new SendGoggleConfigureToServer(lens, !wasEnabled));
					if(!wasEnabled || !lens.requireEnableKey()){
						MiscUtil.displayMessage(play, Component.translatable("tt.crossroads.goggles.enabled"));
					}else{
						MiscUtil.displayMessage(play, Component.translatable("tt.crossroads.goggles.disabled"));
					}
					return;
				}
			}
		}

		//Trigger propeller pack boost when jumping
		KeyMapping boostKey = Keys.asKeyMapping(Keys.boost);
		if(boostKey != null && boostKey.consumeClick()){
			ItemStack chestplate = play.getItemBySlot(EquipmentSlot.CHEST);
			if(play.isFallFlying() && chestplate.getItem() instanceof ArmorPropellerPack pack && pack.getWindLevel(chestplate) > 0){
				CRPackets.sendPacketToServer(new SendElytraBoostToServer());
				ArmorPropellerPack.applyMidairBoost(play);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	@SuppressWarnings("unused")
	public void refreshAlchemy(RecipesUpdatedEvent e){
		ReagentManager.updateFromServer(e.getRecipeManager());
	}

	@SubscribeEvent
	@SuppressWarnings("unused")
	public void viewZoom(ComputeFovModifierEvent e){
		//Zooms in the view to spyglass levels when the player is wearing goggles with the amethyst lens enabled
		if(Minecraft.getInstance().options.getCameraType().isFirstPerson()){
			ItemStack helmet = Minecraft.getInstance().player.getItemBySlot(EquipmentSlot.HEAD);
			boolean doGoggleZoom = (helmet.getItem() == CRItems.armorGoggles || helmet.getItem() == CRItems.armorGogglesReinforced) && helmet.has(CRItems.GOGGLE_LENSES_DATA) && helmet.get(CRItems.GOGGLE_LENSES_DATA).lenses().getBoolean(EnumGoggleLenses.AMETHYST);
			if(doGoggleZoom){
				final float scopingFOV = 0.1F;
				e.setNewFovModifier((float) Mth.lerp(Minecraft.getInstance().options.fovEffectScale().get(), 1.0F, scopingFOV));
			}
		}
	}
}