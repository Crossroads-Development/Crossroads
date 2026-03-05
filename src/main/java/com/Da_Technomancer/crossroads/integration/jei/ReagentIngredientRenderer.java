package com.Da_Technomancer.crossroads.integration.jei;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.api.render.CRRenderUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;

import java.util.ArrayList;
import java.util.List;

public class ReagentIngredientRenderer implements IIngredientRenderer<ReagIngr>{

	private static final ResourceLocation PHIAL_TEXTURE = ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "textures/item/phial_crystal.png");
	private static final ResourceLocation INNER_TEXTURE = ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "textures/item/phial_inner.png");
	protected static final ReagentIngredientRenderer RENDERER = new ReagentIngredientRenderer();

	@Override
	public void render(GuiGraphics graphics, ReagIngr ingredient){
		if(ingredient == null || ingredient.getReag() == null){
			return;
		}

		int[] col = CRRenderUtil.convertColor(ingredient.getReag().getColor(EnumMatterPhase.SOLID));

		PoseStack matrix = graphics.pose();
		RenderSystem.enableBlend();
//		RenderSystem.enableAlphaTest();
		matrix.pushPose();
//		matrix.translate(xPosition, yPosition, 0);

		RenderSystem.setShaderTexture(0, PHIAL_TEXTURE);
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		BufferBuilder buf = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
		buf.addVertex(matrix.last().pose(), 0, 16, 100).setUv(0, 1).setColor(255, 255, 255, 255);
		buf.addVertex(matrix.last().pose(), 16, 16, 100).setUv(1, 1).setColor(255, 255, 255, 255);
		buf.addVertex(matrix.last().pose(), 16, 0, 100).setUv(1, 0).setColor(255, 255, 255, 255);
		buf.addVertex(matrix.last().pose(), 0, 0, 100).setUv(0, 0).setColor(255, 255, 255, 255);
		BufferUploader.drawWithShader(buf.buildOrThrow());

		RenderSystem.setShaderTexture(0, INNER_TEXTURE);
		buf = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
		buf.addVertex(matrix.last().pose(), 0, 16, 200).setUv(0, 1).setColor(col[0], col[1], col[2], col[3]);
		buf.addVertex(matrix.last().pose(), 16, 16, 200).setUv(1, 1).setColor(col[0], col[1], col[2], col[3]);
		buf.addVertex(matrix.last().pose(), 16, 0, 200).setUv(1, 0).setColor(col[0], col[1], col[2], col[3]);
		buf.addVertex(matrix.last().pose(), 0, 0, 200).setUv(0, 0).setColor(col[0], col[1], col[2], col[3]);
		BufferUploader.drawWithShader(buf.buildOrThrow());

		matrix.popPose();

//		RenderHelper.disableStandardItemLighting();
//		RenderSystem.disableAlphaTest();
		RenderSystem.disableBlend();
	}

	@Override
	@Deprecated
	@SuppressWarnings("deprecated")
	public List<Component> getTooltip(ReagIngr ingredient, TooltipFlag tooltipFlag){
		ArrayList<Component> tooltip = new ArrayList<>(3);
		tooltip.add(Component.literal(ingredient.getReag().getName()));
		if(ingredient.parts() > 0){
			if(ingredient.parts() == 1){
				tooltip.add(Component.translatable("tt.crossroads.jei.reag.amount.single", ingredient.parts()));
			}else{
				tooltip.add(Component.translatable("tt.crossroads.jei.reag.amount.plural", ingredient.parts()));
			}
		}
		if(tooltipFlag.isAdvanced()){
			tooltip.add(Component.translatable("tt.crossroads.jei.reag.id", ingredient.getReag().getID()));
		}
		return tooltip;
	}

	@Override
	public void getTooltip(ITooltipBuilder builder, ReagIngr ingredient, TooltipFlag tooltipFlag){
		builder.add(Component.literal(ingredient.getReag().getName()));
		if(ingredient.parts() > 0){
			if(ingredient.parts() == 1){
				builder.add(Component.translatable("tt.crossroads.jei.reag.amount.single", ingredient.parts()));
			}else{
				builder.add(Component.translatable("tt.crossroads.jei.reag.amount.plural", ingredient.parts()));
			}
		}
		if(tooltipFlag.isAdvanced()){
			builder.add(Component.translatable("tt.crossroads.jei.reag.id", ingredient.getReag().getID()));
		}
	}
}
