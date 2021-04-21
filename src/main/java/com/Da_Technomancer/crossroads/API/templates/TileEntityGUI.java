package com.Da_Technomancer.crossroads.API.templates;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;

public abstract class TileEntityGUI<T extends TileEntityContainer<U>, U extends TileEntity & IInventory> extends ContainerScreen<T>{

	protected ArrayList<ITextComponent> tooltip = new ArrayList<>();

	protected TileEntityGUI(T container, PlayerInventory playerInventory, ITextComponent text){
		super(container, playerInventory, text);
	}

	@Override
	protected void init(){
		super.init();
		inventoryLabelX = menu.getInvStart()[0];
		inventoryLabelY = menu.getInvStart()[1] - 12;
	}

	@Override
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks){
		renderBackground(matrix);
		super.render(matrix, mouseX, mouseY, partialTicks);
		renderTooltip(matrix, mouseX, mouseY);
		if(getSlotUnderMouse() == null){
			renderComponentTooltip(matrix, tooltip, mouseX, mouseY);
		}
		tooltip.clear();
	}

	@Override
	protected void renderBg(MatrixStack matrix, float partialTicks, int mouseX, int mouseY){
		//No-op
		//Even though we don't need to implement this method, as this is an abstract class, having this here allows super calls in subclasses.
		//While this currently does nothing, that could change, and allowing super calls in subclasses will make changes seamless
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
