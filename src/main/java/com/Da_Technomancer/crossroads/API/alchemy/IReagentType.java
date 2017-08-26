package com.Da_Technomancer.crossroads.API.alchemy;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IReagentType{

	public double getMeltingPoint();
	
	public double getBoilingPoint();
	
	public default boolean canGlassContain(double temp){
		return true;
	}
	
	public default boolean destroysBadContainer(double temp){
		return false;
	}
	
	public default void onRelease(World world, BlockPos pos, double amount, MatterPhase phase){
		
	}
	
	public default void react(IReactionChamber chamber, IReagent thisReagent){
		
	}
}
