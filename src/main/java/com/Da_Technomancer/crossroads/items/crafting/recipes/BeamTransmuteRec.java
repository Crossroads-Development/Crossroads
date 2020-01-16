package com.Da_Technomancer.crossroads.items.crafting.recipes;

import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.Locale;

public class BeamTransmuteRec implements IRecipe<IInventory>{

	private final ResourceLocation id;
	private final String group;
	private final EnumBeamAlignments align;
	private final boolean voi;
	private final BlockIngredient ingr;
	private final Block output;
	private final int power;

	public BeamTransmuteRec(ResourceLocation location, String name, EnumBeamAlignments align, boolean voi, BlockIngredient input, Block output, int power){
		id = location;
		group = name;
		ingr = input;
		this.align = align;
		this.voi = voi;
		this.output = output;
		this.power = power;
	}

	public EnumBeamAlignments getAlign(){
		return align;
	}

	public boolean isVoid(){
		return voi;
	}

	public BlockIngredient getIngr(){
		return ingr;
	}

	public Block getOutput(){
		return output;
	}

	public int getPower(){
		return power;
	}

	@Override
	public boolean matches(IInventory inv, World worldIn){
		return true;//The entire condition of this recipe is based on block type and power, which can't be determined here
	}

	/**
	 * The condition actually used to determine if this recipe applies
	 * @param alignment The Alignment of the beam
	 * @param voidBeam Whether the beam has any void
	 * @param beamPower The power of the beam. Must meet or exceed the recipe power
	 * @param state The blockstate being hit
	 * @return Whether this recipe can apply
	 */
	public boolean canApply(EnumBeamAlignments alignment, boolean voidBeam, int beamPower, BlockState state){
		return alignment == align && voidBeam == voi && beamPower >= power && ingr.test(state);
	}

	@Override
	public ItemStack getCraftingResult(IInventory inv){
		return getRecipeOutput();
	}

	@Override
	public ItemStack getRecipeOutput(){
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canFit(int width, int height){
		return true;
	}

	@Override
	public ItemStack getIcon(){
		return new ItemStack(CrossroadsBlocks.beamReflector);
	}

	@Override
	public ResourceLocation getId(){
		return id;
	}

	@Override
	public IRecipeSerializer<?> getSerializer(){
		return RecipeHolder.BEAM_TRANSMUTE_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public IRecipeType<?> getType(){
		return RecipeHolder.BEAM_TRANSMUTE_TYPE;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<BeamTransmuteRec>{

		@Override
		public BeamTransmuteRec read(ResourceLocation recipeId, JsonObject json){
			//Normal specification of recipe group and ingredient
			String s = JSONUtils.getString(json, "group", "");

			//Beam alignment as string name (names in com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments), case ignored
			String alignName = JSONUtils.getString(json, "alignment");
			EnumBeamAlignments align;
			try{
				align = EnumBeamAlignments.valueOf(alignName.toUpperCase(Locale.US));
			}catch(NullPointerException e){
				throw new JsonParseException("Non-existent alignment specified");
			}

			//Optional specification of void version of beam, defaults false
			boolean voidBeam = JSONUtils.getBoolean(json, "void", false);
			//Optional specification of minimum beam power. Defaults to 1
			int power = JSONUtils.getInt(json, "power", 1);
			//BlockIngredient input, with name "input"
			BlockIngredient in = CraftingUtil.getBlockIngredient(json, "input", false);
			//Block output
			ResourceLocation outName = new ResourceLocation(JSONUtils.getString(json, "output"));
			Block created = ForgeRegistries.BLOCKS.getValue(outName);
			if(created == null){
				throw new JsonParseException("Non-existent output specified");
			}
			return new BeamTransmuteRec(recipeId, s, align, voidBeam, in, created, power);
		}

		@Nullable
		@Override
		public BeamTransmuteRec read(ResourceLocation recipeId, PacketBuffer buffer){
			String s = buffer.readString(Short.MAX_VALUE);
			EnumBeamAlignments align = EnumBeamAlignments.values()[buffer.readVarInt()];
			boolean voi = buffer.readBoolean();
			int power = buffer.readVarInt();
			Block out = ForgeRegistries.BLOCKS.getValue(buffer.readResourceLocation());
			BlockIngredient input = BlockIngredient.readFromBuffer(buffer);
			return new BeamTransmuteRec(recipeId, s, align, voi, input, out, power);
		}

		@Override
		public void write(PacketBuffer buffer, BeamTransmuteRec recipe){
			buffer.writeString(recipe.getGroup());
			buffer.writeVarInt(recipe.align.ordinal());
			buffer.writeBoolean(recipe.voi);
			buffer.writeVarInt(recipe.power);
			buffer.writeResourceLocation(recipe.output.getRegistryName());
			recipe.ingr.writeToBuffer(buffer);
		}
	}
}
