package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.gui.container.HeatLimiterContainer;
import com.Da_Technomancer.essentials.Essentials;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import com.Da_Technomancer.essentials.packets.EssentialsPackets;
import com.Da_Technomancer.essentials.packets.SendNBTToServer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class HeatLimiterScreen extends ContainerScreen<HeatLimiterContainer>{

	private static final ResourceLocation SEARCH_BAR_TEXTURE = new ResourceLocation(Essentials.MODID, "textures/gui/search_bar.png");
	private TextFieldWidget searchBar;

	public HeatLimiterScreen(HeatLimiterContainer cont, PlayerInventory playerInventory, ITextComponent text){
		super(cont, playerInventory, text);
		ySize = 18;
		xSize = 144;
	}

	@Override
	protected void init(){
		super.init();
		searchBar = new TextFieldWidget(font, guiLeft + 4, guiTop + 8, 144 - 4, 18, new TranslationTextComponent("container.search_bar"));
		searchBar.setCanLoseFocus(false);
		searchBar.setTextColor(-1);
		searchBar.setDisabledTextColour(-1);
		searchBar.setEnableBackgroundDrawing(false);
		searchBar.setMaxStringLength(20);
		searchBar.setResponder(this::entryChanged);
		searchBar.setValidator(s -> {
			final String whitelist = "0123456789 xX*/+-^piPIeE().";
			for(int i = 0; i < s.length(); i++){
				if(!whitelist.contains(s.substring(i, i + 1))){
					return false;
				}
			}

			return true;
		});
		children.add(searchBar);
		setFocusedDefault(searchBar);

		searchBar.setText(container.conf);
	}

	@Override
	public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_){
		String s = searchBar.getText();
		init(p_resize_1_, p_resize_2_, p_resize_3_);
		searchBar.setText(s);
	}

	@Override
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_){
		if(p_keyPressed_1_ == 256){
			minecraft.player.closeScreen();
		}

		return searchBar.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) || searchBar.canWrite() || super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
	}

	@Override
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks){
		renderBackground(matrix);
		super.render(matrix, mouseX, mouseY, partialTicks);
		RenderSystem.disableLighting();
		RenderSystem.disableBlend();
		searchBar.render(matrix, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void func_230450_a_(MatrixStack matrix, float partialTicks, int mouseX, int mouseY){
		minecraft.getTextureManager().bindTexture(SEARCH_BAR_TEXTURE);
		blit(matrix, guiLeft, guiTop, 0, 0, xSize, 18, xSize, 18);
	}

	private void entryChanged(String newFilter){
		float output = RedstoneUtil.interpretFormulaString(newFilter);
		container.output = output;
		CompoundNBT nbt = new CompoundNBT();
		nbt.putFloat("value", output);
		nbt.putString("config", newFilter);
		if(container.pos != null){
			EssentialsPackets.channel.sendToServer(new SendNBTToServer(nbt, container.pos));
		}
	}
}
