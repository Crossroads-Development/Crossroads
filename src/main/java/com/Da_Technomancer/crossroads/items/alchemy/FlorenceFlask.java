package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.items.ModItems;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import org.apache.commons.lang3.tuple.Pair;

public class FlorenceFlask extends AbstractGlassware{

	private final boolean crystal;

	public FlorenceFlask(boolean crystal){
		String name = "florence_flask_" + (crystal ? "cryst" : "glass");
		this.crystal = crystal;
		maxStackSize = 1;
		hasSubtypes = true;
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		ModItems.toRegister.add(this);
		ModItems.itemAddQue(this);
	}

	@Override
	public boolean isCrystal(){
		return crystal;
	}

	@Override
	public int getCapacity(){
		return 100;
	}
}
