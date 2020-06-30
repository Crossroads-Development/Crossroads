package com.Da_Technomancer.crossroads.API.templates;

import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;

public abstract class TileEntityGUI<T extends TileEntityContainer<U>, U extends TileEntity & IInventory> extends ContainerScreen<T>{

	protected PlayerInventory playerInv;
	protected ArrayList<String> tooltip = new ArrayList<>();

	protected TileEntityGUI(T container, PlayerInventory playerInventory, ITextComponent text){
		super(container, playerInventory, text);
		this.playerInv = playerInventory;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks){
		renderBackground();
		super.render(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
		if(getSlotUnderMouse() == null){
			renderTooltip(tooltip, mouseX, mouseY);
		}
		tooltip.clear();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		for(IGuiEventListener gui : children){
			if(gui instanceof IGuiObject){
				((IGuiObject) gui).drawBack(partialTicks, mouseX, mouseY, font);
			}
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		for(IGuiEventListener gui : children){
			if(gui instanceof IGuiObject){
				((IGuiObject) gui).drawFore(mouseX, mouseY, font);
			}
		}

		font.drawString(title.getFormattedText(), 8, 6, 0x404040);
		font.drawString(playerInv.getDisplayName().getFormattedText(), container.getInvStart()[0], container.getInvStart()[1] - 12, 0x404040);
	}

	@Override
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_){
		for(IGuiEventListener gui : children){
			if(gui.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)){
				return true;
			}
		}
		return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
	}

	@Override
	public boolean charTyped(char key, int keyCode){
		for(IGuiEventListener gui : children){
			if(gui.charTyped(key, keyCode)){
				return true;
			}
		}

		return super.charTyped(key, keyCode);
	}
}
