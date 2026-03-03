package com.Da_Technomancer.crossroads.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class MultiLineMessageOverlay implements LayeredDraw.Layer{

	private static final ArrayList<Component> message = new ArrayList<>();
	private static int messageEndTime;
	@Nullable
	private static BlockPos messageWorldPosition;


//	private static Method guiDrawBackdrop = null;
//	private static boolean didInit = false;

	private static final int WHITE = 0xFFFFFF;

	public static void setMessage(ArrayList<Component> newMessage, int duration, @Nullable BlockPos targetPos){
		message.clear();
		message.addAll(newMessage);
		messageEndTime = duration + Minecraft.getInstance().gui.getGuiTicks();
		messageWorldPosition = targetPos;
	}

	@Override
	public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker){
		Minecraft minecraft = Minecraft.getInstance();
		if(!minecraft.options.hideGui && !message.isEmpty() && minecraft.gui.getGuiTicks() < messageEndTime && (messageWorldPosition == null || minecraft.hitResult instanceof BlockHitResult blockResult && messageWorldPosition.equals(blockResult.getBlockPos()))){
//			if(!didInit){
//				didInit = true;
//				guiDrawBackdrop = ReflectionUtil.reflectMethod(CRReflectionClient.GUI_DRAW_BACKDROP);
//			}

			minecraft.getProfiler().push("cr_overlayMessage");
			int remainingTime = messageEndTime - minecraft.gui.getGuiTicks();
			float hue = (float) remainingTime - deltaTracker.getGameTimeDeltaPartialTick(false);
			int opacity = (int) (hue * 255.0F / 20.0F);
			if(opacity > 255){
				opacity = 255;
			}

			if(opacity > 8){
				PoseStack poseStack = guiGraphics.pose();
				poseStack.pushPose();
				poseStack.translate(guiGraphics.guiWidth() / 2D, guiGraphics.guiHeight() - 68, 0.0D);
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				Font font = minecraft.gui.getFont();

				for(int i = 0; i < message.size(); i++){
					Component activeComponent = message.get(i);
					int offset = (i - message.size() + 1) * 10 - 19;
//					if(guiDrawBackdrop != null){
//						try{
//							guiDrawBackdrop.invoke(gui, guiGraphics, font, offset, font.width(activeComponent), WHITE | (opacity << 24));
//						}catch(IllegalAccessException | InvocationTargetException e){
//							throw new RuntimeException(e);
//						}
//					}
					guiGraphics.drawStringWithBackdrop(font, activeComponent, -font.width(activeComponent) / 2, offset, font.width(activeComponent), WHITE | (opacity << 24));
//					guiGraphics.drawString(font, activeComponent.getVisualOrderText(), -font.width(activeComponent) / 2, offset, WHITE | (opacity << 24), true);
//					font.drawShadow(poseStack, activeComponent.getVisualOrderText(), -font.width(activeComponent) / 2, offset, WHITE | (opacity << 24));
				}

				RenderSystem.disableBlend();
				poseStack.popPose();
			}

			minecraft.getProfiler().pop();
		}
	}
}
