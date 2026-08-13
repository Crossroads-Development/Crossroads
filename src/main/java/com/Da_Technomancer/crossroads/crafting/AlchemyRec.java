package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.api.alchemy.EnumReagents;
import com.Da_Technomancer.crossroads.api.alchemy.IReactionChamber;
import com.Da_Technomancer.crossroads.api.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.api.alchemy.ReagentStack;
import com.Da_Technomancer.crossroads.api.beams.BeamUnit;
import com.Da_Technomancer.crossroads.api.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.crafting.IOptionalRecipe;
import com.Da_Technomancer.crossroads.api.heat.HeatUtil;
import com.Da_Technomancer.crossroads.api.packets.StreamCodecUtils;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AlchemyRec implements IOptionalRecipe<RecipeInput>{

	private static final float MAX_BLAST = 8;

	private final String group;

	private final Type type;
	private final double heatChange;
	private final double minTemp;
	private final double maxTemp;
	@Nullable
	private final String catalyst;
	private final boolean charged;
	private final ReagentStack[] reagents;
	private final ReagentStack[] products;
	private final int amountChange;
	private final float data;//What "data" means varies with reaction type. Currently, destructive measures it as blast strength per reaction
	private final boolean active;//If false, disable this recipe. For datapacks
	private final EnumBeamAlignments alignment;//Only used for ELEMENTAL type

	private AlchemyRec(){
		group = "";
		type = Type.NORMAL;
		heatChange = 0;
		minTemp = 0;
		maxTemp = 0;
		catalyst = null;
		charged = false;
		products = reagents = new ReagentStack[0];
		amountChange = 0;
		data = 0;
		active = false;
		alignment = EnumBeamAlignments.NO_MATCH;
	}

	private AlchemyRec(String name, Type type, ReagentStack[] reagents, ReagentStack[] products, String cat, double minTemp, double maxTemp, double heatChange, boolean charged, float data, EnumBeamAlignments alignment){
		group = name;
		this.type = type;
		this.reagents = reagents;
		this.products = products;
		this.catalyst = Serializer.VOID_STR.equals(cat) ? null : cat;
		this.minTemp = minTemp;
		this.maxTemp = maxTemp;
		this.heatChange = heatChange;
		this.charged = charged;
		this.data = data;
		this.active = true;
		this.alignment = alignment;
		int change = 0;

		for(ReagentStack reag : reagents){
			change -= reag.amount();
		}
		for(ReagentStack prod : products){
			change += prod.amount();
		}

		this.amountChange = change;
	}

	@Nullable
	public String getCatalyst(){
		return catalyst;
	}

	private String getNonnullCatalyst(){
		return catalyst == null ? Serializer.VOID_STR : catalyst;
	}

	public double minTemp(){
		return minTemp;
	}

	public double maxTemp(){
		return maxTemp;
	}

	public boolean charged(){
		return charged;
	}

	public double deltaHeatPer(){
		return heatChange;
	}

	public ReagentStack[] getReagents(){
		return reagents;
	}

	public ReagentStack[] getProducts(){
		return products;
	}

	public Type getReactionType(){
		return type;
	}

	private float getData(){
		return data;
	}

	private EnumBeamAlignments getAlignment(){
		return alignment;
	}

	@Override
	public boolean isEnabled(){
		return active;
	}

	/**
	 * Machines performing reactions should call this method ONLY. The other methods are for JEI integration
	 * @param chamb The reaction chamber performing the reaction
	 * @return Whether this reaction was performed
	 */
	public boolean performReaction(IReactionChamber chamb){
		if(!active){
			return false;//If this is not a real reaction, do nothing
		}

		//Check charged, catalyst, temperature, and solvent requirements
		if(charged() && !chamb.isCharged()){
			return false;
		}

		ReagentMap reags = chamb.getReagents();
		if(catalyst != null && reags.getQty(catalyst) <= 0){
			return false;
		}
		double chambTemp = reags.getTempC();
		if(chambTemp > maxTemp() || chambTemp < minTemp()){
			return false;
		}

		int content = reags.getTotalQty();

		//Elemental reactions have special handling
		if(type == Type.ELEMENTAL || type == Type.ELEMENTAL_DESTRUCTIVE){
			if(alignment == EnumBeamAlignments.getAlignment(new BeamUnit(reags.getQty(EnumReagents.PHELOSTOGEN.id()), reags.getQty(EnumReagents.AETHER.id()), reags.getQty(EnumReagents.ADAMANT.id()), 0))){
				int created = 0;
				created += reags.getQty(EnumReagents.PHELOSTOGEN.id());
				created += reags.getQty(EnumReagents.AETHER.id());
				created += reags.getQty(EnumReagents.ADAMANT.id());
				reags.remove(EnumReagents.PHELOSTOGEN.id());
				reags.remove(EnumReagents.AETHER.id());
				reags.remove(EnumReagents.ADAMANT.id());

				for(ReagentStack reag : getProducts()){
					reags.addReagent(reag.getType(), created * reag.amount(), reags.getTempC());
				}

				if(created > 0 && type == Type.ELEMENTAL_DESTRUCTIVE){
					chamb.destroyChamber(Math.min(MAX_BLAST, data * created));
					chamb.addVisualEffect(ParticleTypes.SMOKE, 0, 0, 0);
				}

				return created > 0;
			}
			return false;
		}

		int maxReactions = amountChange <= 0 ? 200 : (chamb.getReactionCapacity() - content) / amountChange;//200 chosen arbitrarily as a moderately large positive number

		int prevMax = 0;
		for(ReagentStack reag : reagents){
			if(reags.getQty(reag.typeId()) <= 0){
				return false;
			}

			int maxFromReag = reags.getQty(reag.typeId()) / reag.amount();
			maxReactions = Math.min(maxReactions, maxFromReag);

			//Destroy the chamber for precise type if the ratio isn't perfect for the input
			if(type == Type.PRECISE && maxReactions > 0){
				if(prevMax != 0 && prevMax != maxFromReag){
					chamb.destroyChamber(0);
				}else{
					prevMax = maxFromReag;
				}
			}
		}

		double deltaHeat = deltaHeatPer();
		double allowedTempChange = deltaHeat < 0 ? maxTemp() - chambTemp : minTemp() - chambTemp;
		if(deltaHeat != 0 && deltaHeat + amountChange * allowedTempChange != 0){
			//temperature change based limit
			maxReactions = Math.min(maxReactions, (int) Math.max(1, -content * allowedTempChange / (deltaHeat + amountChange * allowedTempChange)));
		}

		if(maxReactions <= 0){
			return false;
		}

		for(ReagentStack reag : getProducts()){
			reags.addReagent(reag.getType(), maxReactions * reag.amount(), reags.getTempC());
		}

		for(ReagentStack reag : getReagents()){
			reags.removeReagent(reag.getType(), maxReactions * reag.amount());
		}

		reags.setTemp(HeatUtil.toCelcius((reags.getTempK() * reags.getTotalQty() - deltaHeat * maxReactions) / reags.getTotalQty()));

		if(type == Type.DESTRUCTIVE){
			chamb.destroyChamber(Math.min(MAX_BLAST, data * maxReactions));
			chamb.addVisualEffect(ParticleTypes.SMOKE, 0, 0, 0);
		}

		return true;
	}

	@Override
	public boolean matches(RecipeInput inv, Level worldIn){
		return true;//Irrelevant
	}

	@Override
	public boolean canCraftInDimensions(int width, int height){
		return true;//Irrelevant
	}

	@Override
	public ItemStack getResultItem(){
		return ItemStack.EMPTY;//Irrelevant
	}

	@Override
	public ItemStack getToastSymbol(){
		return new ItemStack(CRItems.florenceFlaskCrystal);
	}

	@Override
	public RecipeSerializer<?> getSerializer(){
		return CRRecipes.ALCHEMY_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public RecipeType<?> getType(){
		return CRRecipes.ALCHEMY_TYPE;
	}

	public static class Serializer implements RecipeSerializer<AlchemyRec>{

		/* The following JSON format is used for Alchemy Recipes:
		 * Anything prefaced by // is a comment, and should not be in a real JSON
		 *
		 * {
		 * 		"type": "crossroads:alchemy", //Tells Minecraft this is an alchemy recipe
		 *		"group": <group>, //Optional, same purpose as vanilla
		 * 		"category": <normal/precise/destructive/elemental>, //Optional, defaults to "normal". Destructive recipes explode (with strength controlled by data), and precise recipes break the chamber if the inputs weren't perfectly balanced. Elemental recipes require specifying an alignment with "data", and will ignore reactants, occurring if the mix of phel., aeth., and adam. matches the alignment.
		 * 		"min_temp": <number>, //Optional, defaults to absolute zero. Sets a minimum temperature for this reaction (celsius)
		 * 		"max_temp": <number>, //Optional, defaults to an unreachable high value. Sets a maximum temperature for this reaction (celsius)
		 *		"heat": <number>, //Optional, defaults to zero. Controls how much heat this reaction releases/absorbs. Negative numbers are exothermic, positive endothermic
		 * 		"catalyst": <string reagent ID or "NONE">, //Optional, defaults to "NONE". Sets a required catalyst to reagent ID if set to something other than NONE.
		 * 		"charged": <true or false>, //Optional, defaults to false. If true, the reaction chamber needs to be charged
		 * 		"data": <number/string alignment name>, //Optional, defaults to 0. Used by destructive type for controlling blast strength (see gunpowder for reference), expects a number. Elemental type reactions require this to be the string name of an alignment.
		 *		"active": <true of false>, //Optional, defaults to true. If false, this recipe will not be added! This is for making it easier to remove reactions through datapacks (to remove a reaction, override it with a version with active=false)
		 *
		 * 		//FOR ONE REAGENT
		 * 		"reagents": {
		 * 			"type": <string reagent ID>, //Sets a required reagent to the reagent ID
		 * 			"qty": <integer> //Optional, defaults to 1. Sets the amount of this reactant for one reaction
		 * 		}
		 * 		//OR
		 * 		//FOR MULTIPLE REAGENTS
		 * 		"reagents": [
		 * 			{	//Specifies the first reagent
		 * 				"type": <string reagent ID>, //Sets a required reagent to the reagent ID
		 * 				"qty": <integer> //Optional, defaults to 1. Sets the amount of this reactant for one reaction
		 * 			},
		 * 			//... As many reagents can be specified as desired
		 * 		]
		 *
		 * 		//FOR ONE PRODUCT
		 * 		"products": {
		 * 			"type": <string reagent ID>, //Sets a required reagent to the reagent ID
		 * 			"qty": <integer> //Optional, defaults to 1. Sets the amount of this product producted in one reaction
		 * 		}
		 * 		//OR
		 * 		//FOR MULTIPLE PRODUCTS (or zero)
		 * 		//If you want zero products, use this method and put nothing in the square brackets
		 * 		"products": [
		 * 			{	//Specifies the first product
		 * 				"type": <string reagent ID>, //Sets a required reagent to the reagent ID
		 * 				"qty": <integer> //Optional, defaults to 1. Sets the amount of this product producted in one reaction
		 * 			},
		 * 			//... As many reagents can be specified as desired
		 * 		]
		 * }
		 *
		 */

		private static final String VOID_STR = "NONE";

		static{
			AlchemyRec disabledRec = new AlchemyRec();
			MapCodec<AlchemyRec> codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
					CraftingUtil.recipeGroupFieldCodec().forGetter(AlchemyRec::getGroup),
					StringRepresentable.fromEnum(AlchemyRec.Type::values).optionalFieldOf("category", Type.NORMAL).forGetter(AlchemyRec::getReactionType),
					CraftingUtil.singleOrListCodec(ReagentStack.CODEC, 1, Integer.MAX_VALUE).xmap(list -> list.toArray(new ReagentStack[0]), List::of).fieldOf("reagents").forGetter(AlchemyRec::getReagents),
					CraftingUtil.singleOrListCodec(ReagentStack.CODEC, 0, Integer.MAX_VALUE).xmap(list -> list.toArray(new ReagentStack[0]), List::of).fieldOf("products").forGetter(AlchemyRec::getProducts),
					Codec.STRING.optionalFieldOf("catalyst", VOID_STR).forGetter(AlchemyRec::getNonnullCatalyst),
					Codec.DOUBLE.optionalFieldOf("min_temp", -300D).forGetter(AlchemyRec::minTemp),
					Codec.DOUBLE.optionalFieldOf("max_temp", Double.POSITIVE_INFINITY).forGetter(AlchemyRec::maxTemp),
					Codec.DOUBLE.optionalFieldOf("heat", 0D).forGetter(AlchemyRec::deltaHeatPer),
					Codec.BOOL.optionalFieldOf("charged", false).forGetter(AlchemyRec::charged),
					//There are two different fields using "data"- mutually exclusive on decode, but not sure if that works on encode
					Codec.FLOAT.lenientOptionalFieldOf("data", 0F).forGetter(AlchemyRec::getData),
					StringRepresentable.fromEnum(EnumBeamAlignments::values).lenientOptionalFieldOf("beam_element", EnumBeamAlignments.NO_MATCH).forGetter(AlchemyRec::getAlignment)
			).apply(instance, AlchemyRec::new));
			CODEC = IOptionalRecipe.codecWithDisable(codec, disabledRec);

			StreamCodec<RegistryFriendlyByteBuf, AlchemyRec> streamCodec = StreamCodecUtils.composite(
					ByteBufCodecs.STRING_UTF8, AlchemyRec::getGroup,
					ByteBufCodecs.fromCodecWithRegistries(StringRepresentable.fromEnum(AlchemyRec.Type::values)), AlchemyRec::getReactionType,
					ByteBufCodecs.collection(ArrayList::new, ReagentStack.STREAM_CODEC).map(list -> list.toArray(new ReagentStack[list.size()]), Lists::newArrayList), AlchemyRec::getReagents,
					ByteBufCodecs.collection(ArrayList::new, ReagentStack.STREAM_CODEC).map(list -> list.toArray(new ReagentStack[list.size()]), Lists::newArrayList), AlchemyRec::getProducts,
					ByteBufCodecs.STRING_UTF8, AlchemyRec::getNonnullCatalyst,
					ByteBufCodecs.DOUBLE, AlchemyRec::minTemp,
					ByteBufCodecs.DOUBLE, AlchemyRec::maxTemp,
					ByteBufCodecs.DOUBLE, AlchemyRec::deltaHeatPer,
					ByteBufCodecs.BOOL, AlchemyRec::charged,
					ByteBufCodecs.FLOAT, AlchemyRec::getData,
					ByteBufCodecs.fromCodecWithRegistries(StringRepresentable.fromEnum(EnumBeamAlignments::values)), AlchemyRec::getAlignment,
					AlchemyRec::new
			);
			STREAM_CODEC = IOptionalRecipe.codecWithDisable(streamCodec, disabledRec);
		}

		public static MapCodec<AlchemyRec> CODEC;
		public static StreamCodec<RegistryFriendlyByteBuf, AlchemyRec> STREAM_CODEC;

		@Override
		public MapCodec<AlchemyRec> codec(){
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, AlchemyRec> streamCodec(){
			return STREAM_CODEC;
		}

	}

	public enum Type implements StringRepresentable{
		NORMAL(),
		PRECISE(),//Destroys the chamber if proportions aren't exact
		DESTRUCTIVE(),//Destroys the chamber
		ELEMENTAL(),//Practitioner stone tier elemental reagent
		ELEMENTAL_DESTRUCTIVE;//Practitioner stone tier elemental reagent, but just destroys it

		@Override
		public String getSerializedName(){
			return name().toLowerCase(Locale.ENGLISH);
		}
	}
}
