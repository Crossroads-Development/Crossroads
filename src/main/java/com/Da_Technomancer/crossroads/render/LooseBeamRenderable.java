package com.Da_Technomancer.crossroads.render;

import com.Da_Technomancer.crossroads.api.render.IVisualEffect;
import com.Da_Technomancer.crossroads.render.tesr.BeamRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

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
	
	public static LooseBeamRenderable readFromNBT(Level world, CompoundTag nbt){
		return new LooseBeamRenderable(nbt.getFloat("x"), nbt.getFloat("y"), nbt.getFloat("z"), nbt.getDouble("length"), nbt.getFloat("angle_x"), nbt.getFloat("angle_y"), nbt.getByte("width"), nbt.getInt("color"));
	}

	@Override
	public boolean render(PoseStack matrix, MultiBufferSource buffer, long worldTime, float partialTicks, Random rand){
		matrix.translate(x, y, z);
		matrix.mulPose(Axis.YP.rotationDegrees(-angleY));
		matrix.mulPose(Axis.XP.rotationDegrees(angleX + 90F));
		BeamRenderer.drawBeam(matrix, buffer.getBuffer(CRRenderTypes.BEAM_TYPE), (float) length, width / 8F, new Color(color));

		if(lastTick != worldTime){
			lastTick = worldTime;
			return lifeTime-- < 0;
		}

		return false;
	}
}
