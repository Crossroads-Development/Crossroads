package com.Da_Technomancer.crossroads.API.technomancy;

import com.Da_Technomancer.crossroads.API.effects.goggles.*;
import com.Da_Technomancer.crossroads.Keys;
import com.Da_Technomancer.crossroads.crafting.CRItemTags;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.IForgeKeyMapping;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Locale;
import java.util.function.Supplier;

public enum EnumGoggleLenses{
	
	//Don't reorder these unless you want to rename all the goggle texture files.
	RUBY(CRItemTags.GEMS_RUBY, "_ruby", new RubyGoggleEffect(), () -> Keys.controlEnergy, true),
	EMERALD(Tags.Items.GEMS_EMERALD, "_emerald", new EmeraldGoggleEffect(), () -> Keys.controlPotential, true),
	DIAMOND(Tags.Items.GEMS_DIAMOND, "_diamond", IGoggleEffect.EMPTY, () -> Keys.controlStability, false),//Effect in SendGoggleConfigureToServer
	QUARTZ(CRItemTags.GEMS_PURE_QUARTZ, "_quartz", new QuartzGoggleEffect(), null, false),
	AMETHYST(Tags.Items.GEMS_AMETHYST, "", IGoggleEffect.EMPTY, () -> Keys.controlZoom, true),//Empty effect, the actual effect is done through EventHandlerClient::viewZoom
	VOID(CRItemTags.GEMS_VOID, "", IGoggleEffect.EMPTY, () -> Keys.controlVoid, true);//Empty effect, the actual effect is done through EventHandlers that check for the void lens.
	
	private final TagKey<Item> item;
	private final String texturePath;
	private final IGoggleEffect effect;
	//This is a supplier to allow lazy-loading the keys, which may not be registered at initialization time
	@Nullable
	private final Supplier<IForgeKeyMapping> key;
	private final boolean requireEnable;

	EnumGoggleLenses(TagKey<Item> item, String texturePath, IGoggleEffect effect, @Nullable Supplier<IForgeKeyMapping> toggleKey, boolean requireEnable){
		this.item = item;
		this.texturePath = texturePath;
		this.effect = effect;
		this.key = toggleKey;
		this.requireEnable = requireEnable;
	}

	public boolean matchesRecipe(ItemStack stack){
		return CRItemTags.tagContains(item, stack.getItem());
	}
	
	public String getTexturePath(){
		return texturePath;
	}

	@Nullable
	@OnlyIn(Dist.CLIENT)
	public IForgeKeyMapping getKey(){
		if(key == null){
			return null;
		}
		return key.get();
	}

	public boolean useKey(){
		return requireEnable;
	}
	
	/**
	 * Call on the server side ONLY.
	 */
	public void doEffect(Level world, Player player, ArrayList<Component> chat, BlockHitResult ray){
		effect.armorTick(world, player, chat, ray);
	}

	@Override
	public String toString(){
		return name().toLowerCase(Locale.US);
	}
}
