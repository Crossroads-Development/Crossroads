package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.Crossroads;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

public class ReagentIngredientRenderer implements IIngredientRenderer<ReagIngr>{

	private static final ResourceLocation PHIAL_TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/item/phial_crystal.png");
	private static final ResourceLocation INNER_TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/item/phial_inner.png");
	protected static final ReagentIngredientRenderer RENDERER = new ReagentIngredientRenderer();

	@Override
	public void render(int xPosition, int yPosition, ReagIngr ingredient){
		GlStateManager.pushMatrix();
		GlStateManager.translated(xPosition, yPosition, 0);
		GlStateManager.enableBlend();
		Minecraft.getInstance().textureManager.bindTexture(PHIAL_TEXTURE);
		BufferBuilder buf = Tessellator.getInstance().getBuffer();
		buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		buf.pos(0, 16, 100).tex(0, 1).endVertex();
		buf.pos(16, 16, 100).tex(1, 1).endVertex();
		buf.pos(16, 0, 100).tex(1, 0).endVertex();
		buf.pos(0, 0, 100).tex(0, 0).endVertex();
		Tessellator.getInstance().draw();

		Minecraft.getInstance().textureManager.bindTexture(INNER_TEXTURE);
		Color col = ingredient.getReag().getColor(EnumMatterPhase.SOLID);
		GlStateManager.color4f((float) col.getRed() / 255F, (float) col.getGreen() / 255F, (float) col.getBlue() / 255F, (float) col.getAlpha() / 255F);
		buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		buf.pos(0, 16, 200).tex(0, 1).endVertex();
		buf.pos(16, 16, 200).tex(1, 1).endVertex();
		buf.pos(16, 0, 200).tex(1, 0).endVertex();
		buf.pos(0, 0, 200).tex(0, 0).endVertex();
		Tessellator.getInstance().draw();
		
		GlStateManager.color4f(1, 1, 1, 1);
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}
	
	@Override
	public List<String> getTooltip(ReagIngr ingredient, ITooltipFlag tooltipFlag){
		return tooltipFlag == TooltipFlags.ADVANCED ? ImmutableList.of(ingredient.getReag().getName(), ingredient.getParts() + (ingredient.getParts() == 1 ? " Part" : " Parts"), "ID: " + ingredient.getReag().getId()) : ingredient.getParts() == 0 ? ImmutableList.of(ingredient.getReag().getName()) : ImmutableList.of(ingredient.getReag().getName(), ingredient.getParts() + (ingredient.getParts() == 1 ? " Part" : " Parts"));
	}
}
