package com.Da_Technomancer.crossroads.integration.JEI;

import java.util.ArrayList;
import java.util.List;

import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCore;
import com.Da_Technomancer.crossroads.API.alchemy.IReagent;
import mezz.jei.api.ingredients.IIngredientType;

public class ReagIngr{

	public static final IIngredientType<ReagIngr> REAG = () -> ReagIngr.class;
	public static final List<ReagIngr> REAG_TYPES = new ArrayList<>();
	
	private final IReagent reag;
	private final int parts;
	
	public ReagIngr(IReagent reag, int parts){
		this.reag = reag;
		this.parts = parts;
		if(parts < 0){
			IllegalArgumentException e = new IllegalArgumentException("ReagIngr constructed with invalid part count!");
			Crossroads.logger.throwing(e);
			throw e;
		}
	}

	public ReagIngr(ReagentStack reag){
		this(reag.getType(), reag.getAmount());
	}

	public IReagent getReag(){
		return reag;
	}
	
	public int getParts(){
		return parts;
	}
	
	protected static void populate(){
		for(IReagent r : AlchemyCore.getRegisteredReags()){
			if(r != null){
				REAG_TYPES.add(new ReagIngr(r, 1));
			}
		}
	}
}
