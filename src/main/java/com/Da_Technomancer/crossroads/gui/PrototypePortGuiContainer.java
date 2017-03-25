package com.Da_Technomancer.crossroads.gui;

import java.io.IOException;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.enums.PrototypePortTypes;
import com.Da_Technomancer.crossroads.API.gui.RadioButtonGuiObject;
import com.Da_Technomancer.crossroads.API.gui.ToggleButtonGuiObject;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToServer;
import com.Da_Technomancer.crossroads.gui.container.PrototypePortContainer;
import com.Da_Technomancer.crossroads.tileentities.technomancy.PrototypePortTileEntity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class PrototypePortGuiContainer extends GuiContainer{

	private static final ResourceLocation GUI_TEXTURES = new ResourceLocation(Main.MODID, "textures/gui/container/prototype_port_gui.png");
	private PrototypePortTileEntity te;
	
	private RadioButtonGuiObject types;
	private RadioButtonGuiObject in_out;
	private RadioButtonGuiObject side;
	
	public PrototypePortGuiContainer(PrototypePortTileEntity te){
		super(new PrototypePortContainer(te));
		this.te = te;
		
		this.xSize = 160;
		this.ySize = 160;
	}

	@Override
	public void initGui(){
		super.initGui();
		types = new RadioButtonGuiObject(new ToggleButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 20, 10, 50, "Redst."), new ToggleButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 90, 10, 50, "Rotary"), new ToggleButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 20, 35, 50, "Beams"), new ToggleButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 90, 35, 50, "Heat"));
		in_out = new RadioButtonGuiObject(new ToggleButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 50, 70, 30, "In"), new ToggleButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 80, 70, 30, "Out"));
		side = new RadioButtonGuiObject(new ToggleButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 10, 105, 40, "Down"), new ToggleButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 10, 130, 40, "Up"), new ToggleButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 60, 105, 40, "North"), new ToggleButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 60, 130, 40, "South"), new ToggleButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 110, 105, 40, "West"), new ToggleButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 110, 130, 40, "East"));
		
		PrototypePortTypes type = te.getType();
		types.setPressed(type == PrototypePortTypes.REDSTONE_IN || type == PrototypePortTypes.REDSTONE_OUT ? 0 : type == PrototypePortTypes.ROTARY ? 1 : type == PrototypePortTypes.MAGIC_IN || type == PrototypePortTypes.MAGIC_OUT ? 2 : 3);
		in_out.setLocked((types.getPressed() & 1) == 1);
		in_out.setPressed(type.isInput() ? 0 : 1);
		side.setPressed(te.getSide().getIndex());
	}
	
	@Override
	public void onGuiClosed(){
		super.onGuiClosed();
		te.setSide(EnumFacing.getFront(side.getPressed()));
		int typeIndex = types.getPressed();
		PrototypePortTypes type = typeIndex == 0 ? (in_out.getPressed() == 0 ? PrototypePortTypes.REDSTONE_IN : PrototypePortTypes.REDSTONE_OUT) : typeIndex == 1 ? PrototypePortTypes.ROTARY : typeIndex == 2 ? (in_out.getPressed() == 0 ? PrototypePortTypes.MAGIC_IN : PrototypePortTypes.MAGIC_OUT) : PrototypePortTypes.HEAT;
		te.setType(type);
		ModPackets.network.sendToServer(new SendIntToServer("side_type", side.getPressed() + (type.ordinal() << 3), te.getPos(), te.getWorld().provider.getDimension()));
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		GlStateManager.color(1, 1, 1);
		mc.getTextureManager().bindTexture(GUI_TEXTURES);

		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		drawTexturedModalRect(i, j, 0, 0, xSize, ySize);
		
		types.drawBack(partialTicks, mouseX, mouseY, fontRendererObj);
		in_out.drawBack(partialTicks, mouseX, mouseY, fontRendererObj);
		side.drawBack(partialTicks, mouseX, mouseY, fontRendererObj);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		types.drawFore(mouseX, mouseY, fontRendererObj);
		in_out.drawFore(mouseX, mouseY, fontRendererObj);
		side.drawFore(mouseX, mouseY, fontRendererObj);
	}
	
	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException {
		super.mouseClicked(x, y, button);
		if(types.mouseClicked(x, y, button)){
			in_out.setLocked((types.getPressed() & 1) == 1);
			return;
		}
		if(in_out.mouseClicked(x, y, button)){
			return;
		}
		side.mouseClicked(x, y, button);
	}
}
