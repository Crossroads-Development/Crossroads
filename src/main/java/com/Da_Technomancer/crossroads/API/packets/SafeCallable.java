package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.API.alchemy.LooseArcRenderable;
import com.Da_Technomancer.crossroads.API.technomancy.LooseBeamRenderable;
import com.Da_Technomancer.crossroads.Main;
import net.minecraft.client.gui.GuiNewChat;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**Certain packets need to call or store code in render side only stuff, and can not do so without crashing due to (for example) WorldClient not existing on the server side.
 * In those cases, the packets should call methods in this class
 * This class is for CLIENT SIDE CODE ONLY
 */	
public class SafeCallable{

	public static final ArrayList<LooseBeamRenderable> beamsToRender = new ArrayList<LooseBeamRenderable>();
	public static final ArrayList<LooseArcRenderable> arcsToRender = new ArrayList<LooseArcRenderable>();
	
	protected static final Method printChatNoLog;

	static{
		Method holder = null;
		try{
			for(Method m : GuiNewChat.class.getDeclaredMethods()){
				if("func_146237_a".equals(m.getName()) || "setChatLine".equals(m.getName())){
					holder = m;
					holder.setAccessible(true);
					break;
				}
			}
			//For no apparent reason ReflectionHelper consistently crashes in an obfus. environment for me with this method, so the above for loop is used instead.
			//holder = ReflectionHelper.findMethod(GuiNewChat.class, "setChatLine", "func_146237_a", ITextComponent.class, int.class, int.class, boolean.class);
		}catch(Exception e){
			Main.logger.catching(e);
		}
		printChatNoLog = holder;
	}
	
	public static int playerTickCount = 1;
}
