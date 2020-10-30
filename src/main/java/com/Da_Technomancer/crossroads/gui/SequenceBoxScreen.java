package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.SequenceBoxContainer;
import com.Da_Technomancer.crossroads.tileentities.technomancy.SequenceBoxTileEntity;
import com.Da_Technomancer.essentials.Essentials;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
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
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;

import java.util.function.Predicate;

public class SequenceBoxScreen extends ContainerScreen<SequenceBoxContainer>{

	private static final ResourceLocation SEARCH_BAR_TEXTURE = new ResourceLocation(Essentials.MODID, "textures/gui/search_bar.png");
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/container/sequence_box_gui.png");

	//Should probably explain what's going on here:
	//The 'proper' way to have a multiline edit-able text screen is to basically make my own, which would involve writing handling for copy-pasting, multi-line selection, weird keyboard buttons, and so on
	//Instead of doing all that, we use a bunch of TextFieldWidgets laid alongside each other, with each Widget being one line of text
	//Scrolling up or down means moving all the text between the widgets, and doing all sorts of careful handling to prevent infinite loops
	//Somehow this was simpler and less error-prone than doing it 'properly' (none of this stuff has MCP mappings, OK?)

	private final TextFieldWidget[] inputBars = new TextFieldWidget[8];
	private int topIndex = 0;//Index in the container input list corresponding to the text widget at the top of the UI
	private boolean shiftingUI = false;//When true, all changes to the contents of the input bars is internal and not player input

	public SequenceBoxScreen(SequenceBoxContainer container, PlayerInventory inv, ITextComponent name){
		super(container, inv, name);
		xSize = 176;
		ySize = 166;
	}

	@Override
	protected void init(){
		super.init();
		final Predicate<String> validator = s -> {
			final String whitelist = "0123456789 xX*/+-^piPIeE().";
			for(int i = 0; i < s.length(); i++){
				if(!whitelist.contains(s.substring(i, i + 1))){
					return false;
				}
			}
			return true;
		};

		for(int i = 0; i < inputBars.length; i++){
			inputBars[i] = new TextFieldWidget(font, guiLeft + 24, guiTop + 24 + 18 * i, 144 - 4, 18, new StringTextComponent(""));
			inputBars[i].setCanLoseFocus(true);
			inputBars[i].setTextColor(-1);
			inputBars[i].setDisabledTextColour(-1);
			inputBars[i].setEnableBackgroundDrawing(false);
			inputBars[i].setMaxStringLength(20);
			inputBars[i].setText(container.inputs.size() > i ? container.inputs.get(i) : "");
			inputBars[i].setResponder(this::entryChanged);
			inputBars[i].setValidator(validator);
			children.add(inputBars[i]);
		}
	}

	@Override
	public void resize(Minecraft minecraft, int width, int height){
		String[] text = new String[inputBars.length];
		for(int i = 0; i < inputBars.length; i++){
			text[i] = inputBars[i].getText();
		}
		init(minecraft, width, height);
		for(int i = 0; i < inputBars.length; i++){
			inputBars[i].setText(text[i]);
		}
	}


	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrix, float partialTicks, int mouseX, int mouseY){
		RenderSystem.color4f(1, 1, 1, 1);
		Minecraft.getInstance().getTextureManager().bindTexture(BACKGROUND_TEXTURE);

		blit(matrix, guiLeft, guiTop, 0, 0, xSize, ySize);

		//Text bars
		minecraft.getTextureManager().bindTexture(SEARCH_BAR_TEXTURE);
		for(TextFieldWidget bar : inputBars){
			blit(matrix, bar.x - 2, bar.y - 8, 0, 0, bar.getWidth(), 18, 144, 18);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrix, int mouseX, int mouseY){
		font.func_243248_b(matrix, title, titleX, titleY, 0x404040);

		for(int i = 0; i < inputBars.length; i++){
			int lineNumber = i + topIndex;
			String lineStr = "" + (lineNumber + 1);
			font.drawString(matrix, lineStr, 8, 24 + i * 18, lineNumber == container.outputIndex ? 0xFF0000 : lineNumber < container.inputs.size() ? 0xFFFF00 : 0x404040);
		}
	}

	@Override
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks){
		renderBackground(matrix);
		super.render(matrix, mouseX, mouseY, partialTicks);
		RenderSystem.disableLighting();
		RenderSystem.disableBlend();
		for(TextFieldWidget bar : inputBars){
			bar.render(matrix, mouseX, mouseY, partialTicks);
		}
	}

	private void moveToIndex(int newTopIndex){
		entryChanged("");//Make sure any data is saved
		shiftingUI = true;
		topIndex = Math.min(newTopIndex, SequenceBoxTileEntity.MAX_VALUES - inputBars.length);
		for(int i = 0; i < inputBars.length; i++){
			int barIndex = topIndex + i;
			inputBars[i].setText(barIndex < container.inputs.size() ? container.inputs.get(barIndex) : "");
		}

		shiftingUI = false;
	}

	private void setSelection(int index){
		index = Math.min(index, SequenceBoxTileEntity.MAX_VALUES - 1);
		for(TextFieldWidget widget : inputBars){
			if((index < 0 || widget != inputBars[index]) && widget.isFocused()){
				widget.changeFocus(false);
			}
		}

		if(index != -1){
			if(!inputBars[index].isFocused()){
				inputBars[index].changeFocus(true);
			}
			setListener(inputBars[index]);
		}else{
			setListener(null);
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
			minecraft.player.closeScreen();
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
			container.outputIndex = Math.min(getSelection() + topIndex, container.inputs.size() - 1);
			updateTEWithPacket();
		}else{
			for(TextFieldWidget bar : inputBars){
				if(bar.keyPressed(keyCode, scanCode, modifiers) || bar.canWrite()){
					return true;
				}
			}
			return super.keyPressed(keyCode, scanCode, modifiers);
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
				if(inputIndex + 1 >= SequenceBoxTileEntity.MAX_VALUES){
					continue;//Should never happen
				}
				String barContents = inputBars[barIndex].getText();
				boolean hasContents = !barContents.trim().isEmpty();
				float barValue = RedstoneUtil.interpretFormulaString(barContents);
				if(hasContents){
					while(container.inputs.size() <= inputIndex){
						container.inputs.add("");//Pad the inputs list with empty strings to reach this index
					}
					container.inputs.set(inputIndex, barContents);
				}else if(inputIndex == container.inputs.size() - 1){
					container.inputs.remove(inputIndex);//We are the last entry in inputs, and the bar is now empty. Remove
				}else if(inputIndex < container.inputs.size() - 1){
					container.inputs.set(inputIndex, "");//We are an entry that is still needed as padding for a later padding. Change our own value, but don't change the overall length
				}
			}

			container.outputIndex = Math.min(container.outputIndex, container.inputs.size() - 1);
			updateTEWithPacket();
		}
	}

	private void updateTEWithPacket(){
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt("output_index", container.outputIndex);
		for(int i = 0; i < container.inputs.size(); i++){
			nbt.putFloat(i + "_val", RedstoneUtil.interpretFormulaString(container.inputs.get(i)));
			nbt.putString(i + "_str", container.inputs.get(i));
		}
		CRPackets.sendPacketToServer(new SendNBTToServer(nbt, container.pos));
	}
}
