package com.Da_Technomancer.crossroads.API.alchemy;

import javax.annotation.Nonnull;

public class ReagentStack{

	//The default reagent used for empty stacks in place of null
	private static final IReagent DEFAULT = AlchemyCore.REAGENTS.get(EnumReagents.WATER.id());

	private final IReagent type;
	private final int amount;

	public ReagentStack(IReagent type, int amount){
		this.type = type == null ? DEFAULT : type;
		this.amount = amount;
	}

	public boolean isEmpty(){
		return amount <= 0;
	}

	@Nonnull
	public IReagent getType(){
		return type;
	}

	/**
	 * @return The amount of this substance. In moles (where applicable). 
	 */
	public int getAmount(){
		return amount;
	}

	@Override
	public String toString(){
		return isEmpty() ? "Empty Reagent" : (type.getName() + ", Amount: " + amount);
	}
}
