package com.Da_Technomancer.crossroads.items.witchcraft;

import com.Da_Technomancer.crossroads.api.witchcraft.EntityTemplate;
import com.Da_Technomancer.crossroads.api.witchcraft.IPerishable;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BloodSample extends Item implements IPerishable{

	private static final long LIFETIME = 20 * 60 * 60 * 2;
	private static final int FRESHNESS_BONUS = 25;

	public BloodSample(){
		this("blood_sample");
	}

	public BloodSample(String name){
		super(new Item.Properties().stacksTo(1));//Not added to any creative tab
		CRItems.queueForRegister(name, this, null);
	}

	public ItemStack withEntityData(ItemStack stack, LivingEntity source){
		stack.set(CRItems.GENETICS_DATA, EntityTemplate.getTemplateFromEntity(source).withQuality(0));
		stack.set(CRItems.ENTITY_SOURCE_DATA, EntitySourceData.create(source));
		return stack;
	}

	/**
	 * @see #getAdjustedTemplate
	 * @param stack Itemstack with the blood sample (or centrifuged blood sample)
	 * @return The template stored on the stack, before any adjustment for freshness
	 */
	public static EntityTemplate getBaseTemplate(ItemStack stack){
		return stack.getOrDefault(CRItems.GENETICS_DATA, EntityTemplate.DEFAULT);
	}

	public static EntityTemplate getAdjustedTemplate(ItemStack stack, Level world){
		//Blood samples have +20 quality for fresh samples
		//This isn't handled natively in the template, so we have to special-case anything reading the quality of a blood sample (or centrifuged blood sample)
		EntityTemplate template = getBaseTemplate(stack);
		if(!IPerishable.isSpoiled(stack, world)){
			template = template.withQuality(template.quality() + FRESHNESS_BONUS);
		}
		return template;
	}

	@Override
	public long getLifetime(){
		return LIFETIME;
	}

	@Override
	public double getFreezeTemperature(){
		return 0;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag){
		if(this != CRItems.separatedBloodSample){
			tooltip.add(Component.translatable("tt.crossroads.blood_sample.craft"));
		}
		EntityTemplate template = getAdjustedTemplate(stack, context.level());
		template.addTooltip(tooltip, context.level());
		IPerishable.addTooltip(stack, context.level(), tooltip);
	}

	@Override
	public void verifyComponentsAfterLoad(ItemStack pStack) {
		//Copied from PlayerHeadItem
		EntitySourceData prevData = pStack.get(CRItems.ENTITY_SOURCE_DATA);
		ResolvableProfile resolvableprofile;
		if(prevData != null && prevData.playerProfile.isPresent() && !(resolvableprofile = prevData.playerProfile.get()).isResolved()){
			resolvableprofile.resolve().thenAcceptAsync(fixedProfile -> pStack.set(CRItems.ENTITY_SOURCE_DATA, new EntitySourceData(prevData.baseUUID, Optional.of(fixedProfile))), SkullBlockEntity.CHECKED_MAIN_THREAD_EXECUTOR);
		}
	}

	public static record EntitySourceData(@Nonnull UUID baseUUID, @Nonnull Optional<ResolvableProfile> playerProfile){

		public static final Codec<EntitySourceData> CODEC = RecordCodecBuilder.create(instance -> instance.group(UUIDUtil.CODEC.fieldOf("base_uuid").forGetter(EntitySourceData::baseUUID), ResolvableProfile.CODEC.optionalFieldOf("player_profile").forGetter(EntitySourceData::playerProfile)).apply(instance, EntitySourceData::new));
		public static final StreamCodec<ByteBuf, EntitySourceData> STREAM_CODEC = StreamCodec.composite(UUIDUtil.STREAM_CODEC, EntitySourceData::baseUUID, ByteBufCodecs.optional(ResolvableProfile.STREAM_CODEC), EntitySourceData::playerProfile, EntitySourceData::new);

		public static EntitySourceData create(LivingEntity sourceEntity){
			if(sourceEntity instanceof Player player){
				//Also save the identity of the player it came from, if applicable
				return new EntitySourceData(sourceEntity.getUUID(), Optional.of(new ResolvableProfile(player.getGameProfile())));
			}
			return new EntitySourceData(sourceEntity.getUUID(), Optional.empty());
		}

		@Nonnull
		public UUID effectiveUUID(){
			return playerProfile.isPresent() ? playerProfile.get().id().orElse(baseUUID) : baseUUID;
		}
	}
}
