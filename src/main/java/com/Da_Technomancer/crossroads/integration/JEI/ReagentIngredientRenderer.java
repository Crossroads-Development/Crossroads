package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.Crossroads;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ReagentIngredientRenderer implements IIngredientRenderer<ReagIngr>{

	private static final ResourceLocation PHIAL_TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/item/phial_crystal.png");
	private static final ResourceLocation INNER_TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/item/phial_inner.png");
	protected static final ReagentIngredientRenderer RENDERER = new ReagentIngredientRenderer();

	@Override
	public void render(MatrixStack matrix, int xPosition, int yPosition, ReagIngr ingredient){
		RenderSystem.enableDepthTest();
		RenderHelper.enableStandardItemLighting();
		matrix.push();
		matrix.translate(xPosition, yPosition, 0);

		BufferBuilder buf = Tessellator.getInstance().getBuffer();

		Minecraft.getInstance().textureManager.bindTexture(PHIAL_TEXTURE);
		buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		buf.pos(0, 16, 100).tex(0, 1).endVertex();
		buf.pos(16, 16, 100).tex(1, 1).endVertex();
		buf.pos(16, 0, 100).tex(1, 0).endVertex();
		buf.pos(0, 0, 100).tex(0, 0).endVertex();
		Tessellator.getInstance().draw();

		Color col = ingredient.getReag().getColor(EnumMatterPhase.SOLID);

		Minecraft.getInstance().textureManager.bindTexture(INNER_TEXTURE);
		buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
		buf.pos(matrix.getLast().getMatrix(), 0, 16, 200).color(col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha()).tex(0, 1).endVertex();
		buf.pos(matrix.getLast().getMatrix(), 16, 16, 200).color(col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha()).tex(1, 1).endVertex();
		buf.pos(matrix.getLast().getMatrix(), 16, 0, 200).color(col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha()).tex(1, 0).endVertex();
		buf.pos(matrix.getLast().getMatrix(), 0, 0, 200).color(col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha()).tex(0, 0).endVertex();
		Tessellator.getInstance().draw();

		matrix.pop();

		RenderSystem.disableBlend();
		RenderHelper.disableStandardItemLighting();
	}
	
	@Override
	public List<ITextComponent> getTooltip(ReagIngr ingredient, ITooltipFlag tooltipFlag){
		ArrayList<ITextComponent> tooltip = new ArrayList<>(3);
		tooltip.add(new StringTextComponent(ingredient.getReag().getName()));
		if(ingredient.getParts() > 0){
			if(ingredient.getParts() == 1){
				tooltip.add(new TranslationTextComponent("tt.crossroads.jei.reag.amount.single", ingredient.getParts()));
			}else{
				tooltip.add(new TranslationTextComponent("tt.crossroads.jei.reag.amount.plural", ingredient.getParts()));
			}
		}
		if(tooltipFlag.isAdvanced()){
			tooltip.add(new TranslationTextComponent("tt.crossroads.jei.reag.id", ingredient.getReag().getId()));
		}
		return tooltip;
	}
}
