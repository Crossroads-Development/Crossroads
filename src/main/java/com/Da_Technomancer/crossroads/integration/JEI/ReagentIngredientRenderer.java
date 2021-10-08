package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class ReagentIngredientRenderer implements IIngredientRenderer<ReagIngr>{

	private static final ResourceLocation PHIAL_TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/item/phial_crystal.png");
	private static final ResourceLocation INNER_TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/item/phial_inner.png");
	protected static final ReagentIngredientRenderer RENDERER = new ReagentIngredientRenderer();

	@Override
	public void render(PoseStack matrix, int xPosition, int yPosition, ReagIngr ingredient){
		if(ingredient == null || ingredient.getReag() == null){
			return;
		}

		int[] col = CRRenderUtil.convertColor(ingredient.getReag().getColor(EnumMatterPhase.SOLID));

		RenderSystem.enableBlend();
		RenderSystem.enableAlphaTest();
		matrix.pushPose();
		matrix.translate(xPosition, yPosition, 0);

		BufferBuilder buf = Tesselator.getInstance().getBuilder();

		Minecraft.getInstance().textureManager.bind(PHIAL_TEXTURE);
		buf.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
		buf.vertex(matrix.last().pose(), 0, 16, 100).color(255, 255, 255, 255).uv(0, 1).endVertex();
		buf.vertex(matrix.last().pose(), 16, 16, 100).color(255, 255, 255, 255).uv(1, 1).endVertex();
		buf.vertex(matrix.last().pose(), 16, 0, 100).color(255, 255, 255, 255).uv(1, 0).endVertex();
		buf.vertex(matrix.last().pose(), 0, 0, 100).color(255, 255, 255, 255).uv(0, 0).endVertex();
		Tesselator.getInstance().end();

		Minecraft.getInstance().textureManager.bind(INNER_TEXTURE);
		buf.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
		buf.vertex(matrix.last().pose(), 0, 16, 200).color(col[0], col[1], col[2], col[3]).uv(0, 1).endVertex();
		buf.vertex(matrix.last().pose(), 16, 16, 200).color(col[0], col[1], col[2], col[3]).uv(1, 1).endVertex();
		buf.vertex(matrix.last().pose(), 16, 0, 200).color(col[0], col[1], col[2], col[3]).uv(1, 0).endVertex();
		buf.vertex(matrix.last().pose(), 0, 0, 200).color(col[0], col[1], col[2], col[3]).uv(0, 0).endVertex();
		Tesselator.getInstance().end();

		matrix.popPose();

//		RenderHelper.disableStandardItemLighting();
		RenderSystem.disableAlphaTest();
		RenderSystem.disableBlend();
	}
	
	@Override
	public List<Component> getTooltip(ReagIngr ingredient, TooltipFlag tooltipFlag){
		ArrayList<Component> tooltip = new ArrayList<>(3);
		tooltip.add(new TextComponent(ingredient.getReag().getName()));
		if(ingredient.getParts() > 0){
			if(ingredient.getParts() == 1){
				tooltip.add(new TranslatableComponent("tt.crossroads.jei.reag.amount.single", ingredient.getParts()));
			}else{
				tooltip.add(new TranslatableComponent("tt.crossroads.jei.reag.amount.plural", ingredient.getParts()));
			}
		}
		if(tooltipFlag.isAdvanced()){
			tooltip.add(new TranslatableComponent("tt.crossroads.jei.reag.id", ingredient.getReag().getID()));
		}
		return tooltip;
	}
}
