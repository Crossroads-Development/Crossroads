package com.Da_Technomancer.crossroads.integration.patchouli;

import org.apache.commons.lang3.StringUtils;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;

//this basically emulates the behavior that patchouli has with inline variables, except draws values from the datamap
public class KeyTextProcessor implements IComponentProcessor {
	private String text;

	@Override
	public  void setup(IVariableProvider<String> variables) {
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
}
