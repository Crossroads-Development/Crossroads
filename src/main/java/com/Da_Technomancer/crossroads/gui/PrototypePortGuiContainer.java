package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.templates.RadioButtonGuiObject;
import com.Da_Technomancer.crossroads.API.templates.TextBarGuiObject;
import com.Da_Technomancer.crossroads.API.templates.ToggleButtonGuiObject;
import com.Da_Technomancer.crossroads.API.packets.CrossroadsPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToServer;
import com.Da_Technomancer.crossroads.API.packets.SendStringToServer;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypePortTypes;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.PrototypePortContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class PrototypePortGuiContainer extends ContainerScreen{

	private static final ResourceLocation GUI_TEXTURES = new ResourceLocation(Crossroads.MODID, "textures/gui/container/prototype_port_gui.png");
	private PrototypePortTileEntity te;
	
	private RadioButtonGuiObject types;
	private RadioButtonGuiObject in_out;
	private RadioButtonGuiObject side;
	private TextBarGuiObject descBar;
	
	public PrototypePortGuiContainer(PrototypePortTileEntity te){
		super(new PrototypePortContainer(te));
		this.te = te;
		
		this.xSize = 160;
		this.ySize = 160;
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks){
		renderBackground();
		super.render(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	public void init(){
		super.init();
		types = new RadioButtonGuiObject(new ToggleButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 20, 10, 50, "Redst."), new ToggleButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 90, 10, 50, "Rotary"), new ToggleButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 20, 35, 50, "Beams"), new ToggleButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 90, 35, 50, "Heat"));
		in_out = new RadioButtonGuiObject(new ToggleButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 10, 70, 30, "In"), new ToggleButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 40, 70, 30, "Out"));
		side = new RadioButtonGuiObject(new ToggleButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 10, 105, 40, "Down"), new ToggleButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 10, 130, 40, "Up"), new ToggleButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 60, 105, 40, "North"), new ToggleButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 60, 130, 40, "South"), new ToggleButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 110, 105, 40, "West"), new ToggleButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 110, 130, 40, "East"));
		descBar = new TextBarGuiObject((width - xSize) / 2, (height - ySize) / 2, 72, 70, 80, 20, "Description", (Character key) -> StringUtils.isAsciiPrintable(String.valueOf(key)));

		PrototypePortTypes type = te.getType();
		types.setPressed(type == PrototypePortTypes.REDSTONE_IN || type == PrototypePortTypes.REDSTONE_OUT ? 0 : type == PrototypePortTypes.ROTARY ? 1 : type == PrototypePortTypes.MAGIC_IN || type == PrototypePortTypes.MAGIC_OUT ? 2 : 3);
		in_out.setLocked((types.getPressed() & 1) == 1);
		in_out.setPressed(type.isInput() ? 0 : 1);
		side.setPressed(te.getSide().getIndex());
		descBar.setText(te.desc);
	}
	
	@Override
	public void onGuiClosed(){
		super.onGuiClosed();
		te.setSide(Direction.byIndex(side.getPressed()));
		int typeIndex = types.getPressed();
		PrototypePortTypes type = typeIndex == 0 ? (in_out.getPressed() == 0 ? PrototypePortTypes.REDSTONE_IN : PrototypePortTypes.REDSTONE_OUT) : typeIndex == 1 ? PrototypePortTypes.ROTARY : typeIndex == 2 ? (in_out.getPressed() == 0 ? PrototypePortTypes.MAGIC_IN : PrototypePortTypes.MAGIC_OUT) : PrototypePortTypes.HEAT;
		te.setType(type);
		te.desc = descBar.getText();
		CrossroadsPackets.network.sendToServer(new SendIntToServer((byte) 0, side.getPressed() + (type.ordinal() << 3), te.getPos(), te.getWorld().provider.getDimension()));
		CrossroadsPackets.network.sendToServer(new SendStringToServer("desc", descBar.getText(), te.getPos(), te.getWorld().provider.getDimension()));
		te.getWorld().markBlockRangeForRenderUpdate(te.getPos(), te.getPos());
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		GlStateManager.color(1, 1, 1);
		Minecraft.getInstance().getTextureManager().bindTexture(GUI_TEXTURES);

		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		blit(i, j, 0, 0, xSize, ySize);
		
		types.drawBack(partialTicks, mouseX, mouseY, fontRenderer);
		in_out.drawBack(partialTicks, mouseX, mouseY, fontRenderer);
		side.drawBack(partialTicks, mouseX, mouseY, fontRenderer);
		descBar.drawBack(partialTicks, mouseX, mouseY, fontRenderer);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		types.drawFore(mouseX, mouseY, fontRenderer);
		in_out.drawFore(mouseX, mouseY, fontRenderer);
		side.drawFore(mouseX, mouseY, fontRenderer);
		descBar.drawFore(mouseX, mouseY, fontRenderer);
	}

	@Override
	protected void keyTyped(char key, int keyCode) throws IOException{
		if(!descBar.charTyped(key, keyCode)){
			super.keyTyped(key, keyCode);
		}
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
		if(descBar.mouseClicked(x, y, button)){
			return;
		}
		side.mouseClicked(x, y, button);
	}
}
