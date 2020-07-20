package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.API.CRReflection;
import com.Da_Technomancer.crossroads.render.IVisualEffect;
import com.Da_Technomancer.essentials.ReflectionUtil;
import net.minecraft.client.gui.NewChatGui;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Certain packets need to call or store code in render side only stuff, and can not do so without crashing due to (for example) WorldClient not existing on the server side.
 * In those cases, the packets should call methods in this class
 * This class is for CLIENT SIDE CODE ONLY
 *
 * Any changes to this class must be tested on a dedicated server to be accepted
 */	
public class SafeCallable{

	public static final ArrayList<IVisualEffect> effectsToRender = new ArrayList<>();

	private static Method printChatNoLog;

	@OnlyIn(Dist.CLIENT)
	public static Method getPrintChatNoLog(){
		if(printChatNoLog == null){
			printChatNoLog = ReflectionUtil.reflectMethod(CRReflection.SET_CHAT);
		}
		return printChatNoLog;
	}

	public static Class<?> getChatClass(){
		return NewChatGui.class;
	}

	public static int playerTickCount = 0;
}
