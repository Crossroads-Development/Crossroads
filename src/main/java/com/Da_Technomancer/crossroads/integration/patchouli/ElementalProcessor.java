package com.Da_Technomancer.crossroads.integration.patchouli;

import com.Da_Technomancer.crossroads.API.packets.StoreNBTToClient;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.StringUtils;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;

//This was really fun.. not
public class ElementalProcessor implements IComponentProcessor {
	String element;
	private String text;

	@Override
	public void setup(IVariableProvider<String> variables) {
		element = variables.get("element");
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
		//(Supplier<Object>) () -> {NBTTagCompound elementTag = StoreNBTToClient.clientPlayerTag.getCompoundTag("elements"); Object[] out = new Object[elementTag.getKeySet().size() * 2]; int arrayIndex = 0; for(int i = EnumMagicElements.values().length - 1; i >= 0; i--){EnumMagicElements elem = EnumMagicElements.values()[i]; if(elementTag.hasKey(elem.name())){out[arrayIndex++] = "info.elements." + elem.name().toLowerCase() + (elem == EnumMagicElements.TIME ? !ModConfig.getConfigBool(ModConfig.allowTimeBeam, true) ? ".dis" : "" : ""); out[arrayIndex++] = true;}} return out;
	}

	@Override
	public boolean allowRender(String group) {
		NBTTagCompound elementTag = StoreNBTToClient.clientPlayerTag.getCompoundTag("elements");
		if (group.equals("text")) {
			return elementTag.getBoolean(element.toUpperCase());
		}
		if (group.equals("undiscovered")) {
			return !elementTag.getBoolean(element.toUpperCase());
		}
		return true;
	}

}
