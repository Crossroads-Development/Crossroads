package com.Da_Technomancer.crossroads.crafting.recipes;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.tileentities.fluid.WaterCentrifugeTileEntity;
import com.Da_Technomancer.essentials.blocks.BlockUtil;
import com.google.gson.JsonArray;
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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CentrifugeRec implements IOptionalRecipe<IInventory>{

	private static final Random RAND = new Random();

	private final ResourceLocation id;
	private final String group;

	private final FluidStack input;

	private final FluidStack fluidOutput;
	private final WeightOutput[] outputs;
	private final boolean active;

	private final int totalWeight;//Cached

	public CentrifugeRec(ResourceLocation location, String name, FluidStack input, FluidStack fluidOutput, WeightOutput[] outputs, boolean active){
		id = location;
		group = name;
		this.input = input;
		this.fluidOutput = fluidOutput;
		this.outputs = outputs;
		this.active = active;

		int weight = 0;
		for(WeightOutput out : outputs){
			weight += out.weight;
		}
		totalWeight = weight;
	}

	@Override
	public boolean matches(IInventory inv, World worldIn){
		FluidStack teInput;
		return active && inv instanceof WaterCentrifugeTileEntity && BlockUtil.sameFluid(teInput = ((WaterCentrifugeTileEntity) inv).getInputFluid(), input) && teInput.getAmount() >= input.getAmount();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height){
		return true;
	}

	/**
	 * Gets a list of every produced item. Used for JEI support
	 * @return Every produced item
	 */
	public List<ItemStack> getOutputList(){
		List<ItemStack> out = new ArrayList<>(outputs.length);
		for(WeightOutput output : outputs){
			out.add(output.item);
		}
		return out;
	}

	@Override
	public ItemStack getResultItem(){
		int selected = RAND.nextInt(totalWeight);
		for(WeightOutput out : outputs){
			selected -= out.weight;
			if(selected <= 0){
				return out.item;
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getToastSymbol(){
		return new ItemStack(CRBlocks.waterCentrifuge);
	}

	@Override
	public ResourceLocation getId(){
		return id;
	}

	@Override
	public IRecipeSerializer<?> getSerializer(){
		return CRRecipes.CENTRIFUGE_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public IRecipeType<?> getType(){
		return CRRecipes.CENTRIFUGE_TYPE;
	}

	public FluidStack getInput(){
		return input.copy();
	}

	public FluidStack getFluidOutput(){
		return fluidOutput.copy();
	}

	@Override
	public boolean isEnabled(){
		return active;
	}

	private static class WeightOutput{

		private final ItemStack item;
		private final int weight;

		public WeightOutput(ItemStack item, int weight){
			this.item = item;
			this.weight = weight;
		}

		private void serialize(PacketBuffer buf){
			buf.writeItem(item);
			buf.writeVarInt(weight);
		}

		private static WeightOutput deserialize(PacketBuffer buf){
			return new WeightOutput(buf.readItem(), buf.readVarInt());
		}
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CentrifugeRec>{

		@Override
		public CentrifugeRec fromJson(ResourceLocation recipeId, JsonObject json){
			//Normal specification of recipe group and output
			String s = JSONUtils.getAsString(json, "group", "");
			if(!CraftingUtil.isActiveJSON(json)){
				return new CentrifugeRec(recipeId, s, FluidStack.EMPTY, FluidStack.EMPTY, new WeightOutput[0], false);
			}

			FluidStack input = CraftingUtil.getFluidStack(json, "input");
			FluidStack outputFluid = CraftingUtil.getFluidStack(json, "output_fluid");

			//Read the item output(s)
			//If the output is specified as an object, treat it as a single weighted output
			//If it's an array, expect it to be an array of objects of outputs

			WeightOutput[] outputs;
			if(JSONUtils.isArrayNode(json, "output")){
				JsonArray arr = JSONUtils.getAsJsonArray(json, "output");
				outputs = new WeightOutput[arr.size()];
				for(int i = 0; i < outputs.length; i++){
					outputs[i] = readOutput(arr.get(i).getAsJsonObject());
				}
			}else{
				outputs = new WeightOutput[1];
				outputs[0] = readOutput(JSONUtils.getAsJsonObject(json, "output"));
			}

			return new CentrifugeRec(recipeId, s, input, outputFluid, outputs, true);
		}

		/**
		 * Reads a single weight object from a Json Object
		 * @param json The Json Object containing the output
		 * @return The contained output
		 */
		private static WeightOutput readOutput(JsonObject json){
			/*
			 * Expects format:
			 * "item": "<item type>"
			 * "count": <number>
			 * "weight": <number, optional- defaults to 1>
			 */
			return new WeightOutput(CraftingUtil.getItemStack(json, "output", true, false), JSONUtils.getAsInt(json, "weight", 1));
		}

		@Nullable
		@Override
		public CentrifugeRec fromNetwork(ResourceLocation recipeId, PacketBuffer buffer){
			String s = buffer.readUtf(Short.MAX_VALUE);
			if(!buffer.readBoolean()){
				return new CentrifugeRec(recipeId, s, FluidStack.EMPTY, FluidStack.EMPTY, new WeightOutput[0], false);
			}
			FluidStack input = buffer.readFluidStack();
			FluidStack fluidOut = buffer.readFluidStack();
			WeightOutput[] outputs = new WeightOutput[buffer.readVarInt()];
			for(int i = 0; i < outputs.length; i++){
				outputs[i] = WeightOutput.deserialize(buffer);
			}
			return new CentrifugeRec(recipeId, s, input, fluidOut, outputs, true);
		}

		@Override
		public void toNetwork(PacketBuffer buffer, CentrifugeRec recipe){
			buffer.writeUtf(recipe.getGroup());
			buffer.writeBoolean(recipe.active);
			buffer.writeFluidStack(recipe.input);
			buffer.writeFluidStack(recipe.fluidOutput);
			buffer.writeVarInt(recipe.outputs.length);
			for(WeightOutput out : recipe.outputs){
				out.serialize(buffer);
			}
		}
	}
}
