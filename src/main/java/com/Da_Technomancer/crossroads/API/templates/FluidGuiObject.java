package com.Da_Technomancer.crossroads.API.templates;

import com.Da_Technomancer.crossroads.Main;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public class FluidGuiObject implements IGuiObject{

	private static BiMap<Fluid, Integer> fluidKeys;

	public static int fluidToPacket(@Nullable  FluidStack stack){
		if(fluidKeys == null || fluidKeys.size() != FluidRegistry.getMaxID()){
			fluidKeys = HashBiMap.create(FluidRegistry.getRegisteredFluidIDs());//Ok, so getRegisteredFluidIDs may possibly be kinda deprecated, but let's be honest here: It's never going to be removed. Also this is more efficient/easier than string ids for syncing
		}

		if(stack == null || stack.amount == 0){
			return 0;
		}

		Integer fluidKey = fluidKeys.get(stack.getFluid());

		return fluidKey == null ? 0 : (stack.amount & 0xFFFFF) | ((fluidKey & 0xFFF) << 20);
	}

	@Nullable
	public static FluidStack packetToFluid(int packet){
		if(fluidKeys == null || fluidKeys.size() != FluidRegistry.getMaxID()){
			fluidKeys = HashBiMap.create(FluidRegistry.getRegisteredFluidIDs());//Ok, so getRegisteredFluidIDs may possibly be kinda deprecated, but let's be honest here: It's never going to be removed. Also this is more efficient/easier than string ids for syncing
		}

		if(packet == 0){
			return null;
		}

		Fluid f = fluidKeys.inverse().get(packet >>> 20);
		int qty = packet & 0xFFFFF;
		if(f == null || qty == 0){
			return null;
		}

		return new FluidStack(f, qty);
	}


	private static final int MAX_HEIGHT = 48;
	private static final ResourceLocation OVERLAY = new ResourceLocation(Main.MODID, "textures/gui/rectangle_fluid_overlay.png");
	private final MachineGUI gui;
	private final int fieldIndex;
	private final int capacity;
	private final int x;
	private final int y;
	private final int windowX;
	private final int windowY;


	public FluidGuiObject(MachineGUI gui, int fieldIndex, int capacity, int windowX, int windowY, int x, int y){
		this.gui = gui;
		this.fieldIndex = fieldIndex;
		this.capacity = capacity;
		this.x = x;
		this.y = y;
		this.windowX = windowX;
		this.windowY = windowY;
	}

	@Override
	public boolean buttonPress(char key, int keyCode){
		return false;
	}

	@Override
	public boolean mouseClicked(int x, int y, int button){
		return false;
	}

	@Override
	public boolean drawBack(float partialTicks, int mouseX, int mouseY, FontRenderer fontRenderer){
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		Gui.drawRect(x + windowX, y + windowY - MAX_HEIGHT, x + windowX + 16, y + windowY, 0xFF959595);
		GlStateManager.color(1, 1, 1, 1);

		FluidStack fluid = packetToFluid(gui.te.getField(fieldIndex));
		if(fluid == null){
			return true;
		}

		TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(fluid.getFluid().getStill().toString());
		if(sprite == null){
			sprite = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
		}
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		int height = (int) (MAX_HEIGHT * (float) fluid.amount / (float) capacity);
		gui.drawTexturedModalRect(x + windowX, y + windowY - height, sprite, 16, height);
		return true;
	}

	@Override
	public boolean drawFore(int mouseX, int mouseY, FontRenderer fontRenderer){
		Minecraft.getMinecraft().getTextureManager().bindTexture(OVERLAY);
		Gui.drawModalRectWithCustomSizedTexture(x, y - MAX_HEIGHT, 0, 0, 16, MAX_HEIGHT, 16, MAX_HEIGHT);

		if(mouseX >= x + windowX && mouseX <= x + windowX + 16 && mouseY >= y + windowY - MAX_HEIGHT && mouseY <= y + windowY){
			FluidStack fluid = packetToFluid(gui.te.getField(fieldIndex));
			if(fluid == null){
				gui.tooltip.add("Empty");
			}else{
				gui.tooltip.add(fluid.getLocalizedName());
				gui.tooltip.add(fluid.amount + "/" + capacity);
			}
		}
		return true;
	}
}
