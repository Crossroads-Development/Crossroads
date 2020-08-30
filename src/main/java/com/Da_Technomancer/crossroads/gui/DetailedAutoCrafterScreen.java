package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.gui.AutoCrafterScreen;
import com.Da_Technomancer.essentials.gui.container.AutoCrafterContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class DetailedAutoCrafterScreen extends AutoCrafterScreen{

	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/container/detailed_auto_crafter.png");

	public DetailedAutoCrafterScreen(AutoCrafterContainer cont, PlayerInventory playerInventory, ITextComponent text){
		super(cont, playerInventory, text);
	}

	@Override
	protected ResourceLocation getBackgroundTexture(){
		return GUI_TEXTURE;
	}
}
