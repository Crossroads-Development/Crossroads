package com.Da_Technomancer.crossroads.API.packets;

import java.awt.Color;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.gui.ILogUser;
import com.Da_Technomancer.crossroads.API.gui.OutputLogGuiObject;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("serial")
public class SendLogToClient extends Message<SendLogToClient>{

	public SendLogToClient(){
		
	}

	public String logName;
	public String text;
	public int col;
	public boolean clear;

	public SendLogToClient(String logName, String text, @Nullable Color color, boolean clear){
		this.logName = logName;
		this.text = text;
		this.col = color == null ? -1 : color.getRGB();
		this.clear = clear;
	}

	@Override
	public IMessage handleMessage(MessageContext context){
		if(context.side != Side.CLIENT){
			System.err.println("MessageToClient received on wrong side:" + context.side);
			return null;
		}

		Minecraft minecraft = Minecraft.getMinecraft();
		minecraft.addScheduledTask(new Runnable(){
			public void run(){
				processMessage(logName, text, col == -1 ? null : new Color(col), clear);
			}
		});

		return null;
	}

	public void processMessage(String logName, String text, Color col, boolean clear){
		GuiScreen gui = Minecraft.getMinecraft().currentScreen;
		if(gui instanceof ILogUser){
			OutputLogGuiObject log = ((ILogUser) gui).getLog(logName);
			if(log != null){
				if(clear){
					log.clearLog();
				}
				log.addText(text, col);
			}
		}
	}
}
