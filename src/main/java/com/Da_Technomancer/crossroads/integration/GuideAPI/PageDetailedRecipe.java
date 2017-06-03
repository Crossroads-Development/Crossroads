package com.Da_Technomancer.crossroads.integration.GuideAPI;

import com.Da_Technomancer.crossroads.Main;

import amerifrance.guideapi.api.impl.Book;
import amerifrance.guideapi.api.impl.abstraction.CategoryAbstract;
import amerifrance.guideapi.api.impl.abstraction.EntryAbstract;
import amerifrance.guideapi.gui.GuiBase;
import amerifrance.guideapi.page.PageIRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PageDetailedRecipe extends PageIRecipe{

	private final int type;

	/**
	 * For the Detailed Crafter.
	 * 
	 * @param recipe
	 * @param type Technomancy: 0, Alchemy: NYI (1), Witchcraft: NYI (2)
	 */
	public PageDetailedRecipe(IRecipe recipe, int type){
		super(recipe);
		this.type = type;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void draw(Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX, int mouseY, GuiBase guiBase, FontRenderer fontRendererObj) {
		super.draw(book, category, entry, guiLeft, guiTop, mouseX, mouseY, guiBase, fontRendererObj);
		int iconX = 90 + (guiLeft + guiBase.xSize / 7);
        int iconY = 54 + (guiTop + guiBase.xSize / 5) + 1;
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(Main.MODID, "textures/gui/container/detailed_crafter.png"));
		if(type == 0){
			guiBase.drawTexturedModalRect(iconX, iconY, 176, 0, 16, 16);
		}else if(type == 1){
			guiBase.drawTexturedModalRect(iconX, iconY, 176, 16, 16, 16);
		}else if(type == 2){
			guiBase.drawTexturedModalRect(iconX, iconY, 176, 32, 16, 16);
		}
	}
}
