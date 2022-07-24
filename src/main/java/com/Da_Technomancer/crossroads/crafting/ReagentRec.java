package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.alchemy.*;
import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.crafting.FluidIngredient;
import com.Da_Technomancer.crossroads.effects.alchemy_effects.*;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.function.Function;

public class ReagentRec implements Recipe<Container>, IReagent{

	private final ResourceLocation location;
	private final String group;
	private final String id;
	private final double melting;
	private final double boiling;
	private final boolean flame;
	private final TagKey<Item> solid;
	private final FluidIngredient fluid;
	private final int fluidQty;
	private final ContainRequirements containment;
	private final int[] colMap;//Used for serialization
	private final Function<EnumMatterPhase, Color> colorFunc;
	private final String effectName;//Used for serialization
	@Nonnull
	private final IAlchEffect effect;
	private final String flameName;//Used for serialization
	private final Function<Integer, Integer> flameFunction;

	public ReagentRec(ResourceLocation location, String group, String id, double melting, double boiling, boolean flame, TagKey<Item> solid, FluidIngredient fluid, int fluidQty, ContainRequirements containment, int[] colMap, Function<EnumMatterPhase, Color> colorFunc, String effectName, @Nonnull IAlchEffect effect, String flameName, Function<Integer, Integer> flameFunction){
		this.location = location;
		this.group = group;
		this.id = id;
		this.melting = melting;
		this.boiling = boiling;
		this.flame = flame;
		this.solid = solid;
		this.fluid = fluid;
		this.fluidQty = fluidQty;
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
	public boolean matches(Container inv, Level worldIn){
		return true;
	}

	@Override
	public ItemStack assemble(Container inv){
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
	public FluidIngredient getFluid(){
		return fluid;
	}

	@Override
	public int getFluidQty(){
		return fluidQty;
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

	public TagKey<Item> getSolid(){
		return solid;
	}

	/**
	 * @param reag The reagent (assumes phase is SOLID)
	 * @return The matching solid ItemStack. ItemStack.EMPTY if there either isn't enough material (or cannot be solidifed for any other reason).
	 */
	@Override
	public ItemStack getStackFromReagent(ReagentStack reag){
		ItemStack out = !reag.isEmpty() && reag.getType() == this ? new ItemStack(CraftingUtil.getTagEntry(solid), 1) : ItemStack.EMPTY;
		out.setCount(reag.getAmount());
		return out;
	}

	@Override
	public TagKey<Item> getJEISolids(){
		return solid;
	}

	@Override
	public RecipeSerializer<?> getSerializer(){
		return CRRecipes.REAGENT_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public RecipeType<?> getType(){
		return CRRecipes.REAGENT_TYPE;
	}

	public static class Serializer implements RecipeSerializer<ReagentRec>{


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
			String group = GsonHelper.getAsString(json, "group", "");
			String id = GsonHelper.getAsString(json, "id").toLowerCase(Locale.US).replace(' ', '_');
			double melting = GsonHelper.getAsString(json, "melting", "-275").equals("never") ? Short.MAX_VALUE - 1 : GsonHelper.getAsFloat(json, "melting", -275);
			double boiling = GsonHelper.getAsString(json, "boiling", "-274").equals("never") ? Short.MAX_VALUE : GsonHelper.getAsFloat(json, "boiling", -274);
			if(melting > boiling){
				boiling = melting;//Equal melting and boiling point would cause sublimation, skipping liquid
			}
			TagKey<Item> item = CraftingUtil.getTagKey(ForgeRegistries.Keys.ITEMS, new ResourceLocation(GsonHelper.getAsString(json, "item", "crossroads:empty")));
			//Fluid definition is optional, but must have a quantity and be specified in a subelement if present
			Pair<FluidIngredient, Integer> fluid = json.has("fluid") ? CraftingUtil.getFluidIngredientAndQuantity(json, "fluid", false, -1) : null;
			ContainRequirements vessel = containTypeMap.getOrDefault(GsonHelper.getAsString(json, "vessel", "none"), ContainRequirements.NONE);
			String effectName = GsonHelper.getAsString(json, "effect", "none");
			IAlchEffect effect = effectMap.getOrDefault(effectName, null);
			String flameName = GsonHelper.getAsString(json, "flame", "none");
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

			return new ReagentRec(recipeId, group, id, melting, boiling, flame, item, fluid == null ? FluidIngredient.EMPTY : fluid.getLeft(), fluid == null ? 0 : fluid.getRight(), vessel, colEncodeMap, colorFunction, effectName, effect, flameName, flameFunc);
		}

		@Nullable
		@Override
		public ReagentRec fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer){
			String group = buffer.readUtf(Short.MAX_VALUE);
			String id = buffer.readUtf();
			double melting = buffer.readDouble();
			double boiling = buffer.readDouble();
			boolean flame = buffer.readBoolean();
			TagKey<Item> solid = CraftingUtil.getTagKey(ForgeRegistries.Keys.ITEMS, new ResourceLocation(buffer.readUtf()));
			FluidIngredient fl = FluidIngredient.readFromBuffer(buffer);
			int flQty = buffer.readVarInt();
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
			return new ReagentRec(recipeId, group, id, melting, boiling, flame, solid, fl, flQty, vessel, colMapInt, colFunc, effectName, effect, flameName, flameFunc);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, ReagentRec recipe){
			buffer.writeUtf(recipe.getGroup());
			buffer.writeUtf(recipe.id);
			buffer.writeDouble(recipe.melting);
			buffer.writeDouble(recipe.boiling);
			buffer.writeBoolean(recipe.flame);
			buffer.writeUtf(recipe.solid.location().toString());
			recipe.fluid.writeToBuffer(buffer);
			buffer.writeVarInt(recipe.fluidQty);
			buffer.writeVarInt(recipe.containment.ordinal());
			buffer.writeVarIntArray(recipe.colMap);
			buffer.writeUtf(recipe.effectName);
			buffer.writeUtf(recipe.flameName);
		}
	}
	
	private static final HashMap<String, ContainRequirements> containTypeMap = new HashMap<>(3);//No register method for this, as it maps to an enum
	private static final HashMap<String, Function<Integer, Integer>> flameRadiusMap = new HashMap<>(5);
	private static final HashMap<String, IAlchEffect> effectMap = new HashMap<>(19);

	/**
	 * Adds a new alchemy effect, which reagents can use by setting their effect to the passed id
	 * Should be called BEFORE recipes are loaded from data
	 * @param id A unique string ID representing the effect. This method will overwrite the existing effect for this ID
	 * @param effect The alchemy effect to perform
	 */
	public static void registerEffect(String id, IAlchEffect effect){
		effectMap.put(id, effect);
	}

	/**
	 * Adds a new function for determining the radius of a flame reagent
	 * Function converts between quantity of the flame reagent and the final radius
	 * Should be called BEFORE recipes are loaded from data
	 * @param id A unique string ID representing the function. This method will overwrite the existing function for this ID
	 * @param flameRadiusFormula The function to apply
	 */
	public static void registerFlameFormula(String id, Function<Integer, Integer> flameRadiusFormula){
		flameRadiusMap.put(id, flameRadiusFormula);
	}

	static{
		containTypeMap.put("glass", ContainRequirements.NONE);
		containTypeMap.put("crystal", ContainRequirements.CRYSTAL_EVAP);
		containTypeMap.put("destructive", ContainRequirements.CRYSTAL_DESTROY);

		registerFlameFormula("none", qty -> 0);
		registerFlameFormula("small", qty -> Math.min(8, (int) Math.round(qty / 2D)));
		registerFlameFormula("large", qty -> CRConfig.allowHellfire.get() ? Math.min(64, qty * 4) : Math.min(8, (int) Math.round(qty / 2D)));
		registerFlameFormula("fixed_small", qty -> qty == 0 ? 0 : 8);//Constant 8 block range, regardless of quantity
		registerFlameFormula("fixed_large", qty -> qty == 0 ? 0 : CRConfig.allowHellfire.get() ? 64 : 8);//Constant 64 block range, regardless of quantity

		registerEffect("none", new NoneEffect());
		registerEffect("acid", new AcidAlchemyEffect());
		registerEffect("acid_gold", new AquaRegiaAlchemyEffect());
		registerEffect("disinfect", new DisinfectAlchemyEffect());
		registerEffect("drop_phil_stone", new SpawnItemAlchemyEffect(CRItems.philosopherStone));
		registerEffect("drop_prac_stone", new SpawnItemAlchemyEffect(CRItems.practitionerStone));
		registerEffect("electric", new VoltusEffect());
		registerEffect("poison", new ChlorineAlchemyEffect());
		registerEffect("salt", new SaltAlchemyEffect());
		registerEffect("salt_alc", new AlcSaltAlchemyEffect());
		registerEffect("terraform_desert", new LumenEffect());
		registerEffect("terraform_nether", new EldrineEffect());
		registerEffect("terraform_ocean", new FusasEffect());
		registerEffect("terraform_plains", new AetherEffect());
		registerEffect("terraform_snow", new StasisolEffect());
		registerEffect("terraform_mushroom", new MushroomTerraformEffect());
		registerEffect("terraform_jungle", new JungleTerraformEffect());
		registerEffect("terraform_end", new EndTerraformEffect());
		registerEffect("terraform_flower_forest", new FlowerForestTerraformEffect());
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
