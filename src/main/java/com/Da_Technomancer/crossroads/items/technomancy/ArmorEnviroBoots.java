package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.ReplaceDisk;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.NeoForgeMod;

import java.util.List;
import java.util.Optional;

public class ArmorEnviroBoots extends TechnomancyArmor{

//	private static final UUID SOUL_SPEED_BOOT_ID = UUID.fromString("87f46a96-686f-4796-b035-22e16ee9e038");//Duplicate of LivingEntity::SOUL_SPEED_BOOT_ID

	private static final ReplaceDisk FROST_WALKER_EFFECT = new ReplaceDisk(
			new LevelBasedValue.Clamped(LevelBasedValue.perLevel(3.0F, 1.0F), 0.0F, 16.0F),
			LevelBasedValue.constant(1.0F),
			new Vec3i(0, -1, 0),
			Optional.of(
					BlockPredicate.allOf(
							BlockPredicate.matchesTag(new Vec3i(0, 1, 0), BlockTags.AIR),
							BlockPredicate.matchesBlocks(Blocks.WATER),
							BlockPredicate.matchesFluids(Fluids.WATER),
							BlockPredicate.unobstructed()
					)
			),
			BlockStateProvider.simple(Blocks.FROSTED_ICE),
			Optional.of(GameEvent.BLOCK_PLACE)
	);

	public ArmorEnviroBoots(boolean reinforced){
		super(Type.BOOTS, reinforced);
		String name = reinforced ? "enviro_boots_reinforced" : "enviro_boots";
		CRItems.queueForRegister(name, this);
	}

	//Feather falling: Done via damage protection in EventHandlerCommon
	//Depth strider: Done via swim speed attribute
	//Frost walker: Done via onArmorTick() and damage prevention in EventHandlerCommon
	//Soul speed: Done via adding the enchantment effect (not literally enchanting the item)

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag){
		super.appendHoverText(stack, context, tooltip, flag);
		tooltip.add(Component.translatable("tt.crossroads.enviro_boots.desc"));
		tooltip.add(Component.translatable("tt.crossroads.enviro_boots.frost"));
		tooltip.add(Component.translatable("tt.crossroads.enviro_boots.quip").setStyle(MiscUtil.TT_QUIP));
	}

	private ItemAttributeModifiers expandedAttributeModifiers = null;//Used to add swim speed to the armor modifiers

	@Override
	public ItemAttributeModifiers getDefaultAttributeModifiers(){
		if(expandedAttributeModifiers == null){
			expandedAttributeModifiers = super.getDefaultAttributeModifiers().withModifierAdded(NeoForgeMod.SWIM_SPEED, new AttributeModifier(ResourceLocation.withDefaultNamespace("enchantment.depth_strider"), CRConfig.enviroBootDepth.get(), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.FEET);
		}
		return expandedAttributeModifiers;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotIndex, boolean isSelected){
		if(entity instanceof Player player && player.getItemBySlot(EquipmentSlot.FEET) == stack){
			//Activate frost walker when sneaking
			int frostWalkLevel = CRConfig.enviroBootFrostWalk.get();
			if(player.isShiftKeyDown() && level instanceof ServerLevel sLevel && frostWalkLevel > 0){
				FROST_WALKER_EFFECT.apply(sLevel, frostWalkLevel, null, player, player.position());
			}

//			//Soul speed
//			BlockState belowState = getStateBelow(player);
//			if(!belowState.isAir()){
//				int speedLevel = CRConfig.enviroBootSoulSpeed.get();
//				if(speedLevel > 0 && !player.getAbilities().flying && CraftingUtil.tagContains(BlockTags.SOUL_SPEED_BLOCKS, belowState.getBlock())){//Re-implementation of player.onSoulSpeedBlock()
//					AttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
//					if(speedAttribute != null && speedAttribute.getModifier(SOUL_SPEED_BOOT_ID) == null){
//						speedAttribute.addTransientModifier(new AttributeModifier(SOUL_SPEED_BOOT_ID, "Soul speed boost", (double) (0.03F * (1.0F + (float) speedLevel * 0.35F)), AttributeModifier.Operation.ADDITION));
//					}
//				}
//			}
		}
	}

	@Override
	public ItemEnchantments getAllEnchantments(ItemStack stack, HolderLookup.RegistryLookup<Enchantment> lookup){
		//TODO test
		int soulSpeedConfig = CRConfig.enviroBootSoulSpeed.get();
		if(soulSpeedConfig > 0){
			ItemEnchantments.Mutable enchants = new ItemEnchantments.Mutable(super.getAllEnchantments(stack, lookup));
			//TODO replace the MiscUtil.lookupResourceKey call with a direct reference to the enchantments registry once it gets added to BuiltInRegistries
			enchants.upgrade(new Holder.Direct<>(MiscUtil.lookupResourceKey(Enchantments.SOUL_SPEED)), soulSpeedConfig);
			return enchants.toImmutable();
		}
		return super.getAllEnchantments(stack, lookup);
	}

//	private static BlockState getStateBelow(LivingEntity entity){
//		//Re-implementation of Entity::getStateBelow
//		BlockPos downPos;
//		Vec3 entityPositionVec = entity.position();
//		int i = Mth.floor(entityPositionVec.x);
//		int j = Mth.floor(entityPositionVec.y - (double) 0.2F);
//		int k = Mth.floor(entityPositionVec.z);
//		BlockPos blockpos = new BlockPos(i, kpos)){
//			BlockPos blockpos1 = blockpos.below();
//			BlockState blockstate = entity.level().getBlockState(blockpos1);
//			if(blockstate.collisionExtendsVertically(entity.level(), blockpos1, entity)){
//				downPos = blockpos1;
//			}else{
//				downPos = blockpos;
//			}
//		}else{
//			downPos = blockpos;j, k);
//			if(entity.level().isEmptyBlock(bloc
//		}
//
//		return entity.level().getBlockState(downPos);
//	}

	@Override
	public boolean canWalkOnPowderedSnow(ItemStack stack, LivingEntity wearer){
		return true;
	}
}
