package com.Da_Technomancer.crossroads.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;

import java.util.Random;

public interface IVisualEffect{

	/**
	 * Renders the effect in the world.
	 * Matrix will be pushed and popped by the caller
	 * @param matrix A matrix translated to world 0,0,0
	 * @param buffer A buffer
	 * @param worldTime The world time
	 * @param partialTicks Partial ticks [0, 1]
	 * @param rand A shared random instance
	 * @return Whether this effect should 'expire' and be removed this tick
	 */
	boolean render(MatrixStack matrix, IRenderTypeBuffer buffer, long worldTime, float partialTicks, Random rand);

}
