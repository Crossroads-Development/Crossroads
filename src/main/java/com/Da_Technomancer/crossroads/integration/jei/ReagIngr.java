package com.Da_Technomancer.crossroads.integration.jei;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.alchemy.IReagent;
import com.Da_Technomancer.crossroads.api.alchemy.ReagentManager;
import com.Da_Technomancer.crossroads.api.alchemy.ReagentStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mezz.jei.api.ingredients.IIngredientType;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public record ReagIngr(@Nonnull String reag, int parts){

	public static final IIngredientType<ReagIngr> REAG = () -> ReagIngr.class;
	public static final List<ReagIngr> REAG_TYPES = new ArrayList<>();
	public static final Codec<ReagIngr> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.STRING.fieldOf("reagent").forGetter(ReagIngr::reag), Codec.INT.fieldOf("parts").forGetter(ReagIngr::parts)).apply(instance, ReagIngr::new));

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
		this(reag.typeId(), reag.amount());
	}

	public IReagent getReag(){
		return ReagentManager.getReagent(reag);
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
