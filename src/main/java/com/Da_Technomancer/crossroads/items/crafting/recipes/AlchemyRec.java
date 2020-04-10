package com.Da_Technomancer.crossroads.items.crafting.recipes;

import com.Da_Technomancer.crossroads.API.alchemy.*;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.items.crafting.CraftingUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.Locale;

public class AlchemyRec implements IOptionalRecipe<IInventory>{

	private static final float MAX_BLAST = 8;

	private final ResourceLocation id;
	private final String group;

	private final Type type;
	private final double heatChange;
	private final double minTemp;
	private final double maxTemp;
	private final IReagent cat;
	private final boolean charged;
	private final ReagentStack[] reagents;
	private final ReagentStack[] products;
	private final int amountChange;
	private final float data;//What "data" means varies with reaction type. Currently, destructive measures it as blast strength per reaction
	private final boolean real;//If false, disable this recipe. For datapacks

	public AlchemyRec(ResourceLocation location, String name, Type type, ReagentStack[] reagents, ReagentStack[] products, @Nullable IReagent cat, double minTemp, double maxTemp, double heatChange, boolean charged, float data, boolean real){
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
	public IReagent getCatalyst(){
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

	public boolean isDestructive(){
		return type == Type.DESTRUCTIVE || type == Type.PRECISE;
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

		if(type == Type.ELEMENTAL){
			//Elemental type requires that product be length 1 and contain an elemental reagent

			//Chamber must be charged to begin
			if(chamb.isCharged()){
				IElementReagent prod = (IElementReagent) products[0].getType();

				//Requires practitioner's catalyst
				ReagentMap reags = chamb.getReagants();
				if(reags.getQty(EnumReagents.PRACTITIONER.id()) != 0 && prod.getAlignment() == EnumBeamAlignments.getAlignment(new BeamUnit(reags.getQty(EnumReagents.PHELOSTOGEN.id()), reags.getQty(EnumReagents.AETHER.id()), reags.getQty(EnumReagents.ADAMANT.id()), 0))){
					int created = 0;
					created += reags.getQty(EnumReagents.PHELOSTOGEN.id());
					created += reags.getQty(EnumReagents.AETHER.id());
					created += reags.getQty(EnumReagents.ADAMANT.id());
					reags.remove(AlchemyCore.REAGENTS.get(EnumReagents.PHELOSTOGEN.id()));
					reags.remove(AlchemyCore.REAGENTS.get(EnumReagents.AETHER.id()));
					reags.remove(AlchemyCore.REAGENTS.get(EnumReagents.ADAMANT.id()));
					reags.addReagent(prod, created, reags.getTempC());
					return created > 0;
				}
			}
			return false;
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

		int maxReactions = amountChange <= 0 ? 200 : (chamb.getReactionCapacity() - content) / amountChange;//200 chosen arbitrarily as a moderately large positive number

		int prevMax = 0;
		for(ReagentStack reag : reagents){
			if(reags.getQty(reag.getType()) <= 0){
				return false;
			}

			int maxFromReag = reags.getQty(reag.getType()) / reag.getAmount();
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
	public boolean matches(IInventory inv, World worldIn){
		return true;//Irrelevant
	}

	@Override
	public ItemStack getCraftingResult(IInventory inv){
		return getRecipeOutput();//Irrelevant
	}

	@Override
	public boolean canFit(int width, int height){
		return true;//Irrelevant
	}

	@Override
	public ItemStack getRecipeOutput(){
		return ItemStack.EMPTY;//Irrelevant
	}

	@Override
	public ItemStack getIcon(){
		return new ItemStack(CRItems.florenceFlaskCrystal);
	}

	@Override
	public ResourceLocation getId(){
		return id;
	}

	@Override
	public IRecipeSerializer<?> getSerializer(){
		return CRRecipes.ALCHEMY_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public IRecipeType<?> getType(){
		return CRRecipes.ALCHEMY_TYPE;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<AlchemyRec>{

		/* The following JSON format is used for Alchemy Recipes:
		 * Anything prefaced by // is a comment, and should not be in a real JSON
		 *
		 * {
		 * 		"type": "crossroads:alchemy", //Tells Minecraft this is an alchemy recipe
		 *		"group": <group>, //Optional, same purpose as vanilla
		 * 		"category": <normal/precise/destructive>, //Optional, defaults to "normal". Destructive recipes explode (with strength controlled by data), and precise recipes break the chamber if the inputs weren't perfectly balanced
		 *		"min_temp": <number>, //Optional, defaults to absolute zero. Sets a minimum temperature for this reaction (celsius)
		 * 		"max_temp": <number>, //Optional, defaults to an unreachable high value. Sets a maximum temperature for this reaction (celsius)
		 *		"heat": <number>, //Optional, defaults to zero. Controls how much heat this reaction releases/absorbs. Negative numbers are exothermic, positive endothermic
		 * 		"catalyst": <string reagent ID or "NONE">, //Optional, defaults to "NONE". Sets a required catalyst to reagent ID if set to something other than NONE.
		 * 		"charged": <true or false>, //Optional, defaults to false. If true, the reaction chamber needs to be charged
		 * 		"data": <number>, //Optional, defaults to 0. Only used by destructive type for controlling blast strength (see gunpowder for reference)
		 *		"active": <true of false>, //Optional, defaults to true. If false, this recipe will not be added! This is for making it easier to remove reactions through datapacks (to remove a reaction, override it with a version with real=false)
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
		public AlchemyRec read(ResourceLocation recipeId, JsonObject json){
			//Normal specification of recipe group and output
			String group = JSONUtils.getString(json, "group", "");

			boolean real = CraftingUtil.isActiveJSON(json);//JSONUtils.getBoolean(json, "real", true);
			if(real){
				//Only bother reading the whole file if this is a real recipe
				Type type = Type.getType(JSONUtils.getString(json, "category", "normal"));
				double minTemp = JSONUtils.getFloat(json, "min_temp", -300F);
				double maxTemp = JSONUtils.getFloat(json, "max_temp", Short.MAX_VALUE);
				double heatChange = JSONUtils.getFloat(json, "heat", 0);
				String s = JSONUtils.getString(json, "catalyst", VOID_STR);
				IReagent cat = s.equals(VOID_STR) ? null : AlchemyCore.REAGENTS.get(s);
				boolean charge = JSONUtils.getBoolean(json, "charged", false);
				float data = JSONUtils.getFloat(json, "data", 0);

				JsonArray jsonR;
				if(JSONUtils.isJsonArray(json, "reagents")){
					jsonR = JSONUtils.getJsonArray(json, "reagents");
				}else{
					jsonR = new JsonArray();
					jsonR.add(JSONUtils.getJsonObject(json, "reagents"));
				}
				ReagentStack[] reags = new ReagentStack[jsonR.size()];
				for(int i = 0; i < reags.length; i++){
					JsonElement elem = jsonR.get(i);
					if(elem instanceof JsonObject){
						JsonObject obj = (JsonObject) elem;
						IReagent reagent = AlchemyCore.REAGENTS.get(JSONUtils.getString(obj, "type"));
						assert reagent != null;
						reags[i] = new ReagentStack(reagent, JSONUtils.getInt(obj, "qty", 1));
					}
				}
				if(JSONUtils.isJsonArray(json, "products")){
					jsonR = JSONUtils.getJsonArray(json, "products");
				}else{
					jsonR = new JsonArray();
					jsonR.add(JSONUtils.getJsonObject(json, "products"));
				}
				ReagentStack[] prods = new ReagentStack[jsonR.size()];
				for(int i = 0; i < prods.length; i++){
					JsonElement elem = jsonR.get(i);
					if(elem instanceof JsonObject){
						JsonObject obj = (JsonObject) elem;
						IReagent reagent = AlchemyCore.REAGENTS.get(JSONUtils.getString(obj, "type"));
						assert reagent != null;
						prods[i] = new ReagentStack(reagent, JSONUtils.getInt(obj, "qty", 1));
					}
				}

				return new AlchemyRec(recipeId, group, type, reags, prods, cat, minTemp, maxTemp, heatChange, charge, data, true);
			}else{
				return new AlchemyRec(recipeId, group, Type.NORMAL, new ReagentStack[0], new ReagentStack[0], null, HeatUtil.ABSOLUTE_ZERO, HeatUtil.ABSOLUTE_ZERO, 0, false, 0, false);
			}
		}

		private static final String VOID_STR = "NONE";

		@Nullable
		@Override
		public AlchemyRec read(ResourceLocation recipeId, PacketBuffer buffer){
			boolean real = buffer.readBoolean();
			String group = buffer.readString(Short.MAX_VALUE);

			if(real){
				Type type = Type.values()[buffer.readByte()];
				float heatChange = buffer.readFloat();
				float minTemp = buffer.readFloat();
				float maxTemp = buffer.readFloat();
				String s = buffer.readString(Short.MAX_VALUE);
				IReagent catalyst = s.equals(VOID_STR) ? null : AlchemyCore.REAGENTS.get(s);
				boolean charged = buffer.readBoolean();
				ReagentStack[] reags = new ReagentStack[buffer.readByte()];
				for(int i = 0; i < reags.length; i++){
					reags[i] = new ReagentStack(AlchemyCore.REAGENTS.get(buffer.readString()), buffer.readByte());
				}
				ReagentStack[] prod = new ReagentStack[buffer.readByte()];
				for(int i = 0; i < prod.length; i++){
					prod[i] = new ReagentStack(AlchemyCore.REAGENTS.get(buffer.readString()), buffer.readByte());
				}
				float data = buffer.readFloat();

				return new AlchemyRec(recipeId, group, type, reags, prod, catalyst, minTemp, maxTemp, heatChange, charged, data, true);
			}else{
				return new AlchemyRec(recipeId, group, Type.NORMAL, new ReagentStack[0], new ReagentStack[0], null, HeatUtil.ABSOLUTE_ZERO, HeatUtil.ABSOLUTE_ZERO, 0, false, 0, false);
			}
		}

		@Override
		public void write(PacketBuffer buffer, AlchemyRec recipe){
			buffer.writeBoolean(recipe.real);
			buffer.writeString(recipe.getGroup());//group
			if(recipe.real){
				buffer.writeByte(recipe.type.ordinal());//type
				buffer.writeFloat((float) recipe.heatChange);//heat
				buffer.writeFloat((float) recipe.minTemp);//min temp
				buffer.writeFloat((float) recipe.maxTemp);//max temp
				buffer.writeString(recipe.cat == null ? VOID_STR : recipe.cat.getId());//catalyst
				buffer.writeBoolean(recipe.charged);//charged
				int total = recipe.reagents.length;
				buffer.writeByte(total);//Number of reagents
				for(ReagentStack reag : recipe.reagents){
					buffer.writeString(reag.getType().getId());//reag type
					buffer.writeByte(reag.getAmount());//reag qty
				}
				total = recipe.products.length;
				buffer.writeByte(total);//Number of products
				for(ReagentStack reag : recipe.products){
					buffer.writeString(reag.getType().getId());//prod type
					buffer.writeByte(reag.getAmount());//prod qty
				}
				buffer.writeFloat(recipe.data);//data
			}
		}
	}
	
	public enum Type{
		NORMAL(),
		PRECISE(),//Destroys the chamber if proportions aren't exact
		DESTRUCTIVE(),//Destroys the chamber
		ELEMENTAL();//Hardcoded, not JSON-able

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
