package com.Da_Technomancer.crossroads.API.alchemy;

import java.awt.Color;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.effects.alchemy.IAlchEffect;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ElementalReagent implements IDynamicReagent, IElementReagent{
	
	private final int index;
	private final boolean destroyContainer;
	private final byte level;
	
	/**
	 * Energy, potential, and stability determine base composition, void determines possible variance from that at randomization.
	 */
	private final MagicUnit possibleRange;
	private MagicUnit range;
	
	private final Color color;
	private final IAlchEffect effect;
	private final int minMelt;
	private final int maxMelt;
	private final int minBoil;
	private final int maxBoil;
	private int melt;
	private int boil;
	private final Item solidForm;
	
	public ElementalReagent(int index, byte level, int minMelt, int maxMelt, int minBoil, int maxBoil, Color color, IAlchEffect effect, boolean destroyContainer, MagicUnit possibleRange, @Nullable Item solidForm){
		this.index = index;
		this.destroyContainer = destroyContainer;
		this.level = level;
		this.possibleRange = possibleRange;
		this.effect = effect;
		this.minBoil = minBoil;
		this.maxBoil = maxBoil;
		this.minMelt = minMelt;
		this.maxMelt = maxMelt;
		this.color = color;
		this.solidForm = solidForm;
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
	public void onRelease(World world, BlockPos pos, double amount, EnumMatterPhase phase, ReagentStack[] contents){
		if(effect != null){
			effect.doEffectAdv(world, pos, amount, phase, contents);
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
	public void setProperties(int seed){
		melt = minMelt + ((seed >> 1) % (1 + maxMelt - minMelt));
		boil = Math.min(melt + 1, minBoil + ((seed >> 3) % (1 + maxBoil - minBoil)));
		int variance = possibleRange.getVoid() + 1;
		range = new MagicUnit(possibleRange.getEnergy() + ((seed >> 1) % variance), possibleRange.getPotential() + ((seed >> 4) % variance), possibleRange.getStability() + ((seed >> 8) % variance), 0);
		
		AlchemyCore.ELEMENTAL_REAGS.add(this);
	}

	@Override
	public void setReactions(int seed){
		
	}

	@Override
	public byte getLevel(){
		return level;
	}	
}
