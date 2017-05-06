package com.Da_Technomancer.crossroads;

import java.awt.Color;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.enums.GoggleLenses;
import com.Da_Technomancer.crossroads.API.packets.SafeCallable;
import com.Da_Technomancer.crossroads.API.technomancy.FieldWorldSavedData;
import com.Da_Technomancer.crossroads.API.technomancy.LooseBeamRenderable;
import com.Da_Technomancer.crossroads.items.MagicUsingItem;
import com.Da_Technomancer.crossroads.items.ModItems;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityBeaconRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class EventHandlerClient{

	private static final ResourceLocation TEXTURE_FIELDS = new ResourceLocation(Main.MODID, "textures/gui/field.png");

	@SubscribeEvent
	public void drawFieldsAndBeams(RenderWorldLastEvent e){
		Minecraft game = Minecraft.getMinecraft();
		if(game.player.getItemStackFromSlot(EntityEquipmentSlot.HEAD) != null && game.player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == ModItems.moduleGoggles && game.player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).hasTagCompound()){
			game.mcProfiler.startSection(Main.MODNAME + ": Field Render");
			Chunk chunk = game.world.getChunkFromBlockCoords(game.player.getPosition());
			byte[][][] fields = FieldWorldSavedData.get(game.world).fieldNodes.get(MiscOp.getLongFromChunk(chunk));
			if(fields != null){
				GlStateManager.pushMatrix();
				GlStateManager.pushAttrib();
				GlStateManager.disableLighting();
				GlStateManager.enableAlpha();
				GlStateManager.enableBlend();
				float brightX = OpenGlHelper.lastBrightnessX;
				float brightY = OpenGlHelper.lastBrightnessY;
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
				GlStateManager.disableCull();
				GlStateManager.translate(chunk.getPos().getXStart() - game.player.getPositionEyes(e.getPartialTicks()).xCoord, 0, chunk.getPos().getZStart() - game.player.getPositionEyes(e.getPartialTicks()).zCoord);
				Tessellator tes = Tessellator.getInstance();
				VertexBuffer buf = tes.getBuffer();
				for(int i = 1; i >= 0; i--){
					if(i == 0 ? ModConfig.fieldLinesEnergy.getBoolean() : ModConfig.fieldLinesPotential.getBoolean()){
						GlStateManager.translate(0, .01F, 0);
						if(game.player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getTagCompound().hasKey(i == 0 ? GoggleLenses.RUBY.name() : GoggleLenses.EMERALD.name())){
							GlStateManager.color(i == 0 ? 1 : 0, i == 1 ? 1 : 0, 0, .4F);
							GlStateManager.glLineWidth(10F);
							GlStateManager.disableTexture2D();
							buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
							for(int j = 0; j < 8; j++){
								for(int k = 0; k < 8; k++){
									if(j < 7){
										buf.pos(1 + (2 * j), (((float) fields[i][j][k]) + 1F) / 8F, 1 + (2 * k)).endVertex();
										buf.pos(3 + (2 * j), (((float) fields[i][j + 1][k]) + 1F) / 8F, 1 + (2 * k)).endVertex();
									}
									//
									if(k < 7){
										buf.pos(1 + (2 * j), (((float) fields[i][j][k]) + 1F) / 8F, 1 + (2 * k)).endVertex();
										buf.pos(1 + (2 * j), (((float) fields[i][j][k + 1]) + 1F) / 8F, 3 + (2 * k)).endVertex();
									}
								}
							}
							tes.draw();
							GlStateManager.color(1F, 1F, 1F);
							GlStateManager.enableTexture2D();
						}
					}else{
						game.getTextureManager().bindTexture(TEXTURE_FIELDS);
						GlStateManager.translate(0, .01F, 0);
						if(game.player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getTagCompound().hasKey(i == 0 ? GoggleLenses.RUBY.name() : GoggleLenses.EMERALD.name())){
							GlStateManager.color(i == 0 ? 1 : 0, i == 1 ? 1 : 0, 0, .4F);
							buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
							for(int j = 0; j < 7; j++){
								for(int k = 0; k < 7; k++){
									buf.pos(1 + (2 * j), (((float) fields[i][j][k]) + 1F) / 8F, 1 + (2 * k)).tex(0, 0).endVertex();
									buf.pos(3 + (2 * j), (((float) fields[i][j + 1][k]) + 1F) / 8F, 1 + (2 * k)).tex(1, 0).endVertex();
									buf.pos(3 + (2 * j), (((float) fields[i][j + 1][k + 1]) + 1F) / 8F, 3 + (2 * k)).tex(1, 1).endVertex();
									buf.pos(1 + (2 * j), (((float) fields[i][j][k + 1]) + 1F) / 8F, 3 + (2 * k)).tex(0, 1).endVertex();
								}
							}
							tes.draw();
							GlStateManager.color(1F, 1F, 1F);
						}
					}
				}

				GlStateManager.enableCull();
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);
				GlStateManager.disableBlend();
				GlStateManager.disableAlpha();
				GlStateManager.enableLighting();
				GlStateManager.popAttrib();
				GlStateManager.popMatrix();
			}
			game.mcProfiler.endSection();
		}

		if(!SafeCallable.beamsToRender.isEmpty()){
			GlStateManager.disableLighting();
			GlStateManager.disableCull();
			float brightX = OpenGlHelper.lastBrightnessX;
			float brightY = OpenGlHelper.lastBrightnessY;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
			Minecraft.getMinecraft().getTextureManager().bindTexture(TileEntityBeaconRenderer.TEXTURE_BEACON_BEAM);

			ArrayList<LooseBeamRenderable> toRemove = new ArrayList<LooseBeamRenderable>();

			for(LooseBeamRenderable beam : SafeCallable.beamsToRender){
				GlStateManager.pushMatrix();
				GlStateManager.pushAttrib();
				Color col = new Color(beam.color);
				GlStateManager.color(col.getRed() / 255F, col.getGreen() / 255F, col.getBlue() / 255F);
				GlStateManager.translate(beam.x - game.player.posX, beam.y - game.player.posY, beam.z - game.player.posZ);
				GlStateManager.rotate(-beam.angleY, 0, 1, 0);
				GlStateManager.rotate(beam.angleX + 90F, 1, 0, 0);
				final double small = -(beam.width / 16D);
				final double big = (beam.width / 16D);
				final int length = beam.length;

				Tessellator tes = Tessellator.getInstance();
				VertexBuffer buf = tes.getBuffer();
				buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				//+Z
				buf.pos(small, length, big).tex(1, 0).endVertex();
				buf.pos(small, 0, big).tex(1, length).endVertex();
				buf.pos(big, 0, big).tex(0, length).endVertex();
				buf.pos(big, length, big).tex(0, 0).endVertex();
				//-Z
				buf.pos(big, length, small).tex(1, 0).endVertex();
				buf.pos(big, 0, small).tex(1, length).endVertex();
				buf.pos(small, 0, small).tex(0, length).endVertex();
				buf.pos(small, length, small).tex(0, 0).endVertex();
				//-X
				buf.pos(small, length, small).tex(1, 0).endVertex();
				buf.pos(small, 0, small).tex(1, length).endVertex();
				buf.pos(small, 0, big).tex(0, length).endVertex();
				buf.pos(small, length, big).tex(0, 0).endVertex();
				//+X
				buf.pos(big, length, big).tex(1, 0).endVertex();
				buf.pos(big, 0, big).tex(1, length).endVertex();
				buf.pos(big, 0, small).tex(0, length).endVertex();
				buf.pos(big, length, small).tex(0, 0).endVertex();
				tes.draw();
				GlStateManager.color(1, 1, 1);
				GlStateManager.popAttrib();
				GlStateManager.popMatrix();

				if(beam.lastTick != game.world.getTotalWorldTime()){
					beam.lastTick = game.world.getTotalWorldTime();
					if(beam.lifeTime-- < 0){
						toRemove.add(beam);
					}
				}
			}

			for(LooseBeamRenderable beam : toRemove){
				SafeCallable.beamsToRender.remove(beam);
			}

			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);
			GlStateManager.enableCull();
			GlStateManager.enableLighting();
		}
	}

	@SubscribeEvent
	public void voidGoggleGlow(RenderWorldLastEvent e){
		WorldClient world = Minecraft.getMinecraft().world;
		if(world.getTotalWorldTime() % 5 == 0){
			ItemStack helmet = Minecraft.getMinecraft().player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
			boolean glow = helmet != null && helmet.getItem() == ModItems.moduleGoggles && helmet.hasTagCompound() && helmet.getTagCompound().hasKey(GoggleLenses.VOID.name());
			for(Entity ent : world.getLoadedEntityList()){
				NBTTagCompound entNBT = ent.getEntityData();
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
	}

	private static final ResourceLocation MAGIC_BAR_BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/magic_info_back.png");
	private static final ResourceLocation MAGIC_BAR_FOREGROUND = new ResourceLocation(Main.MODID, "textures/gui/magic_info_front.png");
	private static final ResourceLocation COLOR_SHEET = new ResourceLocation(Main.MODID, "textures/blocks/color_sheet.png");
	
	@SubscribeEvent
	public void magicUsingItemOverlay(RenderGameOverlayEvent e){
		if(e.getType() == ElementType.HOTBAR){
			EntityPlayer player = Minecraft.getMinecraft().player;
			ItemStack offStack = player.getHeldItem(EnumHand.OFF_HAND);
			if(offStack.getItem() == ModItems.beamCage){
				NBTTagCompound nbt = offStack.hasTagCompound() ? offStack.getTagCompound() : new NBTTagCompound();
				GlStateManager.pushMatrix();
				GlStateManager.pushAttrib();
				GlStateManager.enableBlend();
				Minecraft.getMinecraft().getTextureManager().bindTexture(MAGIC_BAR_BACKGROUND);
				Tessellator tes = Tessellator.getInstance();
				VertexBuffer buf = tes.getBuffer();
				buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				buf.pos(0, 120, -3).tex(0, 1).endVertex();
				buf.pos(117, 120, -3).tex(1, 1).endVertex();
				buf.pos(117, 60, -3).tex(1, 0).endVertex();
				buf.pos(0, 60, -3).tex(0, 0).endVertex();
				tes.draw();
				
				Minecraft.getMinecraft().getTextureManager().bindTexture(COLOR_SHEET);
				buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
				for(int i = 0; i < 4; i++){
					int extension = 9 * nbt.getInteger(i == 0 ? "stored_ENERGY" : i == 1 ? "stored_POTENTIAL" : i == 2 ? "stored_STABILITY" : "stored_VOID") / 128;
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
			if(mainStack.getItem() instanceof MagicUsingItem){
				NBTTagCompound nbt = mainStack.hasTagCompound() ? mainStack.getTagCompound() : new NBTTagCompound();
				GlStateManager.pushMatrix();
				GlStateManager.pushAttrib();
				GlStateManager.enableBlend();
				Minecraft.getMinecraft().getTextureManager().bindTexture(MAGIC_BAR_BACKGROUND);
				Tessellator tes = Tessellator.getInstance();
				VertexBuffer buf = tes.getBuffer();
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
				GlStateManager.disableAlpha();
				GlStateManager.color(1, 1, 1);
				GlStateManager.disableBlend();
				GlStateManager.popAttrib();
				GlStateManager.popMatrix();
			}
		}
	}
}
