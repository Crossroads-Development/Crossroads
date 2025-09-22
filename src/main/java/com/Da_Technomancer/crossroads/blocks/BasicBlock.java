package com.Da_Technomancer.crossroads.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BasicBlock extends Block{

	public static final MapCodec<Block> CODEC = simpleCodec(BasicBlock::new);

	private BasicBlock(BlockBehaviour.Properties prop){
		super(prop);
	}

	public BasicBlock(String name, BlockBehaviour.Properties prop){
		this(prop);
		CRBlocks.queueForRegister(name, this);
	}

	@Override
	protected MapCodec<? extends Block> codec(){
		return CODEC;
	}
}
