package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.api.CircuitUtil;
import com.Da_Technomancer.crossroads.gui.container.MasterAxisCreativeContainer;
import com.Da_Technomancer.essentials.Essentials;
import com.Da_Technomancer.essentials.api.packets.EssentialsPackets;
import com.Da_Technomancer.essentials.api.packets.SendNBTToServer;
import com.Da_Technomancer.essentials.api.redstone.RedstoneUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MasterAxisCreativeScreen extends AbstractContainerScreen<MasterAxisCreativeContainer>{

	private static final ResourceLocation SEARCH_BAR_TEXTURE = new ResourceLocation(Essentials.MODID, "textures/gui/search_bar.png");
	private EditBox searchBar;

	public MasterAxisCreativeScreen(MasterAxisCreativeContainer cont, Inventory playerInventory, Component text){
		super(cont, playerInventory, text);
		imageHeight = 18;
		imageWidth = 144;
	}

	@Override
	protected void init(){
		super.init();
		searchBar = CircuitUtil.createFormulaInputUIComponent(this, font, 4,8, Component.translatable("container.crossroads.master_axis_creative.text_input"), this::entryChanged, menu.conf);
		addWidget(searchBar);
		setInitialFocus(searchBar);
	}

	@Override
	public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_){
		String s = searchBar.getValue();
		init(p_resize_1_, p_resize_2_, p_resize_3_);
		searchBar.setValue(s);
	}

	@Override
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_){
		if(p_keyPressed_1_ == 256){
			minecraft.player.closeContainer();
		}

		return searchBar.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) || searchBar.canConsumeInput() || super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
	}

	@Override
	public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks){
		renderBackground(matrix);
		super.render(matrix, mouseX, mouseY, partialTicks);
//		RenderSystem.disableLighting();
		RenderSystem.disableBlend();
		searchBar.render(matrix, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void renderBg(PoseStack matrix, float partialTicks, int mouseX, int mouseY){
		RenderSystem.setShaderTexture(0, SEARCH_BAR_TEXTURE);
		blit(matrix, leftPos, topPos, 0, 0, imageWidth, 18, imageWidth, 18);
	}

	private void entryChanged(String newFilter){
		float output = RedstoneUtil.interpretFormulaString(newFilter);
		menu.output = output;
		CompoundTag nbt = new CompoundTag();
		nbt.putFloat("value", output);
		nbt.putString("config", newFilter);
		if(menu.pos != null){
			EssentialsPackets.channel.sendToServer(new SendNBTToServer(nbt, menu.pos));
		}
	}

	//MCP note: draw tooltip/foreground
	@Override
	protected void renderLabels(PoseStack matrix, int p_230451_2_, int p_230451_3_){
		//Don't render text overlays
	}
}
