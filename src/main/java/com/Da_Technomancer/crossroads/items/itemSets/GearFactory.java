package com.Da_Technomancer.crossroads.items.itemSets;

import com.Da_Technomancer.crossroads.API.rotary.EnumGearType;
import com.Da_Technomancer.crossroads.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GearFactory{

	public static final BasicGear[] BASIC_GEARS = new BasicGear[EnumGearType.values().length];
	public static final LargeGear[] LARGE_GEARS = new LargeGear[EnumGearType.values().length];
	public static final ToggleGear[] TOGGLE_GEARS = new ToggleGear[EnumGearType.values().length];
	public static final ToggleGear[] INV_TOGGLE_GEARS = new ToggleGear[EnumGearType.values().length];

	public static void init(){
		for(EnumGearType typ : EnumGearType.values()){
			int index = typ.ordinal();
			BASIC_GEARS[index] = new BasicGear(typ);
			LARGE_GEARS[index] = new LargeGear(typ);
			TOGGLE_GEARS[index] = new ToggleGear(typ, false);
			INV_TOGGLE_GEARS[index] = new ToggleGear(typ, true);
		}
	}

	@SideOnly(Side.CLIENT)
	public static void clientInit(){
		ItemColors itemColor = Minecraft.getMinecraft().getItemColors();
		for(EnumGearType typ : EnumGearType.values()){
			int index = typ.ordinal();
			IItemColor itemColoring = (ItemStack stack, int tintIndex) -> tintIndex == 0 ? typ.getColor().getRGB() : -1;
			itemColor.registerItemColorHandler(itemColoring, BASIC_GEARS[index], LARGE_GEARS[index], TOGGLE_GEARS[index], INV_TOGGLE_GEARS[index]);

			if(typ == EnumGearType.IRON){
				itemColor.registerItemColorHandler(itemColoring, ModItems.axleIron, ModItems.clutchIron, ModItems.clutchInvertedIron);
			}else if(typ == EnumGearType.COPSHOWIUM){
				itemColor.registerItemColorHandler(itemColoring, ModItems.axleCopshowium, ModItems.clutchCopshowium, ModItems.clutchInvertedCopshowium);
			}
		}
	}
}
