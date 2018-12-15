package com.Da_Technomancer.crossroads.render;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public interface IVisualEffect{

	public boolean render(Tessellator tess, BufferBuilder buf, long worldTime, double playerX, double playerY, double playerZ, Vec3d playerLook, Random rand, float partialTicks);

}
