package com.Da_Technomancer.crossroads.render;

import com.Da_Technomancer.crossroads.api.CRReflectionClient;
import com.Da_Technomancer.essentials.api.ReflectionUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class MultiLineMessageOverlay implements IGuiOverlay{

	private static final ArrayList<Component> message = new ArrayList<>();
	private static int messageEndTime;
	@Nullable
	private static BlockPos messageWorldPosition;


	private static Method guiDrawBackdrop = null;
	private static boolean didInit = false;

	private static final int WHITE = 0xFFFFFF;

	public static void setMessage(ArrayList<Component> newMessage, int duration, @Nullable BlockPos targetPos){
		message.clear();
		message.addAll(newMessage);
		messageEndTime = duration + Minecraft.getInstance().gui.getGuiTicks();
		messageWorldPosition = targetPos;
	}

	@Override
	public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight){
		if(!gui.getMinecraft().options.hideGui && !message.isEmpty() && gui.getGuiTicks() < messageEndTime && (messageWorldPosition == null || gui.getMinecraft().hitResult instanceof BlockHitResult blockResult && messageWorldPosition.equals(blockResult.getBlockPos()))){
			if(!didInit){
				didInit = true;
				guiDrawBackdrop = ReflectionUtil.reflectMethod(CRReflectionClient.GUI_DRAW_BACKDROP);
			}

			gui.getMinecraft().getProfiler().push("cr_overlayMessage");
			int remainingTime = messageEndTime - gui.getGuiTicks();
			float hue = (float) remainingTime - partialTick;
			int opacity = (int) (hue * 255.0F / 20.0F);
			if(opacity > 255){
				opacity = 255;
			}

			if(opacity > 8){
				poseStack.pushPose();
				poseStack.translate(screenWidth / 2D, screenHeight - 68, 0.0D);
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				Font font = gui.getFont();

				for(int i = 0; i < message.size(); i++){
					Component activeComponent = message.get(i);
					int offset = (i - message.size() + 1) * 10 - 19;
					if(guiDrawBackdrop != null){
						try{
							guiDrawBackdrop.invoke(gui, poseStack, font, offset, font.width(activeComponent), WHITE | (opacity << 24));
						}catch(IllegalAccessException | InvocationTargetException e){
							throw new RuntimeException(e);
						}
					}
					font.drawShadow(poseStack, activeComponent.getVisualOrderText(), -font.width(activeComponent) / 2, offset, WHITE | (opacity << 24));
				}

				RenderSystem.disableBlend();
				poseStack.popPose();
			}

			gui.getMinecraft().getProfiler().pop();
		}
	}
}
