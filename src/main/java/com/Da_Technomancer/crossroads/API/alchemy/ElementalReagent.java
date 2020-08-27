package com.Da_Technomancer.crossroads.API.alchemy;

import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.effects.alchemy.IAlchEffect;
import com.google.common.collect.ImmutableList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

public class ElementalReagent implements IElementReagent{

	private final boolean destroyContainer;
	private final String name;
	private final EnumBeamAlignments element;
	private final Color color;
	private final IAlchEffect effect;
	private final int melt;
	private final int boil;
	private final Item solidForm;
	
	public ElementalReagent(String name, int melt, int boil, IAlchEffect effect, boolean destroyContainer, EnumBeamAlignments element, Color color, @Nullable Item solidForm){
		this.name = name;
		this.destroyContainer = destroyContainer;
		this.element = element;
		this.effect = effect;
		this.boil = boil;
		this.melt = melt;
		this.solidForm = solidForm;
		this.color = color;
		if(solidForm != null){
			AlchemyCore.getItemToReagent().put((stack) -> stack.getItem() == solidForm, this);
		}
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
	public Color getColor(EnumMatterPhase phase){
		return color;
	}
	
	@Override
	public boolean requiresCrystal(){
		return true;
	}
	
	@Override
	public boolean destroysBadContainer(){
		return destroyContainer;
	}

	@Override
	public String getId(){
		return name;
	}

	@Nullable
	@Override
	public IAlchEffect getEffect(){
		return effect;
	}
	
	/**
	 * @param reag The reagent
	 * @return The matching solid ItemStack. ItemStack.EMPTY if there either isn't enough material (or cannot be solidifed for any other reason). 
	 */
	@Override
	public ItemStack getStackFromReagent(ReagentStack reag){
		return !reag.isEmpty() && reag.getType() == this ? new ItemStack(solidForm, reag.getAmount()) : ItemStack.EMPTY;
	}

	@Override
	public List<ItemStack> getJEISolids(){
		return solidForm == null ? ImmutableList.of() : ImmutableList.of(new ItemStack(solidForm));
	}

	@Override
	public EnumBeamAlignments getAlignment(){
		return element;
	}
}
