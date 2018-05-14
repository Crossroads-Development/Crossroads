package com.Da_Technomancer.crossroads;

import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.alchemy.LooseArcRenderable;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SafeCallable;
import com.Da_Technomancer.crossroads.API.packets.SendGoggleConfigureToServer;
import com.Da_Technomancer.crossroads.API.technomancy.ChunkField;
import com.Da_Technomancer.crossroads.API.technomancy.EnumGoggleLenses;
import com.Da_Technomancer.crossroads.API.technomancy.FieldWorldSavedData;
import com.Da_Technomancer.crossroads.API.technomancy.LooseBeamRenderable;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.technomancy.MagicUsingItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityBeaconRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public final class EventHandlerClient{

	private static final ResourceLocation TEXTURE_FIELDS = new ResourceLocation(Main.MODID, "textures/gui/field.png");

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

		//Fields
		if(helmet.getItem() == ModItems.moduleGoggles && helmet.hasTagCompound()){
			game.mcProfiler.startSection(Main.MODNAME + ": Field Render");
			Chunk chunk = game.world.getChunkFromBlockCoords(game.player.getPosition());
			ChunkField fields = FieldWorldSavedData.get(game.world).fieldNodes.get(MiscOp.getLongFromChunk(chunk));
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
				GlStateManager.translate(chunk.getPos().getXStart() - game.player.getPositionEyes(e.getPartialTicks()).x, 0, chunk.getPos().getZStart() - game.player.getPositionEyes(e.getPartialTicks()).z);
				Tessellator tes = Tessellator.getInstance();
				BufferBuilder buf = tes.getBuffer();
				if(helmet.getTagCompound().hasKey(EnumGoggleLenses.QUARTZ.name())){

					//Rate layer
					GlStateManager.translate(0, .01F, 0);
					if(ModConfig.fieldLinesPotential.getBoolean()){
						GlStateManager.color(0, 1, 0);
						GlStateManager.glLineWidth(10F);
						GlStateManager.disableTexture2D();
						buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
						for(int j = 0; j < 16; j++){
							for(int k = 0; k < 16; k++){
								if(j != 15){
									buf.pos(0.5D + j, (((float) fields.nodes[j][k]) + 1F) / 8F, 0.5D + k).endVertex();
									buf.pos(1.5D + j, (((float) fields.nodes[j + 1][k]) + 1F) / 8F, 0.5D + k).endVertex();
								}
								//
								if(k != 15){
									buf.pos(0.5D + j, (((float) fields.nodes[j][k]) + 1F) / 8F, 0.5D + k).endVertex();
									buf.pos(0.5D + j, (((float) fields.nodes[j][k + 1]) + 1F) / 8F, 1.5D + k).endVertex();
								}
							}
						}
						tes.draw();
						GlStateManager.color(1F, 1F, 1F);
						GlStateManager.enableTexture2D();
					}else{
						game.getTextureManager().bindTexture(TEXTURE_FIELDS);
						GlStateManager.color(0, 1, 0, .3F);
						buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
						for(int j = 0; j < 15; j++){
							for(int k = 0; k < 15; k++){
								buf.pos(0.5D + j, (((float) fields.nodes[j][k]) + 1F) / 8F, 0.5D + k).tex(0, 0).endVertex();
								buf.pos(1.5D + j, (((float) fields.nodes[j + 1][k]) + 1F) / 8F, 0.5D + k).tex(1, 0).endVertex();
								buf.pos(1.5D + j, (((float) fields.nodes[j + 1][k + 1]) + 1F) / 8F, 1.5D + k).tex(1, 1).endVertex();
								buf.pos(0.5D + j, (((float) fields.nodes[j][k + 1]) + 1F) / 8F, 1.5D + k).tex(0, 1).endVertex();
							}
						}
						tes.draw();
						GlStateManager.color(1F, 1F, 1F);
					}

					//Flux layer
					GlStateManager.translate(0, .01F, 0);
					game.getTextureManager().bindTexture(TEXTURE_FIELDS);
					GlStateManager.color(1, 0, 0, .3F);
					buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
					buf.pos(0.5D, (((float) fields.flux) + 1F) / 8F, 0.5D).tex(0, 0).endVertex();
					buf.pos(15.5D, (((float) fields.flux) + 1F) / 8F, 0.5D).tex(1, 0).endVertex();
					buf.pos(15.5D, (((float) fields.flux) + 1F) / 8F, 15.5D).tex(1, 1).endVertex();
					buf.pos(0.5D, (((float) fields.flux) + 1F) / 8F, 15.5D).tex(0, 1).endVertex();
					tes.draw();
					GlStateManager.color(1F, 1F, 1F);
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

		//Loose beams
		if(!SafeCallable.beamsToRender.isEmpty()){
			game.mcProfiler.startSection(Main.MODNAME + ": Loose beam render");

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
				BufferBuilder buf = tes.getBuffer();
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

			game.mcProfiler.endSection();
		}

		//Lightning arcs
		if(!SafeCallable.arcsToRender.isEmpty()){
			game.mcProfiler.startSection(Main.MODNAME + ": Lightning arc render");

			GlStateManager.enableBlend();
			GlStateManager.disableLighting();
			GlStateManager.disableCull();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.disableTexture2D();
			float brightX = OpenGlHelper.lastBrightnessX;
			float brightY = OpenGlHelper.lastBrightnessY;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);

			ArrayList<LooseArcRenderable> toRemove = new ArrayList<LooseArcRenderable>();

			final float arcWidth = 0.05F;

			for(LooseArcRenderable arc : SafeCallable.arcsToRender){
				GlStateManager.pushMatrix();
				GlStateManager.pushAttrib();
				Color col = new Color(arc.color, true);
				GlStateManager.color(col.getRed() / 255F, col.getGreen() / 255F, col.getBlue() / 255F, col.getAlpha() / 255F);
				GlStateManager.translate(-game.player.posX, -game.player.posY, -game.player.posZ);
				Tessellator tes = Tessellator.getInstance();
				BufferBuilder buf = tes.getBuffer();
				buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

				Pair<Vec3d, Vec3d> sourceVec = arc.getCurrentEndpoints(e.getPartialTicks());

				float distance = (float) sourceVec.getLeft().subtract(sourceVec.getRight()).lengthVector();//(float) Math.sqrt(Math.pow(arc.xEn - arc.xSt, 2D) + Math.pow(arc.yEn - arc.ySt, 2D) + Math.pow(arc.zEn - arc.zSt, 2D));

				boolean invalidState = false;

				if(arc.lastTick != game.world.getTotalWorldTime()){
					invalidState = (game.world.getTotalWorldTime() % 5 == 0) || (arc.lastTick < 0);
					arc.lastTick = game.world.getTotalWorldTime();
					if(arc.lifeTime-- < 0){
						toRemove.add(arc);
					}
				}

				int diverged = (int) (arc.diffusionRate * arc.count);

				Vec3d deltaVec = sourceVec.getRight().subtract(sourceVec.getLeft());
				Vec3d stepVec = deltaVec.scale(arc.length / distance);
				for(int i = 0; i < arc.count - diverged; i++){
					Vec3d prev = sourceVec.getLeft();
					for(int node = 0; node < (int) (distance / arc.length) - 1; node++){
						Vec3d next = arc.states[i][node];


						if(invalidState){
							next = sourceVec.getLeft().add(stepVec.scale(node + 1)).addVector(RAND.nextFloat() - .5F, RAND.nextFloat() - .5F, RAND.nextFloat() - .5F);//next = new Vec3d(arc.xSt + (arc.xEn - arc.xSt) * (float) (node + 1) / distance + RAND.nextFloat() - .5F, arc.ySt + (arc.yEn - arc.ySt) * (float) (node + 1) / distance + RAND.nextFloat() - .5F, arc.zSt + (arc.zEn - arc.zSt) * (float) (node + 1) / distance + RAND.nextFloat() - .5F);
							arc.states[i][node] = next;
						}

						Vec3d vec = next.subtract(prev).crossProduct(game.player.getLook(e.getPartialTicks()));//new Vec3d(prev.x - game.player.posX, prev.y - game.player.posY, prev.z - game.player.posZ).crossProduct(new Vec3d(next.x - game.player.posX, next.y - game.player.posY, next.z - game.player.posZ));
						vec = vec.normalize().scale(arcWidth / 2F);
						buf.pos(next.x - vec.x, next.y - vec.y, next.z - vec.z).endVertex();
						buf.pos(next.x + vec.x, next.y + vec.y, next.z + vec.z).endVertex();
						buf.pos(prev.x + vec.x, prev.y + vec.y, prev.z + vec.z).endVertex();
						buf.pos(prev.x - vec.x, prev.y - vec.y, prev.z - vec.z).endVertex();

						prev = next;
					}

					Vec3d normal = prev.subtract(sourceVec.getRight()).crossProduct(game.player.getLook(e.getPartialTicks()));//new Vec3d(prev.x - game.player.posX, prev.y - game.player.posY, prev.z - game.player.posZ).crossProduct(new Vec3d(sourceVec.getRight().x - game.player.posX, sourceVec.getRight().y - game.player.posY, sourceVec.getRight().z - game.player.posZ));
					normal = normal.normalize().scale(arcWidth / 2F);

					buf.pos(sourceVec.getRight().x - normal.x, sourceVec.getRight().y - normal.y, sourceVec.getRight().z - normal.z).endVertex();
					buf.pos(sourceVec.getRight().x + normal.x, sourceVec.getRight().y + normal.y, sourceVec.getRight().z + normal.z).endVertex();
					buf.pos(prev.x + normal.x, prev.y + normal.y, prev.z + normal.z).endVertex();
					buf.pos(prev.x - normal.x, prev.y - normal.y, prev.z - normal.z).endVertex();
				}

				for(int i = arc.count - diverged; i < arc.count; i++){
					Vec3d prev = sourceVec.getLeft();//new Vec3d(arc.xSt, arc.ySt, arc.zSt);
					for(int node = 0; node < (int) (distance / arc.length) - 1; node++){
						Vec3d next = arc.states[i][node];

						if(invalidState){
							next = new Vec3d(prev.x + deltaVec.x * (RAND.nextFloat() * 0.2F - .05F) + RAND.nextFloat() * 0.5 - .25F, prev.y + deltaVec.y * (RAND.nextFloat() * .2F - .05F) + RAND.nextFloat() * 0.5 - .25F, prev.z + deltaVec.z * (RAND.nextFloat() * .2F - .05F) + RAND.nextFloat() * 0.5 - .25F);
							arc.states[i][node] = next;
						}

						Vec3d vec = next.subtract(prev).crossProduct(game.player.getLook(e.getPartialTicks()));//new Vec3d(prev.x - game.player.posX, prev.y - game.player.posY, prev.z - game.player.posZ).crossProduct(new Vec3d(next.x - game.player.posX, next.y - game.player.posY, next.z - game.player.posZ));
						vec = vec.normalize().scale(arcWidth / 2F);

						buf.pos(next.x - vec.x, next.y - vec.y, next.z - vec.z).endVertex();
						buf.pos(next.x + vec.x, next.y + vec.y, next.z + vec.z).endVertex();
						buf.pos(prev.x + vec.x, prev.y + vec.y, prev.z + vec.z).endVertex();
						buf.pos(prev.x - vec.x, prev.y - vec.y, prev.z - vec.z).endVertex();

						prev = next;
					}
				}

				tes.draw();
				GlStateManager.color(1, 1, 1);
				GlStateManager.popAttrib();
				GlStateManager.popMatrix();
			}

			for(LooseArcRenderable arc : toRemove){
				SafeCallable.arcsToRender.remove(arc);
			}

			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);
			GlStateManager.enableTexture2D();
			GlStateManager.enableCull();
			GlStateManager.disableBlend();
			GlStateManager.enableLighting();

			game.mcProfiler.endSection();
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
			if(!entNBT.hasKey("fStop")){
				player.updateBlocked = false;
			}else{
				entNBT.removeTag("fStop");
			}
			if(SafeCallable.playerTickCount == 0){
				if(player.updateBlocked){
					entNBT.setBoolean("fStop", true);
				}else{
					player.updateBlocked = true;
				}
			}else{
				player.updateBlocked = false;
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
		if(play.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).isEmpty() && helmet.getItem() == ModItems.moduleGoggles && helmet.hasTagCompound()){
			NBTTagCompound nbt = helmet.getTagCompound();
			for(EnumGoggleLenses lens : EnumGoggleLenses.values()){
				if(lens.getKey() != null && lens.getKey().isPressed() && nbt.hasKey(lens.name())){
					ModPackets.network.sendToServer(new SendGoggleConfigureToServer(lens, !nbt.getBoolean(lens.name())));
					break;
				}
			}
		}
	}
}