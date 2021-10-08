package com.Da_Technomancer.crossroads.API.technomancy;

import com.Da_Technomancer.crossroads.API.effects.goggles.*;
import com.Da_Technomancer.crossroads.Keys;
import com.Da_Technomancer.crossroads.crafting.CRItemTags;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.Tag;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.IForgeKeybinding;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Locale;
import java.util.function.Supplier;

public enum EnumGoggleLenses{
	
	//Don't reorder these unless you want to rename all the goggle texture files.
	RUBY(CRItemTags.GEMS_RUBY, "_ruby", new RubyGoggleEffect(), () -> Keys.controlEnergy, true),
	EMERALD(Tags.Items.GEMS_EMERALD, "_emerald", new EmeraldGoggleEffect(), () -> Keys.controlPotential, true),
	DIAMOND(Tags.Items.GEMS_DIAMOND, "_diamond", new DiamondGoggleEffect(), () -> Keys.controlStability, false),
	QUARTZ(CRItemTags.GEMS_PURE_QUARTZ, "_quartz", new QuartzGoggleEffect(), null, false),
	VOID(CRItemTags.GEMS_VOID, "", new VoidGoggleEffect(), () -> Keys.controlVoid, true);
	
	private final Tag<Item> item;
	private final String texturePath;
	private final IGoggleEffect effect;
	//This is a supplier to allow lazy-loading the keys, which may not be registered at initialization time
	@Nullable
	private final Supplier<IForgeKeybinding> key;
	private final boolean requireEnable;

	EnumGoggleLenses(Tag<Item> item, String texturePath, IGoggleEffect effect, @Nullable Supplier<IForgeKeybinding> toggleKey, boolean requireEnable){
		this.item = item;
		this.texturePath = texturePath;
		this.effect = effect;
		this.key = toggleKey;
		this.requireEnable = requireEnable;
	}

	public boolean matchesRecipe(ItemStack stack){
		return item.contains(stack.getItem());
	}
	
	public String getTexturePath(){
		return texturePath;
	}

	@Nullable
	@OnlyIn(Dist.CLIENT)
	public KeyMapping getKey(){
		if(key == null){
			return null;
		}
		IForgeKeybinding keybinding = key.get();
		if(keybinding == null){
			return null;
		}
		return keybinding.getKeyBinding();
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
