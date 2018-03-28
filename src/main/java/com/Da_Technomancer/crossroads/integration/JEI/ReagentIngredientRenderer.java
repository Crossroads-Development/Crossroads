package com.Da_Technomancer.crossroads.integration.JEI;

import java.awt.Color;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.google.common.collect.ImmutableList;

import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.util.ResourceLocation;

public class ReagentIngredientRenderer implements IIngredientRenderer<ReagIngr>{

	private static final ResourceLocation PHIAL_TEXTURE = new ResourceLocation(Main.MODID, "textures/items/phial_crystal.png");
	private static final ResourceLocation INNER_TEXTURE = new ResourceLocation(Main.MODID, "textures/items/phial_inner.png");
	protected static final ReagentIngredientRenderer RENDERER = new ReagentIngredientRenderer();
	
	@Override
	public void render(Minecraft minecraft, int xPosition, int yPosition, ReagIngr ingredient){
		GlStateManager.pushMatrix();
		GlStateManager.translate(xPosition, yPosition, 0);
		GlStateManager.enableAlpha();
		minecraft.renderEngine.bindTexture(PHIAL_TEXTURE);
		BufferBuilder buf = Tessellator.getInstance().getBuffer();
		buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		buf.pos(0, 16, 300).tex(0, 1).endVertex();
		buf.pos(16, 16, 300).tex(1, 1).endVertex();
		buf.pos(16, 0, 300).tex(1, 0).endVertex();
		buf.pos(0, 0, 300).tex(0, 0).endVertex();
		Tessellator.getInstance().draw();
		
		minecraft.renderEngine.bindTexture(INNER_TEXTURE);
		Color col = ingredient.getReag().getColor(EnumMatterPhase.SOLID);
		GlStateManager.color((float) col.getRed() / 255F, (float) col.getGreen() / 255F, (float) col.getBlue() / 255F, (float) col.getAlpha() / 255F);
		buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		buf.pos(0, 16, 200).tex(0, 1).endVertex();
		buf.pos(16, 16, 200).tex(1, 1).endVertex();
		buf.pos(16, 0, 200).tex(1, 0).endVertex();
		buf.pos(0, 0, 200).tex(0, 0).endVertex();
		Tessellator.getInstance().draw();
		
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.disableAlpha();
		GlStateManager.popMatrix();
	}
	
	@Override
	public List<String> getTooltip(Minecraft minecraft, ReagIngr ingredient, ITooltipFlag tooltipFlag){
		return tooltipFlag == TooltipFlags.ADVANCED ? ImmutableList.of(ingredient.getReag().getName(), ingredient.getParts() + (ingredient.getParts() == 1 ? " Part" : " Parts"), "ID: " + ingredient.getReag().getIndex()) : ImmutableList.of(ingredient.getReag().getName(), ingredient.getParts() + (ingredient.getParts() == 1 ? " Part" : " Parts"));
	}
}
