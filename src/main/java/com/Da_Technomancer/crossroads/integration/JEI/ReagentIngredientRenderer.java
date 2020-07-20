package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class ReagentIngredientRenderer implements IIngredientRenderer<ReagIngr>{

	private static final ResourceLocation PHIAL_TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/item/phial_crystal.png");
	private static final ResourceLocation INNER_TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/item/phial_inner.png");
	protected static final ReagentIngredientRenderer RENDERER = new ReagentIngredientRenderer();

	@Override
	public void render(int xPosition, int yPosition, ReagIngr ingredient){
		RenderSystem.enableBlend();
		RenderSystem.enableAlphaTest();
//		RenderHelper.enableStandardItemLighting();

		RenderSystem.pushMatrix();
		RenderSystem.translated(xPosition, yPosition, 0);

		BufferBuilder buf = Tessellator.getInstance().getBuffer();

		Minecraft.getInstance().textureManager.bindTexture(PHIAL_TEXTURE);
		buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
		buf.pos(0, 16, 100).color(255, 255, 255, 255).tex(0, 1).endVertex();
		buf.pos(16, 16, 100).color(255, 255, 255, 255).tex(1, 1).endVertex();
		buf.pos(16, 0, 100).color(255, 255, 255, 255).tex(1, 0).endVertex();
		buf.pos(0, 0, 100).color(255, 255, 255, 255).tex(0, 0).endVertex();
		Tessellator.getInstance().draw();

		int[] col = CRRenderUtil.convertColor(ingredient.getReag().getColor(EnumMatterPhase.SOLID));

		Minecraft.getInstance().textureManager.bindTexture(INNER_TEXTURE);
		buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
		buf.pos(0, 16, 200).color(col[0], col[1], col[2], col[3]).tex(0, 1).endVertex();
		buf.pos(16, 16, 200).color(col[0], col[1], col[2], col[3]).tex(1, 1).endVertex();
		buf.pos(16, 0, 200).color(col[0], col[1], col[2], col[3]).tex(1, 0).endVertex();
		buf.pos(0, 0, 200).color(col[0], col[1], col[2], col[3]).tex(0, 0).endVertex();
		Tessellator.getInstance().draw();

		RenderSystem.popMatrix();

//		RenderHelper.disableStandardItemLighting();
		RenderSystem.disableAlphaTest();
		RenderSystem.disableBlend();
	}
	
	@Override
	public List<String> getTooltip(ReagIngr ingredient, ITooltipFlag tooltipFlag){
		return tooltipFlag == TooltipFlags.ADVANCED ? ImmutableList.of(ingredient.getReag().getName(), ingredient.getParts() + (ingredient.getParts() == 1 ? " Part" : " Parts"), "ID: " + ingredient.getReag().getId()) : ingredient.getParts() == 0 ? ImmutableList.of(ingredient.getReag().getName()) : ImmutableList.of(ingredient.getReag().getName(), ingredient.getParts() + (ingredient.getParts() == 1 ? " Part" : " Parts"));
	}
}
