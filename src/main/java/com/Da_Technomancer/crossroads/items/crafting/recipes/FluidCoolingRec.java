package com.Da_Technomancer.crossroads.items.crafting.recipes;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import com.Da_Technomancer.crossroads.tileentities.heat.FluidCoolingChamberTileEntity;
import com.Da_Technomancer.essentials.blocks.BlockUtil;
import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class FluidCoolingRec implements IRecipe<IInventory>{

	private final ResourceLocation id;
	private final String group;

	private final FluidStack input;
	private final ItemStack created;
	private final float maxTemp;
	private final float addedHeat;

	public FluidCoolingRec(ResourceLocation location, String name, FluidStack input, ItemStack output, float maxTemp, float addedHeat){
		id = location;
		group = name;
		this.input = input;
		this.created = output;
		this.maxTemp = maxTemp;
		this.addedHeat = addedHeat;
	}

	public FluidStack getInput(){
		return input;
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
		return inv instanceof FluidCoolingChamberTileEntity && BlockUtil.sameFluid(((FluidCoolingChamberTileEntity) inv).getFluid(), input);
	}

	@Override
	public ItemStack getCraftingResult(IInventory inv){
		return getRecipeOutput();
	}

	@Override
	public boolean canFit(int width, int height){
		return true;
	}

	@Override
	public ItemStack getRecipeOutput(){
		return created;
	}

	@Override
	public ItemStack getIcon(){
		return new ItemStack(CRBlocks.fluidCoolingChamber);
	}

	@Override
	public ResourceLocation getId(){
		return id;
	}

	@Override
	public IRecipeSerializer<?> getSerializer(){
		return RecipeHolder.FLUID_COOLING_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public IRecipeType<?> getType(){
		return RecipeHolder.FLUID_COOLING_TYPE;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<FluidCoolingRec>{

		@Override
		public FluidCoolingRec read(ResourceLocation recipeId, JsonObject json){
			//Normal specification of recipe group and ingredient
			String s = JSONUtils.getString(json, "group", "");

			FluidStack input = CraftingUtil.getFluidStack(json, "input");
			ItemStack output = CraftingUtil.getItemStack(json, "output", true, false);
			float maxTemp = JSONUtils.getFloat(json, "max_temp");
			float tempChange = JSONUtils.getFloat(json, "temp_change", 0);
			return new FluidCoolingRec(recipeId, s, input, output, maxTemp, tempChange);
		}

		@Nullable
		@Override
		public FluidCoolingRec read(ResourceLocation recipeId, PacketBuffer buffer){
			String s = buffer.readString(Short.MAX_VALUE);

			FluidStack input = FluidStack.readFromPacket(buffer);
			ItemStack output = buffer.readItemStack();
			float maxTemp = buffer.readFloat();
			float tempChange = buffer.readFloat();
			return new FluidCoolingRec(recipeId, s, input, output, maxTemp, tempChange);
		}

		@Override
		public void write(PacketBuffer buffer, FluidCoolingRec recipe){
			buffer.writeString(recipe.getGroup());

			recipe.getInput().writeToPacket(buffer);
			buffer.writeItemStack(recipe.getRecipeOutput());
			recipe.getIngredients().get(0).write(buffer);
			buffer.writeFloat(recipe.getMaxTemp());
			buffer.writeFloat(recipe.getAddedHeat());
		}
	}
}
