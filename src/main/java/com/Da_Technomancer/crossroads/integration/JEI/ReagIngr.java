package com.Da_Technomancer.crossroads.integration.JEI;

import java.util.ArrayList;
import java.util.List;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCore;
import com.Da_Technomancer.crossroads.API.alchemy.IReagent;

public class ReagIngr{
	
	public static final List<ReagIngr> REAG_TYPES = new ArrayList<ReagIngr>();
	
	private final IReagent reag;
	private final int parts;
	
	public ReagIngr(IReagent reag, int parts){
		this.reag = reag;
		this.parts = parts;
		if(parts < 0){
			IllegalArgumentException e = new IllegalArgumentException("ReagIngr constructed with invalid part count!");
			Main.logger.throwing(e);
			throw e;
		}
	}

	public IReagent getReag(){
		return reag;
	}
	
	public int getParts(){
		return parts;
	}
	
	public static void populate(){
		for(IReagent r : AlchemyCore.REAGENTS){
			if(r != null){
				REAG_TYPES.add(new ReagIngr(r, 1));
			}
		}
	}
}
