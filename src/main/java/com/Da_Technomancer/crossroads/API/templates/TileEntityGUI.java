package com.Da_Technomancer.crossroads.API.templates;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;

import java.util.ArrayList;

public abstract class TileEntityGUI<T extends TileEntityContainer<U>, U extends TileEntity & IInventory> extends ContainerScreen<T>{

	protected PlayerInventory playerInv;
	protected ArrayList<ITextProperties> tooltip = new ArrayList<>();

	protected TileEntityGUI(T container, PlayerInventory playerInventory, ITextComponent text){
		super(container, playerInventory, text);
		this.playerInv = playerInventory;
	}

	@Override
	protected void init(){
		super.init();
		field_238744_r_ = container.getInvStart()[0];//MCP note: player inventory text overlay x position
		field_238745_s_ = container.getInvStart()[1] - 12;//MCP note: player inventory text overlay y position
	}

	@Override
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks){
		renderBackground(matrix);
		super.render(matrix, mouseX, mouseY, partialTicks);
		func_230459_a_(matrix, mouseX, mouseY);//render tooltip
		if(getSlotUnderMouse() == null){
			renderTooltip(matrix, tooltip, mouseX, mouseY);
		}
		tooltip.clear();
	}

	@Override
	protected void func_230450_a_(MatrixStack matrix, float partialTicks, int mouseX, int mouseY){
		matrix.push();
		drawGuiContainerBackgroundLayer(matrix, partialTicks, mouseX, mouseY);
		matrix.translate(guiLeft, guiTop, 0);
		drawGuiContainerForegroundLayer(matrix, mouseX, mouseY);
		matrix.pop();
	}

	/**
	 * Draw the background layer of the UI. Called before drawGuiContainerForegroundLayer
	 * @param matrix Matrix, relative to the top left of the screen
	 * @param partialTicks Partial ticks
	 * @param mouseX Mouse x coordinate, relative to UI start
	 * @param mouseY Mouse y coordinate, relative to UI start
	 */
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrix, float partialTicks, int mouseX, int mouseY){

	}

	/**
	 * Draw the foreground layer of the UI. Called after drawGuiContainerBackgroundLayer
	 * @param matrix Matrix, relative to the top left of the UI (not screen)
	 * @param mouseX Mouse x coordinate, relative to UI start
	 * @param mouseY Mouse y coordinate, relative to UI start
	 */
	protected void drawGuiContainerForegroundLayer(MatrixStack matrix, int mouseX, int mouseY){

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
