package com.Da_Technomancer.crossroads.API.alchemy;

import java.awt.Color;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.effects.alchemy.IAlchEffect;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ElementalReagent implements IElementReagent{
	
	private final int index;
	private final boolean destroyContainer;
	private final byte level;
	private final String name;
	private final MagicUnit range;
	private final Color color;
	private final IAlchEffect effect;
	private final int melt;
	private final int boil;
	private final Item solidForm;
	private final IElementReagent secondaryBase;
	
	public ElementalReagent(String name, int index, byte level, int melt, int boil, IAlchEffect effect, boolean destroyContainer, MagicUnit range, @Nullable Item solidForm){
		this(name, index, level, melt, boil, effect, destroyContainer, range, solidForm, null);
	}
	
	public ElementalReagent(String name, int index, byte level, int melt, int boil, IAlchEffect effect, boolean destroyContainer, MagicUnit range, @Nullable Item solidForm, @Nullable IElementReagent secondaryBase){
		this.name = name;
		this.index = index;
		this.destroyContainer = destroyContainer;
		this.level = level;
		this.range = range;
		this.effect = effect;
		this.boil = boil;
		this.melt = melt;
		this.solidForm = solidForm;
		this.secondaryBase = secondaryBase;
		this.color = range.getTrueRGB();
		AlchemyCore.ELEMENTAL_REAGS.add(this);
	}
	
	@Override
	public double getMeltingPoint(){
		return melt;
	}

	@Override
	public double getBoilingPoint(){
		return boil;
	}

	@Override
	public int getIndex(){
		return index;
	}

	@Override
	public Color getColor(EnumMatterPhase phase){
		return color;
	}
	
	@Override
	public boolean canGlassContain(){
		return false;
	}
	
	@Override
	public boolean destroysBadContainer(){
		return destroyContainer;
	}
	
	@Override
	public void onRelease(World world, BlockPos pos, double amount, double temp, EnumMatterPhase phase, ReagentStack[] contents){
		if(effect != null){
			effect.doEffectAdv(world, pos, amount, temp, phase, contents);
		}
	}

	@Override
	@Nullable
	public ReagentStack getReagentFromStack(ItemStack stack){
		return stack.getItem() == solidForm ? new ReagentStack(this, 1) : null;
	}
	
	/**
	 * @param reag The reagent
	 * @return The matching solid ItemStack. ItemStack.EMPTY if there either isn't enough material (or cannot be solidifed for any other reason). 
	 */
	@Override
	public ItemStack getStackFromReagent(ReagentStack reag){
		return reag != null && reag.getType() == this && reag.getAmount() >= 1 ? new ItemStack(solidForm, (int) reag.getAmount()) : ItemStack.EMPTY;
	}
	
	@Override
	public MagicUnit getAlignment(){
		return range;
	}

	@Override
	public byte getLevel(){
		return level;
	}

	@Override
	public IElementReagent getSecondaryBase(){
		return secondaryBase;
	}

	@Override
	public String getName(){
		return name;
	}	
}
