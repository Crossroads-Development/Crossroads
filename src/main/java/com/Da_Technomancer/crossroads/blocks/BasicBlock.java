package com.Da_Technomancer.crossroads.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BasicBlock extends Block{

	public BasicBlock(String name, BlockBehaviour.Properties prop){
		super(prop);
		CRBlocks.toRegister.put(name, this);
		CRBlocks.blockAddQue(name, this);
	}
}
