package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.FrostWalkerEnchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ArmorEnviroBoots extends TechnomancyArmor{

	private static final UUID SOUL_SPEED_BOOT_ID = UUID.fromString("87f46a96-686f-4796-b035-22e16ee9e038");//Duplicate of LivingEntity::SOUL_SPEED_BOOT_ID
	private Multimap<Attribute, AttributeModifier> attributes = null;//Used for the swimming speed boost ('depth strider' like effect)
	private Multimap<Attribute, AttributeModifier> attributesReinf = null;

	public ArmorEnviroBoots(){
		super(EquipmentSlotType.FEET);
		String name = "enviro_boots";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	//Feather falling: Done via damage protection in EventHandlerCommon
	//Depth strider: Done via swim speed attribute
	//Frost walker: Done via onArmorTick() and damage prevention in EventHandlerCommon
	//Soul speed: Done via onArmorTick()

	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		tooltip.add(new TranslationTextComponent("tt.crossroads.enviro_boots.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.enviro_boots.frost"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.enviro_boots.quip").setStyle(MiscUtil.TT_QUIP));
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack){
		if(slot == EquipmentSlotType.FEET){
			if(isReinforced(stack)){
				if(attributesReinf == null){
					//We have to lazy-load this, as ForgeMod.SWIM_SPEED isn't populated at construct time
					ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
					builder.putAll(super.getAttributeModifiers(EquipmentSlotType.FEET, stack));
					builder.put(ForgeMod.SWIM_SPEED.get(), new AttributeModifier("depth_strider", CRConfig.enviroBootDepth.get(), AttributeModifier.Operation.ADDITION));
					attributesReinf = builder.build();
				}
				return attributesReinf;
			}else{
				if(attributes == null){
					//We have to lazy-load this, as ForgeMod.SWIM_SPEED isn't populated at construct time
					ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
					builder.putAll(super.getAttributeModifiers(EquipmentSlotType.FEET, stack));
					builder.put(ForgeMod.SWIM_SPEED.get(), new AttributeModifier("depth_strider", CRConfig.enviroBootDepth.get(), AttributeModifier.Operation.ADDITION));
					attributes = builder.build();
				}
				return attributes;
			}
		}
		return super.getAttributeModifiers(slot, stack);
	}

	@Override
	public void onArmorTick(ItemStack stack, World world, PlayerEntity player){
		//Activate frost walker when sneaking
		int frostWalkLevel = CRConfig.enviroBootFrostWalk.get();
		if(player.isShiftKeyDown() && !world.isClientSide && frostWalkLevel > 0){
			FrostWalkerEnchantment.onEntityMoved(player, world, player.blockPosition(), frostWalkLevel);
		}

		//Soul speed
		BlockState belowState = getStateBelow(player);
		if(!belowState.isAir()){
			int level = CRConfig.enviroBootSoulSpeed.get();
			if(level > 0 && !player.abilities.flying && belowState.getBlock().is(BlockTags.SOUL_SPEED_BLOCKS)){//Re-implementation of player.onSoulSpeedBlock()
				ModifiableAttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
				if(speedAttribute != null && speedAttribute.getModifier(SOUL_SPEED_BOOT_ID) == null){
					speedAttribute.addTransientModifier(new AttributeModifier(SOUL_SPEED_BOOT_ID, "Soul speed boost", (double) (0.03F * (1.0F + (float) level * 0.35F)), AttributeModifier.Operation.ADDITION));
				}
			}
		}
	}

	private static BlockState getStateBelow(LivingEntity entity){
		//Re-implementation of Entity::getStateBelow
		BlockPos downPos;
		Vector3d entityPositionVec = entity.position();
		int i = MathHelper.floor(entityPositionVec.x);
		int j = MathHelper.floor(entityPositionVec.y - (double) 0.2F);
		int k = MathHelper.floor(entityPositionVec.z);
		BlockPos blockpos = new BlockPos(i, j, k);
		if(entity.level.isEmptyBlock(blockpos)){
			BlockPos blockpos1 = blockpos.below();
			BlockState blockstate = entity.level.getBlockState(blockpos1);
			if(blockstate.collisionExtendsVertically(entity.level, blockpos1, entity)){
				downPos = blockpos1;
			}else{
				downPos = blockpos;
			}
		}else{
			downPos = blockpos;
		}

		return entity.level.getBlockState(downPos);
	}
}
