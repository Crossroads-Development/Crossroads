package com.Da_Technomancer.crossroads.API;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** This class is for holding a list of changes being made to a world, but that haven't actually happened yet. 
 * It is able to return the blockstate for a given position as if these changes actually happened. Finally it can do all the saved changes. 
 * This is for making large changes to the world step by step without a huge number of unnecessary block updates.
 * 
 * Note that the changes are saved in a LinkedHashMap, so the last change set will be performed last when doChanges() is called
 */
public class WorldBuffer{
	
	private final World worldObj;
	private final LinkedHashMap<BlockPos, IBlockState> memory = new LinkedHashMap<BlockPos, IBlockState>();
	
	public WorldBuffer(World worldObj){
		this.worldObj = worldObj;
	}
	
	public World getWorld(){
		return worldObj;
	}
	
	public void addChange(BlockPos pos, IBlockState state){
		pos = pos.toImmutable();
		if(memory.containsKey(pos)){
			memory.remove(pos);
		}
		memory.put(pos, state);
	}
	
	public IBlockState getBlockState(BlockPos pos){
		if(memory.containsKey(pos)){
			return memory.get(pos);
		}
		
		return worldObj.getBlockState(pos);
	}

	public void doChanges(){
		for(Entry<BlockPos, IBlockState> ent : memory.entrySet()){
			if(worldObj.getBlockState(ent.getKey()) != ent.getValue()){
				worldObj.setBlockState(ent.getKey(), ent.getValue());
			}
		}
		memory.clear();
	}
}
