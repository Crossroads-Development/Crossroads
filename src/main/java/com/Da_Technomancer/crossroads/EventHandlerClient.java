package com.Da_Technomancer.crossroads;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SafeCallable;
import com.Da_Technomancer.crossroads.API.packets.SendGoggleConfigureToServer;
import com.Da_Technomancer.crossroads.API.technomancy.EnumGoggleLenses;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.technomancy.BeamCage;
import com.Da_Technomancer.crossroads.items.technomancy.BeamUsingItem;
import com.Da_Technomancer.crossroads.items.technomancy.PrototypeWatch;
import com.Da_Technomancer.crossroads.render.IVisualEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public final class EventHandlerClient{

	private static final Random RAND = new Random();

	@SubscribeEvent
	public void drawFieldsAndBeams(RenderWorldLastEvent e){
		Minecraft game = Minecraft.getMinecraft();
		ItemStack helmet = Minecraft.getMinecraft().player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

		//Goggle entity glowing
		if(game.world.getTotalWorldTime() % 5 == 0){
			boolean glow = helmet.getItem() == ModItems.moduleGoggles && helmet.hasTagCompound() && helmet.getTagCompound().getBoolean(EnumGoggleLenses.VOID.name());
			for(Entity ent : game.world.getLoadedEntityList()){
				NBTTagCompound entNBT = ent.getEntityData();
				if(entNBT == null){
					continue;//Should never be null, but some mods override the entNBT method to return null for some reason
				}
				if(!entNBT.hasKey("glow")){
					ent.setGlowing(false);
				}else{
					entNBT.removeTag("glow");
				}

				if(glow){
					if(ent.isGlowing()){
						entNBT.setBoolean("glow", true);
					}else{
						ent.setGlowing(true);
					}
				}
			}
		}

		//IVisualEffects
		if(!SafeCallable.effectsToRender.isEmpty()){
			game.profiler.startSection(Main.MODNAME + ": Visual Effects Draw");

			GlStateManager.disableLighting();
			GlStateManager.disableCull();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			float brightX = OpenGlHelper.lastBrightnessX;
			float brightY = OpenGlHelper.lastBrightnessY;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);

			ArrayList<IVisualEffect> toRemove = new ArrayList<>();
			Tessellator tes = Tessellator.getInstance();
			BufferBuilder buf = tes.getBuffer();
			long worldTime = game.world.getTotalWorldTime();

			for(IVisualEffect effect : SafeCallable.effectsToRender){
				GlStateManager.pushMatrix();
				GlStateManager.pushAttrib();

				if(effect.render(tes, buf, worldTime, game.player.posX, game.player.posY, game.player.posZ, game.player.getLook(e.getPartialTicks()), RAND, e.getPartialTicks())){
					toRemove.add(effect);
				}

				GlStateManager.popAttrib();
				GlStateManager.popMatrix();
			}

			SafeCallable.effectsToRender.removeAll(toRemove);

			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);
			GlStateManager.enableCull();
			GlStateManager.disableBlend();
			GlStateManager.enableLighting();

			game.profiler.endSection();
		}
	}

	private static final ResourceLocation MAGIC_BAR_BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/magic_info_back.png");
	private static final ResourceLocation MAGIC_BAR_FOREGROUND = new ResourceLocation(Main.MODID, "textures/gui/magic_info_front.png");
	private static final ResourceLocation WATCH_BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/watch_info_back.png");
	private static final ResourceLocation COLOR_SHEET = new ResourceLocation(Main.MODID, "textures/blocks/color_sheet.png");

	@SubscribeEvent
	public void magicUsingItemOverlay(RenderGameOverlayEvent e){
		if(e.getType() == ElementType.HOTBAR){
			EntityPlayer player = Minecraft.getMinecraft().player;
			ItemStack offStack = player.getHeldItem(EnumHand.OFF_HAND);
			if(offStack.getItem() == ModItems.beamCage){
				BeamUnit stored = BeamCage.getStored(offStack);
				GlStateManager.pushMatrix();
				GlStateManager.pushAttrib();
				GlStateManager.enableBlend();
				Minecraft.getMinecraft().getTextureManager().bindTexture(MAGIC_BAR_BACKGROUND);
				Tessellator tes = Tessellator.getInstance();
				BufferBuilder buf = tes.getBuffer();
				buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				buf.pos(0, 120, -3).tex(0, 1).endVertex();
				buf.pos(117, 120, -3).tex(1, 1).endVertex();
				buf.pos(117, 60, -3).tex(1, 0).endVertex();
				buf.pos(0, 60, -3).tex(0, 0).endVertex();
				tes.draw();

				Minecraft.getMinecraft().getTextureManager().bindTexture(COLOR_SHEET);
				buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
				for(int i = 0; i < 4; i++){
					int extension = 9 * (stored == null ? 0 : stored.getValues()[i]) / 128;
					buf.pos(24, 84 + (9 * i), -2).tex(.25F + (((float) i) * .0625F), .0625F).color(i == 0 ? 255 : 0, i == 1 ? 255 : 0, i == 2 ? 255 : 0, 255).endVertex();
					buf.pos(24 + extension, 84 + (9 * i), -2).tex(.3125F + (((float) i) * .0625F), .0625F).color(i == 0 ? 255 : 0, i == 1 ? 255 : 0, i == 2 ? 255 : 0, 255).endVertex();
					buf.pos(24 + extension, 78 + (9 * i), -2).tex(.3125F + (((float) i) * .0625F), 0).color(i == 0 ? 255 : 0, i == 1 ? 255 : 0, i == 2 ? 255 : 0, 255).endVertex();
					buf.pos(24, 78 + (9 * i), -2).tex(.25F + (((float) i) * .0625F), 0).color(i == 0 ? 255 : 0, i == 1 ? 255 : 0, i == 2 ? 255 : 0, 255).endVertex();
				}
				tes.draw();

				Minecraft.getMinecraft().getTextureManager().bindTexture(MAGIC_BAR_FOREGROUND);
				buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				buf.pos(0, 120, -1).tex(0, 1).endVertex();
				buf.pos(117, 120, -1).tex(1, 1).endVertex();
				buf.pos(117, 60, -1).tex(1, 0).endVertex();
				buf.pos(0, 60, -1).tex(0, 0).endVertex();
				tes.draw();

				Minecraft.getMinecraft().fontRenderer.drawString(offStack.getDisplayName(), 16, 65, Color.DARK_GRAY.getRGB());
				GlStateManager.disableAlpha();
				GlStateManager.color(1, 1, 1);
				GlStateManager.disableBlend();
				GlStateManager.popAttrib();
				GlStateManager.popMatrix();
			}
			ItemStack mainStack = player.getHeldItem(EnumHand.MAIN_HAND);
			if(mainStack.getItem() instanceof BeamUsingItem){
				NBTTagCompound nbt = mainStack.hasTagCompound() ? mainStack.getTagCompound() : new NBTTagCompound();
				GlStateManager.pushMatrix();
				GlStateManager.pushAttrib();
				GlStateManager.enableBlend();
				Minecraft.getMinecraft().getTextureManager().bindTexture(MAGIC_BAR_BACKGROUND);
				Tessellator tes = Tessellator.getInstance();
				BufferBuilder buf = tes.getBuffer();
				buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				buf.pos(0, 60, -3).tex(0, 1).endVertex();
				buf.pos(117, 60, -3).tex(1, 1).endVertex();
				buf.pos(117, 0, -3).tex(1, 0).endVertex();
				buf.pos(0, 0, -3).tex(0, 0).endVertex();
				tes.draw();

				Minecraft.getMinecraft().getTextureManager().bindTexture(COLOR_SHEET);
				buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
				for(int i = 0; i < 4; i++){
					int extension = 9 * nbt.getInteger(i == 0 ? "ENERGY" : i == 1 ? "POTENTIAL" : i == 2 ? "STABILITY" : "VOID");
					buf.pos(24, 24 + (9 * i), -2).tex(.25F + (((float) i) * .0625F), .0625F).color(i == 0 ? 255 : 0, i == 1 ? 255 : 0, i == 2 ? 255 : 0, 255).endVertex();
					buf.pos(24 + extension, 24 + (9 * i), -2).tex(.3125F + (((float) i) * .0625F), .0625F).color(i == 0 ? 255 : 0, i == 1 ? 255 : 0, i == 2 ? 255 : 0, 255).endVertex();
					buf.pos(24 + extension, 18 + (9 * i), -2).tex(.3125F + (((float) i) * .0625F), 0).color(i == 0 ? 255 : 0, i == 1 ? 255 : 0, i == 2 ? 255 : 0, 255).endVertex();
					buf.pos(24, 18 + (9 * i), -2).tex(.25F + (((float) i) * .0625F), 0).color(i == 0 ? 255 : 0, i == 1 ? 255 : 0, i == 2 ? 255 : 0, 255).endVertex();
				}
				tes.draw();

				Minecraft.getMinecraft().getTextureManager().bindTexture(MAGIC_BAR_FOREGROUND);
				buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				buf.pos(0, 60, -1).tex(0, 1).endVertex();
				buf.pos(117, 60, -1).tex(1, 1).endVertex();
				buf.pos(117, 0, -1).tex(1, 0).endVertex();
				buf.pos(0, 0, -1).tex(0, 0).endVertex();
				tes.draw();

				Minecraft.getMinecraft().fontRenderer.drawString(mainStack.getDisplayName(), 16, 5, Color.DARK_GRAY.getRGB());

				//Special Watch overlay
				if(mainStack.getItem() == ModItems.watch){
					GlStateManager.color(1, 1, 1);
					Minecraft.getMinecraft().getTextureManager().bindTexture(WATCH_BACKGROUND);
					buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
					buf.pos(117, 60, -1).tex(0, 1).endVertex();
					buf.pos(234, 60, -1).tex(1, 1).endVertex();
					buf.pos(234, 0, -1).tex(1, 0).endVertex();
					buf.pos(117, 0, -1).tex(0, 0).endVertex();
					tes.draw();

					double[] values = PrototypeWatch.getValues(mainStack);

					if(values != null){
						for(int i = 0; i < 3; i++){
							values[i] = MiscUtil.betterRound(values[i], 3);
							Minecraft.getMinecraft().fontRenderer.drawString("" + values[i], 146 - Minecraft.getMinecraft().fontRenderer.getStringWidth("" + values[i]) / 2, 16 + 10 * i, Color.DARK_GRAY.getRGB());
						}
					}
				}


				GlStateManager.disableAlpha();
				GlStateManager.color(1, 1, 1);
				GlStateManager.disableBlend();
				GlStateManager.popAttrib();
				GlStateManager.popMatrix();
			}
		}
	}

	@SubscribeEvent
	public void dilatePlayerTime(ClientTickEvent e){
		if(e.phase == Phase.END){
			EntityPlayer player = Minecraft.getMinecraft().player;
			if(player == null){
				return;
			}
			NBTTagCompound entNBT = player.getEntityData();

			if(entNBT.getBoolean(EventHandlerCommon.MAIN_KEY)){
				if(!entNBT.getBoolean(EventHandlerCommon.SUB_KEY)){
					player.updateBlocked = false;
				}
				entNBT.setBoolean(EventHandlerCommon.MAIN_KEY, false);
				entNBT.setBoolean(EventHandlerCommon.SUB_KEY, false);
			}


			if(SafeCallable.playerTickCount == 0){
				entNBT.setBoolean(EventHandlerCommon.MAIN_KEY, true);
				if(player.updateBlocked){
					entNBT.setBoolean(EventHandlerCommon.SUB_KEY, true);
				}else{
					player.updateBlocked = true;
				}
			}

			if(SafeCallable.playerTickCount == 0){
				if(player.updateBlocked){
					entNBT.setBoolean("fStop", true);
				}else{
					player.updateBlocked = true;
				}
			}else if(!player.updateBlocked){
				int extraTicks = SafeCallable.playerTickCount - 1;
				for(int i = 0; i < extraTicks; i++){
					player.onUpdate();
				}
			}

			SafeCallable.playerTickCount = 1;
		}
	}

	@SubscribeEvent
	public void toggleGoggles(InputEvent.KeyInputEvent e){
		EntityPlayer play = Minecraft.getMinecraft().player;
		ItemStack helmet = play.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		if(play.getHeldItemMainhand().isEmpty() && helmet.getItem() == ModItems.moduleGoggles && helmet.hasTagCompound()){
			NBTTagCompound nbt = helmet.getTagCompound();
			for(EnumGoggleLenses lens : EnumGoggleLenses.values()){
				KeyBinding key = lens.getKey();
				if(key != null && key.isPressed() && key.isKeyDown() && nbt.hasKey(lens.name())){
					ModPackets.network.sendToServer(new SendGoggleConfigureToServer(lens, !nbt.getBoolean(lens.name())));
					break;
				}
			}
		}
	}
}