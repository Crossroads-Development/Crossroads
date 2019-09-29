package com.Da_Technomancer.crossroads.API.alchemy;

import com.Da_Technomancer.crossroads.API.effects.alchemy.IAlchEffect;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.crafting.CRItemTags;
import com.google.common.collect.ImmutableList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.Tag;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class StaticReagent implements IReagent{

	private final double melting;
	private final double boiling;
	private final int containType;
	private final IAlchEffect effect;
	private final String name;
	private final Function<EnumMatterPhase, Color> color;
	private final Tag<Item> itemTag;
//	private final Predicate<Item> isSolid;
	private final Supplier<Item> solid;

	/**
	 * @param name Material id
	 * @param meltingPoint Melting temperature. Must be lower than boilingPoint.
	 * @param boilingPoint Boiling temperature. Must be higher than meltingPoint.
	 * @param color A function giving the color of this reagent based on phase.
	 * @param tag An item tag defining all items considered this reagent in solid form.
	 * @param containType 0: Normal; 1: Vanishes in glass; 2: Destroys glass.
	 * @param effect The effect this has when released. Null for none.
	 */
	public StaticReagent(String name, double meltingPoint, double boilingPoint, Function<EnumMatterPhase, Color> color, @Nullable Tag<Item> tag, int containType, @Nullable IAlchEffect effect){
		this.name = name;
		if(boilingPoint <= meltingPoint){
			throw Crossroads.logger.throwing(new IllegalArgumentException("Boiling point must be greater than melting point. Material Type: " + name));
		}
		melting = meltingPoint;
		boiling = boilingPoint;
//		this.isSolid = tag == null ? null : tag::contains;
		this.solid = null;
		if(tag != null){
			AlchemyCore.ITEM_TO_REAGENT.put(tag::contains, this);
		}
		this.containType = containType;
		this.effect = effect;
		this.color = color;
		this.itemTag = tag;
	}

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
	@Deprecated
	public StaticReagent(String name, double meltingPoint, double boilingPoint, Function<EnumMatterPhase, Color> color, @Nullable Predicate<Item> isSolid, @Nullable Supplier<Item> solid, int containType, @Nullable IAlchEffect effect){
		this.name = name;
		if(boilingPoint <= meltingPoint){
			throw Crossroads.logger.throwing(new IllegalArgumentException("Boiling point must be greater than melting point. Material Type: " + name));
		}
		melting = meltingPoint;
		boiling = boilingPoint;
//		this.isSolid = isSolid;
		this.solid = solid;
		if(isSolid != null){
			AlchemyCore.ITEM_TO_REAGENT.put(isSolid, this);
		}
		this.containType = containType;
		this.effect = effect;
		this.color = color;
		this.itemTag = null;
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
		if(itemTag == null){
			ItemStack out = !reag.isEmpty() && solid != null && reag.getType() == this ? new ItemStack(solid.get(), 1) : ItemStack.EMPTY;
			out.setCount(reag.getAmount());
			return out;
		}else{
			ItemStack out = !reag.isEmpty() && reag.getType() == this ? new ItemStack(CRItemTags.getTagEntry(itemTag), 1) : ItemStack.EMPTY;
			out.setCount(reag.getAmount());
			return out;
		}
	}

	@Override
	public List<ItemStack> getJEISolids(){
		if(itemTag != null){
			return itemTag.getAllElements().stream().map(item -> new ItemStack(item, 1)).collect(Collectors.toList());
		}
		return solid == null ? ImmutableList.of() : ImmutableList.of(new ItemStack(solid.get(), 1));
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
