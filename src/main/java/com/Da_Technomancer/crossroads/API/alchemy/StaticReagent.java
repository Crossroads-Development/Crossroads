package com.Da_Technomancer.crossroads.API.alchemy;

import com.Da_Technomancer.crossroads.API.effects.alchemy.IAlchEffect;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.crafting.RecipePredicate;
import com.google.common.collect.ImmutableList;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class StaticReagent implements IReagent{

	private final double melting;
	private final double boiling;
	private final Predicate<ItemStack> isSolid;
	private final Supplier<ItemStack> solid;
	private final int containType;
	private final IAlchEffect effect;
	private final String name;
	private final Function<EnumMatterPhase, Color> color;

	/**
	 * @param name Material id
	 * @param meltingPoint Melting temperature. Must be lower than boilingPoint. 
	 * @param boilingPoint Boiling temperature. Must be higher than meltingPoint.
	 * @param color A function giving the color of this reagent based on phase. 
	 * @param isSolid A Predicate on whether something represents this in solid form
	 * @param solid The supplier giving item that represents this in solid form.
	 * @param containType 0: Normal; 1: Vanishes in glass; 2: Destroys glass.
	 * @param effect The effect this has when released. Null for none. 
	 */
	public StaticReagent(String name, double meltingPoint, double boilingPoint, Function<EnumMatterPhase, Color> color, @Nullable Predicate<ItemStack> isSolid, @Nullable Supplier<ItemStack> solid, int containType, @Nullable IAlchEffect effect){
		this.name = name;
		if(boilingPoint <= meltingPoint){
			throw Crossroads.logger.throwing(new IllegalArgumentException("Boiling point must be greater than melting point. Material Type: " + name));
		}
		melting = meltingPoint;
		boiling = boilingPoint;
		this.isSolid = isSolid;
		this.solid = solid;
		if(isSolid != null){
			AlchemyCore.ITEM_TO_REAGENT.put(isSolid, this);
		}
		this.containType = containType;
		this.effect = effect;
		this.color = color;
	}

	@Override
	public String getId(){
		return name;
	}

	@Override
	public double getMeltingPoint(){
		return melting;
	}

	@Override
	public double getBoilingPoint(){
		return boiling;
	}

	/**
	 * @param reag The reagent (assumes phase is SOLID)
	 * @return The matching solid ItemStack. ItemStack.EMPTY if there either isn't enough material (or cannot be solidifed for any other reason). 
	 */
	@Override
	public ItemStack getStackFromReagent(ReagentStack reag){
		ItemStack out = !reag.isEmpty() && solid != null && reag.getType() == this ? solid.get() : ItemStack.EMPTY;
		out.setCount(reag.getAmount());
		return out;
	}

	@Override
	public List<ItemStack> getJEISolids(){
		if(isSolid instanceof RecipePredicate){
			return ((RecipePredicate<ItemStack>) isSolid).getMatchingList();
		}
		return solid == null ? ImmutableList.of() : ImmutableList.of(solid.get());
	}

	@Override
	public boolean canGlassContain(){
		return (containType & 3) == 0;
	}

	@Override
	public boolean destroysBadContainer(){
		return containType == 2;
	}

	@Nullable
	@Override
	public IAlchEffect getEffect(EnumMatterPhase phase){
		return effect;
	}

	@Override
	public Color getColor(EnumMatterPhase phase){
		return color.apply(phase);
	}
}
