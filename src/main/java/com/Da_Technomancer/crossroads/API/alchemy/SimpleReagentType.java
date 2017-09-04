package com.Da_Technomancer.crossroads.API.alchemy;

import java.awt.Color;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.effects.alchemy.IAlchEffect;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SimpleReagentType implements IReagentType{

	private final double melting;
	private final double boiling;
	private final int catalType;
	private final int itemAmount;
	private final Item solid;
	private final int containType;
	private final IAlchEffect effect;
	private final String name;
	private final SolventType solvent;
	private final SolventType solute;
	private final Function<MatterPhase, Color> color;
	private final int index;

	/**
	 * @param name Material name. 
	 * @param meltingPoint Melting temperature. Must be lower than boilingPoint. 
	 * @param boilingPoint Boiling temperature. Must be higher than meltingPoint. 
	 * @param index The index in the {@link AlchemyCraftingManager#REAGENTS} array.
	 * @param color A function giving the color of this reagent based on phase. 
	 */
	public SimpleReagentType(String name, double meltingPoint, double boilingPoint, int index, Function<MatterPhase, Color> color){
		this(name, meltingPoint, boilingPoint, index, color, null, 0, false);
	}

	/**
	 * @param name Material name. 
	 * @param meltingPoint Melting temperature. Must be lower than boilingPoint. 
	 * @param boilingPoint Boiling temperature. Must be higher than meltingPoint. 
	 * @param index The index in the {@link AlchemyCraftingManager#REAGENTS} array.
	 * @param color A function giving the color of this reagent based on phase. 
	 * @param solid The item that represents this in solid form. 
	 * @param itemQuantity The amount of reagent 1 item is equivelent to. 
	 * @param base Whether this is a constant material (in all worlds). Doesn't matter if solid is null. 
	 */
	public SimpleReagentType(String name, double meltingPoint, double boilingPoint, int index, Function<MatterPhase, Color> color, @Nullable Item solid, int itemQuantity, boolean base){
		this(name, meltingPoint, boilingPoint, index, color, solid, itemQuantity, base, 0, null, null);
	}

	/**
	 * @param name Material name. 
	 * @param meltingPoint Melting temperature. Must be lower than boilingPoint. 
	 * @param boilingPoint Boiling temperature. Must be higher than meltingPoint. 
	 * @param index The index in the {@link AlchemyCraftingManager#REAGENTS} array.
	 * @param color A function giving the color of this reagent based on phase. 
	 * @param solid The item that represents this in solid form. 
	 * @param itemQuantity The amount of reagent 1 item is equivelent to. 
	 * @param base Whether this is a constant material (in all worlds). 
	 * @param catalType 0: None; 1: Alkhest; 2: Anti-Alkhest
	 * @param solventType Sets the solvent type
	 * @param soluteType Sets the solute type
	 */
	public SimpleReagentType(String name, double meltingPoint, double boilingPoint, int index, Function<MatterPhase, Color> color, @Nullable Item solid, int itemQuantity, boolean base, int catalType, @Nullable SolventType solventType, @Nullable SolventType soluteType){
		this(name, meltingPoint, boilingPoint, index, color, solid, itemQuantity, base, catalType, solventType, soluteType, 0, null);
	}

	/**
	 * @param name Material name. 
	 * @param meltingPoint Melting temperature. Must be lower than boilingPoint. 
	 * @param boilingPoint Boiling temperature. Must be higher than meltingPoint. 
	 * @param index The index in the {@link AlchemyCraftingManager#REAGENTS} array.
	 * @param color A function giving the color of this reagent based on phase. 
	 * @param solid The item that represents this in solid form. 
	 * @param itemQuantity The amount of reagent 1 item is equivelent to. 
	 * @param base Whether this is a constant material (in all worlds). 
	 * @param catalType 0: None; 1: Alkhest; 2: Anti-Alkhest
	 * @param solventType Sets the solvent type
	 * @param soluteType Sets the solute type
	 * @param containType 0: Normal; 1: Vanishes in glass; 2: Destroys glass. 
	 * @param effect The effect this has when released. Null for none. 
	 */
	public SimpleReagentType(String name, double meltingPoint, double boilingPoint, int index, Function<MatterPhase, Color> color, @Nullable Item solid, int itemQuantity, boolean base, int catalType, @Nullable SolventType solventType, @Nullable SolventType soluteType, int containType, @Nullable IAlchEffect effect){
		this.name = name;
		if(boilingPoint <= meltingPoint){
			throw Main.logger.throwing(new IllegalArgumentException("Boiling point must be greater than melting point. Material Type: " + name));
		}
		melting = meltingPoint;
		boiling = boilingPoint;
		this.solid = solid;
		this.itemAmount = itemQuantity;
		if(solid != null){
			if(base){
				AlchemyCraftingManager.BASE_ITEM_TO_REAGENT.put(solid, this);
			}else{
				AlchemyCraftingManager.ITEM_TO_REAGENT.put(solid, this);
			}
		}
		this.catalType = catalType;
		this.containType = containType;
		this.effect = effect;
		this.solvent = solventType;
		this.solute = soluteType;
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
	public IReagent getReagentFromStack(ItemStack stack){
		return stack.getItem() == solid ? new SimpleReagent(this, 30D, itemAmount) : null;
	}

	/**
	 * @param reag The reagent
	 * @return The matching solid ItemStack. ItemStack.EMPTY if there either isn't enough material (or cannot be solidifed for any other reason). 
	 */
	@Override
	public ItemStack getStackFromReagent(IReagent reag){
		return reag != null && reag.getType() == this && reag.getPhase() == MatterPhase.SOLID && reag.getAmount() >= itemAmount ? new ItemStack(solid, itemAmount) : ItemStack.EMPTY;
	}

	@Override
	public boolean isAlkhest(){
		return catalType == 1;
	}

	@Override
	public boolean isAntiAlkhest(){
		return catalType == 2;
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
	public void onRelease(World world, BlockPos pos, double amount, MatterPhase phase){
		if(effect != null){
			effect.doEffect(world, pos, amount, phase);
		}
	}

	/**
	 * @return The type of solvent this acts as. 
	 */
	@Override
	@Nullable
	public SolventType solventType(){
		return solvent;
	}

	/**
	 * @return The type of solvent this needs to be in to dissolve. 
	 */
	@Override
	@Nullable
	public SolventType soluteType(){
		return solute;
	}

	@Override
	public Color getColor(MatterPhase phase){
		return color.apply(phase);
	}

	@Override
	public int getIndex(){
		return index;
	}
}
