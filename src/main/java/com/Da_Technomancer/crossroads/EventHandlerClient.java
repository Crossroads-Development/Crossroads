package com.Da_Technomancer.crossroads;

import org.lwjgl.opengl.GL11;

import com.Da_Technomancer.crossroads.API.enums.GoggleLenses;
import com.Da_Technomancer.crossroads.API.fields.FieldWorldSavedData;
import com.Da_Technomancer.crossroads.items.ModItems;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class EventHandlerClient{

	private static final ResourceLocation TEXTURE_FIELDS = new ResourceLocation(Main.MODID, "textures/model/field.png");

	@SubscribeEvent
	public void drawFields(RenderWorldLastEvent e){
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
				game.getTextureManager().bindTexture(TEXTURE_FIELDS);
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
				GlStateManager.disableCull();
				GlStateManager.translate(chunk.getChunkCoordIntPair().getXStart() - game.thePlayer.getPositionEyes(e.getPartialTicks()).xCoord, 0, chunk.getChunkCoordIntPair().getZStart() - game.thePlayer.getPositionEyes(e.getPartialTicks()).zCoord);
				Tessellator tes = Tessellator.getInstance();
				VertexBuffer buf = tes.getBuffer();
				for(int i = 1; i >= 0; i--){
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
	}
}
