package com.Da_Technomancer.crossroads.API.alchemy;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;

public class LooseArcRenderable{
	
	public final float xSt;
	public final float ySt;
	public final float zSt;
	public final float xEn;
	public final float yEn;
	public final float zEn;
	public final int count;
	public final float diffusionRate;
	public final int color;
	public byte lifeTime = 10;
	public long lastTick = -1;
	
	public Vec3d[][] states;
	
	public LooseArcRenderable(float xSt, float ySt, float zSt, float xEn, float yEn, float zEn, int count, float diffusionRate, int color){
		this.xSt = xSt;
		this.ySt = ySt;
		this.zSt = zSt;
		this.xEn = xEn;
		this.yEn = yEn;
		this.zEn = zEn;
		this.count = count;
		this.diffusionRate = diffusionRate;
		this.color = color;
		states = new Vec3d[count][(int) (Math.sqrt(Math.pow(xEn - xSt, 2D) + Math.pow(yEn - ySt, 2D) + Math.pow(zEn - zSt, 2D)) - 1)];
	}

	public void saveToNBT(NBTTagCompound nbt){
		nbt.setFloat("x", xSt);
		nbt.setFloat("y", ySt);
		nbt.setFloat("z", zSt);
		nbt.setFloat("xE", xEn);
		nbt.setFloat("yE", yEn);
		nbt.setFloat("zE", zEn);
		nbt.setInteger("count", count);
		nbt.setFloat("diffu", diffusionRate);
		nbt.setInteger("color", color);
	}
	
	public static LooseArcRenderable readFromNBT(NBTTagCompound nbt){
		return new LooseArcRenderable(nbt.getFloat("x"), nbt.getFloat("y"), nbt.getFloat("z"), nbt.getFloat("xE"), nbt.getFloat("yE"), nbt.getFloat("zE"), nbt.getInteger("count"), nbt.getFloat("diffu"), nbt.getInteger("color"));
	}
}
