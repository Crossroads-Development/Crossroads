package com.Da_Technomancer.crossroads.API.alchemy;

import com.Da_Technomancer.crossroads.API.effects.alchemy.IAlchEffect;
import com.Da_Technomancer.crossroads.Main;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.function.Function;

public class StaticReagent implements IReagent{

	private final double melting;
	private final double boiling;
	private final double itemAmount;
	private final Item solid;
	private final int containType;
	private final IAlchEffect effect;
	private final String name;
	private final EnumSolventType solvent;
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
		this(name, meltingPoint, boilingPoint, index, color, null, 0);
	}

	/**
	 * @param name Material name. 
	 * @param meltingPoint Melting temperature. Must be lower than boilingPoint. 
	 * @param boilingPoint Boiling temperature. Must be higher than meltingPoint. 
	 * @param index The index in the {@link AlchemyCore#REAGENTS} array.
	 * @param color A function giving the color of this reagent based on phase. 
	 * @param solid The item that represents this in solid form. 
	 * @param itemQuantity The amount of reagent 1 item is equivalent to. 
	 */
	public StaticReagent(String name, double meltingPoint, double boilingPoint, int index, Function<EnumMatterPhase, Color> color, @Nullable Item solid, int itemQuantity){
		this(name, meltingPoint, boilingPoint, index, color, solid, itemQuantity, null);
	}

	/**
	 * @param name Material name. 
	 * @param meltingPoint Melting temperature. Must be lower than boilingPoint. 
	 * @param boilingPoint Boiling temperature. Must be higher than meltingPoint. 
	 * @param index The index in the {@link AlchemyCore#REAGENTS} array.
	 * @param color A function giving the color of this reagent based on phase. 
	 * @param solid The item that represents this in solid form. 
	 * @param itemQuantity The amount of reagent 1 item is equivalent to.
	 * @param solventType Sets the solvent type
	 */
	public StaticReagent(String name, double meltingPoint, double boilingPoint, int index, Function<EnumMatterPhase, Color> color, @Nullable Item solid, int itemQuantity, @Nullable EnumSolventType solventType){
		this(name, meltingPoint, boilingPoint, index, color, solid, itemQuantity, solventType, 0, null);
	}

	/**
	 * @param name Material name. 
	 * @param meltingPoint Melting temperature. Must be lower than boilingPoint. 
	 * @param boilingPoint Boiling temperature. Must be higher than meltingPoint. 
	 * @param index The index in the {@link AlchemyCore#REAGENTS} array.
	 * @param color A function giving the color of this reagent based on phase. 
	 * @param solid The item that represents this in solid form. 
	 * @param itemQuantity The amount of reagent 1 item is equivalent to.
	 * @param solventType Sets the solvent type
	 * @param containType 0: Normal; 1: Vanishes in glass; 2: Destroys glass. 
	 * @param effect The effect this has when released. Null for none. 
	 */
	public StaticReagent(String name, double meltingPoint, double boilingPoint, int index, Function<EnumMatterPhase, Color> color, @Nullable Item solid, double itemQuantity, @Nullable EnumSolventType solventType, int containType, @Nullable IAlchEffect effect){
		this.name = name;
		if(boilingPoint <= meltingPoint){
			throw Main.logger.throwing(new IllegalArgumentException("Boiling point must be greater than melting point. Material Type: " + name));
		}
		melting = meltingPoint;
		boiling = boilingPoint;
		this.solid = solid;
		this.itemAmount = itemQuantity;
		if(solid != null){
			AlchemyCore.ITEM_TO_REAGENT.put(solid, this);
		}
		this.containType = containType;
		this.effect = effect;
		this.solvent = solventType;
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
		return stack.getItem() == solid ? new ReagentStack(this, itemAmount) : null;
	}

	/**
	 * @param reag The reagent (assumes phase is SOLID)
	 * @return The matching solid ItemStack. ItemStack.EMPTY if there either isn't enough material (or cannot be solidifed for any other reason). 
	 */
	@Override
	public ItemStack getStackFromReagent(ReagentStack reag){
		return reag != null && reag.getType() == this && reag.getAmount() >= itemAmount - AlchemyCore.MIN_QUANTITY ? new ItemStack(solid, (int) ((reag.getAmount() + AlchemyCore.MIN_QUANTITY) / itemAmount)) : ItemStack.EMPTY;
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

	/**
	 * @return The type of solvent this acts as. 
	 */
	@Override
	@Nullable
	public EnumSolventType solventType(){
		return solvent;
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
