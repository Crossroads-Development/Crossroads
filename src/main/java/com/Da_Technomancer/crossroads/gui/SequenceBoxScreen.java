package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.gui.container.SequenceBoxContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.function.Predicate;

public class SequenceBoxScreen extends ContainerScreen<SequenceBoxContainer>{

	//Should probably explain what's going on here:
	//The 'proper' way to have a multiline edit-able text screen is to basically make my own, which would involve writing handling for copy-pasting, multi-line selection, weird keyboard buttons, and so on
	//Instead of doing all that, we use a bunch of TextFieldWidgets laid alongside each other, with each Widget being one line of text
	//Scrolling up or down means moving all the text between the widgets, and doing all sorts of careful handling to prevent infinite loops
	//Somehow this was simpler and less error-prone than doing it 'properly' (none of this stuff has MCP mappings, OK?)

	private final TextFieldWidget[] inputBars = new TextFieldWidget[8];

	public SequenceBoxScreen(SequenceBoxContainer container, PlayerInventory inv, ITextComponent name){
		super(container, inv, name);
		xSize = 176;
		ySize = 90;
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
			inputBars[i] = new TextFieldWidget(font, guiLeft + 18, guiTop + 20 + 18 * i, 144 - 4, 18, new StringTextComponent(""));
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
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y){
		//TODO
	}

	private void entryChanged(String ignored){
		//We ignore the passed string, as it doesn't tell us which line changed or the context, only the new text on the changed line
		//TODO
	}

	//TODO
}
