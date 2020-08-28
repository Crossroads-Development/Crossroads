package com.Da_Technomancer.crossroads.integration.JEI;

import java.util.ArrayList;
import java.util.List;

import com.Da_Technomancer.crossroads.API.alchemy.ReagentManager;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.API.alchemy.IReagent;
import mezz.jei.api.ingredients.IIngredientType;

public class ReagIngr{

	public static final IIngredientType<ReagIngr> REAG = () -> ReagIngr.class;
	public static final List<ReagIngr> REAG_TYPES = new ArrayList<>();
	
	private final String reag;
	private final int parts;

	public ReagIngr(IReagent reag, int parts){
		this(reag.getID(), parts);
	}

	public ReagIngr(String reag, int parts){
		this.reag = reag;
		this.parts = parts;
		if(parts < 0){
			IllegalArgumentException e = new IllegalArgumentException("ReagIngr constructed with invalid part count!");
			Crossroads.logger.throwing(e);
			throw e;
		}
	}

	public ReagIngr(ReagentStack reag){
		this(reag.getId(), reag.getAmount());
	}

	public IReagent getReag(){
		return ReagentManager.getReagent(reag);
	}

	public String getID(){
		return reag;
	}
	
	public int getParts(){
		return parts;
	}
	
	protected static void populate(){
		REAG_TYPES.clear();
		for(IReagent r : ReagentManager.getRegisteredReags()){
			if(r != null){
				REAG_TYPES.add(new ReagIngr(r, 1));
			}
		}
	}
}
