package com.Da_Technomancer.crossroads.API.alchemy;

import com.Da_Technomancer.crossroads.API.effects.alchemy.IAlchEffect;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.items.crafting.RecipePredicate;
import com.google.common.collect.ImmutableList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class StaticReagent implements IReagent{

	private final double melting;
	private final double boiling;
	private final double itemAmount;
	private final Predicate<ItemStack> isSolid;
	private final Supplier<ItemStack> solid;
	private final int containType;
	private final IAlchEffect effect;
	private final String name;
	private final Function<EnumMatterPhase, Color> color;
	private final int index;

	/**
	 * @param name Material name. 
	 * @param meltingPoint Melting temperature. Must be lower than boilingPoint. 
	 * @param boilingPoint Boiling temperature. Must be higher than meltingPoint. 
	 * @param index The index in the {@link AlchemyCore#REAGENTS} array.
	 * @param color A function giving the color of this reagent based on phase. 
	 */
	public StaticReagent(String name, double meltingPoint, double boilingPoint, int index, Function<EnumMatterPhase, Color> color){
		this(name, meltingPoint, boilingPoint, index, color, null, null, 0);
	}

	/**
	 * @param name Material name. 
	 * @param meltingPoint Melting temperature. Must be lower than boilingPoint. 
	 * @param boilingPoint Boiling temperature. Must be higher than meltingPoint. 
	 * @param index The index in the {@link AlchemyCore#REAGENTS} array.
	 * @param color A function giving the color of this reagent based on phase.
	 * @param isSolid A Predicate on whether something represents this in solid form
	 * @param solid The supplier giving item that represents this in solid form.
	 * @param itemQuantity The amount of reagent 1 item is equivalent to.
	 */
	public StaticReagent(String name, double meltingPoint, double boilingPoint, int index, Function<EnumMatterPhase, Color> color, @Nullable Predicate<ItemStack> isSolid, @Nullable Supplier<ItemStack> solid, int itemQuantity){
		this(name, meltingPoint, boilingPoint, index, color, isSolid, solid, itemQuantity, 0, null);
	}

	/**
	 * @param name Material name. 
	 * @param meltingPoint Melting temperature. Must be lower than boilingPoint. 
	 * @param boilingPoint Boiling temperature. Must be higher than meltingPoint. 
	 * @param index The index in the {@link AlchemyCore#REAGENTS} array.
	 * @param color A function giving the color of this reagent based on phase. 
	 * @param isSolid A Predicate on whether something represents this in solid form
	 * @param solid The supplier giving item that represents this in solid form.
	 * @param itemQuantity The amount of reagent 1 item is equivalent to.
	 * @param containType 0: Normal; 1: Vanishes in glass; 2: Destroys glass.
	 * @param effect The effect this has when released. Null for none. 
	 */
	public StaticReagent(String name, double meltingPoint, double boilingPoint, int index, Function<EnumMatterPhase, Color> color, @Nullable Predicate<ItemStack> isSolid, @Nullable Supplier<ItemStack> solid, double itemQuantity, int containType, @Nullable IAlchEffect effect){
		this.name = name;
		if(boilingPoint <= meltingPoint){
			throw Main.logger.throwing(new IllegalArgumentException("Boiling point must be greater than melting point. Material Type: " + name));
		}
		melting = meltingPoint;
		boiling = boilingPoint;
		this.isSolid = isSolid;
		this.solid = solid;
		this.itemAmount = itemQuantity;
		if(isSolid != null){
			AlchemyCore.ITEM_TO_REAGENT.put(isSolid, this);
		}
		this.containType = containType;
		this.effect = effect;
		this.color = color;
		this.index = index;
	}

	@Override
	public String getName(){
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

	@Nullable
	@Override
	public ReagentStack getReagentFromStack(ItemStack stack){
		return isSolid != null && isSolid.test(stack) ? new ReagentStack(this, itemAmount) : null;
	}

	/**
	 * @param reag The reagent (assumes phase is SOLID)
	 * @return The matching solid ItemStack. ItemStack.EMPTY if there either isn't enough material (or cannot be solidifed for any other reason). 
	 */
	@Override
	public ItemStack getStackFromReagent(ReagentStack reag){
		ItemStack out = reag != null && solid != null && reag.getType() == this && reag.getAmount() >= itemAmount - AlchemyCore.MIN_QUANTITY ? solid.get() : ItemStack.EMPTY;
		out.setCount((int) ((reag.getAmount() + AlchemyCore.MIN_QUANTITY) / itemAmount));
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

	@Override
	public void onRelease(World world, BlockPos pos, double amount, double temp, EnumMatterPhase phase, ReagentStack[] contents){
		if(effect != null){
			effect.doEffectAdv(world, pos, amount, temp, phase, contents);
		}
	}

	@Override
	public Color getColor(EnumMatterPhase phase){
		return color.apply(phase);
	}

	@Override
	public int getIndex(){
		return index;
	}
}
