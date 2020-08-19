package com.Da_Technomancer.crossroads;

import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.packets.*;
import com.Da_Technomancer.crossroads.API.technomancy.EnumGoggleLenses;
import com.Da_Technomancer.crossroads.integration.curios.CurioHelper;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.technomancy.BeamCage;
import com.Da_Technomancer.crossroads.items.technomancy.BeamUsingItem;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.render.IVisualEffect;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
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
import net.minecraft.util.math.vector.Vector3d;
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

//		//Goggle entity glowing (Moved to tick event handler)
//		game.getProfiler().startSection(Crossroads.MODNAME + ": Goggle Glowing Application");
//		handleGoggleGlowing(game);
//		game.getProfiler().endSection();

		//IVisualEffects
		if(!AddVisualToClient.effectsToRender.isEmpty()){
			game.getProfiler().startSection(Crossroads.MODNAME + ": Visual Effects Draw");

			MatrixStack matrix = e.getMatrixStack();

			matrix.push();
			Vector3d cameraPos = CRRenderUtil.getCameraPos();
			matrix.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);//Translate to 0,0,0 world coords

			ArrayList<IVisualEffect> toRemove = new ArrayList<>();
			IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
			long worldTime = game.world.getGameTime();
			float partialTicks = e.getPartialTicks();

			for(IVisualEffect effect : AddVisualToClient.effectsToRender){
				matrix.push();

				if(effect.render(matrix, buffer, worldTime, partialTicks, RAND)){
					toRemove.add(effect);
				}

				matrix.pop();
			}

			AddVisualToClient.effectsToRender.removeAll(toRemove);

			buffer.finish();//Due to weirdness surrounding how this event is called, we need to force anything in the buffer to render immediately to prevent something else changing render system settings
			matrix.pop();

			game.getProfiler().endSection();
		}
	}

	private static void handleGoggleGlowing(Minecraft game){
		//Handles glow in the dark entities when wearing goggles
		if(game.world.getGameTime() % 5 == 0){
			ItemStack helmet = Minecraft.getInstance().player.getItemStackFromSlot(EquipmentSlotType.HEAD);
			boolean doGlowing = helmet.getItem() == CRItems.moduleGoggles && helmet.hasTag() && helmet.getTag().getBoolean(EnumGoggleLenses.VOID.toString());
			for(Entity ent : game.world.getAllEntities()){
				CompoundNBT entNBT = ent.getPersistentData();
				if(entNBT == null){
					Crossroads.logger.info("Found entity with null persistent data! Report to the mod author of the mod that added the entity: %s", ent.getType().getRegistryName().toString());
					continue;//Should never be null, but some mods override the entNBT method to return null for some reason
				}

				//The NBT shenanigans is to prevent this purely client side glowing effect from interfering with server-side glowing effects (such as being hit with the glowing arrow) when disabled
				if(!entNBT.contains("cr_glow")){
					ent.setGlowing(false);
				}else{
					entNBT.remove("cr_glow");
				}

				if(doGlowing){
					if(ent.isGlowing()){
						entNBT.putBoolean("cr_glow", true);
					}else{
						ent.setGlowing(true);
					}
				}
			}
		}
	}

	private static final ResourceLocation MAGIC_BAR_BACKGROUND = new ResourceLocation(Crossroads.MODID, "textures/gui/magic_info_back.png");
	private static final ResourceLocation MAGIC_BAR_FOREGROUND = new ResourceLocation(Crossroads.MODID, "textures/gui/magic_info_front.png");
	private static final ResourceLocation COLOR_SHEET = new ResourceLocation(Crossroads.MODID, "textures/block/color_sheet.png");

	@SubscribeEvent
	@SuppressWarnings("unused")
	public void magicUsingItemOverlay(RenderGameOverlayEvent e){
		if(e.getType() == ElementType.HOTBAR){
			PlayerEntity player = Minecraft.getInstance().player;

			//Beam cage overlay
			ItemStack cageStack = CurioHelper.getEquipped(CRItems.beamCage, player);
			if(!cageStack.isEmpty()){
				BeamUnit stored = BeamCage.getStored(cageStack);
				RenderSystem.pushMatrix();
				RenderSystem.pushLightingAttributes();
				RenderSystem.enableBlend();
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
				buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
				for(int i = 0; i < 4; i++){
					int extension = 72 * stored.getValues()[i] / BeamCage.CAPACITY;
					int[] col = new int[4];
					col[3] = 255;
					col[i] = 255;//For void, overrides the alpha. Conveniently not an issue
					buf.pos(24, 84 + (9 * i), -2).color(col[0], col[1], col[2], col[3]).tex(.25F + (((float) i) * .0625F), .0625F).endVertex();
					buf.pos(24 + extension, 84 + (9 * i), -2).color(col[0], col[1], col[2], col[3]).tex(.3125F + (((float) i) * .0625F), .0625F).endVertex();
					buf.pos(24 + extension, 78 + (9 * i), -2).color(col[0], col[1], col[2], col[3]).tex(.3125F + (((float) i) * .0625F), 0).endVertex();
					buf.pos(24, 78 + (9 * i), -2).color(col[0], col[1], col[2], col[3]).tex(.25F + (((float) i) * .0625F), 0).endVertex();
				}
				tes.draw();

				Minecraft.getInstance().getTextureManager().bindTexture(MAGIC_BAR_FOREGROUND);
				buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				buf.pos(0, 120, -1).tex(0, 1).endVertex();
				buf.pos(117, 120, -1).tex(1, 1).endVertex();
				buf.pos(117, 60, -1).tex(1, 0).endVertex();
				buf.pos(0, 60, -1).tex(0, 0).endVertex();
				tes.draw();

				Minecraft.getInstance().fontRenderer.drawString(e.getMatrixStack(), cageStack.getDisplayName().getString(), 16, 65, Color.DARK_GRAY.getRGB());
				RenderSystem.color4f(1, 1, 1, 1);
				RenderSystem.disableAlphaTest();
				RenderSystem.disableBlend();
				RenderSystem.popAttributes();
				RenderSystem.popMatrix();
			}

			//Beam using item overlay
			ItemStack mainStack = player.getHeldItem(Hand.MAIN_HAND);
			if(mainStack.getItem() instanceof BeamUsingItem){
				RenderSystem.pushMatrix();
				RenderSystem.pushLightingAttributes();
				RenderSystem.enableBlend();
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
				buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
				byte[] settings = BeamUsingItem.getSetting(mainStack);
				for(int i = 0; i < 4; i++){
					int[] col = new int[4];
					col[3] = 255;
					col[i] = 255;//For void, overrides the alpha. Conveniently not an issue
					int extension = 9 * settings[i];
					buf.pos(24, 24 + (9 * i), -2).color(col[0], col[1], col[2], col[3]).tex(.25F + (((float) i) * .0625F), .0625F).endVertex();
					buf.pos(24 + extension, 24 + (9 * i), -2).color(col[0], col[1], col[2], col[3]).tex(.3125F + (((float) i) * .0625F), .0625F).endVertex();
					buf.pos(24 + extension, 18 + (9 * i), -2).color(col[0], col[1], col[2], col[3]).tex(.3125F + (((float) i) * .0625F), 0).endVertex();
					buf.pos(24, 18 + (9 * i), -2).color(col[0], col[1], col[2], col[3]).tex(.25F + (((float) i) * .0625F), 0).endVertex();
				}
				tes.draw();

				Minecraft.getInstance().getTextureManager().bindTexture(MAGIC_BAR_FOREGROUND);
				buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				buf.pos(0, 60, -1).tex(0, 1).endVertex();
				buf.pos(117, 60, -1).tex(1, 1).endVertex();
				buf.pos(117, 0, -1).tex(1, 0).endVertex();
				buf.pos(0, 0, -1).tex(0, 0).endVertex();
				tes.draw();

				Minecraft.getInstance().fontRenderer.drawString(e.getMatrixStack(), mainStack.getDisplayName().getString(), 16, 5, Color.DARK_GRAY.getRGB());

				RenderSystem.disableAlphaTest();
				RenderSystem.color4f(1, 1, 1, 1);
				RenderSystem.disableBlend();
				RenderSystem.popAttributes();
				RenderSystem.popMatrix();
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

			//Goggle entity glowing
			Minecraft game = Minecraft.getInstance();
			game.getProfiler().startSection(Crossroads.MODNAME + ": Goggle Glowing Application");
			handleGoggleGlowing(game);
			game.getProfiler().endSection();

			//Handle time dilation for players
			if(SendPlayerTickCountToClient.playerTickCount > 0){
				for(int i = 0; i < SendPlayerTickCountToClient.playerTickCount; i++){
					player.tick();
				}
				SendPlayerTickCountToClient.playerTickCount = 0;
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
				((BeamUsingItem) stack.getItem()).adjustSetting(Minecraft.getInstance().player, stack, key, !play.isSneaking());
			}
		}else if(helmet.getItem() == CRItems.moduleGoggles && helmet.hasTag()){
			CompoundNBT nbt = helmet.getTag();
			for(EnumGoggleLenses lens : EnumGoggleLenses.values()){
				KeyBinding key = lens.getKey();
				if(key != null && key.isPressed() && key.isKeyDown() && nbt.contains(lens.toString())){
					CRPackets.channel.sendToServer(new SendGoggleConfigureToServer(lens, !nbt.getBoolean(lens.toString())));
					break;
				}
			}
		}
	}
}