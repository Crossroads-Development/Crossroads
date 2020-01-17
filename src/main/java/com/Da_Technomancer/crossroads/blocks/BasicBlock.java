package com.Da_Technomancer.crossroads.blocks;

import net.minecraft.block.Block;

public class BasicBlock extends Block{

	public BasicBlock(String name, Block.Properties prop){
		super(prop);
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}
}
