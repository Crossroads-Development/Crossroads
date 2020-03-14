package com.Da_Technomancer.crossroads.API.templates;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.essentials.gui.container.FluidSlotManager;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;

public abstract class MachineGUI<T extends MachineContainer<U>, U extends InventoryTE> extends ContainerScreen<T>{

	protected U te;
	protected PlayerInventory playerInv;
	protected ArrayList<String> tooltip = new ArrayList<>();

	protected MachineGUI(T container, PlayerInventory playerInventory, ITextComponent text){
		super(container, playerInventory, text);
		this.te = container.te;
		this.playerInv = playerInventory;
	}

	/**
	 * Helper function to init a fluid manager on a per-screen level
	 * @param index The index of the fluid manager
	 * @param x The x position for it to start
	 * @param y The y position for it to start
	 */
	protected void initFluidManager(int index, int x, int y){
		te.fluidManagers[index].initScreen((width - xSize) / 2, (height - ySize) / 2, x, y, container.fluidManagerRefs[index][0], container.fluidManagerRefs[index][1]);
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
		for(FluidSlotManager manager : te.fluidManagers){
			manager.renderBack(partialTicks, mouseX, mouseY, font);
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
		font.drawString(playerInv.getDisplayName().getFormattedText(), container.getInvStart()[0], container.getInvStart()[1] - 12, 4210752);


		if(container.heatRef != null){
			String s = MiscUtil.localize("container.crossroads.boilerplate.temp", container.heatRef.get());
			font.drawString(s, xSize - 8 - font.getStringWidth(s), 6, 0x404040);
		}
		if(container.rotRef != null){
			String s = MiscUtil.localize("container.crossroads.boilerplate.speed", container.rotRef.get() / 100D);
			font.drawString(s, xSize - 8 - font.getStringWidth(s), te.useHeat() ? 16 : 6, 0x404040);
		}

		for(FluidSlotManager manager : te.fluidManagers){
			manager.renderFore(mouseX, mouseY, font, tooltip);
		}
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
