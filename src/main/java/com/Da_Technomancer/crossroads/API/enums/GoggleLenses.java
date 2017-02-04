package com.Da_Technomancer.crossroads.API.enums;

import java.util.ArrayList;

import com.Da_Technomancer.crossroads.API.technomancy.DiamondGoggleEffect;
import com.Da_Technomancer.crossroads.API.technomancy.EmeraldGoggleEffect;
import com.Da_Technomancer.crossroads.API.technomancy.IGoggleEffect;
import com.Da_Technomancer.crossroads.API.technomancy.QuartzGoggleEffect;
import com.Da_Technomancer.crossroads.API.technomancy.RubyGoggleEffect;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.crafting.CraftingStack;
import com.Da_Technomancer.crossroads.items.crafting.ICraftingStack;
import com.Da_Technomancer.crossroads.items.crafting.OreDictCraftingStack;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public enum GoggleLenses{
	
	
	
	//Don't reorder these unless you want to rename all the goggle texture files.
	RUBY(new OreDictCraftingStack("gemRuby", 1), "ruby", new RubyGoggleEffect()),
	EMERALD(new OreDictCraftingStack("gemEmerald", 1), "emerald", new EmeraldGoggleEffect()),
	DIAMOND(new OreDictCraftingStack("gemDiamond", 1), "diamond", new DiamondGoggleEffect()),
	QUARTZ(new CraftingStack(ModItems.pureQuartz, 1, 0), "quartz", new QuartzGoggleEffect());
	
	private final ICraftingStack item;
	private final String texturePath;
	private final IGoggleEffect effect;
	
	GoggleLenses(ICraftingStack item, String texturePath, IGoggleEffect effect){
		this.item = item;
		this.texturePath = texturePath;
		this.effect = effect;
	}

	public boolean matchesRecipe(ItemStack stack){
		return item.softMatch(stack);
	}
	
	public String getTexturePath(){
		return texturePath;
	}
	
	/**
	 * Call on the server side ONLY.
	 */
	public void doEffect(World world, EntityPlayer player, ArrayList<String> chat){
		effect.armorTick(world, player, chat);
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
