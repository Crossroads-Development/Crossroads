package com.Da_Technomancer.crossroads;

import com.Da_Technomancer.crossroads.API.alchemy.ReagentManager;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.packets.*;
import com.Da_Technomancer.crossroads.API.technomancy.EnumGoggleLenses;
import com.Da_Technomancer.crossroads.integration.curios.CurioHelper;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.technomancy.ArmorPropellerPack;
import com.Da_Technomancer.crossroads.items.technomancy.BeamCage;
import com.Da_Technomancer.crossroads.items.technomancy.BeamUsingItem;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.render.IVisualEffect;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public final class EventHandlerClient{

	private static final Random RAND = new Random();

	@SubscribeEvent
	@SuppressWarnings("unused")
	public void drawFieldsAndBeams(RenderWorldLastEvent e){
		Minecraft game = Minecraft.getInstance();

//		//Goggle entity glowing (Moved to tick event handler)
//		game.getProfiler().startSection(Crossroads.MODNAME + ": Goggle Glowing Application");
//		handleGoggleGlowing(game);
//		game.getProfiler().endSection();

		//IVisualEffects
		if(!AddVisualToClient.effectsToRender.isEmpty()){
			game.getProfiler().push(Crossroads.MODNAME + ": Visual Effects Draw");

			PoseStack matrix = e.getMatrixStack();

			matrix.pushPose();
			Vec3 cameraPos = CRRenderUtil.getCameraPos();
			matrix.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);//Translate to 0,0,0 world coords

			ArrayList<IVisualEffect> toRemove = new ArrayList<>();
			MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
			long worldTime = game.level.getGameTime();
			float partialTicks = e.getPartialTicks();

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
			boolean doGlowing = helmet.getItem() == CRItems.armorGoggles && helmet.hasTag() && helmet.getTag().getBoolean(EnumGoggleLenses.VOID.toString());
			for(Entity ent : game.level.entitiesForRendering()){
				CompoundTag entNBT = ent.getPersistentData();
				if(entNBT == null){
					Crossroads.logger.info("Found entity with null persistent data! Report to the mod author of the mod that added the entity: %s", ent.getType().getRegistryName().toString());
					continue;//Should never be null, but some mods override the entNBT method to return null for some reason
				}

				//The NBT shenanigans is to prevent this purely client side glowing effect from interfering with server-side glowing effects (such as being hit with the glowing arrow) when disabled
				if(!entNBT.contains("cr_glow")){
					ent.setGlowingTag(false);
				}else{
					entNBT.remove("cr_glow");
				}

				if(doGlowing){
					if(ent.hasGlowingTag()){
						entNBT.putBoolean("cr_glow", true);
					}else{
						ent.setGlowingTag(true);
					}
				}
			}
		}
	}

//	private static final ResourceLocation MAGIC_BAR_BACKGROUND = new ResourceLocation(Crossroads.MODID, "textures/gui/magic_info_back.png");
//	private static final ResourceLocation MAGIC_BAR_FOREGROUND = new ResourceLocation(Crossroads.MODID, "textures/gui/magic_info_front.png");
//	private static final ResourceLocation COLOR_SHEET = new ResourceLocation(Crossroads.MODID, "textures/block/color_sheet.png");

//	@SubscribeEvent
//	@SuppressWarnings("unused")
//	public void magicUsingItemOverlay(RenderGameOverlayEvent.PostLayer e){
//		//TODO definitely need to test this
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
//					builder.vertex(matrix.last().pose(), 0, 120, -3).uv(0, 0.5F).endVertex();
//					builder.vertex(matrix.last().pose(), 117, 120, -3).uv(1, 0.5F).endVertex();
//					builder.vertex(matrix.last().pose(), 117, 60, -3).uv(1, 0).endVertex();
//					builder.vertex(matrix.last().pose(), 0, 60, -3).uv(0, 0).endVertex();
////					buf.vertex(0, 120, -3).uv(0, 1).endVertex();
////					buf.vertex(117, 120, -3).uv(1, 1).endVertex();
////					buf.vertex(117, 60, -3).uv(1, 0).endVertex();
////					buf.vertex(0, 60, -3).uv(0, 0).endVertex();
////					tes.end();
//
////					RenderSystem.setShaderTexture(0, COLOR_SHEET);
////					buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
//					for(int i = 0; i < 4; i++){
//						float fullness = (float) stored.getValues()[i] / BeamCage.CAPACITY;
//						int extension = (int) (72 * fullness);
//						builder.vertex(matrix.last().pose(), 24, 84 + (9 * i), -2).uv(barUSt, barVSt + barVWid * (i + 1)).endVertex();
//						builder.vertex(matrix.last().pose(), 24 + extension, 84 + (9 * i), -2).uv(barUSt + (barUEn - barUSt) * fullness, barVSt + barVWid * (i + 1)).endVertex();
//						builder.vertex(matrix.last().pose(), 24 + extension, 78 + (9 * i), -2).uv(barUSt + (barUEn - barUSt) * fullness, barVSt + barVWid * i).endVertex();
//						builder.vertex(matrix.last().pose(), 24, 78 + (9 * i), -2).uv(barUSt, barVSt + barVWid * i).endVertex();
////						int[] col = new int[4];
////						col[3] = 255;
////						col[i] = 255;//For void, overrides the alpha. Conveniently not an issue
////						buf.vertex(24, 84 + (9 * i), -2).color(col[0], col[1], col[2], col[3]).uv(.25F + (((float) i) * .0625F), .0625F).endVertex();
////						buf.vertex(24 + extension, 84 + (9 * i), -2).color(col[0], col[1], col[2], col[3]).uv(.3125F + (((float) i) * .0625F), .0625F).endVertex();
////						buf.vertex(24 + extension, 78 + (9 * i), -2).color(col[0], col[1], col[2], col[3]).uv(.3125F + (((float) i) * .0625F), 0).endVertex();
////						buf.vertex(24, 78 + (9 * i), -2).color(col[0], col[1], col[2], col[3]).uv(.25F + (((float) i) * .0625F), 0).endVertex();
//					}
////					tes.end();
//
////					RenderSystem.setShaderTexture(0, MAGIC_BAR_FOREGROUND);
////					buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
////					buf.vertex(0, 120, -1).uv(0, 1).endVertex();
////					buf.vertex(117, 120, -1).uv(1, 1).endVertex();
////					buf.vertex(117, 60, -1).uv(1, 0).endVertex();
////					buf.vertex(0, 60, -1).uv(0, 0).endVertex();
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
//					builder.vertex(matrix.last().pose(), 0, 60, -3).uv(0, 0.5F).endVertex();
//					builder.vertex(matrix.last().pose(), 117, 60, -3).uv(1, 0.5F).endVertex();
//					builder.vertex(matrix.last().pose(), 117, 0, -3).uv(1, 0).endVertex();
//					builder.vertex(matrix.last().pose(), 0, 0, -3).uv(0, 0).endVertex();
////					buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
////					buf.vertex(0, 60, -3).uv(0, 1).endVertex();
////					buf.vertex(117, 60, -3).uv(1, 1).endVertex();
////					buf.vertex(117, 0, -3).uv(1, 0).endVertex();
////					buf.vertex(0, 0, -3).uv(0, 0).endVertex();
////					tes.end();
//
////					RenderSystem.setShaderTexture(0, COLOR_SHEET);
////					buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
//					byte[] settings = BeamUsingItem.getSetting(mainStack);
//					for(int i = 0; i < 4; i++){
//						float fullness = (float) settings[i] / 8;
//						int extension = (int) (72 * fullness);
//						builder.vertex(matrix.last().pose(), 24, 24 + (9 * i), -2).uv(barUSt, barVSt + barVWid * (i + 1)).endVertex();
//						builder.vertex(matrix.last().pose(), 24 + extension, 24 + (9 * i), -2).uv(barUSt + (barUEn - barUSt) * fullness, barVSt + barVWid * (i + 1)).endVertex();
//						builder.vertex(matrix.last().pose(), 24 + extension, 18 + (9 * i), -2).uv(barUSt + (barUEn - barUSt) * fullness, barVSt + barVWid * i).endVertex();
//						builder.vertex(matrix.last().pose(), 24, 18 + (9 * i), -2).uv(barUSt, barVSt + barVWid * i).endVertex();
////						int[] col = new int[4];
////						col[3] = 255;
////						col[i] = 255;//For void, overrides the alpha. Conveniently not an issue
////						int extension = 9 * settings[i];
////						buf.vertex(24, 24 + (9 * i), -2).color(col[0], col[1], col[2], col[3]).uv(.25F + (((float) i) * .0625F), .0625F).endVertex();
////						buf.vertex(24 + extension, 24 + (9 * i), -2).color(col[0], col[1], col[2], col[3]).uv(.3125F + (((float) i) * .0625F), .0625F).endVertex();
////						buf.vertex(24 + extension, 18 + (9 * i), -2).color(col[0], col[1], col[2], col[3]).uv(.3125F + (((float) i) * .0625F), 0).endVertex();
////						buf.vertex(24, 18 + (9 * i), -2).color(col[0], col[1], col[2], col[3]).uv(.25F + (((float) i) * .0625F), 0).endVertex();
//					}
////					tes.end();
//
////					RenderSystem.setShaderTexture(0, MAGIC_BAR_FOREGROUND);
////					buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
////					buf.vertex(0, 60, -1).uv(0, 1).endVertex();
////					buf.vertex(117, 60, -1).uv(1, 1).endVertex();
////					buf.vertex(117, 0, -1).uv(1, 0).endVertex();
////					buf.vertex(0, 0, -1).uv(0, 0).endVertex();
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
	public void dilatePlayerTime(TickEvent.ClientTickEvent e){
		if(e.phase == TickEvent.Phase.END){
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
	}

	@SubscribeEvent
	@SuppressWarnings("unused")
	public void keyListener(InputEvent.KeyInputEvent e){
		if(Minecraft.getInstance().screen != null || !Keys.keysInitialized){
			return;//Only accept key hits if the player isn't in a UI
		}
		Player play = Minecraft.getInstance().player;

		ItemStack helmet = play.getItemBySlot(EquipmentSlot.HEAD);
		if(!play.getMainHandItem().isEmpty()){
			int key = Keys.isKeyActiveAndMatch(Keys.controlEnergy, e.getKey(), e.getScanCode()) ? 0 : Keys.isKeyActiveAndMatch(Keys.controlPotential, e.getKey(), e.getScanCode()) ? 1 : Keys.isKeyActiveAndMatch(Keys.controlStability, e.getKey(), e.getScanCode()) ? 2 : Keys.isKeyActiveAndMatch(Keys.controlVoid, e.getKey(), e.getScanCode()) ? 3 : -1;
			ItemStack stack = play.getMainHandItem();
			if(key != -1 && stack.getItem() instanceof BeamUsingItem){
				((BeamUsingItem) stack.getItem()).adjustSetting(Minecraft.getInstance().player, stack, key, !play.isShiftKeyDown());
				return;
			}
		}else if(helmet.getItem() == CRItems.armorGoggles && helmet.hasTag()){
			CompoundTag nbt = helmet.getTag();
			for(EnumGoggleLenses lens : EnumGoggleLenses.values()){
				KeyMapping key = Keys.asKeyMapping(lens.getKey());
				if(key != null && key.consumeClick() && key.isDown() && nbt.contains(lens.toString())){
					CRPackets.channel.sendToServer(new SendGoggleConfigureToServer(lens, !nbt.getBoolean(lens.toString())));
					return;
				}
			}
		}

		//Trigger propeller pack boost when jumping
		KeyMapping boostKey = Keys.asKeyMapping(Keys.boost);
		if(boostKey != null && boostKey.consumeClick()){
			ItemStack chestplate = play.getItemBySlot(EquipmentSlot.CHEST);
			if(play.isFallFlying() && chestplate.getItem() == CRItems.propellerPack && CRItems.propellerPack.getWindLevel(chestplate) > 0){
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
}