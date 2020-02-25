package com.Da_Technomancer.crossroads;

import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.packets.SafeCallable;
import com.Da_Technomancer.crossroads.API.packets.SendGoggleConfigureToServer;
import com.Da_Technomancer.crossroads.API.technomancy.EnumGoggleLenses;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.technomancy.BeamCage;
import com.Da_Technomancer.crossroads.items.technomancy.BeamUsingItem;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.render.IVisualEffect;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public final class EventHandlerClient{

	private static final Random RAND = new Random();

	@SubscribeEvent
	@SuppressWarnings("unused")
	public void drawFieldsAndBeams(RenderWorldLastEvent e){
		Minecraft game = Minecraft.getInstance();
		ItemStack helmet = Minecraft.getInstance().player.getItemStackFromSlot(EquipmentSlotType.HEAD);

		//Goggle entity glowing
		if(game.world.getGameTime() % 5 == 0){
			boolean glow = helmet.getItem() == CRItems.moduleGoggles && helmet.hasTag() && helmet.getTag().getBoolean(EnumGoggleLenses.VOID.name());
			for(Entity ent : game.world.getAllEntities()){
				CompoundNBT entNBT = ent.getPersistentData();
				if(entNBT == null){
					continue;//Should never be null, but some mods override the entNBT method to return null for some reason
				}
				if(!entNBT.contains("glow")){
					ent.setGlowing(false);
				}else{
					entNBT.remove("glow");
				}

				if(glow){
					if(ent.isGlowing()){
						entNBT.putBoolean("glow", true);
					}else{
						ent.setGlowing(true);
					}
				}
			}
		}

		//IVisualEffects
		if(!SafeCallable.effectsToRender.isEmpty()){
			game.getProfiler().startSection(Crossroads.MODNAME + ": Visual Effects Draw");

			GlStateManager.disableLighting();
			GlStateManager.disableCull();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			CRRenderUtil.setBrightLighting();

			ArrayList<IVisualEffect> toRemove = new ArrayList<>();
			Tessellator tes = Tessellator.getInstance();
			BufferBuilder buf = tes.getBuffer();
			long worldTime = game.world.getGameTime();

			for(IVisualEffect effect : SafeCallable.effectsToRender){
				GlStateManager.pushMatrix();
				GlStateManager.pushLightingAttributes();
				Vec3d eyePos = game.player.getEyePosition(e.getPartialTicks());
				if(effect.render(tes, buf, worldTime, eyePos.x, eyePos.y, eyePos.z, game.player.getLook(e.getPartialTicks()), RAND, e.getPartialTicks())){
					toRemove.add(effect);
				}

				GlStateManager.popAttributes();
				GlStateManager.popMatrix();
			}

			SafeCallable.effectsToRender.removeAll(toRemove);

//			CRRenderUtil.restoreLighting(lighting);
			GlStateManager.enableCull();
			GlStateManager.disableBlend();
			GlStateManager.enableLighting();

			game.getProfiler().endSection();
		}
	}

	private static final ResourceLocation MAGIC_BAR_BACKGROUND = new ResourceLocation(Crossroads.MODID, "textures/gui/magic_info_back.png");
	private static final ResourceLocation MAGIC_BAR_FOREGROUND = new ResourceLocation(Crossroads.MODID, "textures/gui/magic_info_front.png");
	private static final ResourceLocation COLOR_SHEET = new ResourceLocation(Crossroads.MODID, "textures/blocks/color_sheet.png");

	@SubscribeEvent
	@SuppressWarnings("unused")
	public void magicUsingItemOverlay(RenderGameOverlayEvent e){
		if(e.getType() == ElementType.HOTBAR){
			PlayerEntity player = Minecraft.getInstance().player;
			ItemStack offStack = player.getHeldItem(Hand.OFF_HAND);
			if(offStack.getItem() == CRItems.beamCage){
				BeamUnit stored = BeamCage.getStored(offStack);
				GlStateManager.pushMatrix();
				GlStateManager.pushLightingAttributes();
				GlStateManager.enableBlend();
				Minecraft.getInstance().getTextureManager().bindTexture(MAGIC_BAR_BACKGROUND);
				Tessellator tes = Tessellator.getInstance();
				BufferBuilder buf = tes.getBuffer();
				buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				buf.pos(0, 120, -3).tex(0, 1).endVertex();
				buf.pos(117, 120, -3).tex(1, 1).endVertex();
				buf.pos(117, 60, -3).tex(1, 0).endVertex();
				buf.pos(0, 60, -3).tex(0, 0).endVertex();
				tes.draw();

				Minecraft.getInstance().getTextureManager().bindTexture(COLOR_SHEET);
				buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
				for(int i = 0; i < 4; i++){
					int extension = 9 * stored.getValues()[i] / 128;
					buf.pos(24, 84 + (9 * i), -2).tex(.25F + (((float) i) * .0625F), .0625F).color(i == 0 ? 255 : 0, i == 1 ? 255 : 0, i == 2 ? 255 : 0, 255).endVertex();
					buf.pos(24 + extension, 84 + (9 * i), -2).tex(.3125F + (((float) i) * .0625F), .0625F).color(i == 0 ? 255 : 0, i == 1 ? 255 : 0, i == 2 ? 255 : 0, 255).endVertex();
					buf.pos(24 + extension, 78 + (9 * i), -2).tex(.3125F + (((float) i) * .0625F), 0).color(i == 0 ? 255 : 0, i == 1 ? 255 : 0, i == 2 ? 255 : 0, 255).endVertex();
					buf.pos(24, 78 + (9 * i), -2).tex(.25F + (((float) i) * .0625F), 0).color(i == 0 ? 255 : 0, i == 1 ? 255 : 0, i == 2 ? 255 : 0, 255).endVertex();
				}
				tes.draw();

				Minecraft.getInstance().getTextureManager().bindTexture(MAGIC_BAR_FOREGROUND);
				buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				buf.pos(0, 120, -1).tex(0, 1).endVertex();
				buf.pos(117, 120, -1).tex(1, 1).endVertex();
				buf.pos(117, 60, -1).tex(1, 0).endVertex();
				buf.pos(0, 60, -1).tex(0, 0).endVertex();
				tes.draw();

				Minecraft.getInstance().fontRenderer.drawString(offStack.getDisplayName().getFormattedText(), 16, 65, Color.DARK_GRAY.getRGB());
				GlStateManager.disableBlend();
				GlStateManager.color3f(1, 1, 1);
				GlStateManager.disableBlend();
				GlStateManager.popAttributes();
				GlStateManager.popMatrix();
			}
			ItemStack mainStack = player.getHeldItem(Hand.MAIN_HAND);
			if(mainStack.getItem() instanceof BeamUsingItem){
				CompoundNBT nbt = mainStack.hasTag() ? mainStack.getTag() : new CompoundNBT();
				GlStateManager.pushMatrix();
				GlStateManager.pushLightingAttributes();
				GlStateManager.enableBlend();
				Minecraft.getInstance().getTextureManager().bindTexture(MAGIC_BAR_BACKGROUND);
				Tessellator tes = Tessellator.getInstance();
				BufferBuilder buf = tes.getBuffer();
				buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				buf.pos(0, 60, -3).tex(0, 1).endVertex();
				buf.pos(117, 60, -3).tex(1, 1).endVertex();
				buf.pos(117, 0, -3).tex(1, 0).endVertex();
				buf.pos(0, 0, -3).tex(0, 0).endVertex();
				tes.draw();

				Minecraft.getInstance().getTextureManager().bindTexture(COLOR_SHEET);
				buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
				for(int i = 0; i < 4; i++){
					int extension = 9 * nbt.getInt(i == 0 ? "ENERGY" : i == 1 ? "POTENTIAL" : i == 2 ? "STABILITY" : "VOID");
					buf.pos(24, 24 + (9 * i), -2).tex(.25F + (((float) i) * .0625F), .0625F).color(i == 0 ? 255 : 0, i == 1 ? 255 : 0, i == 2 ? 255 : 0, 255).endVertex();
					buf.pos(24 + extension, 24 + (9 * i), -2).tex(.3125F + (((float) i) * .0625F), .0625F).color(i == 0 ? 255 : 0, i == 1 ? 255 : 0, i == 2 ? 255 : 0, 255).endVertex();
					buf.pos(24 + extension, 18 + (9 * i), -2).tex(.3125F + (((float) i) * .0625F), 0).color(i == 0 ? 255 : 0, i == 1 ? 255 : 0, i == 2 ? 255 : 0, 255).endVertex();
					buf.pos(24, 18 + (9 * i), -2).tex(.25F + (((float) i) * .0625F), 0).color(i == 0 ? 255 : 0, i == 1 ? 255 : 0, i == 2 ? 255 : 0, 255).endVertex();
				}
				tes.draw();

				Minecraft.getInstance().getTextureManager().bindTexture(MAGIC_BAR_FOREGROUND);
				buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				buf.pos(0, 60, -1).tex(0, 1).endVertex();
				buf.pos(117, 60, -1).tex(1, 1).endVertex();
				buf.pos(117, 0, -1).tex(1, 0).endVertex();
				buf.pos(0, 0, -1).tex(0, 0).endVertex();
				tes.draw();

				Minecraft.getInstance().fontRenderer.drawString(mainStack.getDisplayName().getFormattedText(), 16, 5, Color.DARK_GRAY.getRGB());

				GlStateManager.disableBlend();
				GlStateManager.color3f(1, 1, 1);
				GlStateManager.disableBlend();
				GlStateManager.popAttributes();
				GlStateManager.popMatrix();
			}
		}
	}

	@SubscribeEvent
	@SuppressWarnings("unused")
	public void dilatePlayerTime(TickEvent.ClientTickEvent e){
		if(e.phase == TickEvent.Phase.END){
			PlayerEntity player = Minecraft.getInstance().player;
			if(player == null){
				return;
			}
			if(SafeCallable.playerTickCount > 0){
				for(int i = 0; i < SafeCallable.playerTickCount; i++){
					player.tick();
				}
				SafeCallable.playerTickCount = 0;
			}
		}
	}

	@SubscribeEvent
	@SuppressWarnings("unused")
	public void elementKeys(InputEvent.KeyInputEvent e){
		PlayerEntity play = Minecraft.getInstance().player;
		if(Minecraft.getInstance().currentScreen != null){
			return;//Only accept key hits if the player isn't in a UI
		}

		ItemStack helmet = play.getItemStackFromSlot(EquipmentSlotType.HEAD);
		if(!play.getHeldItemMainhand().isEmpty()){
			int key = Keys.controlEnergy.matchesKey(e.getKey(), e.getScanCode()) ? 0 : Keys.controlPotential.matchesKey(e.getKey(), e.getScanCode()) ? 1 : Keys.controlStability.matchesKey(e.getKey(), e.getScanCode()) ? 2 : Keys.controlVoid.matchesKey(e.getKey(), e.getScanCode()) ? 3 : -1;
			ItemStack stack = play.getHeldItemMainhand();
			if(key != -1 && stack.getItem() instanceof BeamUsingItem){
				((BeamUsingItem) stack.getItem()).adjustSetting(Minecraft.getInstance().player, stack, key, play.isSneaking());
			}
		}else if(helmet.getItem() == CRItems.moduleGoggles && helmet.hasTag()){
			CompoundNBT nbt = helmet.getTag();
			for(EnumGoggleLenses lens : EnumGoggleLenses.values()){
				KeyBinding key = lens.getKey();
				if(key != null && key.isPressed() && key.isKeyDown() && nbt.contains(lens.name())){
					CRPackets.channel.sendToServer(new SendGoggleConfigureToServer(lens, !nbt.getBoolean(lens.name())));
					break;
				}
			}
		}
	}
}