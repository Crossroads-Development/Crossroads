package com.Da_Technomancer.crossroads;

import java.awt.Color;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import com.Da_Technomancer.crossroads.API.enums.GoggleLenses;
import com.Da_Technomancer.crossroads.API.packets.SafeCallable;
import com.Da_Technomancer.crossroads.API.technomancy.FieldWorldSavedData;
import com.Da_Technomancer.crossroads.API.technomancy.LooseBeamRenderable;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class EventHandlerClient{

	private static final ResourceLocation TEXTURE_FIELDS = new ResourceLocation(Main.MODID, "textures/model/field.png");

	@SubscribeEvent
	public void drawFieldsAndBeams(RenderWorldLastEvent e){
		Minecraft game = Minecraft.getMinecraft();
		if(game.thePlayer.getItemStackFromSlot(EntityEquipmentSlot.HEAD) != null && game.thePlayer.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == ModItems.moduleGoggles && game.thePlayer.getItemStackFromSlot(EntityEquipmentSlot.HEAD).hasTagCompound()){
			game.mcProfiler.startSection("crossroadsFieldRender");
			Chunk chunk = game.theWorld.getChunkFromBlockCoords(game.thePlayer.getPosition());
			byte[][][] fields = FieldWorldSavedData.get(game.theWorld).fieldNodes.get(FieldWorldSavedData.getLongFromChunk(chunk));
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
				GlStateManager.translate(chunk.getChunkCoordIntPair().getXStart() - game.thePlayer.getPositionEyes(e.getPartialTicks()).xCoord, 0, chunk.getChunkCoordIntPair().getZStart() - game.thePlayer.getPositionEyes(e.getPartialTicks()).zCoord);
				Tessellator tes = Tessellator.getInstance();
				VertexBuffer buf = tes.getBuffer();
				for(int i = 1; i >= 0; i--){
					if(i == 0 ? ModConfig.fieldLinesEnergy.getBoolean() : ModConfig.fieldLinesPotential.getBoolean()){
						GlStateManager.translate(0, .01F, 0);
						if(game.thePlayer.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getTagCompound().hasKey(i == 0 ? GoggleLenses.RUBY.name() : GoggleLenses.EMERALD.name())){
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
						if(game.thePlayer.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getTagCompound().hasKey(i == 0 ? GoggleLenses.RUBY.name() : GoggleLenses.EMERALD.name())){
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
				GlStateManager.translate(beam.x - game.thePlayer.posX, beam.y - game.thePlayer.posY, beam.z - game.thePlayer.posZ);
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
				
				if(beam.lastTick != game.theWorld.getTotalWorldTime()){
					beam.lastTick = game.theWorld.getTotalWorldTime();
					if(beam.lifeTime-- <= 0){
						toRemove.add(beam);
					}
				}
			}
			
			for(LooseBeamRenderable beam : toRemove){
				SafeCallable.beamsToRender.remove(beam);
			}

			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);
			GlStateManager.enableCull();
			GlStateManager.enableLighting();		}
	}

	@SubscribeEvent
	public void voidGoggleGlow(RenderWorldLastEvent e){
		WorldClient world = Minecraft.getMinecraft().theWorld;
		EntityPlayer play = Minecraft.getMinecraft().thePlayer;
		if(world.getTotalWorldTime() % 5 == 0){
			boolean glow = play.getItemStackFromSlot(EntityEquipmentSlot.HEAD) != null && play.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == ModItems.moduleGoggles && play.getItemStackFromSlot(EntityEquipmentSlot.HEAD).hasTagCompound() && play.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getTagCompound().hasKey(GoggleLenses.VOID.name());
			for(Entity ent : world.getLoadedEntityList()){
				if(!ent.getEntityData().hasKey("glow")){
					ent.setGlowing(false);
				}else{
					ent.getEntityData().removeTag("glow");
				}

				if(glow){
					if(ent.isGlowing()){
						ent.getEntityData().setBoolean("glow", true);
					}else{
						ent.setGlowing(true);
					}
				}
			}
		}
	}
}
