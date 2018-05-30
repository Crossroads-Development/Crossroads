package com.Da_Technomancer.crossroads.API.technomancy;

import net.minecraft.nbt.NBTTagCompound;

public class LooseBeamRenderable{
	
	public final double x;
	public final double y;
	public final double z;
	public final double length;
	public final float angleX;
	public final float angleY;
	public final byte width;
	public final int color;
	public byte lifeTime = 6;
	public long lastTick = 0;
	
	public LooseBeamRenderable(double x, double y, double z, double length, float angleX, float angleY, byte width, int color){
		this.x = x;
		this.y = y;
		this.z = z;
		this.length = length;
		this.angleX = angleX;
		this.angleY = angleY;
		this.width = width;
		this.color = color;
	}

	public void saveToNBT(NBTTagCompound nbt){
		nbt.setDouble("x", x);
		nbt.setDouble("y", y);
		nbt.setDouble("z", z);
		nbt.setDouble("length", length);
		nbt.setFloat("angleX", angleX);
		nbt.setFloat("angleY", angleY);
		nbt.setByte("width", width);
		nbt.setInteger("color", color);
	}
	
	public static LooseBeamRenderable readFromNBT(NBTTagCompound nbt){
		return new LooseBeamRenderable(nbt.getFloat("x"), nbt.getFloat("y"), nbt.getFloat("z"), nbt.getDouble("length"), nbt.getFloat("angleX"), nbt.getFloat("angleY"), nbt.getByte("width"), nbt.getInteger("color"));
	}
}
