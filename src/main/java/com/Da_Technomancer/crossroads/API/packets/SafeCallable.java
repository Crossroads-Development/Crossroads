package com.Da_Technomancer.crossroads.API.packets;

import java.util.ArrayList;

import com.Da_Technomancer.crossroads.API.technomancy.LooseBeamRenderable;

/**Certain packets need to call or store code in client side only stuff, and can not do so without crashing due to (for example) WorldClient not existing on the server side.
 * In those cases, the packets should call methods in this class
 * This class is for CLIENT SIDE CODE ONLY
 */	
public class SafeCallable{
	
	public static final ArrayList<LooseBeamRenderable> beamsToRender = new ArrayList<LooseBeamRenderable>();
}
