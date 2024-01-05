package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.MathUtil;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.packets.CRPackets;
import com.Da_Technomancer.crossroads.api.packets.SendLongToServer;
import com.Da_Technomancer.crossroads.api.templates.MachineScreen;
import com.Da_Technomancer.crossroads.api.templates.WidgetUtil;
import com.Da_Technomancer.crossroads.blocks.rotary.SteamTurbineTileEntity;
import com.Da_Technomancer.crossroads.gui.container.SteamTurbineContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.gui.widget.ExtendedButton;

public class SteamTurbineScreen extends MachineScreen<SteamTurbineContainer, SteamTurbineTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/container/radiator_gui.png");

	private ExtendedButton upButton;
	private ExtendedButton downButton;
	private WidgetUtil.SettingAdjustListener listener;

	public SteamTurbineScreen(SteamTurbineContainer cont, Inventory playerInv, Component name){
		super(cont, playerInv, name);
	}

	@Override
	public void init(){
		super.init();
		initFluidManager(1, 10, 70);//Steam
		initFluidManager(0, 70, 70);//D-water
		upButton = new ExtendedButton(getGuiLeft() + imageWidth - 40, getGuiTop() + 18, 16, 16, Component.literal("˄"), button -> adjustMode(1));
		downButton = new ExtendedButton(getGuiLeft() + imageWidth - 40, getGuiTop() + 54, 16, 16, Component.literal("˅"), button -> adjustMode(-1));
		addRenderableWidget(upButton);
		addRenderableWidget(downButton);
		listener = new WidgetUtil.SettingAdjustListener(0, 0, SteamTurbineTileEntity.TIERS.length - 1, downButton, upButton);
		menu.addSlotListener(listener);
		listener.updateButtons(menu.mode.get());
	}

	private void adjustMode(int change){
		int newMode = menu.mode.get() + change;
		newMode = MathUtil.clamp(newMode, 0, SteamTurbineTileEntity.TIERS.length - 1);
		menu.mode.set(newMode);
		listener.updateButtons(newMode);
		if(te != null){
			CRPackets.channel.sendToServer(new SendLongToServer(5, newMode, te.getBlockPos()));
		}
	}

	@Override
	protected void renderBg(GuiGraphics matrix, float partialTicks, int mouseX, int mouseY){
		matrix.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		super.renderBg(matrix, partialTicks, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(GuiGraphics matrix, int mouseX, int mouseY){
		super.renderLabels(matrix, mouseX, mouseY);

		//Render a label for power production rate
		if(menu.mode != null){
			String s = MiscUtil.localize("tt.crossroads.steam_turbine.yield", SteamTurbineTileEntity.TIERS[menu.mode.get()] * CRConfig.steamWorth.get() / 1000 * CRConfig.jouleWorth.get());
			matrix.drawString(font, s, imageWidth - 32 - font.width(s) / 2, 36, 0x404040, false);
			s = MiscUtil.localize("tt.crossroads.boilerplate.fluid_rate", SteamTurbineTileEntity.TIERS[menu.mode.get()]);
			matrix.drawString(font, s, imageWidth - 32 - font.width(s) / 2, 44, 0x404040, false);
		}
	}
}
