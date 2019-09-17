package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.API.templates.OutputLogGuiObject;
import com.Da_Technomancer.essentials.packets.ClientPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.lang.reflect.Field;

public class SendLogToClient extends ClientPacket{

	public String logName;
	public String text;
	public int col;
	public boolean clear;

	private static final Field[] FIELDS = fetchFields(SendLogToClient.class, "logName", "text", "col", "clear");

	@SuppressWarnings("unused")
	public SendLogToClient(){

	}

	public SendLogToClient(String logName, String text, @Nullable Color color, boolean clear){
		this.logName = logName;
		this.text = text;
		this.col = color == null ? -1 : color.getRGB();
		this.clear = clear;
	}

	@Nonnull
	@Override
	protected Field[] getFields(){
		return FIELDS;
	}

	@Override
	protected void run(){
		Screen gui = Minecraft.getInstance().currentScreen;
		if(gui instanceof OutputLogGuiObject.ILogUser){
			OutputLogGuiObject log = ((OutputLogGuiObject.ILogUser) gui).getLog(logName);
			if(log != null){
				if(clear){
					log.clearLog();
				}
				log.addText(text, col == -1 ? null : new Color(col));
			}
		}
	}
}
