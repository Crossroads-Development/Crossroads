package com.Da_Technomancer.crossroads.api;

import com.Da_Technomancer.essentials.api.ReflectionUtil;
import net.minecraft.client.gui.Gui;

public enum CRReflectionClient implements ReflectionUtil.IReflectionKey{

	GUI_DRAW_BACKDROP(Gui.class, "m_93039_", "drawBackdrop", "Multi-line messages");


	private final Class<?> clazz;
	public final String obf;//Obfuscated name
	public final String mcp;//Human readable mapped name
	private final String purpose;

	CRReflectionClient(Class<?> clazz, String obf, String mcp, String purpose){
		this.clazz = clazz;
		this.obf = obf;
		this.mcp = mcp;
		this.purpose = purpose;
	}

	@Override
	public Class<?> getSourceClass(){
		return clazz;
	}

	@Override
	public String getObfName(){
		return obf;
	}

	@Override
	public String getMcpName(){
		return mcp;
	}

	@Override
	public String getPurpose(){
		return purpose;
	}

}
