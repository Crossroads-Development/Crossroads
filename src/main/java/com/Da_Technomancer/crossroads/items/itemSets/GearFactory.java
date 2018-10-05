package com.Da_Technomancer.crossroads.items.itemSets;

import com.Da_Technomancer.crossroads.API.rotary.GearTypes;
import com.Da_Technomancer.crossroads.blocks.rotary.ToggleGear;
import com.Da_Technomancer.crossroads.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;

public class GearFactory{

	public static final HashMap<GearTypes, BasicGear> BASIC_GEARS = new HashMap<GearTypes, BasicGear>();
	public static final HashMap<GearTypes, LargeGear> LARGE_GEARS = new HashMap<GearTypes, LargeGear>();
	public static final HashMap<GearTypes, ToggleGear> TOGGLE_GEARS = new HashMap<GearTypes, ToggleGear>();

	public static void init(){
		BASIC_GEARS.clear();
		LARGE_GEARS.clear();
		TOGGLE_GEARS.clear();
		for(GearTypes typ : GearTypes.values()){
			BASIC_GEARS.put(typ, new BasicGear(typ));
			LARGE_GEARS.put(typ, new LargeGear(typ));
			TOGGLE_GEARS.put(typ, new ToggleGear(typ));
		}

	}

	@SideOnly(Side.CLIENT)
	public static void clientInit(){
		ItemColors itemColor = Minecraft.getMinecraft().getItemColors();
		for(GearTypes typ : GearTypes.values()){
			int colorCode = typ.getColor().getRGB();
			IItemColor itemColoring = (ItemStack stack, int tintIndex) -> tintIndex == 0 ? colorCode : -1;
			itemColor.registerItemColorHandler(itemColoring, BASIC_GEARS.get(typ), LARGE_GEARS.get(typ));
			itemColor.registerItemColorHandler(itemColoring, TOGGLE_GEARS.get(typ));

			if(typ == GearTypes.IRON){
				itemColor.registerItemColorHandler(itemColoring, ModItems.axleIron, ModItems.clutchIron, ModItems.clutchInvertedIron);
			}else if(typ == GearTypes.COPSHOWIUM){
				itemColor.registerItemColorHandler(itemColoring, ModItems.axleCopshowium, ModItems.clutchCopshowium, ModItems.clutchInvertedCopshowium);
			}
		}
	}
}
