package com.Da_Technomancer.crossroads.integration.jei;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.api.render.CRRenderUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;

import java.util.ArrayList;
import java.util.List;

public class ReagentIngredientRenderer implements IIngredientRenderer<ReagIngr>{

	private static final ResourceLocation PHIAL_TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/item/phial_crystal.png");
	private static final ResourceLocation INNER_TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/item/phial_inner.png");
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

		BufferBuilder buf = Tesselator.getInstance().getBuilder();
		RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
		RenderSystem.setShaderTexture(0, PHIAL_TEXTURE);
		buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
		buf.vertex(matrix.last().pose(), 0, 16, 100).color(255, 255, 255, 255).uv(0, 1).endVertex();
		buf.vertex(matrix.last().pose(), 16, 16, 100).color(255, 255, 255, 255).uv(1, 1).endVertex();
		buf.vertex(matrix.last().pose(), 16, 0, 100).color(255, 255, 255, 255).uv(1, 0).endVertex();
		buf.vertex(matrix.last().pose(), 0, 0, 100).color(255, 255, 255, 255).uv(0, 0).endVertex();
		Tesselator.getInstance().end();

		RenderSystem.setShaderTexture(0, INNER_TEXTURE);
		buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
		buf.vertex(matrix.last().pose(), 0, 16, 200).color(col[0], col[1], col[2], col[3]).uv(0, 1).endVertex();
		buf.vertex(matrix.last().pose(), 16, 16, 200).color(col[0], col[1], col[2], col[3]).uv(1, 1).endVertex();
		buf.vertex(matrix.last().pose(), 16, 0, 200).color(col[0], col[1], col[2], col[3]).uv(1, 0).endVertex();
		buf.vertex(matrix.last().pose(), 0, 0, 200).color(col[0], col[1], col[2], col[3]).uv(0, 0).endVertex();
		Tesselator.getInstance().end();

		matrix.popPose();

//		RenderHelper.disableStandardItemLighting();
//		RenderSystem.disableAlphaTest();
		RenderSystem.disableBlend();
	}
	
	@Override
	public List<Component> getTooltip(ReagIngr ingredient, TooltipFlag tooltipFlag){
		ArrayList<Component> tooltip = new ArrayList<>(3);
		tooltip.add(Component.literal(ingredient.getReag().getName()));
		if(ingredient.getParts() > 0){
			if(ingredient.getParts() == 1){
				tooltip.add(Component.translatable("tt.crossroads.jei.reag.amount.single", ingredient.getParts()));
			}else{
				tooltip.add(Component.translatable("tt.crossroads.jei.reag.amount.plural", ingredient.getParts()));
			}
		}
		if(tooltipFlag.isAdvanced()){
			tooltip.add(Component.translatable("tt.crossroads.jei.reag.id", ingredient.getReag().getID()));
		}
		return tooltip;
	}
}
