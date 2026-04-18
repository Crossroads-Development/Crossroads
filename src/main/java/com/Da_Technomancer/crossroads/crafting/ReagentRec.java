package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.alchemy.*;
import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.crafting.FluidIngredient;
import com.Da_Technomancer.crossroads.api.packets.StreamCodecUtils;
import com.Da_Technomancer.crossroads.effects.alchemy_effects.*;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class ReagentRec implements Recipe<RecipeInput>, IReagent{

	private final String group;
	private final String id;
	private final double melting;
	private final double boiling;
	private final boolean flame;
	private final TagKey<Item> solid;
	private final FluidIngredient fluid;
	private final int fluidQty;
	private final ContainRequirements containment;
	private final PhaseColorMap colMap;
	private final String effectName;//Used for serialization
	@Nonnull
	private final IAlchEffect effect;
	private final String flameName;//Used for serialization
	private final Function<Integer, Integer> flameFunction;

	private ReagentRec(String group, String id, double melting, double boiling, TagKey<Item> solid, FluidIngredient fluid, int fluidQty, ContainRequirements containment, PhaseColorMap colMap, String effectName, String flameName){
		this.group = group;
		this.id = id;
		this.melting = melting;
		this.boiling = boiling;
		this.solid = solid;
		this.fluid = fluid;
		this.fluidQty = fluidQty;
		this.containment = containment;
		this.colMap = colMap;
		this.effectName = effectName;
		this.flameName = flameName;
		this.effect = effectMap.getOrDefault(effectName, effectMap.get("none"));
		this.flameFunction = flameRadiusMap.getOrDefault(flameName, flameRadiusMap.get("none"));
		this.flame = flameFunction != flameRadiusMap.get("none");
		ReagentManager.updateReagent(this);
	}

	@Override
	public boolean equals(Object o){
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		ReagentRec that = (ReagentRec) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(id);
	}

	@Override
	public boolean matches(RecipeInput input, Level worldIn){
		return true;
	}

	@Override
	public ItemStack assemble(RecipeInput input, HolderLookup.Provider provider){
		return getResultItem(provider);
	}

	@Override
	public boolean canCraftInDimensions(int width, int height){
		return true;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider provider){
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getToastSymbol(){
		return new ItemStack(CRItems.phialGlass);
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
		return colMap.apply(phase);
	}

	@Override
	@Nonnull
	public IAlchEffect getEffect(){
		//FlameEffect does nothing, but is used for localization
		return "none".equals(effectName) && flame ? FlameEffect.INSTANCE : effect;
	}

	@Override
	public boolean requiresCrystal(){
		return containment.requireCrystal;
	}

	@Override
	public boolean destroysBadContainer(){
		return containment.destructive;
	}

	private ContainRequirements getContainment(){
		return containment;
	}

	private PhaseColorMap getColMap(){
		return colMap;
	}

	private String getEffectName(){
		return effectName;
	}

	private String getFlameName(){
		return flameName;
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
	 * @return The matching solid ItemStack. ItemStack.EMPTY if there either isn't enough material (or cannot be solidified for any other reason).
	 */
	@Override
	public ItemStack getStackFromReagent(ReagentStack reag){
		if(!reag.isEmpty() && this.equals(reag.getType())){
			Item item = CraftingUtil.getTagEntry(solid);
			if(item == null){
				return ItemStack.EMPTY;
			}
			return new ItemStack(item, reag.amount());
		}
		return ItemStack.EMPTY;
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
		 * Reagents can be added or overwritten, but the default reagents can not be removed- their properties can be completely changed, but something with their ID must exist
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

		static{
			MapCodec<ReagentRec> codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
					CraftingUtil.recipeGroupFieldCodec().forGetter(ReagentRec::getGroup),
					Codec.STRING.fieldOf("id").forGetter(ReagentRec::getID),
					Codec.withAlternative(Codec.DOUBLE, Codec.STRING.flatXmap(str -> "never".equals(str) ? DataResult.success(Short.MAX_VALUE - 1D) : DataResult.error(() -> "Must be a number or \"never\"", 0D), val -> DataResult.success(val.toString()))).optionalFieldOf("melting", -275D).forGetter(ReagentRec::getMeltingPoint),
					Codec.withAlternative(Codec.DOUBLE, Codec.STRING.flatXmap(str -> "never".equals(str) ? DataResult.success((double) Short.MAX_VALUE) : DataResult.error(() -> "Must be a number or \"never\""), val -> DataResult.success(val.toString()))).optionalFieldOf("boiling", -274D).forGetter(ReagentRec::getBoilingPoint),
					TagKey.codec(Registries.ITEM).optionalFieldOf("item", CRItemTags.EMPTY).forGetter(ReagentRec::getSolid),
					CraftingUtil.fluidIngredientMapCodec("fluid", false).orElse(FluidIngredient.EMPTY).forGetter(ReagentRec::getFluid),
					ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("fluid_amount", 0).forGetter(ReagentRec::getFluidQty),
					StringRepresentable.fromEnum(ContainRequirements::values).optionalFieldOf("vessel", ContainRequirements.NONE).forGetter(ReagentRec::getContainment),
					PhaseColorMap.CODEC.optionalFieldOf("color", PhaseColorMap.DEFAULT).forGetter(ReagentRec::getColMap),
					Codec.STRING.optionalFieldOf("effect", "none").forGetter(ReagentRec::getEffectName),
					Codec.STRING.optionalFieldOf("flame", "none").forGetter(ReagentRec::getFlameName)
			).apply(instance, ReagentRec::new));
			CODEC = codec.validate((ReagentRec rec) -> !rec.getFluid().isStrictlyEmpty() && rec.getFluidQty() == 0 ? DataResult.error(() -> "Must specify fluid quantity") : DataResult.success(rec));
		}

		public static final MapCodec<ReagentRec> CODEC;
		public static final StreamCodec<RegistryFriendlyByteBuf, ReagentRec> STREAM_CODEC = StreamCodecUtils.composite(
				ByteBufCodecs.STRING_UTF8, ReagentRec::getGroup,
				ByteBufCodecs.STRING_UTF8, ReagentRec::getID,
				ByteBufCodecs.DOUBLE, ReagentRec::getMeltingPoint,
				ByteBufCodecs.DOUBLE, ReagentRec::getBoilingPoint,
				ByteBufCodecs.fromCodecWithRegistries(TagKey.codec(Registries.ITEM)), ReagentRec::getSolid,
				FluidIngredient.STREAM_CODEC, ReagentRec::getFluid,
				ByteBufCodecs.VAR_INT, ReagentRec::getFluidQty,
				ByteBufCodecs.fromCodecWithRegistries(StringRepresentable.fromEnum(ContainRequirements::values)), ReagentRec::getContainment,
				PhaseColorMap.STREAM_CODEC, ReagentRec::getColMap,
				ByteBufCodecs.STRING_UTF8, ReagentRec::getEffectName,
				ByteBufCodecs.STRING_UTF8, ReagentRec::getFlameName,
				ReagentRec::new
		);

		@Override
		public MapCodec<ReagentRec> codec(){
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, ReagentRec> streamCodec(){
			return STREAM_CODEC;
		}
	}

	private record PhaseColorMap(Color flame, Color gas, Color liq, Color solid, int[] serialized) implements Function<EnumMatterPhase, Color>{

		private static final PhaseColorMap DEFAULT = new PhaseColorMap(Color.WHITE);

		private static final Codec<PhaseColorMap> CODEC = Codec.withAlternative(
				RecordCodecBuilder.create(instance -> instance.group(
						CraftingUtil.COLOR_CODEC.fieldOf("base").forGetter(PhaseColorMap::solid),
						CraftingUtil.COLOR_CODEC.optionalFieldOf("flame").forGetter(PhaseColorMap::flameOpt),
						CraftingUtil.COLOR_CODEC.optionalFieldOf("gas").forGetter(PhaseColorMap::gasOpt),
						CraftingUtil.COLOR_CODEC.optionalFieldOf("liquid").forGetter(PhaseColorMap::liqOpt),
						CraftingUtil.COLOR_CODEC.optionalFieldOf("solid").forGetter(PhaseColorMap::solidOpt)
				).apply(instance, PhaseColorMap::new)),
				CraftingUtil.COLOR_CODEC.flatComapMap(PhaseColorMap::new, colMap -> DataResult.error(() -> "Can't encode PhaseColorMap to a single color")));;
		private static final StreamCodec<ByteBuf, PhaseColorMap> STREAM_CODEC = StreamCodec.of(
				(buf, val) -> {
					for(int i = 0; i < 4; i++){
						buf.writeInt(val.serialized[i]);
					}
				},
				buf -> new PhaseColorMap(new int[] {buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt()}));

		private PhaseColorMap(int[] serialized){
			this(new Color(serialized[0], true), new Color(serialized[1], true), new Color(serialized[2], true), new Color(serialized[3], true), serialized);
		}

		private PhaseColorMap(@Nonnull Color base){
			this(base, base, base, base);
		}

		private PhaseColorMap(@Nonnull Color flame, @Nonnull Color gas, @Nonnull Color liq, @Nonnull Color sol){
			this(flame, gas, liq, sol, new int[] {flame.getRGB(), gas.getRGB(), liq.getRGB(), sol.getRGB()});
		}

		private PhaseColorMap(@Nonnull Color base, @Nonnull Optional<Color> flameOpt, @Nonnull Optional<Color> gasOpt, @Nonnull Optional<Color> liqOpt, @Nonnull Optional<Color> solidOpt){
			this(flameOpt.orElse(base), gasOpt.orElse(base), liqOpt.orElse(base), solidOpt.orElse(base));
		}

		private Optional<Color> flameOpt(){
			return Optional.of(flame);
		}

		private Optional<Color> gasOpt(){
			return Optional.of(gas);
		}

		private Optional<Color> liqOpt(){
			return Optional.of(liq);
		}

		private Optional<Color> solidOpt(){
			return Optional.of(solid);
		}

		@Override
		public Color apply(EnumMatterPhase phase){
			return switch(phase){
				case FLAME -> flame;
				case GAS -> gas;
				case LIQUID -> liq;
				case SOLID -> solid;
			};
		}
	}

	private static final HashMap<String, Function<Integer, Integer>> flameRadiusMap = new HashMap<>(5);
	private static final HashMap<String, IAlchEffect> effectMap = new HashMap<>(20);

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
		registerEffect("hydrate", new HydrateEffect());
	}

	private enum ContainRequirements implements StringRepresentable{

		NONE(false, false, "glass"),//Safe
		CRYSTAL_EVAP(true, false, "crystal"),//Euclid
		CRYSTAL_DESTROY(true, true, "destructive");//Keter

		public final boolean requireCrystal;
		public final boolean destructive;
		public final String name;

		ContainRequirements(boolean requireCrystal, boolean destructive, String name){
			this.requireCrystal = requireCrystal;
			this.destructive = destructive;
			this.name = name;
		}

		@Override
		public String getSerializedName(){
			return name;
		}
	}
}
