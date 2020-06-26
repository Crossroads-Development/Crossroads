package com.Da_Technomancer.crossroads.fluids;

import net.minecraft.fluid.Fluid;

import java.util.ArrayList;

public final class CRFluids{

	public static GenericFluid.FluidData distilledWater;
	public static GenericFluid.FluidData dirtyWater;
	public static GenericFluid.FluidData steam;
	public static GenericFluid.FluidData liquidFat;
	public static GenericFluid.FluidData moltenIron;
	public static GenericFluid.FluidData moltenGold;
	public static GenericFluid.FluidData moltenCopper;
	public static GenericFluid.FluidData moltenTin;
	public static GenericFluid.FluidData moltenCopshowium;

	public static ArrayList<Fluid> toRegister = new ArrayList<>();

	public static void init(){
		distilledWater = GenericFluid.create("distilled_water", false, false);
		dirtyWater = GenericFluid.create("dirty_water", false, false);
		steam = GenericFluid.create("steam", false, true);
		liquidFat = GenericFluid.create("liquid_fat", false, false);
		moltenIron = GenericFluid.create("molten_iron", true, false);
		moltenGold = GenericFluid.create("molten_gold", true, false);
		moltenCopper = GenericFluid.create("molten_copper", true, false);
		moltenTin = GenericFluid.create("molten_tin", true, false);
		moltenCopshowium = GenericFluid.create("molten_copshowium", true, false);
	}
}
