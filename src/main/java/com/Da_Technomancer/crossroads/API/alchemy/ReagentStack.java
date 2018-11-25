package com.Da_Technomancer.crossroads.API.alchemy;

import javax.annotation.Nonnull;

public class ReagentStack{

	//The default reagent used for empty stacks in place of null
	private static final IReagent DEFAULT = AlchemyCore.REAGENTS.get(EnumReagents.WATER.id());
	public static final ReagentStack EMPTY = new ReagentStack(DEFAULT, 0);

	private final IReagent type;
	private int amount;

	public ReagentStack(IReagent type, int amount){
		this.type = type == null ? DEFAULT : type;
		this.amount = amount;
	}

	public boolean isEmpty(){
		return amount <= 0 || type == null;
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

	public void setAmount(int amountIn){
		amount = Math.max(0, amountIn);

}
	/**
	 * @param amountChange
	 * @return The new amount. 
	 */
	public int increaseAmount(int amountChange){
		setAmount(amount + amountChange);
		return amount;
	}

	@Override
	public String toString(){
		return isEmpty() ? "Empty Reagent" : (type.getName() + ", Amount: " + amount);
	}
}
