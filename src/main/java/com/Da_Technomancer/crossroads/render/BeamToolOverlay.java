package com.Da_Technomancer.crossroads.render;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.beams.BeamUnit;
import com.Da_Technomancer.crossroads.integration.curios.CurioHelper;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.technomancy.BeamCage;
import com.Da_Technomancer.crossroads.items.technomancy.BeamUsingItem;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.awt.*;

public class BeamToolOverlay implements LayeredDraw.Layer{

	@Override
	public void render(GuiGraphics graphics, DeltaTracker tracker){
		Player player = Minecraft.getInstance().player;

		if(player == null){
			return;
		}

		//Beam cage overlay
		ItemStack cageStack = CurioHelper.getEquipped(CRItems.beamCage, player);
		ItemStack mainStack = player.getItemInHand(InteractionHand.MAIN_HAND);

		boolean renderToolOverlay = mainStack.getItem() instanceof BeamUsingItem;
		boolean renderCageOverlay = !cageStack.isEmpty() && (CRConfig.cageMeterOverlay.get() || renderToolOverlay);

		if(renderCageOverlay || renderToolOverlay){
			PoseStack matrix = graphics.pose();
			//Use the batched renderer instead of the Tesselator

			//1536 copied from other uses in vanilla code
			MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(new ByteBufferBuilder(1536));
//			PoseStack matrix = e.getMatrixStack();
			matrix.pushPose();
			//Makes the UI overlay smaller
			matrix.scale(0.75F, 0.75F, 1);

            float barUSt = 8F / 39F;
            float barUEn = 32F / 39F;
            float barVSt = 26F / 40F;
            float barVWid = 3F / 40F;

			if(renderCageOverlay){
				VertexConsumer builder = buffer.getBuffer(CRRenderTypes.BEAM_INFO_TYPE);

				BeamUnit stored = BeamCage.getStored(cageStack);

				builder.addVertex(matrix.last().pose(), 0, 120, -3).setUv(0, 0.5F);
				builder.addVertex(matrix.last().pose(), 117, 120, -3).setUv(1, 0.5F);
				builder.addVertex(matrix.last().pose(), 117, 60, -3).setUv(1, 0);
				builder.addVertex(matrix.last().pose(), 0, 60, -3).setUv(0, 0);

				for(int i = 0; i < 4; i++){
					float fullness = (float) stored.getValues()[i] / BeamCage.CAPACITY;
					int extension = (int) (72 * fullness);
					builder.addVertex(matrix.last().pose(), 24, 87 + (9 * i), -2).setUv(barUSt, barVSt + barVWid * (i + 1));
					builder.addVertex(matrix.last().pose(), 24 + extension, 87 + (9 * i), -2).setUv(barUSt + (barUEn - barUSt) * fullness, barVSt + barVWid * (i + 1));
					builder.addVertex(matrix.last().pose(), 24 + extension, 78 + (9 * i), -2).setUv(barUSt + (barUEn - barUSt) * fullness, barVSt + barVWid * i);
					builder.addVertex(matrix.last().pose(), 24, 78 + (9 * i), -2).setUv(barUSt, barVSt + barVWid * i);
				}

				//As this is an unbatched environment, we need to manually force the buffer to render before drawing fonts
				buffer.endBatch();

				graphics.drawString(Minecraft.getInstance().font, cageStack.getHoverName().getString(), 16, 65, Color.DARK_GRAY.getRGB(), false);
			}

			//Beam using item overlay
			if(renderToolOverlay){
				VertexConsumer builder = buffer.getBuffer(CRRenderTypes.BEAM_INFO_TYPE);

				builder.addVertex(matrix.last().pose(), 0, 60, -3).setUv(0, 0.5F);
				builder.addVertex(matrix.last().pose(), 117, 60, -3).setUv(1, 0.5F);
				builder.addVertex(matrix.last().pose(), 117, 0, -3).setUv(1, 0);
				builder.addVertex(matrix.last().pose(), 0, 0, -3).setUv(0, 0);

				byte[] settings = BeamUsingItem.getSetting(mainStack);
				for(int i = 0; i < 4; i++){
					float fullness = (float) settings[i] / 8;
					int extension = (int) (72 * fullness);
					builder.addVertex(matrix.last().pose(), 24, 27 + (9 * i), -2).setUv(barUSt, barVSt + barVWid * (i + 1));
					builder.addVertex(matrix.last().pose(), 24 + extension, 27 + (9 * i), -2).setUv(barUSt + (barUEn - barUSt) * fullness, barVSt + barVWid * (i + 1));
					builder.addVertex(matrix.last().pose(), 24 + extension, 18 + (9 * i), -2).setUv(barUSt + (barUEn - barUSt) * fullness, barVSt + barVWid * i);
					builder.addVertex(matrix.last().pose(), 24, 18 + (9 * i), -2).setUv(barUSt, barVSt + barVWid * i);
				}

				//As this is an unbatched environment, we need to manually force the buffer to render before drawing fonts
				buffer.endBatch();

				graphics.drawString(Minecraft.getInstance().font, mainStack.getHoverName().getString(), 16, 5, Color.DARK_GRAY.getRGB(), false);
			}

			matrix.popPose();
		}
	}
}
