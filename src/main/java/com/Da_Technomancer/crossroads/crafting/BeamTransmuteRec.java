package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.api.crafting.BlockIngredient;
import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.crafting.IOptionalRecipe;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Locale;

public class BeamTransmuteRec implements IOptionalRecipe<Container>{

	private final ResourceLocation id;
	private final String group;
	private final EnumBeamAlignments align;
	private final boolean voi;
	private final BlockIngredient ingr;
	private final Block output;
	private final int power;
	private final boolean active;

	public BeamTransmuteRec(ResourceLocation location, String name, EnumBeamAlignments align, boolean voi, BlockIngredient input, Block output, int power, boolean active){
		id = location;
		group = name;
		ingr = input;
		this.align = align;
		this.voi = voi;
		this.output = output;
		this.power = power;
		this.active = active;
	}

	public EnumBeamAlignments getAlign(){
		return align;
	}

	public boolean isVoid(){
		return voi;
	}

	public BlockIngredient getIngr(){
		return isEnabled() ? ingr : BlockIngredient.EMPTY;
	}

	public Block getOutput(){
		return output;
	}

	public int getPower(){
		return Math.max(power, 0);
	}

	@Override
	public boolean matches(Container inv, Level worldIn){
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
	public ItemStack assemble(Container inv){
		return getResultItem();
	}

	@Override
	public ItemStack getResultItem(){
		return new ItemStack(getOutput());
	}

	@Override
	public boolean canCraftInDimensions(int width, int height){
		return true;
	}

	@Override
	public ItemStack getToastSymbol(){
		return new ItemStack(CRBlocks.beamReflector);
	}

	@Override
	public ResourceLocation getId(){
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer(){
		return CRRecipes.BEAM_TRANSMUTE_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public boolean isEnabled(){
		return active;
	}

	@Override
	public RecipeType<?> getType(){
		return CRRecipes.BEAM_TRANSMUTE_TYPE;
	}

	public static class Serializer implements RecipeSerializer<BeamTransmuteRec>{

		@Override
		public BeamTransmuteRec fromJson(ResourceLocation recipeId, JsonObject json){
			//Normal specification of recipe group and ingredient
			String s = GsonHelper.getAsString(json, "group", "");

			if(!CraftingUtil.isActiveJSON(json)){
				return new BeamTransmuteRec(recipeId, s, EnumBeamAlignments.NO_MATCH, false, BlockIngredient.EMPTY, Blocks.AIR, 0, false);
			}

			//Beam alignment as string name (names in com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments), case ignored
			String alignName = GsonHelper.getAsString(json, "alignment");
			EnumBeamAlignments align;
			try{
				align = EnumBeamAlignments.valueOf(alignName.toUpperCase(Locale.US));
			}catch(NullPointerException e){
				throw new JsonParseException("Non-existent alignment specified");
			}

			//Optional specification of void version of beam, defaults false
			boolean voidBeam = GsonHelper.getAsBoolean(json, "void", false);
			//Optional specification of minimum beam power. Defaults to 1
			int power = GsonHelper.getAsInt(json, "power", 1);
			//BlockIngredient input, with name "input"
			BlockIngredient in = CraftingUtil.getBlockIngredient(json, "input", false);
			//Block output
			ResourceLocation outName = new ResourceLocation(GsonHelper.getAsString(json, "output"));
			Block created = ForgeRegistries.BLOCKS.getValue(outName);
			if(created == null){
				throw new JsonParseException("Non-existent output specified");
			}
			return new BeamTransmuteRec(recipeId, s, align, voidBeam, in, created, power, true);
		}

		@Nullable
		@Override
		public BeamTransmuteRec fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer){
			String s = buffer.readUtf(Short.MAX_VALUE);
			boolean active = buffer.readBoolean();
			if(active){
				EnumBeamAlignments align = EnumBeamAlignments.values()[buffer.readVarInt()];
				boolean voi = buffer.readBoolean();
				int power = buffer.readVarInt();
				Block out = ForgeRegistries.BLOCKS.getValue(buffer.readResourceLocation());
				BlockIngredient input = BlockIngredient.readFromBuffer(buffer);
				return new BeamTransmuteRec(recipeId, s, align, voi, input, out, power, true);
			}else{
				return new BeamTransmuteRec(recipeId, s, EnumBeamAlignments.NO_MATCH, false, BlockIngredient.EMPTY, Blocks.AIR, 0, false);
			}
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, BeamTransmuteRec recipe){
			buffer.writeUtf(recipe.getGroup());
			buffer.writeBoolean(recipe.active);
			if(recipe.active){
				buffer.writeVarInt(recipe.align.ordinal());
				buffer.writeBoolean(recipe.voi);
				buffer.writeVarInt(recipe.power);
				buffer.writeResourceLocation(MiscUtil.getRegistryName(recipe.output, ForgeRegistries.BLOCKS));
				recipe.ingr.writeToBuffer(buffer);
			}
		}
	}
}
