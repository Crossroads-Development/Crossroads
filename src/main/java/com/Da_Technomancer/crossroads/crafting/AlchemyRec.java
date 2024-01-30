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
import com.Da_Technomancer.crossroads.items.CRItems;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Locale;

public class AlchemyRec implements IOptionalRecipe<Container>{

	private static final float MAX_BLAST = 8;

	private final ResourceLocation id;
	private final String group;

	private final Type type;
	private final double heatChange;
	private final double minTemp;
	private final double maxTemp;
	private final String cat;
	private final boolean charged;
	private final ReagentStack[] reagents;
	private final ReagentStack[] products;
	private final int amountChange;
	private final float data;//What "data" means varies with reaction type. Currently, destructive measures it as blast strength per reaction
	private final boolean real;//If false, disable this recipe. For datapacks
	private final EnumBeamAlignments alignment;//Only used for ELEMENTAL type

	public AlchemyRec(ResourceLocation location, String name, Type type, ReagentStack[] reagents, ReagentStack[] products, @Nullable String cat, double minTemp, double maxTemp, double heatChange, boolean charged, float data, boolean real, EnumBeamAlignments alignment){
		id = location;
		group = name;

		this.type = type;
		this.reagents = reagents;
		this.products = products;
		this.cat = cat;
		this.minTemp = minTemp;
		this.maxTemp = maxTemp;
		this.heatChange = heatChange;
		this.charged = charged;
		this.data = data;
		this.real = real;
		this.alignment = alignment;
		int change = 0;

		for(ReagentStack reag : reagents){
			change -= reag.getAmount();
		}
		for(ReagentStack prod : products){
			change += prod.getAmount();
		}

		this.amountChange = change;
	}

	@Nullable
	public String getCatalyst(){
		return cat;
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

	@Override
	public boolean isEnabled(){
		return real;
	}

	/**
	 * Machines performing reactions should call this method ONLY. The other methods are for JEI integration
	 * @param chamb The reaction chamber performing the reaction
	 * @return Whether this reaction was performed
	 */
	public boolean performReaction(IReactionChamber chamb){
		if(!real){
			return false;//If this is not a real reaction, do nothing
		}

		//Check charged, catalyst, temperature, and solvent requirements
		if(charged() && !chamb.isCharged()){
			return false;
		}

		ReagentMap reags = chamb.getReagants();
		if(cat != null && reags.getQty(cat) <= 0){
			return false;
		}
		double chambTemp = chamb.getTemp();
		if(chambTemp > maxTemp() || chambTemp < minTemp()){
			return false;
		}

		int content = chamb.getContent();

		//Elemental reactions have special handling
		if(type == Type.ELEMENTAL){
			if(alignment == EnumBeamAlignments.getAlignment(new BeamUnit(reags.getQty(EnumReagents.PHELOSTOGEN.id()), reags.getQty(EnumReagents.AETHER.id()), reags.getQty(EnumReagents.ADAMANT.id()), 0))){
				int created = 0;
				created += reags.getQty(EnumReagents.PHELOSTOGEN.id());
				created += reags.getQty(EnumReagents.AETHER.id());
				created += reags.getQty(EnumReagents.ADAMANT.id());
				reags.remove(EnumReagents.PHELOSTOGEN.id());
				reags.remove(EnumReagents.AETHER.id());
				reags.remove(EnumReagents.ADAMANT.id());

				for(ReagentStack reag : getProducts()){
					reags.addReagent(reag.getType(), created * reag.getAmount(), reags.getTempC());
				}

				return created > 0;
			}
			return false;
		}

		int maxReactions = amountChange <= 0 ? 200 : (chamb.getReactionCapacity() - content) / amountChange;//200 chosen arbitrarily as a moderately large positive number

		int prevMax = 0;
		for(ReagentStack reag : reagents){
			if(reags.getQty(reag.getId()) <= 0){
				return false;
			}

			int maxFromReag = reags.getQty(reag.getId()) / reag.getAmount();
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
		if(deltaHeat != 0){
			//temperature change based limit
			double allowedTempChange = deltaHeat < 0 ? maxTemp() - chambTemp : minTemp() - chambTemp;
			maxReactions = Math.min(maxReactions, (int) Math.max(1, -content * allowedTempChange / (deltaHeat + amountChange * allowedTempChange)));
		}

		if(maxReactions <= 0){
			return false;
		}

		for(ReagentStack reag : getProducts()){
			reags.addReagent(reag.getType(), maxReactions * reag.getAmount(), reags.getTempC());
		}

		for(ReagentStack reag : getReagents()){
			reags.removeReagent(reag.getType(), maxReactions * reag.getAmount());
		}

		reags.setTemp(HeatUtil.toCelcius((reags.getTempK() * reags.getTotalQty() - deltaHeat * maxReactions) / reags.getTotalQty()));

		if(type == Type.DESTRUCTIVE){
			chamb.destroyChamber(Math.min(MAX_BLAST, data * maxReactions));
			chamb.addVisualEffect(ParticleTypes.SMOKE, 0, 0, 0);
		}

		return true;
	}

	@Override
	public boolean matches(Container inv, Level worldIn){
		return true;//Irrelevant
	}

	@Override
	public ItemStack assemble(Container inv){
		return getResultItem();//Irrelevant
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
	public ResourceLocation getId(){
		return id;
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

		@Override
		public AlchemyRec fromJson(ResourceLocation recipeId, JsonObject json){
			//Normal specification of recipe group and output
			String group = GsonHelper.getAsString(json, "group", "");

			boolean real = CraftingUtil.isActiveJSON(json);
			if(real){
				//Only bother reading the whole file if this is a real recipe
				Type type = Type.getType(GsonHelper.getAsString(json, "category", "normal"));
				double minTemp = GsonHelper.getAsFloat(json, "min_temp", -300F);
				double maxTemp = GsonHelper.getAsFloat(json, "max_temp", Short.MAX_VALUE);
				double heatChange = GsonHelper.getAsFloat(json, "heat", 0);
				String s = GsonHelper.getAsString(json, "catalyst", VOID_STR);
				String cat = s.equals(VOID_STR) ? null : s;
				boolean charge = GsonHelper.getAsBoolean(json, "charged", false);

				float data = 0;
				EnumBeamAlignments alignment = EnumBeamAlignments.NO_MATCH;
				if(type == Type.DESTRUCTIVE){
					data = GsonHelper.getAsFloat(json, "data", 0);
				}else if(type == Type.ELEMENTAL){
					alignment = EnumBeamAlignments.valueOf(GsonHelper.getAsString(json, "data", "no_match").toUpperCase(Locale.US));
				}

				JsonArray jsonR;
				if(GsonHelper.isArrayNode(json, "reagents")){
					jsonR = GsonHelper.getAsJsonArray(json, "reagents");
				}else{
					jsonR = new JsonArray();
					jsonR.add(GsonHelper.getAsJsonObject(json, "reagents"));
				}
				ReagentStack[] reags = new ReagentStack[jsonR.size()];
				for(int i = 0; i < reags.length; i++){
					JsonElement elem = jsonR.get(i);
					if(elem instanceof JsonObject){
						JsonObject obj = (JsonObject) elem;
						String reagent = GsonHelper.getAsString(obj, "type");
						reags[i] = new ReagentStack(reagent, GsonHelper.getAsInt(obj, "qty", 1));
					}
				}
				if(GsonHelper.isArrayNode(json, "products")){
					jsonR = GsonHelper.getAsJsonArray(json, "products");
				}else{
					jsonR = new JsonArray();
					jsonR.add(GsonHelper.getAsJsonObject(json, "products"));
				}
				ReagentStack[] prods = new ReagentStack[jsonR.size()];
				for(int i = 0; i < prods.length; i++){
					JsonElement elem = jsonR.get(i);
					if(elem instanceof JsonObject){
						JsonObject obj = (JsonObject) elem;
						String reagent = GsonHelper.getAsString(obj, "type");
						prods[i] = new ReagentStack(reagent, GsonHelper.getAsInt(obj, "qty", 1));
					}
				}

				return new AlchemyRec(recipeId, group, type, reags, prods, cat, minTemp, maxTemp, heatChange, charge, data, true, alignment);
			}else{
				return new AlchemyRec(recipeId, group, Type.NORMAL, new ReagentStack[0], new ReagentStack[0], null, HeatUtil.ABSOLUTE_ZERO, HeatUtil.ABSOLUTE_ZERO, 0, false, 0, false, EnumBeamAlignments.NO_MATCH);
			}
		}

		private static final String VOID_STR = "NONE";

		@Nullable
		@Override
		public AlchemyRec fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer){
			boolean real = buffer.readBoolean();
			String group = buffer.readUtf(Short.MAX_VALUE);

			if(real){
				Type type = Type.values()[buffer.readByte()];
				float heatChange = buffer.readFloat();
				float minTemp = buffer.readFloat();
				float maxTemp = buffer.readFloat();
				String s = buffer.readUtf(Short.MAX_VALUE);
				String catalyst = s.equals(VOID_STR) ? null : s;
				boolean charged = buffer.readBoolean();
				ReagentStack[] reags = new ReagentStack[buffer.readByte()];
				for(int i = 0; i < reags.length; i++){
					reags[i] = new ReagentStack(buffer.readUtf(), buffer.readByte());
				}
				ReagentStack[] prod = new ReagentStack[buffer.readByte()];
				for(int i = 0; i < prod.length; i++){
					prod[i] = new ReagentStack(buffer.readUtf(), buffer.readByte());
				}
				float data = buffer.readFloat();
				EnumBeamAlignments alignment = EnumBeamAlignments.values()[buffer.readVarInt()];

				return new AlchemyRec(recipeId, group, type, reags, prod, catalyst, minTemp, maxTemp, heatChange, charged, data, true, alignment);
			}else{
				return new AlchemyRec(recipeId, group, Type.NORMAL, new ReagentStack[0], new ReagentStack[0], null, HeatUtil.ABSOLUTE_ZERO, HeatUtil.ABSOLUTE_ZERO, 0, false, 0, false, EnumBeamAlignments.NO_MATCH);
			}
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, AlchemyRec recipe){
			buffer.writeBoolean(recipe.real);
			buffer.writeUtf(recipe.getGroup());//group
			if(recipe.real){
				buffer.writeByte(recipe.type.ordinal());//type
				buffer.writeFloat((float) recipe.heatChange);//heat
				buffer.writeFloat((float) recipe.minTemp);//min temp
				buffer.writeFloat((float) recipe.maxTemp);//max temp
				buffer.writeUtf(recipe.cat == null ? VOID_STR : recipe.cat);//catalyst
				buffer.writeBoolean(recipe.charged);//charged
				int total = recipe.reagents.length;
				buffer.writeByte(total);//Number of reagents
				for(ReagentStack reag : recipe.reagents){
					buffer.writeUtf(reag.getType().getID());//reag type
					buffer.writeByte(reag.getAmount());//reag qty
				}
				total = recipe.products.length;
				buffer.writeByte(total);//Number of products
				for(ReagentStack reag : recipe.products){
					buffer.writeUtf(reag.getType().getID());//prod type
					buffer.writeByte(reag.getAmount());//prod qty
				}
				buffer.writeFloat(recipe.data);//data
				buffer.writeVarInt(recipe.alignment.ordinal());
			}
		}
	}

	public enum Type{
		NORMAL(),
		PRECISE(),//Destroys the chamber if proportions aren't exact
		DESTRUCTIVE(),//Destroys the chamber
		ELEMENTAL();//Practitioner stone tier elemental reagent

		public static Type getType(String s){
			s = s.toUpperCase(Locale.ENGLISH);
			for(Type t : values()){
				if(t.name().equals(s)){
					return t;
				}
			}
			return NORMAL;
		}
	}
}
