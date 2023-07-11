package com.Da_Technomancer.crossroads.api.templates;

import net.minecraft.world.item.BlockItem;

/**
 * If placed on a block, will be able to create custom itemblock rather than getting generic one created at registration time
 */
public interface ICustomItemBlock{

	BlockItem createItemBlock();
}
