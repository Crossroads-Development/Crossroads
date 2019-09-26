package com.Da_Technomancer.crossroads.API.technomancy;

import com.Da_Technomancer.crossroads.API.effects.goggles.*;
import com.Da_Technomancer.crossroads.Keys;
import com.Da_Technomancer.crossroads.items.CrossroadsItems;
import com.Da_Technomancer.crossroads.items.crafting.ItemRecipePredicate;
import com.Da_Technomancer.crossroads.items.crafting.TagCraftingStack;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.function.Predicate;

public enum EnumGoggleLenses{
	
	//Don't reorder these unless you want to rename all the goggle texture files.
	RUBY(new TagCraftingStack("gemRuby"), "_ruby", new RubyGoggleEffect(), Keys.controlEnergy, true),
	EMERALD(new TagCraftingStack("gemEmerald"), "_emerald", new EmeraldGoggleEffect(), Keys.controlPotential, true),
	DIAMOND(new TagCraftingStack("gemDiamond"), "_diamond", new DiamondGoggleEffect(), Keys.controlStability, false),
	QUARTZ(new ItemRecipePredicate(CrossroadsItems.pureQuartz, 0), "_quartz", new QuartzGoggleEffect(), null, false),
	VOID(new ItemRecipePredicate(OreSetup.voidCrystal, 0), "", new VoidGoggleEffect(), Keys.controlVoid, true);
	
	private final Predicate<ItemStack> item;
	private final String texturePath;
	private final IGoggleEffect effect;
	private final KeyBinding key;
	private final boolean showState;

	EnumGoggleLenses(Predicate<ItemStack> item, String texturePath, IGoggleEffect effect, @Nullable KeyBinding toggleKey, boolean showState){
		this.item = item;
		this.texturePath = texturePath;
		this.effect = effect;
		this.key = toggleKey;
		this.showState = showState;
	}

	public boolean matchesRecipe(ItemStack stack){
		return item.test(stack);
	}
	
	public String getTexturePath(){
		return texturePath;
	}

	public KeyBinding getKey(){
		return key;
	}

	public boolean shouldShowState(){
		return showState;
	}
	
	/**
	 * Call on the server side ONLY.
	 */
	public void doEffect(World world, PlayerEntity player, ArrayList<ITextComponent> chat, BlockRayTraceResult ray){
		effect.armorTick(world, player, chat, ray);
	}
	
	/**This will return the name with all but the first char being lowercase,
	 * so COPPER becomes Copper, which is good for oreDict and registry
	 */
	@Override
	public String toString(){
		String name = name();
		char char1 = name.charAt(0);
		name = name.substring(1);
		name = name.toLowerCase();
		name = char1 + name;
		return name;
	}
}
