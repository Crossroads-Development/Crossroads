package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.ambient.sounds.CRSounds;
import com.Da_Technomancer.crossroads.api.AdvancementTracker;
import com.Da_Technomancer.crossroads.api.EnumPath;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class PathSigil extends Item{

	private final EnumPath path;

	protected PathSigil(EnumPath path){
		super(new Properties());
		this.path = path;
		String name = "sigil_" + path.toString();
		CRItems.queueForRegister(name, this);
	}

	public EnumPath getPath(){
		return path;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag){
		if(stack.has(CRItems.SIGIL_RECIPE_DATA)){
			DetailedCrafterRecipeReference recipe = stack.get(CRItems.SIGIL_RECIPE_DATA);
			if(recipe.craftedItem != null){
				tooltip.add(Component.translatable("tt.crossroads.path_sigil.item").append(MiscUtil.asMutable(recipe.craftedItem.getDescription()).withStyle(MiscUtil.TT_DYNAMIC)));
			}else{
				tooltip.add(Component.translatable("tt.crossroads.path_sigil.item").append(Component.literal(recipe.craftedItemID.toString()).withStyle(MiscUtil.TT_DYNAMIC)));
			}
			tooltip.add(Component.translatable("tt.crossroads.path_sigil.desc.forget"));
		}
		tooltip.add(Component.translatable("tt.crossroads.path_sigil.desc"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand){
		if(playerIn == null){
			return super.use(worldIn, playerIn, hand);
		}
		if(worldIn.isClientSide){
			AdvancementTracker.listen();
		}

		ItemStack held = playerIn.getItemInHand(hand);

		if(held.has(CRItems.SIGIL_RECIPE_DATA)){
			if(getPath().isUnlocked(playerIn)){
				held.remove(CRItems.SIGIL_RECIPE_DATA);
				CRSounds.playSoundClientLocal(worldIn, playerIn.blockPosition().above(), SoundEvents.SPYGLASS_USE, SoundSource.PLAYERS, 1, 1);
				return InteractionResultHolder.success(held);
			}else{
				CRSounds.playSoundClientLocal(worldIn, playerIn.blockPosition().above(), SoundEvents.ANVIL_DESTROY, SoundSource.PLAYERS, 1, 1);
				if(worldIn.isClientSide){
					MiscUtil.displayMessage(playerIn, Component.translatable("tt.crossroads.path_sigil.fail"));
				}
				return InteractionResultHolder.fail(held);
			}
		}
		return InteractionResultHolder.pass(held);
	}

	public static record DetailedCrafterRecipeReference(ResourceLocation craftedItemID, @Nullable Item craftedItem){

		public static final Codec<DetailedCrafterRecipeReference> CODEC = ResourceLocation.CODEC.xmap(DetailedCrafterRecipeReference::new, DetailedCrafterRecipeReference::craftedItemID);
		public static final StreamCodec<ByteBuf, DetailedCrafterRecipeReference> STREAM_CODEC = ResourceLocation.STREAM_CODEC.map(DetailedCrafterRecipeReference::new, DetailedCrafterRecipeReference::craftedItemID);

		public DetailedCrafterRecipeReference(ResourceLocation craftedItemID){
			this(craftedItemID, BuiltInRegistries.ITEM.get(craftedItemID));
		}
	}
}
