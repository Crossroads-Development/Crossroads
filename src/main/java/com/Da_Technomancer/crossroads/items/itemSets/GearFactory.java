package com.Da_Technomancer.crossroads.items.itemSets;

import java.util.HashMap;

import com.Da_Technomancer.crossroads.API.enums.GearTypes;
import com.Da_Technomancer.crossroads.blocks.rotary.ToggleGear;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
			IItemColor itemColoring = new IItemColor(){
				@Override
				public int getColorFromItemstack(ItemStack stack, int tintIndex){
					return colorCode;
				}
			};
			itemColor.registerItemColorHandler(itemColoring, new Item[]{BASIC_GEARS.get(typ), LARGE_GEARS.get(typ)});
			itemColor.registerItemColorHandler(itemColoring, new Block[]{TOGGLE_GEARS.get(typ)});
		}
	}
}
