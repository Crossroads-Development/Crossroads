package com.Da_Technomancer.crossroads.API.alchemy;

import javax.annotation.Nonnull;

public class ReagentStack{

	//The default reagent used for empty stacks in place of null
	private static final String DEFAULT = EnumReagents.WATER.id();

	private final String typeId;
	private final int amount;

	public ReagentStack(String type, int amount){
		this.typeId = type == null ? DEFAULT : type;
		this.amount = amount;
	}

	public ReagentStack(IReagent type, int amount){
		this.typeId = type.getId();
		this.amount = amount;
	}

	public boolean isEmpty(){
		return amount <= 0;
	}

	@Nonnull
	public IReagent getType(){
		IReagent type = AlchemyCore.getReagent(typeId);
		return type == null ? AlchemyCore.getReagent(DEFAULT) : type;
	}

	/**
	 * @return The amount of this substance. In moles (where applicable). 
	 */
	public int getAmount(){
		return amount;
	}

	@Override
	public String toString(){
		if(isEmpty()){
			return "Empty Reagent";
		}else{
			IReagent reag = AlchemyCore.getReagent(typeId);
			if(reag == null){
				return "Unresolved: " + typeId + ", Qty: " + amount;
			}else{
				return reag.getName() + ", Qty: " + amount;
			}
		}
	}
}
