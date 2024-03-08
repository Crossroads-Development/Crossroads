package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.FrostWalkerEnchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ArmorEnviroBoots extends TechnomancyArmor{

	private static final UUID SOUL_SPEED_BOOT_ID = UUID.fromString("87f46a96-686f-4796-b035-22e16ee9e038");//Duplicate of LivingEntity::SOUL_SPEED_BOOT_ID
	private Multimap<Attribute, AttributeModifier> attributes = null;//Used for the swimming speed boost ('depth strider' like effect)
	private Multimap<Attribute, AttributeModifier> attributesReinf = null;

	public ArmorEnviroBoots(){
		super(Type.BOOTS);
		String name = "enviro_boots";
		CRItems.queueForRegister(name, this);
	}

	//Feather falling: Done via damage protection in EventHandlerCommon
	//Depth strider: Done via swim speed attribute
	//Frost walker: Done via onArmorTick() and damage prevention in EventHandlerCommon
	//Soul speed: Done via onArmorTick()

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		tooltip.add(Component.translatable("tt.crossroads.enviro_boots.desc"));
		tooltip.add(Component.translatable("tt.crossroads.enviro_boots.frost"));
		tooltip.add(Component.translatable("tt.crossroads.enviro_boots.quip").setStyle(MiscUtil.TT_QUIP));
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack){
		if(slot == EquipmentSlot.FEET){
			if(isReinforced(stack)){
				if(attributesReinf == null){
					//We have to lazy-load this, as ForgeMod.SWIM_SPEED isn't populated at construct time
					ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
					builder.putAll(super.getAttributeModifiers(EquipmentSlot.FEET, stack));
					builder.put(ForgeMod.SWIM_SPEED.get(), new AttributeModifier("depth_strider", CRConfig.enviroBootDepth.get(), AttributeModifier.Operation.ADDITION));
					attributesReinf = builder.build();
				}
				return attributesReinf;
			}else{
				if(attributes == null){
					//We have to lazy-load this, as ForgeMod.SWIM_SPEED isn't populated at construct time
					ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
					builder.putAll(super.getAttributeModifiers(EquipmentSlot.FEET, stack));
					builder.put(ForgeMod.SWIM_SPEED.get(), new AttributeModifier("depth_strider", CRConfig.enviroBootDepth.get(), AttributeModifier.Operation.ADDITION));
					attributes = builder.build();
				}
				return attributes;
			}
		}
		return super.getAttributeModifiers(slot, stack);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotIndex, boolean isSelected){
		if(entity instanceof Player player && player.getItemBySlot(EquipmentSlot.FEET) == stack){
			//Activate frost walker when sneaking
			int frostWalkLevel = CRConfig.enviroBootFrostWalk.get();
			if(player.isShiftKeyDown() && !level.isClientSide && frostWalkLevel > 0){
				FrostWalkerEnchantment.onEntityMoved(player, level, player.blockPosition(), frostWalkLevel);
			}

			//Soul speed
			BlockState belowState = getStateBelow(player);
			if(!belowState.isAir()){
				int speedLevel = CRConfig.enviroBootSoulSpeed.get();
				if(speedLevel > 0 && !player.getAbilities().flying && CraftingUtil.tagContains(BlockTags.SOUL_SPEED_BLOCKS, belowState.getBlock())){//Re-implementation of player.onSoulSpeedBlock()
					AttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
					if(speedAttribute != null && speedAttribute.getModifier(SOUL_SPEED_BOOT_ID) == null){
						speedAttribute.addTransientModifier(new AttributeModifier(SOUL_SPEED_BOOT_ID, "Soul speed boost", (double) (0.03F * (1.0F + (float) speedLevel * 0.35F)), AttributeModifier.Operation.ADDITION));
					}
				}
			}
		}
	}

	private static BlockState getStateBelow(LivingEntity entity){
		//Re-implementation of Entity::getStateBelow
		BlockPos downPos;
		Vec3 entityPositionVec = entity.position();
		int i = Mth.floor(entityPositionVec.x);
		int j = Mth.floor(entityPositionVec.y - (double) 0.2F);
		int k = Mth.floor(entityPositionVec.z);
		BlockPos blockpos = new BlockPos(i, j, k);
		if(entity.level().isEmptyBlock(blockpos)){
			BlockPos blockpos1 = blockpos.below();
			BlockState blockstate = entity.level().getBlockState(blockpos1);
			if(blockstate.collisionExtendsVertically(entity.level(), blockpos1, entity)){
				downPos = blockpos1;
			}else{
				downPos = blockpos;
			}
		}else{
			downPos = blockpos;
		}

		return entity.level().getBlockState(downPos);
	}

	@Override
	public boolean canWalkOnPowderedSnow(ItemStack stack, LivingEntity wearer){
		return true;
	}
}
