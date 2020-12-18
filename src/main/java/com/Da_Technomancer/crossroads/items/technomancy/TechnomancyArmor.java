package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public abstract class TechnomancyArmor extends ArmorItem{

	private static final IArmorMaterial TECHNOMANCY_MAT = new TechnoMat();
	private static final IArmorMaterial TECHNOMANCY_REINFORCED_MAT = new TechnoMatReinforced();
	//Duplicate of the private field in ArmorItem
	protected static final UUID[] ARMOR_MODIFIERS = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};

	protected final Multimap<Attribute, AttributeModifier> reinforcedProperties;

	public TechnomancyArmor(EquipmentSlotType slot){
		super(TECHNOMANCY_MAT, slot, new Properties().group(CRItems.TAB_CROSSROADS).maxStackSize(1));

		//Prepare reinforced properties map
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		UUID uuid = ARMOR_MODIFIERS[slot.getIndex()];
		builder.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", TECHNOMANCY_REINFORCED_MAT.getDamageReductionAmount(slot), AttributeModifier.Operation.ADDITION));
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

	public static boolean hasDurability(ItemStack stack){
		return stack.getDamage() < stack.getMaxDamage() - 1;
	}

	@Override
	public boolean isDamageable(ItemStack stack){
		return isReinforced(stack) && hasDurability(stack);
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack){
		return isReinforced(stack) && isDamaged(stack);
	}

	@Override
	public boolean isRepairable(ItemStack stack){
		return isReinforced(stack);
	}

	@Override
	public boolean isEnchantable(ItemStack stack){
		return true;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack){
		Multimap<Attribute, AttributeModifier> baseMap = super.getAttributeModifiers(slot, stack);//The un-reinforced version with no protection
		if(this.slot == slot && isReinforced(stack) && hasDurability(stack)){
			return reinforcedProperties;
		}
		return baseMap;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		if(isReinforced(stack)){
			tooltip.add(new TranslationTextComponent("tt.crossroads.technomancy_armor.reinforced").setStyle(Style.EMPTY.applyFormatting(TextFormatting.DARK_RED)));
		}
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items){
		if(isInGroup(group)){
			items.add(new ItemStack(this, 1));
			items.add(setReinforced(new ItemStack(this, 1), true));
		}
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type){
		//Switch to reinforced texture as applicable
		if(isReinforced(stack)){
			return slot == EquipmentSlotType.LEGS ? Crossroads.MODID + ":textures/models/armor/technomancy_reinforced_layer_2.png" : Crossroads.MODID + ":textures/models/armor/technomancy_reinforced_layer_1.png";
		}
		return slot == EquipmentSlotType.LEGS ? Crossroads.MODID + ":textures/models/armor/technomancy_layer_2.png" : Crossroads.MODID + ":textures/models/armor/technomancy_layer_1.png";
	}

	private static class TechnoMat implements IArmorMaterial{

		@Override
		public int getDurability(EquipmentSlotType slotIn){
			return ArmorMaterial.NETHERITE.getDurability(slotIn);
		}

		@Override
		public int getDamageReductionAmount(EquipmentSlotType slotIn){
			return 0;
		}

		@Override
		public int getEnchantability(){
			return ArmorMaterial.NETHERITE.getEnchantability();
		}

		@Override
		public SoundEvent getSoundEvent(){
			return SoundEvents.ITEM_ARMOR_EQUIP_IRON;
		}

		@Override
		public Ingredient getRepairMaterial(){
			return Ingredient.fromItems(Items.NETHERITE_INGOT);
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

	private static class TechnoMatReinforced implements IArmorMaterial{

		@Override
		public int getDurability(EquipmentSlotType slotIn){
			return ArmorMaterial.NETHERITE.getDurability(slotIn);
		}

		@Override
		public int getDamageReductionAmount(EquipmentSlotType slotIn){
			return ArmorMaterial.NETHERITE.getDamageReductionAmount(slotIn);
		}

		@Override
		public int getEnchantability(){
			return ArmorMaterial.NETHERITE.getEnchantability();
		}

		@Override
		public SoundEvent getSoundEvent(){
			return ArmorMaterial.IRON.getSoundEvent();
		}

		@Override
		public Ingredient getRepairMaterial(){
			return Ingredient.fromItems(Items.NETHERITE_INGOT);
		}

		@Override
		public String getName(){
			return "technomancy";
		}

		@Override
		public float getToughness(){
			return ArmorMaterial.NETHERITE.getToughness();
		}

		@Override
		public float getKnockbackResistance(){
			return ArmorMaterial.NETHERITE.getKnockbackResistance();
		}
	}
}
