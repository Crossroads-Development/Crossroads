package com.Da_Technomancer.crossroads.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

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

	private static final ResourceLocation TEXTURE_BEACON_BEAM = new ResourceLocation("textures/entity/beacon_beam.png");
	
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
	public boolean render(Tessellator tess, BufferBuilder buf, long worldTime, double playerX, double playerY, double playerZ, Vec3d playerLook, Random rand, float partialTicks){
		Color col = new Color(color);
		GlStateManager.color3f(col.getRed() / 255F, col.getGreen() / 255F, col.getBlue() / 255F);
		GlStateManager.translated(x - playerX, y - playerY, z - playerZ);
		GlStateManager.rotatef(-angleY, 0, 1, 0);
		GlStateManager.rotatef(angleX + 90F, 1, 0, 0);


		Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE_BEACON_BEAM);

		final double small = -(width / 16D);
		final double big = (width / 16D);
		buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		//+Z
		buf.pos(small, length, big).tex(1, 0).endVertex();
		buf.pos(small, 0, big).tex(1, length).endVertex();
		buf.pos(big, 0, big).tex(0, length).endVertex();
		buf.pos(big, length, big).tex(0, 0).endVertex();
		//-Z
		buf.pos(big, length, small).tex(1, 0).endVertex();
		buf.pos(big, 0, small).tex(1, length).endVertex();
		buf.pos(small, 0, small).tex(0, length).endVertex();
		buf.pos(small, length, small).tex(0, 0).endVertex();
		//-X
		buf.pos(small, length, small).tex(1, 0).endVertex();
		buf.pos(small, 0, small).tex(1, length).endVertex();
		buf.pos(small, 0, big).tex(0, length).endVertex();
		buf.pos(small, length, big).tex(0, 0).endVertex();
		//+X
		buf.pos(big, length, big).tex(1, 0).endVertex();
		buf.pos(big, 0, big).tex(1, length).endVertex();
		buf.pos(big, 0, small).tex(0, length).endVertex();
		buf.pos(big, length, small).tex(0, 0).endVertex();
		tess.draw();
		GlStateManager.color3f(1, 1, 1);


		if(lastTick != worldTime){
			lastTick = worldTime;
			return lifeTime-- < 0;
		}

		return false;
	}
}
