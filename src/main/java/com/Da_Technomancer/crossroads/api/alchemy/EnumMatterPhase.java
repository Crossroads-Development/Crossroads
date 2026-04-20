package com.Da_Technomancer.crossroads.api.alchemy;

import com.mojang.serialization.Codec;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum EnumMatterPhase implements StringRepresentable{

	//Order affects rendering in ReagentRenderer

	FLAME(true, true, false),
	GAS(true, true, false),
	LIQUID(true, false, true),
	SOLID(true, false, true);

	private final boolean flows;
	private final boolean flowsUp;
	private final boolean flowsDown;

	EnumMatterPhase(boolean flows, boolean flowsUp, boolean flowsDown){
		this.flows = flows;
		this.flowsUp = flowsUp;
		this.flowsDown = flowsDown;
	}

	public static final Codec<EnumMatterPhase> CODEC = StringRepresentable.fromEnum(EnumMatterPhase::values);

	public boolean flows(){
		return flows;
	}

	public boolean flowsUp(){
		return flows && flowsUp;
	}

	public boolean flowsDown(){
		return flows && flowsDown;
	}

	public boolean canFlow(Direction toDirection){
		return flows() && (toDirection != Direction.UP || flowsUp) && (toDirection != Direction.DOWN || flowsDown);
	}

	@Override
	public String getSerializedName(){
		return name().toLowerCase(Locale.US);
	}
}
