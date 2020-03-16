package com.Da_Technomancer.crossroads.API.technomancy;

import com.Da_Technomancer.crossroads.API.effects.goggles.*;
import com.Da_Technomancer.crossroads.Keys;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.crafting.CRItemTags;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Locale;
import java.util.function.Predicate;

public enum EnumGoggleLenses{
	
	//Don't reorder these unless you want to rename all the goggle texture files.
	RUBY(Ingredient.fromTag(CRItemTags.GEMS_RUBY), "_ruby", new RubyGoggleEffect(), Keys.controlEnergy, true),
	EMERALD(Ingredient.fromTag(Tags.Items.GEMS_EMERALD), "_emerald", new EmeraldGoggleEffect(), Keys.controlPotential, true),
	DIAMOND(Ingredient.fromTag(Tags.Items.GEMS_DIAMOND), "_diamond", new DiamondGoggleEffect(), Keys.controlStability, false),
	QUARTZ(Ingredient.fromItems(CRItems.pureQuartz), "_quartz", new QuartzGoggleEffect(), null, false),
	VOID(Ingredient.fromItems(OreSetup.voidCrystal), "", new VoidGoggleEffect(), Keys.controlVoid, true);
	
	private final Predicate<ItemStack> item;
	private final String texturePath;
	private final IGoggleEffect effect;
	private final KeyBinding key;
	private final boolean requireEnable;

	EnumGoggleLenses(Predicate<ItemStack> item, String texturePath, IGoggleEffect effect, @Nullable KeyBinding toggleKey, boolean requireEnable){
		this.item = item;
		this.texturePath = texturePath;
		this.effect = effect;
		this.key = toggleKey;
		this.requireEnable = requireEnable;
	}

	public boolean matchesRecipe(ItemStack stack){
		return item.test(stack);
	}
	
	public String getTexturePath(){
		return texturePath;
	}

	@Nullable
	public KeyBinding getKey(){
		return key;
	}

	public boolean useKey(){
		return requireEnable;
	}
	
	/**
	 * Call on the server side ONLY.
	 */
	public void doEffect(World world, PlayerEntity player, ArrayList<ITextComponent> chat, BlockRayTraceResult ray){
		effect.armorTick(world, player, chat, ray);
	}

	@Override
	public String toString(){
		return name().toLowerCase(Locale.US);
	}
}
