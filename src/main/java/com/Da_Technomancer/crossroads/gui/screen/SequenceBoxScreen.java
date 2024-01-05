package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.CircuitUtil;
import com.Da_Technomancer.crossroads.api.packets.CRPackets;
import com.Da_Technomancer.crossroads.api.templates.WidgetUtil;
import com.Da_Technomancer.crossroads.blocks.technomancy.SequenceBoxTileEntity;
import com.Da_Technomancer.crossroads.gui.container.SequenceBoxContainer;
import com.Da_Technomancer.essentials.Essentials;
import com.Da_Technomancer.essentials.api.packets.SendNBTToServer;
import com.Da_Technomancer.essentials.api.redstone.RedstoneUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;

public class SequenceBoxScreen extends AbstractContainerScreen<SequenceBoxContainer>{

	private static final ResourceLocation SEARCH_BAR_TEXTURE = new ResourceLocation(Essentials.MODID, "textures/gui/search_bar.png");
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/container/sequence_box_gui.png");

	//Should probably explain what's going on here:
	//The 'proper' way to have a multiline edit-able text screen is to basically make my own, which would involve writing handling for copy-pasting, multi-line selection, weird keyboard buttons, and so on
	//Instead of doing all that, we use a bunch of TextFieldWidgets laid alongside each other, with each Widget being one line of text
	//Scrolling up or down means moving all the text between the widgets, and doing all sorts of careful handling to prevent infinite loops
	//Somehow this was simpler and less error-prone than doing it 'properly' (none of this stuff has MCP mappings, OK?)

	private final EditBox[] inputBars = new EditBox[8];
	private int topIndex = 0;//Index in the container input list corresponding to the text widget at the top of the UI
	private boolean shiftingUI = false;//When true, all changes to the contents of the input bars is internal and not player input

	public SequenceBoxScreen(SequenceBoxContainer container, Inventory inv, Component name){
		super(container, inv, name);
		imageWidth = 176;
		imageHeight = 166;
	}

	@Override
	protected void init(){
		super.init();

		shiftingUI = true;
		for(int i = 0; i < inputBars.length; i++){
			inputBars[i] = CircuitUtil.createFormulaInputUIComponent(this, font, 24, 24 + 18 * i, Component.literal(""), this::entryChanged, menu.inputs.size() > i ? menu.inputs.get(i) : "");
			inputBars[i].setCanLoseFocus(true);
			addRenderableWidget(inputBars[i]);
		}
		shiftingUI = false;
	}

	@Override
	public void resize(Minecraft minecraft, int width, int height){
		WidgetUtil.resize(this, minecraft, width, height);
	}


	@Override
	protected void renderBg(GuiGraphics matrix, float partialTicks, int mouseX, int mouseY){
		RenderSystem.setShaderColor(1, 1, 1, 1);

		matrix.blit(BACKGROUND_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		//Text bars
		RenderSystem.setShaderTexture(0, SEARCH_BAR_TEXTURE);
		for(EditBox bar : inputBars){
			matrix.blit(BACKGROUND_TEXTURE, bar.getX() - 2, bar.getY() - 8, 0, 0, bar.getWidth(), 18, 144, 18);
		}
	}

	@Override
	protected void renderLabels(GuiGraphics matrix, int mouseX, int mouseY){
		matrix.drawString(font, title, titleLabelX, titleLabelY, 0x404040, false);

		for(int i = 0; i < inputBars.length; i++){
			int lineNumber = i + topIndex;
			String lineStr = "" + (lineNumber + 1);
			matrix.drawString(font, lineStr, 8, 24 + i * 18, lineNumber == menu.outputIndex ? 0xFF0000 : lineNumber < menu.inputs.size() ? 0xFFFF00 : 0x404040, false);
		}
	}

	@Override
	public void render(GuiGraphics matrix, int mouseX, int mouseY, float partialTicks){
		renderBackground(matrix);
		super.render(matrix, mouseX, mouseY, partialTicks);
//		RenderSystem.disableLighting();
//		RenderSystem.disableBlend();
	}

	private void moveToIndex(int newTopIndex){
		entryChanged("");//Make sure any data is saved
		shiftingUI = true;
		topIndex = Math.min(newTopIndex, SequenceBoxTileEntity.MAX_VALUES - inputBars.length);
		for(int i = 0; i < inputBars.length; i++){
			int barIndex = topIndex + i;
			inputBars[i].setValue(barIndex < menu.inputs.size() ? menu.inputs.get(barIndex) : "");
		}

		shiftingUI = false;
	}

	private void setSelection(int index){
		index = Math.min(index, SequenceBoxTileEntity.MAX_VALUES - 1);
		for(EditBox widget : inputBars){
			if((index < 0 || widget != inputBars[index]) && widget.isFocused()){
				widget.setFocused(false);
			}
		}

		if(index != -1){
			if(!inputBars[index].isFocused()){
				inputBars[index].setFocused(true);
			}
			setFocused(inputBars[index]);
		}else{
			setFocused(null);
		}
	}

	private int getSelection(){
		for(int i = 0; i < inputBars.length; i++){
			if(inputBars[i].isFocused()){
				return i;
			}
		}
		return -1;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta){
		//Allow moving selection up/down with the scroll wheel
		return keyPressed(delta < 0 ? GLFW.GLFW_KEY_DOWN : GLFW.GLFW_KEY_UP, 0, 0);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers){
		if(keyCode == GLFW.GLFW_KEY_ESCAPE){//256
			minecraft.player.closeContainer();
			return true;
		}

		if(keyCode == GLFW.GLFW_KEY_DOWN || keyCode == GLFW.GLFW_KEY_ENTER){
			int selected = getSelection();
			if(selected == inputBars.length - 1){
				moveToIndex(topIndex + 1);
			}else{
				setSelection(selected + 1);
			}
		}else if(keyCode == GLFW.GLFW_KEY_UP){
			//Move selection up, or scroll up as needed
			int selected = getSelection();
			if(selected > 0){
				setSelection(selected - 1);
			}else if(topIndex > 0){
				moveToIndex(topIndex - 1);
			}
		}else if(keyCode == GLFW.GLFW_KEY_TAB){
			//Change the output index to the currently selected text widget
			menu.outputIndex = Math.min(getSelection() + topIndex, menu.inputs.size() - 1);
			updateTEWithPacket();
		}else{
			return WidgetUtil.keyPressed(keyCode, scanCode, modifiers, inputBars) || super.keyPressed(keyCode, scanCode, modifiers);
		}

		return true;
	}

	private void entryChanged(String ignored){
		//We ignore the passed string, as it doesn't tell us which line changed or the context, only the new text on the changed line
		if(!shiftingUI){
			//We look at all input bars, and their new values
			//This can change the inputs list for values outside these indices, as we might need to extend with 0 values

			for(int barIndex = inputBars.length - 1; barIndex >= 0; barIndex--){
				int inputIndex = barIndex + topIndex;
				if(inputIndex + 1 > SequenceBoxTileEntity.MAX_VALUES){
					continue;//Should never happen
				}
				String barContents = inputBars[barIndex].getValue();
				boolean hasContents = !barContents.trim().isEmpty();
//				float barValue = RedstoneUtil.interpretFormulaString(barContents);
				if(hasContents){
					while(menu.inputs.size() <= inputIndex){
						menu.inputs.add("");//Pad the inputs list with empty strings to reach this index
					}
					menu.inputs.set(inputIndex, barContents);
				}else if(inputIndex == menu.inputs.size() - 1){
					menu.inputs.remove(inputIndex);//We are the last entry in inputs, and the bar is now empty. Remove
				}else if(inputIndex < menu.inputs.size() - 1){
					menu.inputs.set(inputIndex, "");//We are an entry that is still needed as padding for a later padding. Change our own value, but don't change the overall length
				}
			}

			menu.outputIndex = Math.min(menu.outputIndex, menu.inputs.size() - 1);
			updateTEWithPacket();
		}
	}

	private void updateTEWithPacket(){
		if(menu.outputIndex < 0){
			menu.outputIndex = 0;
		}
		CompoundTag nbt = new CompoundTag();
		nbt.putInt("output_index", menu.outputIndex);
		for(int i = 0; i < menu.inputs.size(); i++){
			nbt.putFloat(i + "_val", RedstoneUtil.interpretFormulaString(menu.inputs.get(i)));
			nbt.putString(i + "_str", menu.inputs.get(i));
		}
		CRPackets.sendPacketToServer(new SendNBTToServer(nbt, menu.pos));
	}
}
