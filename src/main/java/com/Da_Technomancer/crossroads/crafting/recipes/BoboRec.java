package com.Da_Technomancer.crossroads.crafting.recipes;

import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class BoboRec implements IOptionalRecipe<IInventory>{

	private final ResourceLocation id;
	private final String group;
	private final Ingredient[] ingr;
	private final ItemStack output;
	private final boolean active;

	public BoboRec(ResourceLocation location, String name, Ingredient[] input, ItemStack output, boolean active){
		id = location;
		group = name;
		ingr = input;
		this.output = output;
		this.active = active;
	}

	@Override
	public boolean matches(IInventory inv, World worldIn){
		if(!isEnabled() || inv.getContainerSize() != 3){
			return false;
		}
		//Known issue: this will pass if one input meets 2+ ingredients, even if the third input is irrelevant
		//No default Crossroads recipes have this issue- it would be silly to add a recipe that does
		for(Ingredient input : ingr){
			boolean pass = false;
			for(int i = 0; i < 3; i++){
				if(input.test(inv.getItem(i))){
					pass = true;
					break;
				}
			}
			if(!pass){
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isEnabled(){
		return active;
	}

	@Override
	public NonNullList<Ingredient> getIngredients(){
		NonNullList<Ingredient> nonnulllist = NonNullList.create();
		nonnulllist.add(ingr[0]);
		nonnulllist.add(ingr[1]);
		nonnulllist.add(ingr[2]);
		return nonnulllist;
	}

	@Override
	public ItemStack assemble(IInventory inv){
		return getResultItem().copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height){
		return true;
	}

	@Override
	public ItemStack getResultItem(){
		return output;
	}

	@Override
	public ItemStack getToastSymbol(){
		return new ItemStack(CRItems.boboRod);
	}

	@Override
	public ResourceLocation getId(){
		return id;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public IRecipeSerializer<?> getSerializer(){
		return CRRecipes.BOBO_SERIAL;
	}

	@Override
	public IRecipeType<?> getType(){
		return CRRecipes.BOBO_TYPE;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<BoboRec>{

		@Override
		public BoboRec fromJson(ResourceLocation recipeId, JsonObject json){
			//Normal specification of recipe group and ingredient
			String s = JSONUtils.getAsString(json, "group", "");

			if(!CraftingUtil.isActiveJSON(json)){
				return new BoboRec(recipeId, s, new Ingredient[] {Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.EMPTY}, ItemStack.EMPTY, false);
			}
			//3 inputs, named input_a, input_b, and input_c
			Ingredient[] ingr = new Ingredient[3];
			ingr[0] = CraftingUtil.getIngredient(json, "input_a", false);
			ingr[1] = CraftingUtil.getIngredient(json, "input_b", false);
			ingr[2] = CraftingUtil.getIngredient(json, "input_c", false);

			//Specify output
			ItemStack output = CraftingUtil.getItemStack(json, "output", false, true);
			return new BoboRec(recipeId, s, ingr, output, true);
		}

		@Nullable
		@Override
		public BoboRec fromNetwork(ResourceLocation recipeId, PacketBuffer buffer){
			String s = buffer.readUtf(Short.MAX_VALUE);
			if(buffer.readBoolean()){
				Ingredient[] inputs = new Ingredient[3];
				for(int i = 0; i < 3; i++){
					inputs[i] = Ingredient.fromNetwork(buffer);
				}
				ItemStack output = buffer.readItem();
				return new BoboRec(recipeId, s, inputs, output, true);
			}else{
				return new BoboRec(recipeId, s, new Ingredient[] {Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.EMPTY}, ItemStack.EMPTY, false);
			}
		}

		@Override
		public void toNetwork(PacketBuffer buffer, BoboRec recipe){
			buffer.writeUtf(recipe.getGroup());
			buffer.writeBoolean(recipe.active);
			if(recipe.active){
				for(Ingredient ingr : recipe.ingr){
					ingr.toNetwork(buffer);
				}
				buffer.writeItem(recipe.output);
			}
		}
	}
}
