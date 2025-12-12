package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.crafting.IOptionalRecipe;
import com.Da_Technomancer.crossroads.api.witchcraft.IEntityModifier;
import com.Da_Technomancer.crossroads.api.witchcraft.IEntityModifierType;
import com.Da_Technomancer.crossroads.effects.entity_modifiers.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;

public class EmbryoLabModifierRec implements IOptionalRecipe<RecipeInput>{

	//This whole scheme for registering modifiers with a datapack kind of sucks
	//You should be able to make new variants of AttributeEntityModifierType with a datapack

	private final String group;
	private final IEntityModifierType<?> modifierType;
	private final int complexity;
	private final int soulComplexity;
	private final Ingredient ingr;
	private final ItemStack returnItem;
	private final boolean active;

	private EmbryoLabModifierRec(){
		this.group = "";
		this.modifierType = null;
		this.complexity = 0;
		this.soulComplexity = 0;
		this.ingr = Ingredient.EMPTY;
		this.returnItem = ItemStack.EMPTY;
		this.active = false;
	}

	private EmbryoLabModifierRec(String group, IEntityModifierType<?> modifierType, int complexity, int soulComplexity, Ingredient ingr, ItemStack returnItem){
		this.group = group;
		this.modifierType = modifierType;
		this.complexity = complexity;
		this.soulComplexity = soulComplexity;
		this.ingr = ingr;
		this.returnItem = returnItem;
		this.active = true;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public ItemStack getResultItem(){
		return returnItem;
	}

	@Override
	public boolean isEnabled(){
		return active;
	}

	public IEntityModifierType<?> getModifierType(){
		return modifierType;
	}

	public int getComplexity(){
		return complexity;
	}

	public int getSoulComplexity(){
		return soulComplexity;
	}

	public IEntityModifier createModifier(ItemStack srcItem){
		return modifierType.createModifier(srcItem, complexity, soulComplexity);
	}

	public Ingredient getIngr(){
		return ingr;
	}

	@Override
	public boolean matches(RecipeInput input, Level world){
		return isEnabled() && ingr.test(input.getItem(0));
	}

	@Override
	public boolean canCraftInDimensions(int width, int height){
		return width != 0 && height != 0;
	}

	@Override
	public RecipeSerializer<?> getSerializer(){
		return CRRecipes.EMBRYO_LAB_MODIFIER_SERIAL;
	}

	@Override
	public RecipeType<?> getType(){
		return CRRecipes.EMBRYO_LAB_MODIFIER_TYPE;
	}

	public static class Serializer implements RecipeSerializer<EmbryoLabModifierRec>{

		static{
			MapCodec<EmbryoLabModifierRec> codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
					CraftingUtil.recipeGroupFieldCodec().forGetter(EmbryoLabModifierRec::getGroup),
					IEntityModifierType.CODEC.fieldOf("modifier").forGetter(EmbryoLabModifierRec::getModifierType),
					Codec.INT.optionalFieldOf("complexity", 0).forGetter(EmbryoLabModifierRec::getComplexity),
					Codec.INT.optionalFieldOf("soul_complexity", 0).forGetter(EmbryoLabModifierRec::getSoulComplexity),
					CraftingUtil.itemIngredientMapCodec("input", false).forGetter(EmbryoLabModifierRec::getIngr),
					CraftingUtil.itemStackMapCodec("returned_container", false, ItemStack.EMPTY).forGetter(EmbryoLabModifierRec::getResultItem)
			).apply(instance, EmbryoLabModifierRec::new));
			StreamCodec<RegistryFriendlyByteBuf, EmbryoLabModifierRec> streamCodec = StreamCodec.composite(
					ByteBufCodecs.STRING_UTF8, EmbryoLabModifierRec::getGroup,
					IEntityModifierType.STREAM_CODEC, EmbryoLabModifierRec::getModifierType,
					ByteBufCodecs.VAR_INT, EmbryoLabModifierRec::getComplexity,
					ByteBufCodecs.VAR_INT, EmbryoLabModifierRec::getSoulComplexity,
					Ingredient.CONTENTS_STREAM_CODEC, EmbryoLabModifierRec::getIngr,
					ItemStack.OPTIONAL_STREAM_CODEC, EmbryoLabModifierRec::getResultItem,
					EmbryoLabModifierRec::new
			);
			EmbryoLabModifierRec disabledRec = new EmbryoLabModifierRec();
			CODEC = IOptionalRecipe.codecWithDisable(codec, disabledRec);
			STREAM_CODEC = IOptionalRecipe.codecWithDisable(streamCodec, disabledRec);
		}

		public static final MapCodec<EmbryoLabModifierRec> CODEC;
		public static final StreamCodec<RegistryFriendlyByteBuf, EmbryoLabModifierRec> STREAM_CODEC;

		@Override
		public MapCodec<EmbryoLabModifierRec> codec(){
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, EmbryoLabModifierRec> streamCodec(){
			return STREAM_CODEC;
		}
	}

	private static final HashMap<String, IEntityModifierType<?>> modifierMap = new HashMap<>(10);

	/**
	 * Adds a new modifier type that can be applied to clones
	 * Should be called BEFORE recipes are loaded from data
	 * If there is a pre-existing modifierType with the same modifier ID, the new type will overwrite
	 * @param modifierType The new modifier type
	 */
	public static void registerEffect(IEntityModifierType<?> modifierType){
		modifierMap.put(modifierType.getModifierID(), modifierType);
	}

	public static IEntityModifierType<?> lookupModifierType(String id){
		return modifierMap.get(id);
	}

	static{
		registerEffect(RespawningEntityModifier.TYPE_INSTANCE);
		registerEffect(PatientEntityModifier.TYPE_INSTANCE);
		registerEffect(ImprintingEntityModifier.TYPE_INSTANCE);
		registerEffect(NamedEntityModifier.TYPE_INSTANCE);
		ResourceLocation modLoc = ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "ent_mod.large");
		registerEffect(new AttributeEntityModifierType("large", Component.translatable("ent_mod.large"),
				new Pair[][] {
						{
								Pair.of(Attributes.SCALE, new AttributeModifier(modLoc, .5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.MAX_HEALTH, new AttributeModifier(modLoc, .5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(modLoc, 1 - 1/Math.pow(1.5, 3), AttributeModifier.Operation.ADD_VALUE)),
								Pair.of(Attributes.ATTACK_DAMAGE, new AttributeModifier(modLoc, .5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(modLoc, .5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.ATTACK_SPEED, new AttributeModifier(modLoc, -.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(modLoc, .5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.BLOCK_INTERACTION_RANGE, new AttributeModifier(modLoc, .5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.FALL_DAMAGE_MULTIPLIER, new AttributeModifier(modLoc, .5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
//								Pair.of(Attributes.SAFE_FALL_DISTANCE, new AttributeModifier(modLoc, .25, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.JUMP_STRENGTH, new AttributeModifier(modLoc, .5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.STEP_HEIGHT, new AttributeModifier(modLoc, 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
						},
						{
								Pair.of(Attributes.SCALE, new AttributeModifier(modLoc, 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.MAX_HEALTH, new AttributeModifier(modLoc, 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(modLoc, 1 - 1/Math.pow(2, 3), AttributeModifier.Operation.ADD_VALUE)),
								Pair.of(Attributes.ATTACK_DAMAGE, new AttributeModifier(modLoc, 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(modLoc, 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.ATTACK_SPEED, new AttributeModifier(modLoc, -.25, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(modLoc, 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.BLOCK_INTERACTION_RANGE, new AttributeModifier(modLoc, 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.FALL_DAMAGE_MULTIPLIER, new AttributeModifier(modLoc, 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
//								Pair.of(Attributes.SAFE_FALL_DISTANCE, new AttributeModifier(modLoc, .5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.JUMP_STRENGTH, new AttributeModifier(modLoc, 2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.STEP_HEIGHT, new AttributeModifier(modLoc, 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
						},
						{
								Pair.of(Attributes.SCALE, new AttributeModifier(modLoc, 4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.MAX_HEALTH, new AttributeModifier(modLoc, 4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(modLoc, 1 - 1/Math.pow(5, 3), AttributeModifier.Operation.ADD_VALUE)),
								Pair.of(Attributes.ATTACK_DAMAGE, new AttributeModifier(modLoc, 5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(modLoc, 10, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.ATTACK_SPEED, new AttributeModifier(modLoc, -.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(modLoc, 4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.BLOCK_INTERACTION_RANGE, new AttributeModifier(modLoc, 4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.FALL_DAMAGE_MULTIPLIER, new AttributeModifier(modLoc, 2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
//								Pair.of(Attributes.SAFE_FALL_DISTANCE, new AttributeModifier(modLoc, 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.JUMP_STRENGTH, new AttributeModifier(modLoc, 4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.STEP_HEIGHT, new AttributeModifier(modLoc, 3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.FOLLOW_RANGE, new AttributeModifier(modLoc, 4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
						}
				}));
		modLoc = ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "ent_mod.small");
		registerEffect(new AttributeEntityModifierType("small", Component.translatable("ent_mod.small"),
				new Pair[][] {
						{
								Pair.of(Attributes.SCALE, new AttributeModifier(modLoc, -.33, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.MAX_HEALTH, new AttributeModifier(modLoc, -.33, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(modLoc, -(1 - 1/Math.pow(1.5, 3)), AttributeModifier.Operation.ADD_VALUE)),
								Pair.of(Attributes.ATTACK_DAMAGE, new AttributeModifier(modLoc, -.33, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(modLoc, -.33, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.ATTACK_SPEED, new AttributeModifier(modLoc, .11, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(modLoc, -.33, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.BLOCK_INTERACTION_RANGE, new AttributeModifier(modLoc, -.33, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.FALL_DAMAGE_MULTIPLIER, new AttributeModifier(modLoc, -.33, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.SAFE_FALL_DISTANCE, new AttributeModifier(modLoc, 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.JUMP_STRENGTH, new AttributeModifier(modLoc, -.33, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.STEP_HEIGHT, new AttributeModifier(modLoc, -0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
						}
				}));
		modLoc = ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "ent_mod.semi_aquatic");
		registerEffect(new AttributeEntityModifierType("semi_aquatic", Component.translatable("ent_mod.semi_aquatic"),
				new Pair[][] {
						{
								Pair.of(Attributes.OXYGEN_BONUS, new AttributeModifier(modLoc, 1024, AttributeModifier.Operation.ADD_VALUE)),
								Pair.of(Attributes.SUBMERGED_MINING_SPEED, new AttributeModifier(modLoc, 1, AttributeModifier.Operation.ADD_VALUE)),
								Pair.of(Attributes.WATER_MOVEMENT_EFFICIENCY, new AttributeModifier(modLoc, 1, AttributeModifier.Operation.ADD_VALUE)),
								Pair.of(Attributes.BURNING_TIME, new AttributeModifier(modLoc, 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
						}
				}));
		modLoc = ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "ent_mod.anti_grav");
		registerEffect(new AttributeEntityModifierType("anti_grav", Component.translatable("ent_mod.anti_grav"),
				new Pair[][] {
						{
								Pair.of(Attributes.GRAVITY, new AttributeModifier(modLoc, -0.9, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)),
								Pair.of(Attributes.SAFE_FALL_DISTANCE, new AttributeModifier(modLoc, 1024, AttributeModifier.Operation.ADD_VALUE)),
								Pair.of(Attributes.FALL_DAMAGE_MULTIPLIER, new AttributeModifier(modLoc, -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
						}
				}));
		modLoc = ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "ent_mod.inbred");
		registerEffect(new AttributeEntityModifierType("inbred", Component.translatable("ent_mod.inbred"),
				new Pair[][] {
						{
								Pair.of(Attributes.SCALE, new AttributeModifier(modLoc, -0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.MAX_HEALTH, new AttributeModifier(modLoc, -0.4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.MOVEMENT_SPEED, new AttributeModifier(modLoc, -0.3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.ATTACK_SPEED, new AttributeModifier(modLoc, -0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.LUCK, new AttributeModifier(modLoc, -1, AttributeModifier.Operation.ADD_VALUE))
						}
				}));
		modLoc = ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "ent_mod.nimble");
		registerEffect(new AttributeEntityModifierType("nimble", Component.translatable("ent_mod.nimble"),
				new Pair[][] {
						{
								Pair.of(Attributes.MOVEMENT_SPEED, new AttributeModifier(modLoc, 0.4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.ATTACK_SPEED, new AttributeModifier(modLoc, 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.JUMP_STRENGTH, new AttributeModifier(modLoc, 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.SAFE_FALL_DISTANCE, new AttributeModifier(modLoc, 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
						},
						{
								Pair.of(Attributes.MOVEMENT_SPEED, new AttributeModifier(modLoc, 0.8, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.ATTACK_SPEED, new AttributeModifier(modLoc, 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.JUMP_STRENGTH, new AttributeModifier(modLoc, 1.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
								Pair.of(Attributes.SAFE_FALL_DISTANCE, new AttributeModifier(modLoc, 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
						}
				}));
	}
}
