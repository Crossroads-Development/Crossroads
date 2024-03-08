package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.templates.ICreativeTabPopulatingItem;
import com.Da_Technomancer.crossroads.crafting.CRItemTags;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public abstract class TechnomancyArmor extends ArmorItem implements ICreativeTabPopulatingItem{

	private static final ArmorMaterial TECHNOMANCY_MAT = new TechnoMat();
	private static final ArmorMaterial TECHNOMANCY_REINFORCED_MAT = new TechnoMatReinforced();
	//Duplicate of the private field in ArmorItem
	protected static final UUID[] ARMOR_MODIFIERS = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
	protected final Multimap<Attribute, AttributeModifier> reinforcedProperties;
	protected final ArmorItem.Type armorType;

	public TechnomancyArmor(ArmorItem.Type type){
		super(TECHNOMANCY_MAT, type, new Properties().stacksTo(1).fireResistant());
		this.armorType = type;
		//Prepare reinforced properties map
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		UUID uuid = ARMOR_MODIFIERS[type.getSlot().getIndex()];
		builder.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", TECHNOMANCY_REINFORCED_MAT.getDefenseForType(type), AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", TECHNOMANCY_REINFORCED_MAT.getToughness(), AttributeModifier.Operation.ADDITION));
		if(TECHNOMANCY_REINFORCED_MAT.getKnockbackResistance() > 0){
			builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, "Armor knockback resistance", TECHNOMANCY_REINFORCED_MAT.getKnockbackResistance(), AttributeModifier.Operation.ADDITION));
		}
		reinforcedProperties = builder.build();
	}

	public static boolean isReinforced(ItemStack stack){
		return stack.getOrCreateTag().getBoolean("techno_reinforced");
	}

	public static ItemStack setReinforced(ItemStack stack, boolean reinforced){
		stack.getOrCreateTag().putBoolean("techno_reinforced", reinforced);
		return stack;
	}

	@Override
	public boolean isEnchantable(ItemStack stack){
		return true;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack){
		Multimap<Attribute, AttributeModifier> baseMap = super.getAttributeModifiers(slot, stack);//The un-reinforced version with no protection
		if(type.getSlot() == slot && isReinforced(stack)){
			return reinforcedProperties;
		}
		return baseMap;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		if(isReinforced(stack)){
			tooltip.add(Component.translatable("tt.crossroads.technomancy_armor.reinforced").setStyle(Style.EMPTY.applyFormat(ChatFormatting.DARK_RED)));
		}
	}

	@Override
	public ItemStack[] populateCreativeTab(){
		return new ItemStack[] {new ItemStack(this), setReinforced(new ItemStack(this), true)};
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type){
		//Switch to reinforced texture as applicable
		if(isReinforced(stack)){
			return slot == EquipmentSlot.LEGS ? Crossroads.MODID + ":textures/models/armor/technomancy_reinforced_layer_2.png" : Crossroads.MODID + ":textures/models/armor/technomancy_reinforced_layer_1.png";
		}
		return slot == EquipmentSlot.LEGS ? Crossroads.MODID + ":textures/models/armor/technomancy_layer_2.png" : Crossroads.MODID + ":textures/models/armor/technomancy_layer_1.png";
	}

	@Override
	public boolean isValidRepairItem(ItemStack stack, ItemStack repairStack){
		if(isReinforced(stack)){
			return TECHNOMANCY_REINFORCED_MAT.getRepairIngredient().test(repairStack);
		}else{
			return TECHNOMANCY_MAT.getRepairIngredient().test(repairStack);
		}
	}

	@Override
	public int getMaxDamage(ItemStack stack){
		if(isReinforced(stack)){
			return TECHNOMANCY_REINFORCED_MAT.getDurabilityForType(armorType);
		}else{
			return TECHNOMANCY_MAT.getDurabilityForType(armorType);
		}
	}

	private static class TechnoMat implements ArmorMaterial{

		@Override
		public int getDurabilityForType(Type type){
			return ArmorMaterials.DIAMOND.getDurabilityForType(type);
		}

		@Override
		public int getDefenseForType(Type type){
			return 0;
		}

		@Override
		public int getEnchantmentValue(){
			return ArmorMaterials.NETHERITE.getEnchantmentValue();
		}

		@Override
		public SoundEvent getEquipSound(){
			return SoundEvents.ARMOR_EQUIP_IRON;
		}

		@Override
		public Ingredient getRepairIngredient(){
			return Ingredient.of(CRItemTags.INGOTS_BRONZE);
		}

		@Override
		public String getName(){
			return "technomancy";
		}

		@Override
		public float getToughness(){
			return 0;
		}

		@Override
		public float getKnockbackResistance(){
			return 0;
		}
	}

	private static class TechnoMatReinforced implements ArmorMaterial{

		@Override
		public int getDurabilityForType(Type type){
			return ArmorMaterials.NETHERITE.getDurabilityForType(type);
		}

		@Override
		public int getDefenseForType(Type type){
			return ArmorMaterials.NETHERITE.getDefenseForType(type);
		}

		@Override
		public int getEnchantmentValue(){
			return ArmorMaterials.NETHERITE.getEnchantmentValue();
		}

		@Override
		public SoundEvent getEquipSound(){
			return ArmorMaterials.IRON.getEquipSound();
		}

		@Override
		public Ingredient getRepairIngredient(){
			return Ingredient.of(Items.NETHERITE_INGOT);
		}

		@Override
		public String getName(){
			return "technomancy";
		}

		@Override
		public float getToughness(){
			return ArmorMaterials.NETHERITE.getToughness();
		}

		@Override
		public float getKnockbackResistance(){
			return ArmorMaterials.NETHERITE.getKnockbackResistance();
		}
	}
}
