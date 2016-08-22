package com.Da_Technomancer.crossroads.API.enums;

import com.Da_Technomancer.crossroads.API.heat.overheatEffects.BlockEffect;
import com.Da_Technomancer.crossroads.API.heat.overheatEffects.DirtEffect;
import com.Da_Technomancer.crossroads.API.heat.overheatEffects.OverheatEffect;
import com.Da_Technomancer.crossroads.API.heat.overheatEffects.SlimeEffect;

import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

public enum HeatInsulators{

	WOOL(.01D, 300, new ResourceLocation("blocks/wool_colored_white"), new BlockEffect(Blocks.FIRE.getDefaultState()), "wool"),
	SLIME(.005D, 500, new ResourceLocation("blocks/slime"), new SlimeEffect(), "slimeball"),
	DIRT(.1D, 42, new ResourceLocation("blocks/dirt"), new DirtEffect(), "dirt"),
	ICE(.00005D, 0, new ResourceLocation("blocks/ice_packed"), new BlockEffect(Blocks.WATER.getDefaultState()), Blocks.ICE),
	OBSIDIAN(0.0001D, 2000, new ResourceLocation("blocks/obsidian"), new BlockEffect(Blocks.LAVA.getDefaultState()), "obsidian");

	private final double rate;
	private final double limit;
	private final ResourceLocation resource;
	private final OverheatEffect effect;
	private final Object item;

	HeatInsulators(double rate, double limit, ResourceLocation resource, OverheatEffect effect, Object item){
		this.rate = rate;
		this.limit = limit;
		this.resource = resource;
		this.effect = effect;
		this.item = item;
	}

	public double getRate(){
		return rate;
	}

	public double getLimit(){
		return limit;
	}

	public ResourceLocation getResource(){
		return resource;
	}

	public OverheatEffect getEffect(){
		return effect;
	}

	public Object getItem(){
		return item;
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
