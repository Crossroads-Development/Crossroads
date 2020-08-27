package com.Da_Technomancer.crossroads.crafting.recipes;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.tileentities.technomancy.CopshowiumCreationChamberTileEntity;
import com.Da_Technomancer.essentials.blocks.BlockUtil;
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

public class CopshowiumRec implements IOptionalRecipe<IInventory>{

	private final ResourceLocation id;
	private final String group;

	private final FluidStack input;
	private final float mult;
	private final boolean flux;
	private final boolean active;

	public CopshowiumRec(ResourceLocation location, String name, FluidStack input, float expandFactor, boolean flux, boolean active){
		id = location;
		group = name;
		this.input = input;
		this.mult = expandFactor;
		this.flux = flux;
		this.active = active;
	}

	@Override
	public boolean isEnabled(){
		return active;
	}

	public FluidStack getInput(){
		return input;
	}

	public float getMult(){
		return mult;
	}

	public boolean isFlux(){
		return flux;
	}

	@Override
	public boolean matches(IInventory inv, World worldIn){
		return active && inv instanceof CopshowiumCreationChamberTileEntity && BlockUtil.sameFluid(((CopshowiumCreationChamberTileEntity) inv).getInputFluid(), input);
	}

	@Override
	public boolean canFit(int width, int height){
		return true;
	}

	@Override
	public ItemStack getRecipeOutput(){
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getIcon(){
		return new ItemStack(CRBlocks.copshowiumCreationChamber);
	}

	@Override
	public ResourceLocation getId(){
		return id;
	}

	@Override
	public IRecipeSerializer<?> getSerializer(){
		return CRRecipes.COPSHOWIUM_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public IRecipeType<?> getType(){
		return CRRecipes.COPSHOWIUM_TYPE;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CopshowiumRec>{

		@Override
		public CopshowiumRec read(ResourceLocation recipeId, JsonObject json){
			//Normal specification of recipe group and ingredient
			String s = JSONUtils.getString(json, "group", "");

			if(!CraftingUtil.isActiveJSON(json)){
				return new CopshowiumRec(recipeId, s, FluidStack.EMPTY, 0, false, false);
			}

			//Specify the fluid input as a fluidstack. The qty is ignored, but must be specified.
			FluidStack input = CraftingUtil.getFluidStack(json, "input");
			input.setAmount(1000);
			//How much to expand the fluid by when crafting
			float mult = JSONUtils.getFloat(json, "mult", 1);
			//Whether this recipe generates temporal entropy
			boolean flux = JSONUtils.getBoolean(json, "entropy", false);
			return new CopshowiumRec(recipeId, s, input, mult, flux, true);
		}

		@Nullable
		@Override
		public CopshowiumRec read(ResourceLocation recipeId, PacketBuffer buffer){
			String s = buffer.readString(Short.MAX_VALUE);
			if(!buffer.readBoolean()){
				return new CopshowiumRec(recipeId, s, FluidStack.EMPTY, 0, false, false);
			}
			FluidStack input = FluidStack.readFromPacket(buffer);
			float mult = buffer.readFloat();
			boolean flux = buffer.readBoolean();
			return new CopshowiumRec(recipeId, s, input, mult, flux, true);
		}

		@Override
		public void write(PacketBuffer buffer, CopshowiumRec recipe){
			buffer.writeString(recipe.getGroup());
			buffer.writeBoolean(recipe.active);
			recipe.getInput().writeToPacket(buffer);
			buffer.writeFloat(recipe.mult);
			buffer.writeBoolean(recipe.flux);
		}
	}
}
