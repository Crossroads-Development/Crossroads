package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.CircuitUtil;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.beams.BeamUnit;
import com.Da_Technomancer.crossroads.gui.container.BeamExtractorCreativeContainer;
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

public class BeamExtractorCreativeScreen extends AbstractContainerScreen<BeamExtractorCreativeContainer>{

	private static final ResourceLocation SEARCH_BAR_TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/container/beam_extractor_creative_gui.png");
	private EditBox[] searchBars;//En, Po, St, Vo

	public BeamExtractorCreativeScreen(BeamExtractorCreativeContainer cont, Inventory playerInventory, Component text){
		super(cont, playerInventory, text);
		imageHeight = 72;
		imageWidth = 164;
	}

	@Override
	protected void init(){
		super.init();
		searchBars = new EditBox[4];
		for(int i = 0; i < 4; i++){
			searchBars[i] = CircuitUtil.createFormulaInputUIComponent(this, font, 4 + 20, 8 + 18*i, Component.translatable("container.search_bar"), this::entryChanged, menu.conf[i]);
			searchBars[i].setCanLoseFocus(true);
			addWidget(searchBars[i]);
		}
//		setInitialFocus(searchBar);
	}

	@Override
	public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_){
		String[] s = new String[searchBars.length];
		for(int i = 0; i < 4; i++){
			s[i] = searchBars[i].getValue();
		}
		init(p_resize_1_, p_resize_2_, p_resize_3_);
		for(int i = 0; i < 4; i++){
			searchBars[i].setValue(s[i]);
		}
	}

	@Override
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_){
		if(p_keyPressed_1_ == 256){
			minecraft.player.closeContainer();
		}

		for(EditBox searchBar : searchBars){
			if(searchBar.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) || searchBar.canConsumeInput()){
				return true;
			}
		}
		return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
	}

	@Override
	public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks){
		renderBackground(matrix);
		super.render(matrix, mouseX, mouseY, partialTicks);
//		RenderSystem.disableLighting();
		RenderSystem.disableBlend();
		for(EditBox searchBar : searchBars){
			searchBar.render(matrix, mouseX, mouseY, partialTicks);
		}
	}

	@Override
	protected void renderBg(PoseStack matrix, float partialTicks, int mouseX, int mouseY){
		RenderSystem.setShaderTexture(0, SEARCH_BAR_TEXTURE);
		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageWidth);
	}

	private void entryChanged(String newFilter){
		CompoundTag nbt = new CompoundTag();
		int[] newOutput = new int[4];
		for(int i = 0; i < 4; i++){
			menu.conf[i] = searchBars[i].getValue();
			newOutput[i] = Math.max(0, Math.round(RedstoneUtil.interpretFormulaString(menu.conf[i])));
			nbt.putString("config_" + i, menu.conf[i]);
		}
		menu.output = new BeamUnit(newOutput);
		menu.output.writeToNBT("value", nbt);
		if(menu.pos != null){
			EssentialsPackets.channel.sendToServer(new SendNBTToServer(nbt, menu.pos));
		}
	}

	//MCP note: draw tooltip/foreground
	@Override
	protected void renderLabels(PoseStack matrix, int p_230451_2_, int p_230451_3_){
		//Don't render default text overlays
		font.draw(matrix, MiscUtil.localize("container.crossroads.beam_extractor_creative.energy"), 8, 6, 0x404040);
		font.draw(matrix, MiscUtil.localize("container.crossroads.beam_extractor_creative.potential"), 8, 24, 0x404040);
		font.draw(matrix, MiscUtil.localize("container.crossroads.beam_extractor_creative.stability"), 8, 42, 0x404040);
		font.draw(matrix, MiscUtil.localize("container.crossroads.beam_extractor_creative.void"), 8, 60, 0x404040);
	}
}
