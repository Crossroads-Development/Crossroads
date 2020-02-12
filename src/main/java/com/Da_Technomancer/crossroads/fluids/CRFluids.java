package com.Da_Technomancer.crossroads.fluids;

import net.minecraft.fluid.Fluid;

import java.util.ArrayList;

public final class CRFluids{

	public static GenericFluid distilledWater;
	public static GenericFluid dirtyWater;
	public static GenericFluid steam;
	public static GenericFluid liquidFat;
	public static GenericFluid moltenIron;
	public static GenericFluid moltenGold;
	public static GenericFluid moltenCopper;
	public static GenericFluid moltenTin;
	public static GenericFluid moltenCopshowium;

	public static ArrayList<Fluid> toRegister = new ArrayList<>();

	public static void init(){
		distilledWater = new GenericFluid("distilled_water", false);
		dirtyWater = new GenericFluid("dirty_water", false);
		steam = new GenericFluid("steam", false);
		liquidFat = new GenericFluid("liquid_fat", false);
		moltenIron = new GenericFluid("molten_iron", true);
		moltenGold = new GenericFluid("molten_gold", true);
		moltenCopper = new GenericFluid("molten_copper", true);
		moltenTin = new GenericFluid("molten_tin", true);
		moltenCopshowium = new GenericFluid("molten_copshowium", true);
	}
}
