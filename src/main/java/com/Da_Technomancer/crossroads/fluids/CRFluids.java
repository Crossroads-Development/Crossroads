package com.Da_Technomancer.crossroads.fluids;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;

public final class CRFluids{

	public static GenericFluid.FluidData distilledWater;
	public static GenericFluid.FluidData dirtyWater;
	public static GenericFluid.FluidData steam;
	public static GenericFluid.FluidData liquidFat;
	//Molten metals
	public static GenericFluid.FluidData moltenIron;
	public static GenericFluid.FluidData moltenGold;
	public static GenericFluid.FluidData moltenCopper;
	public static GenericFluid.FluidData moltenTin;
	public static GenericFluid.FluidData moltenCopshowium;
	//Witchcraft fluids
	public static GenericFluid.FluidData nutrientSolution;
	public static GenericFluid.FluidData fertilizerSolution;
	public static GenericFluid.FluidData soulEssence;

	public static final TagKey<Fluid> STEAM = CraftingUtil.getTagKey(ForgeRegistries.Keys.FLUIDS, new ResourceLocation("forge:steam"));
	public static final TagKey<Fluid> DISTILLED_WATER = CraftingUtil.getTagKey(ForgeRegistries.Keys.FLUIDS, new ResourceLocation(Crossroads.MODID, "distilled_water"));
	public static final TagKey<Fluid> LIQUID_FAT = CraftingUtil.getTagKey(ForgeRegistries.Keys.FLUIDS, new ResourceLocation(Crossroads.MODID, "liquid_fat"));

	public static final HashMap<String, FluidType> toRegisterType = new HashMap<>();
	public static final HashMap<String, Fluid> toRegisterFluid = new HashMap<>();

	private static boolean hasInit = false;

	public static void init(){
		if(hasInit){
			return;
		}
		hasInit = true;

		distilledWater = GenericFluid.create("distilled_water", false, false, 0, true);
		dirtyWater = GenericFluid.create("dirty_water", false, false, 0, true);
		steam = GenericFluid.create("steam", false, true);
		liquidFat = GenericFluid.create("liquid_fat", false, false);
		moltenIron = GenericFluid.create("molten_iron", true, false);
		moltenGold = GenericFluid.create("molten_gold", true, false);
		moltenCopper = GenericFluid.create("molten_copper", true, false);
		moltenTin = GenericFluid.create("molten_tin", true, false);
		moltenCopshowium = GenericFluid.create("molten_copshowium", true, false);
		nutrientSolution = GenericFluid.create("nutrient_solution", false, false, 0, true);
		fertilizerSolution = GenericFluid.create("fertilizer_solution", false, false, 0, true);
		soulEssence = GenericFluid.create("soul_essence", false, true, 3, false);
	}
}
