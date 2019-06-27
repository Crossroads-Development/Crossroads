package com.Da_Technomancer.crossroads.integration.patchouli;

import com.Da_Technomancer.crossroads.API.packets.StoreNBTToClient;
import org.apache.commons.lang3.StringUtils;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;

//the purpose of this is to have text that shows up based on if its multiplayer or not. This is best combined with config flags determining unique behavior about
//this is like a sideonly basically, if side = multiplayer, it will only show if it is in multiplayer
//yes right now it displays a blank page
//From testing there is NO WAY to cause an entire page to vanish
public class MultiPlayerProcessor implements IComponentProcessor {
	private String virtualside;
	private String text;

	@Override
	public void setup(IVariableProvider<String> variables) {
		virtualside = variables.get("side");
		text = variables.get("text");
	}

	@Override
	public String process(String key) {
		if (key.equals("text")) {
			String[] replacements = StringUtils.substringsBetween(text, "#", "#");
			if (replacements != null && replacements.length != 0) {
				for (String replacing : replacements) {
					text = StringUtils.replace(text, "#" + replacing + "#", DataBuilder.dataMap.get(replacing));
				}
			}
			return(text);
		}
		return null;
	}

	@Override
	public boolean allowRender(String group) {
		if (group.equals("filler")) {
			return !(StoreNBTToClient.clientPlayerTag.getBoolean("multiplayer") && virtualside.equals("multiplayer") || !StoreNBTToClient.clientPlayerTag.getBoolean("multiplayer") && virtualside.equals("singleplayer"));
		}

		if (StoreNBTToClient.clientPlayerTag.getBoolean("multiplayer") && virtualside.equals("multiplayer")) {
			return true;
		}
		if (!StoreNBTToClient.clientPlayerTag.getBoolean("multiplayer") && virtualside.equals("singleplayer")) {
			return true;
		}
		return false;
	}
}
