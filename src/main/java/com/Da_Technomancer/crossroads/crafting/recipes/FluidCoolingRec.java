package com.Da_Technomancer.crossroads.crafting.recipes;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.tileentities.heat.FluidCoolingChamberTileEntity;
import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

public class FluidCoolingRec implements IOptionalRecipe<IInventory>{

	private final ResourceLocation id;
	private final String group;

	private final FluidIngredient input;
	private final int inputQty;
	private final ItemStack created;
	private final float maxTemp;
	private final float addedHeat;
	private final boolean active;

	public FluidCoolingRec(ResourceLocation location, String name, FluidIngredient input, int inputQty, ItemStack output, float maxTemp, float addedHeat, boolean active){
		id = location;
		group = name;
		this.input = input;
		this.inputQty = inputQty;
		this.created = output;
		this.maxTemp = maxTemp;
		this.addedHeat = addedHeat;
		this.active = active;
	}

	@Override
	public boolean isEnabled(){
		return active;
	}

	public FluidIngredient getInput(){
		return input;
	}

	public int getInputQty(){
		return inputQty;
	}

	public boolean inputMatches(FluidStack available){
		return input != null && input.test(available) && inputQty <= available.getAmount();
	}

	public ItemStack getCreated(){
		return created;
	}

	public float getMaxTemp(){
		return maxTemp;
	}

	public float getAddedHeat(){
		return addedHeat;
	}

	@Override
	public boolean matches(IInventory inv, World worldIn){
		return active && inv instanceof FluidCoolingChamberTileEntity && input.test(((FluidCoolingChamberTileEntity) inv).getFluid());
	}

	@Override
	public boolean canCraftInDimensions(int width, int height){
		return true;
	}

	@Override
	public ItemStack getResultItem(){
		return created;
	}

	@Override
	public ItemStack getToastSymbol(){
		return new ItemStack(CRBlocks.fluidCoolingChamber);
	}

	@Override
	public ResourceLocation getId(){
		return id;
	}

	@Override
	public IRecipeSerializer<?> getSerializer(){
		return CRRecipes.FLUID_COOLING_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public IRecipeType<?> getType(){
		return CRRecipes.FLUID_COOLING_TYPE;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<FluidCoolingRec>{

		@Override
		public FluidCoolingRec fromJson(ResourceLocation recipeId, JsonObject json){
			//Normal specification of recipe group and ingredient
			String s = JSONUtils.getAsString(json, "group", "");

			if(!CraftingUtil.isActiveJSON(json)){
				return new FluidCoolingRec(recipeId, s, FluidIngredient.EMPTY, 0, ItemStack.EMPTY, 0, 0, false);
			}

			Pair<FluidIngredient, Integer> input = CraftingUtil.getFluidIngredientAndQuantity(json, "input", true, -1);
			ItemStack output = CraftingUtil.getItemStack(json, "output", true, false);
			float maxTemp = JSONUtils.getAsFloat(json, "max_temp");
			float tempChange = JSONUtils.getAsFloat(json, "temp_change", 0);
			return new FluidCoolingRec(recipeId, s, input.getLeft(), input.getRight(), output, maxTemp, tempChange, true);
		}

		@Nullable
		@Override
		public FluidCoolingRec fromNetwork(ResourceLocation recipeId, PacketBuffer buffer){
			String s = buffer.readUtf(Short.MAX_VALUE);
			if(!buffer.readBoolean()){//active
				return new FluidCoolingRec(recipeId, s, FluidIngredient.EMPTY, 0, ItemStack.EMPTY, 0, 0, false);
			}
			FluidIngredient input = FluidIngredient.readFromBuffer(buffer);
			int qty = buffer.readVarInt();
			ItemStack output = buffer.readItem();
			float maxTemp = buffer.readFloat();
			float tempChange = buffer.readFloat();
			return new FluidCoolingRec(recipeId, s, input, qty, output, maxTemp, tempChange, true);
		}

		@Override
		public void toNetwork(PacketBuffer buffer, FluidCoolingRec recipe){
			buffer.writeUtf(recipe.getGroup());
			buffer.writeBoolean(recipe.active);
			if(recipe.active){
				recipe.getInput().writeToBuffer(buffer);
				buffer.writeVarInt(recipe.getInputQty());
				buffer.writeItem(recipe.getResultItem());
				buffer.writeFloat(recipe.getMaxTemp());
				buffer.writeFloat(recipe.getAddedHeat());
			}
		}
	}
}
