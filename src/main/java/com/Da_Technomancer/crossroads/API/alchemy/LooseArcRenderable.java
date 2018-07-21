package com.Da_Technomancer.crossroads.API.alchemy;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.tuple.Pair;

public class LooseArcRenderable{

	private final float xSt;
	private final float ySt;
	private final float zSt;
	private final float xEn;
	private final float yEn;
	private final float zEn;
	private final float xStFin;
	private final float yStFin;
	private final float zStFin;
	private final float xEnFin;
	private final float yEnFin;
	private final float zEnFin;
	public final int count;
	public final float diffusionRate;
	public final int color;
	public byte lifeTime;
	private final byte lifeSpan;
	public long lastTick = -1;
	public final float length;
	
	public final Vec3d[][] states;

	public LooseArcRenderable(Vec3d start, Vec3d end, int count, float diffusionRate, float length, int color){
		this((float) start.x, (float) start.y, (float) start.z, (float) end.x, (float) end.y, (float) end.z, count, diffusionRate, length, color);
	}

	public LooseArcRenderable(float xSt, float ySt, float zSt, float xEn, float yEn, float zEn, int count, float diffusionRate, float length, int color){
		this(xSt, ySt, zSt, xEn, yEn, zEn, xSt, ySt, zSt, xEn, yEn, zEn, count, diffusionRate, length, (byte) 10, color);
	}

	public LooseArcRenderable(float xSt, float ySt, float zSt, float xEn, float yEn, float zEn, float xStFin, float yStFin, float zStFin, float xEnFin, float yEnFin, float zEnFin, int count, float diffusionRate, float length, byte lifespan, int color){
		this.xSt = xSt;
		this.ySt = ySt;
		this.zSt = zSt;
		this.xEn = xEn;
		this.yEn = yEn;
		this.zEn = zEn;
		this.xStFin = xStFin;
		this.yStFin = yStFin;
		this.zStFin = zStFin;
		this.xEnFin = xEnFin;
		this.yEnFin = yEnFin;
		this.zEnFin = zEnFin;
		this.count = count;
		this.diffusionRate = diffusionRate;
		this.color = color;
		this.length = length;
		states = new Vec3d[count][(int) Math.max(Math.sqrt((xEn - xSt) * (xEn - xSt) + (yEn - ySt) * (yEn - ySt) + (zEn - zSt) * (zEn - zSt)) / length - 0.999F, Math.sqrt((xEnFin - xStFin) * (xEnFin - xStFin) + (yEnFin - yStFin) * (yEnFin - yStFin) + (zEnFin - zStFin) * (zEnFin - zStFin)) / length - 0.999F)];//(int) (Math.sqrt(Math.pow(xEn - xSt, 2D) + Math.pow(yEn - ySt, 2D) + Math.pow(zEn - zSt, 2D)) / length) - 1
		this.lifeSpan = lifespan;
		this.lifeTime = lifespan;
	}

	public Pair<Vec3d, Vec3d> getCurrentEndpoints(float partialTicks){
		float mult = ((float) lifeSpan - lifeTime + partialTicks) / (float) lifeSpan;
		return Pair.of(new Vec3d(mult * (xStFin - xSt) + xSt, mult * (yStFin - ySt) + ySt, mult * (zStFin - zSt) + zSt), new Vec3d(mult * (xEnFin - xEn) + xEn, mult * (yEnFin - yEn) + yEn, mult * (zEnFin - zEn) + zEn));
	}

	public void saveToNBT(NBTTagCompound nbt){
		nbt.setFloat("x", xSt);
		nbt.setFloat("y", ySt);
		nbt.setFloat("z", zSt);
		nbt.setFloat("xE", xEn);
		nbt.setFloat("yE", yEn);
		nbt.setFloat("zE", zEn);
		nbt.setFloat("xF", xStFin);
		nbt.setFloat("yF", yStFin);
		nbt.setFloat("zF", zStFin);
		nbt.setFloat("xEF", xEnFin);
		nbt.setFloat("yEF", yEnFin);
		nbt.setFloat("zEF", zEnFin);
		nbt.setInteger("count", count);
		nbt.setFloat("diffu", diffusionRate);
		nbt.setInteger("color", color);
		nbt.setFloat("len", length);
		nbt.setByte("lif", lifeTime);
	}
	
	public static LooseArcRenderable readFromNBT(NBTTagCompound nbt){
		return new LooseArcRenderable(nbt.getFloat("x"), nbt.getFloat("y"), nbt.getFloat("z"), nbt.getFloat("xE"), nbt.getFloat("yE"), nbt.getFloat("zE"), nbt.getFloat("xF"), nbt.getFloat("yF"), nbt.getFloat("zF"), nbt.getFloat("xEF"), nbt.getFloat("yEF"), nbt.getFloat("zEF"), nbt.getInteger("count"), nbt.getFloat("diffu"), nbt.getFloat("len"), nbt.getByte("lif"), nbt.getInteger("color"));
	}
}
