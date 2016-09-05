package com.Da_Technomancer.crossroads.API.enums;

import com.Da_Technomancer.crossroads.API.effects.BlockEffect;
import com.Da_Technomancer.crossroads.API.effects.DirtEffect;
import com.Da_Technomancer.crossroads.API.effects.IEffect;
import com.Da_Technomancer.crossroads.API.effects.SlimeEffect;

import net.minecraft.init.Blocks;

public enum HeatInsulators{

	WOOL(.01D, 300, new BlockEffect(Blocks.FIRE.getDefaultState()), "wool"),
	SLIME(.005D, 500, new SlimeEffect(), "slimeball"),
	DIRT(.1D, 42, new DirtEffect(), "dirt"),
	ICE(.00005D, 0, new BlockEffect(Blocks.WATER.getDefaultState()), Blocks.ICE),
	OBSIDIAN(0.0001D, 2000, new BlockEffect(Blocks.LAVA.getDefaultState()), "obsidian");

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
