package com.Da_Technomancer.crossroads.API.alchemy;

public interface IDynamicReagent extends IReagent{

	@Override
	public default String getName(){
		int index = getIndex();
		String name;
		
		try{
			name = AlchemyCore.CUST_REAG_NAMES[index - AlchemyCore.RESERVED_REAGENT_COUNT];
		}catch(IndexOutOfBoundsException e){
			name = null;
		}
		if(name == null){
			return "???";
		}
		return name;
	}
	
	public void setProperties(int seed);
	
	public void setReactions(int seed);
}
