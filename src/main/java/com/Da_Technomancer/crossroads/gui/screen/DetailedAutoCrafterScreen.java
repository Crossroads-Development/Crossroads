package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

//TODO: Probably outmoded, see DetailedAutoCrafter
public class DetailedAutoCrafterScreen extends AutoCrafterScreen{

	private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "textures/gui/container/detailed_auto_crafter.png");

	public DetailedAutoCrafterScreen(AutoCrafterContainer cont, Inventory playerInventory, Component text){
		super(cont, playerInventory, text);
	}

	@Override
	protected ResourceLocation getBackgroundTexture(){
		return GUI_TEXTURE;
	}
}
