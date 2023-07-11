package com.Da_Technomancer.crossroads.integration;

import com.Da_Technomancer.crossroads.integration.create.CreateHelper;
import com.Da_Technomancer.crossroads.integration.curios.CurioHelper;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.essentials.integration.ESIntegration;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

public class CRIntegration{

	public static void init(){
		//Patchouli
		//Crossroads & Essentials share the guide book; makes sure it also gets added to the CR creative tab
		ESIntegration.bookName = "book.crossroads_essentials.name";
		if(ModList.get().isLoaded(ESIntegration.PATCHOULI_ID)){
			CRItems.addToCreativeTab(() -> new ItemStack[] {ESIntegration.getBookStack()}, CRItems.MAIN_CREATIVE_TAB_ID);
		}

		CurioHelper.initIntegration();
		CreateHelper.initIntegration();
	}
}
