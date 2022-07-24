package com.Da_Technomancer.crossroads.api.packets;

import com.Da_Technomancer.crossroads.api.CRReflection;
import com.Da_Technomancer.essentials.api.ReflectionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.lang.reflect.Method;

/**
 * Certain packets need to call or store code in render side only stuff, and can not do so without crashing due to (for example) WorldClient not existing on the server side.
 * In those cases, the packets should call methods in this class
 * This class is for CLIENT SIDE CODE ONLY
 *
 * Any changes to this class must be tested on a dedicated server to be accepted
 */
public class SafeCallable{

	private static Method printChatNoLog;

	@OnlyIn(Dist.CLIENT)
	public static Method getPrintChatNoLog(){
		if(printChatNoLog == null){
			//NewChatGui::addMessage is a special case- there are several methods with the same name. To ensure we reflect the correct one, we filter by parameter count
			printChatNoLog = ReflectionUtil.reflectMethod(CRReflection.SET_CHAT, (Method m) -> m.getParameterCount() == 4);
		}

		return printChatNoLog;
	}

	public static Class<?> getChatClass(){
		return ChatComponent.class;
	}

	public static Level getClientWorld(){
		return Minecraft.getInstance().level;
	}

	public static Player getClientPlayer(){
		return Minecraft.getInstance().player;
	}
}
