package com.Da_Technomancer.crossroads.API.alchemy;

import com.Da_Technomancer.crossroads.API.effects.alchemy.IAlchEffect;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.function.Function;

public class BaseElementReagent extends StaticReagent implements IElementReagent{

	private final BeamUnit alignment;
	
	public BaseElementReagent(String name, double meltingPoint, double boilingPoint, int index, Function<EnumMatterPhase, Color> color, Item solid, double itemQuantity, int containType, IAlchEffect effect, BeamUnit alignment){
		super(name, meltingPoint, boilingPoint, index, color, solid == null ? null : (stack) -> stack.getItem() == solid, solid == null ? null : () -> new ItemStack(solid), itemQuantity, containType, effect);
		this.alignment = alignment;
		AlchemyCore.ELEMENTAL_REAGS.add(this);
	}

	@Override
	public BeamUnit getAlignment(){
		return alignment;
	}

	@Override
	public byte getLevel(){
		return 0;
	}

	@Override
	public IElementReagent getSecondaryBase(){
		return null;
	}
}
