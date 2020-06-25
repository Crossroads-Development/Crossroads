package com.Da_Technomancer.crossroads.render;

import com.Da_Technomancer.crossroads.render.TESR.BeamRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.nbt.CompoundNBT;

import java.awt.*;
import java.util.Random;

public class LooseBeamRenderable implements IVisualEffect{
	
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

	private LooseBeamRenderable(double x, double y, double z, double length, float angleX, float angleY, byte width, int color){
		this.x = x;
		this.y = y;
		this.z = z;
		this.length = length;
		this.angleX = angleX;
		this.angleY = angleY;
		this.width = width;
		this.color = color;
	}
	
	public static LooseBeamRenderable readFromNBT(CompoundNBT nbt){
		return new LooseBeamRenderable(nbt.getFloat("x"), nbt.getFloat("y"), nbt.getFloat("z"), nbt.getDouble("length"), nbt.getFloat("angle_x"), nbt.getFloat("angle_y"), nbt.getByte("width"), nbt.getInt("color"));
	}

	@Override
	public boolean render(MatrixStack matrix, IRenderTypeBuffer buffer, long worldTime, float partialTicks, Random rand){
		matrix.translate(x, y, z);
		matrix.rotate(Vector3f.YP.rotationDegrees(-angleY));
		matrix.rotate(Vector3f.XP.rotationDegrees(angleX + 90F));
		BeamRenderer.drawBeam(matrix, buffer.getBuffer(CRRenderTypes.BEAM_TYPE), (float) length, width / 8F, new Color(color));

		if(lastTick != worldTime){
			lastTick = worldTime;
			return lifeTime-- < 0;
		}

		return false;
	}
}
