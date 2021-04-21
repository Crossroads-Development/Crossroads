package com.Da_Technomancer.crossroads.crafting.recipes;

import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.IReagent;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentManager;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;
import com.Da_Technomancer.crossroads.API.effects.alchemy.*;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.crafting.CRItemTags;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.function.Function;

public class ReagentRec implements IRecipe<IInventory>, IReagent{

	private final ResourceLocation location;
	private final String group;
	private final String id;
	private final double melting;
	private final double boiling;
	private final boolean flame;
	private final ITag.INamedTag<Item> solid;
	private final FluidStack fluid;
	private final ContainRequirements containment;
	private final int[] colMap;//Used for serialization
	private final Function<EnumMatterPhase, Color> colorFunc;
	private final String effectName;//Used for serialization
	@Nonnull
	private final IAlchEffect effect;
	private final String flameName;//Used for serialization
	private final Function<Integer, Integer> flameFunction;

	public ReagentRec(ResourceLocation location, String group, String id, double melting, double boiling, boolean flame, ITag.INamedTag<Item> solid, FluidStack fluid, ContainRequirements containment, int[] colMap, Function<EnumMatterPhase, Color> colorFunc, String effectName, @Nonnull IAlchEffect effect, String flameName, Function<Integer, Integer> flameFunction){
		this.location = location;
		this.group = group;
		this.id = id;
		this.melting = melting;
		this.boiling = boiling;
		this.flame = flame;
		this.solid = solid;
		this.fluid = fluid;
		this.containment = containment;
		this.colMap = colMap;
		this.colorFunc = colorFunc;
		this.effectName = effectName;
		this.effect = effect;
		this.flameName = flameName;
		this.flameFunction = flameFunction;
		ReagentManager.updateReagent(this);
	}

	@Override
	public boolean matches(IInventory inv, World worldIn){
		return true;
	}

	@Override
	public ItemStack assemble(IInventory inv){
		return getResultItem();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height){
		return true;
	}

	@Override
	public ItemStack getResultItem(){
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getToastSymbol(){
		return new ItemStack(CRItems.phialGlass);
	}

	@Override
	public ResourceLocation getId(){
		return location;//JSON file location
	}

	@Override
	public double getMeltingPoint(){
		return melting;
	}

	@Override
	public double getBoilingPoint(){
		return boiling;
	}

	@Override
	public String getID(){
		return id;//Reagent id
	}

	@Override
	public FluidStack getFluid(){
		return fluid;
	}

	@Override
	public int getFlameRadius(int amount){
		return flameFunction.apply(amount);
	}

	@Override
	public Color getColor(EnumMatterPhase phase){
		return colorFunc.apply(phase);
	}

	@Override
	@Nonnull
	public IAlchEffect getEffect(){
		return effect;
	}

	@Override
	public boolean requiresCrystal(){
		return containment.requireCrystal;
	}

	@Override
	public boolean destroysBadContainer(){
		return containment.destructive;
	}

	@Override
	public boolean isLockedFlame(){
		return flame;
	}

	public ITag<Item> getSolid(){
		return solid;
	}

	/**
	 * @param reag The reagent (assumes phase is SOLID)
	 * @return The matching solid ItemStack. ItemStack.EMPTY if there either isn't enough material (or cannot be solidifed for any other reason).
	 */
	@Override
	public ItemStack getStackFromReagent(ReagentStack reag){
		ItemStack out = !reag.isEmpty() && reag.getType() == this ? new ItemStack(CRItemTags.getTagEntry(solid), 1) : ItemStack.EMPTY;
		out.setCount(reag.getAmount());
		return out;
	}

	@Override
	public ITag<Item> getJEISolids(){
		return solid;
	}

	@Override
	public IRecipeSerializer<?> getSerializer(){
		return CRRecipes.REAGENT_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public IRecipeType<?> getType(){
		return CRRecipes.REAGENT_TYPE;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ReagentRec>{


		/*
		 * Specifications for a custom reagent
		 * Reagents can be added or overwritten, but the default reagents can not be removed- their properties can be completely changed, but something with their ID must exis
		 *
		 * Anything prefaced by // is a comment, and should not be in a real JSON
		 *
		 * {
		 * 		"type": "crossroads:reagents", //Tells Minecraft this is a reagent
		 *		"group": <group>, //Optional, same purpose as vanilla
		 * 		"id": <string reagent id>, //ID of this reagent. Reagents with the same ID will overwrite. All lowercase, no spaces
		 * 		"melting": <number OR "never">, //Melting temperature in degrees C. Optional, defaults to below absolute zero. String "never" will prevent melting
		 * 		"boiling": <number OR "never">, //Boiling temperature in degrees C. Optional, defaults to below absolute zero. Must be higher than melting. String "never" will prevent boiling
		 * 		"item": <tag id>, //The ID of a tag representing the item form of this reagent when solid, 1:1 ratio. Optional, defaults to an empty tag
		 * 		"fluid": { //The liquid and quantity for the liquid version of one unit of this reagent. This entire JSON object is optional
		 * 			"fluid": <fluid id>, //ID of the fluid
		 * 			"amount": <number> //Quantity of the fluid in mb for one unit of this reagent
		 * 		},
		 * 		"vessel": <glass/crystal/destructive>, //The requirements to contain this reagent. Optional, default glass. Destructive requires crystal and will also destroy the container if glass
		 * 		"effect": <string effect name>, //The effect of this reagent. See the full list of values below (effectMap code). Optional, defaults to none
		 * 		"flame": <none/small/large/fixed_small/fixed_large>, //Whether this is a flame reagent, and the size of the flame cloud if so. Optional, defaults to none. Any value other than none will force this to always be flame phase.
		 *
		 * 		//For constant color:
		 * 		"color": <color> //The color of this reagent, as a 6 (rgb) or 8 (argb for alpha) character hexadecimal color code, without a hash sign. Optional, defaults to white
		 *
		 * 		//OR, for phase dependent color:
		 * 		"color": {
		 * 			"base": <color> //The color of any phase not specifically set. Optional, defaults to white
		 * 			"solid": <color> //Color in the solid phase. Optional, defaults to base color
		 * 			"liquid": <color> //Color in the liquid phase. Optional, defaults to base color
		 * 			"gas": <color> //Color in the gas phase. Optional, defaults to base color
		 * 			"flame": <color> //Color in the flame phase. Optional, defaults to base color
		 * 		}
		 * }
		 */
		@Override
		public ReagentRec fromJson(ResourceLocation recipeId, JsonObject json){
			//Normal specification of recipe group and ingredient
			String group = JSONUtils.getAsString(json, "group", "");
			String id = JSONUtils.getAsString(json, "id").toLowerCase(Locale.US).replace(' ', '_');
			double melting = JSONUtils.getAsString(json, "melting", "-275").equals("never") ? Short.MAX_VALUE - 1 : JSONUtils.getAsFloat(json, "melting", -275);
			double boiling = JSONUtils.getAsString(json, "boiling", "-274").equals("never") ? Short.MAX_VALUE : JSONUtils.getAsFloat(json, "boiling", -274);
			if(melting > boiling){
				boiling = melting;//Equal melting and boiling point would cause sublimation, skipping liquid
			}
			ITag.INamedTag<Item> item = ItemTags.bind(JSONUtils.getAsString(json, "item", "crossroads:empty"));
			FluidStack fluid = CraftingUtil.getFluidStack(json, "fluid", FluidStack.EMPTY);
			ContainRequirements vessel = containTypeMap.getOrDefault(JSONUtils.getAsString(json, "vessel", "none"), ContainRequirements.NONE);
			String effectName = JSONUtils.getAsString(json, "effect", "none");
			IAlchEffect effect = effectMap.getOrDefault(effectName, null);
			String flameName = JSONUtils.getAsString(json, "flame", "none");
			Function<Integer, Integer> flameFunc = flameRadiusMap.getOrDefault(flameName, flameRadiusMap.get("none"));
			boolean flame = flameFunc != flameRadiusMap.get("none");
			Function<EnumMatterPhase, Color> colorFunction;
			JsonElement colorElem = json.get("color");
			Color[] colorMap = new Color[EnumMatterPhase.values().length];
			int[] colEncodeMap = new int[colorMap.length];
			if(colorElem.isJsonObject()){
				JsonObject colorObj = colorElem.getAsJsonObject();
				Color base = CraftingUtil.getColor(colorObj, "base", Color.WHITE);
				colorMap[EnumMatterPhase.FLAME.ordinal()] = CraftingUtil.getColor(colorObj, "flame", base);
				colorMap[EnumMatterPhase.GAS.ordinal()] = CraftingUtil.getColor(colorObj, "gas", base);
				colorMap[EnumMatterPhase.LIQUID.ordinal()] = CraftingUtil.getColor(colorObj, "liquid", base);
				colorMap[EnumMatterPhase.SOLID.ordinal()] = CraftingUtil.getColor(colorObj, "solid", base);
			}else{
				Color c = CraftingUtil.getColor(json, "color", Color.WHITE);
				colorMap[0] = colorMap[1] = colorMap[2] = colorMap[3] = c;
			}
			for(int i = 0; i < colEncodeMap.length; i++){
				colEncodeMap[i] = colorMap[i].getRGB();
			}

			colorFunction = elem -> colorMap[elem.ordinal()];

			return new ReagentRec(recipeId, group, id, melting, boiling, flame, item, fluid, vessel, colEncodeMap, colorFunction, effectName, effect, flameName, flameFunc);
		}

		@Nullable
		@Override
		public ReagentRec fromNetwork(ResourceLocation recipeId, PacketBuffer buffer){
			String group = buffer.readUtf(Short.MAX_VALUE);
			String id = buffer.readUtf();
			double melting = buffer.readDouble();
			double boiling = buffer.readDouble();
			boolean flame = buffer.readBoolean();
			ITag.INamedTag<Item> solid = ItemTags.bind(buffer.readUtf());
			Fluid fl = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(buffer.readUtf()));
			int flQty = buffer.readVarInt();
			FluidStack fluid = fl == null || flQty == 0 ? FluidStack.EMPTY : new FluidStack(fl, flQty);
			ContainRequirements vessel = ContainRequirements.values()[buffer.readVarInt()];
			int[] colMapInt = buffer.readVarIntArray();
			Color[] colMap = new Color[colMapInt.length];
			for(int i = 0; i < colMap.length; i++){
				colMap[i] = new Color(colMapInt[i], true);
			}
			Function<EnumMatterPhase, Color> colFunc = phase -> colMap[phase.ordinal()];
			String effectName = buffer.readUtf();
			IAlchEffect effect = effectMap.getOrDefault(effectName, effectMap.get("none"));
			String flameName = buffer.readUtf();
			Function<Integer, Integer> flameFunc = flameRadiusMap.getOrDefault(flameName, flameRadiusMap.get("none"));
			return new ReagentRec(recipeId, group, id, melting, boiling, flame, solid, fluid, vessel, colMapInt, colFunc, effectName, effect, flameName, flameFunc);
		}

		@Override
		public void toNetwork(PacketBuffer buffer, ReagentRec recipe){
			buffer.writeUtf(recipe.getGroup());
			buffer.writeUtf(recipe.id);
			buffer.writeDouble(recipe.melting);
			buffer.writeDouble(recipe.boiling);
			buffer.writeBoolean(recipe.flame);
			buffer.writeUtf(recipe.solid.getName().toString());
			buffer.writeUtf(recipe.fluid.getFluid().getRegistryName().toString());
			buffer.writeVarInt(recipe.fluid.getAmount());
			buffer.writeVarInt(recipe.containment.ordinal());
			buffer.writeVarIntArray(recipe.colMap);
			buffer.writeUtf(recipe.effectName);
			buffer.writeUtf(recipe.flameName);
		}
	}

	private static final HashMap<String, ContainRequirements> containTypeMap = new HashMap<>(3);
	private static final HashMap<String, Function<Integer, Integer>> flameRadiusMap = new HashMap<>(5);
	private static final HashMap<String, IAlchEffect> effectMap = new HashMap<>(15);

	static{
		containTypeMap.put("glass", ContainRequirements.NONE);
		containTypeMap.put("crystal", ContainRequirements.CRYSTAL_EVAP);
		containTypeMap.put("destructive", ContainRequirements.CRYSTAL_DESTROY);

		flameRadiusMap.put("none", qty -> 0);
		flameRadiusMap.put("small", qty -> Math.min(8, (int) Math.round(qty / 2D)));
		flameRadiusMap.put("large", qty -> CRConfig.allowHellfire.get() ? Math.min(64, qty * 4) : Math.min(8, (int) Math.round(qty / 2D)));
		flameRadiusMap.put("fixed_small", qty -> qty == 0 ? 0 : 8);//Constant 8 block range, regardless of quantity
		flameRadiusMap.put("fixed_large", qty -> qty == 0 ? 0 : CRConfig.allowHellfire.get() ? 64 : 8);//Constant 64 block range, regardless of quantity

		effectMap.put("none", new NoneEffect());
		effectMap.put("acid", new AcidAlchemyEffect());
		effectMap.put("acid_gold", new AquaRegiaAlchemyEffect());
		effectMap.put("disinfect", new DisinfectAlchemyEffect());
		effectMap.put("drop_phil_stone", new SpawnItemAlchemyEffect(CRItems.philosopherStone));
		effectMap.put("drop_prac_stone", new SpawnItemAlchemyEffect(CRItems.practitionerStone));
		effectMap.put("electric", new VoltusEffect());
		effectMap.put("poison", new ChlorineAlchemyEffect());
		effectMap.put("salt", new SaltAlchemyEffect());
		effectMap.put("salt_alc", new AlcSaltAlchemyEffect());
		effectMap.put("terraform_desert", new LumenEffect());
		effectMap.put("terraform_nether", new EldrineEffect());
		effectMap.put("terraform_ocean", new FusasEffect());
		effectMap.put("terraform_plains", new AetherEffect());
		effectMap.put("terraform_snow", new StasisolEffect());
		effectMap.put("terraform_mushroom", new MushroomTerraformEffect());
		effectMap.put("terraform_jungle", new JungleTerraformEffect());
		effectMap.put("terraform_end", new EndTerraformEffect());
	}

	private enum ContainRequirements{

		NONE(false, false),//Safe
		CRYSTAL_EVAP(true, false),//Euclid
		CRYSTAL_DESTROY(true, true);//Keter

		public final boolean requireCrystal;
		public final boolean destructive;

		ContainRequirements(boolean requireCrystal, boolean destructive){
			this.requireCrystal = requireCrystal;
			this.destructive = destructive;
		}
	}
}
