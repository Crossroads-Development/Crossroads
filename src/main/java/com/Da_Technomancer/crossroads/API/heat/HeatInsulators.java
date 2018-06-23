package com.Da_Technomancer.crossroads.API.heat;

import com.Da_Technomancer.crossroads.API.effects.BlockEffect;
import com.Da_Technomancer.crossroads.API.effects.DirtEffect;
import com.Da_Technomancer.crossroads.API.effects.IEffect;
import com.Da_Technomancer.crossroads.API.effects.SlimeEffect;

import com.Da_Technomancer.crossroads.items.ModItems;
import net.minecraft.init.Blocks;

public enum HeatInsulators{

	WOOL(.25D, 300D, new BlockEffect(Blocks.FIRE.getDefaultState()), "wool"),
	SLIME(.2D, 500D, new SlimeEffect(), "slimeball"),
	DIRT(.5D, 42D, new DirtEffect(), "dirt"),
	ICE(.001D, 0D, new BlockEffect(Blocks.WATER.getDefaultState()), Blocks.PACKED_ICE),
	OBSIDIAN(.015D, 2_000D, new BlockEffect(Blocks.LAVA.getDefaultState()), "obsidian"),
	DENSUS(0, 10_000D, new BlockEffect(Blocks.LAVA.getDefaultState()), "gemDensus");

	private final double rate;
	private final double limit;
	private final IEffect effect;
	private final Object item;

	HeatInsulators(double rate, double limit, IEffect effect, Object item){
		this.rate = rate;
		this.limit = limit;
		this.effect = effect;
		this.item = item;
	}

	public double getRate(){
		return rate;
	}

	public double getLimit(){
		return limit;
	}

	public IEffect getEffect(){
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
