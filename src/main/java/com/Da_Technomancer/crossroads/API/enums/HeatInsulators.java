package com.Da_Technomancer.crossroads.API.enums;

import com.Da_Technomancer.crossroads.API.heat.overheatEffects.BurnEffect;
import com.Da_Technomancer.crossroads.API.heat.overheatEffects.DirtEffect;
import com.Da_Technomancer.crossroads.API.heat.overheatEffects.MeltEffect;
import com.Da_Technomancer.crossroads.API.heat.overheatEffects.MeltWaterEffect;
import com.Da_Technomancer.crossroads.API.heat.overheatEffects.OverheatEffect;
import com.Da_Technomancer.crossroads.API.heat.overheatEffects.SlimeEffect;

import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

public enum HeatInsulators{

	WOOL(.01D, 300, new ResourceLocation("blocks/wool_colored_white"), new BurnEffect(), Blocks.WOOL),
	SLIME(.005D, 500, new ResourceLocation("blocks/slime"), new SlimeEffect(), "slimeball"),
	DIRT(.1D, 42, new ResourceLocation("blocks/dirt"), new DirtEffect(), "dirt"),
	ICE(.00005D, 0, new ResourceLocation("blocks/ice_packed"), new MeltWaterEffect(), Blocks.ICE),
	OBSIDIAN(0.0001D, 2000, new ResourceLocation("blocks/obsidian"), new MeltEffect(), "obsidian");

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

	@Override
	public String toString(){
		// This will return the name with all but the first char being
		// lowercase,
		// so COPPER becomes Copper, which is good for oreDict and registry
		// names.
		String char1;
		String name = name();
		char1 = name.substring(0, 1);
		name = name.substring(1);
		name = name.toLowerCase();
		name = char1 + name;
		return name;
	}

}
