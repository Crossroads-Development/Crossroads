package com.Da_Technomancer.crossroads.fluids;

import net.minecraft.fluid.Fluid;

import java.util.ArrayList;

public final class CRFluids{

	public static CrossroadsFluid distilledWater;
	public static CrossroadsFluid dirtyWater;
	public static CrossroadsFluid steam;
	public static CrossroadsFluid liquidFat;
	public static CrossroadsFluid moltenIron;
	public static CrossroadsFluid moltenGold;
	public static CrossroadsFluid moltenCopper;
	public static CrossroadsFluid moltenTin;
	public static CrossroadsFluid moltenCopshowium;

	public static ArrayList<Fluid> toRegister = new ArrayList<>();

	public static void init(){
		distilledWater = new CrossroadsFluid("distilled_water", false);
		dirtyWater = new CrossroadsFluid("dirty_water", false);
		steam = new CrossroadsFluid("steam", false);
		liquidFat = new CrossroadsFluid("liquid_fat", false);
		moltenIron = new CrossroadsFluid("molten_iron", true);
		moltenGold = new CrossroadsFluid("molten_gold", true);
		moltenCopper = new CrossroadsFluid("molten_copper", true);
		moltenTin = new CrossroadsFluid("molten_tin", true);
		moltenCopshowium = new CrossroadsFluid("molten_copshowium", true);
	}
}
