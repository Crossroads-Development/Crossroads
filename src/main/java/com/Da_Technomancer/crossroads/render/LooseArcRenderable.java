package com.Da_Technomancer.crossroads.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Random;

public class LooseArcRenderable implements IVisualEffect{

	private final float xSt;
	private final float ySt;
	private final float zSt;
	private final float xEn;
	private final float yEn;
	private final float zEn;
	private final float xStFin;
	private final float yStFin;
	private final float zStFin;
	private final int count;
	private final float diffusionRate;
	private final int color;
	private byte lifeTime;
	private final byte lifeSpan;
	private long lastTick = -1;
	private final Vec3d[][] states;

	private LooseArcRenderable(float xSt, float ySt, float zSt, float xEn, float yEn, float zEn, float xStFin, float yStFin, float zStFin, int count, float diffusionRate, byte lifespan, int color){
		this.xSt = xSt;
		this.ySt = ySt;
		this.zSt = zSt;
		this.xEn = xEn;
		this.yEn = yEn;
		this.zEn = zEn;
		this.xStFin = xStFin;
		this.yStFin = yStFin;
		this.zStFin = zStFin;
		this.count = count;
		this.diffusionRate = diffusionRate;
		this.color = color;
		states = new Vec3d[count][9];
		this.lifeSpan = lifespan;
		this.lifeTime = lifespan;
	}

	public static LooseArcRenderable readFromNBT(CompoundNBT nbt){
		return new LooseArcRenderable(nbt.getFloat("x"), nbt.getFloat("y"), nbt.getFloat("z"), nbt.getFloat("x_e"), nbt.getFloat("y_e"), nbt.getFloat("z_e"), nbt.getFloat("x_f"), nbt.getFloat("y_f"), nbt.getFloat("z_f"), nbt.getInt("count"), nbt.getFloat("diffu"), nbt.getByte("lif"), nbt.getInt("color"));
	}

	@Override
	public boolean render(Tessellator tess, BufferBuilder buf, long worldTime, double playerX, double playerY, double playerZ, Vec3d playerLook, Random rand, float partialTicks){
		final float arcWidth = 0.03F;
		Color col = new Color(color, true);
		float mult = ((float) lifeSpan - lifeTime + partialTicks) / (float) lifeSpan;
		Vec3d start = new Vec3d(mult * (xStFin - xSt) + xSt, mult * (yStFin - ySt) + ySt, mult * (zStFin - zSt) + zSt);

		GlStateManager.disableTexture();
		GlStateManager.color4f(col.getRed() / 255F, col.getGreen() / 255F, col.getBlue() / 255F, col.getAlpha() / 255F);
		GlStateManager.translated(start.x - playerX, start.y - playerY, start.z - playerZ);
		buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

		//If the arc is newly created, generate its path
		if(lastTick != worldTime && lastTick < 0){
			Vec3d lengthVec = new Vec3d(xEn - start.x, yEn - start.y, zEn - start.z);
			double length = lengthVec.length();
			//lengthVec is a normalized vector pointing from the start to end point
			lengthVec = lengthVec.normalize();
			//crossVec is a normalized vector perpendicular to the lengthVec. Used to make bolts jut outwards from the center
			Vec3d crossVec = lengthVec.crossProduct(playerLook);
			//In the unlikely event that playerLook happens to be parallel to lengthVec, we need to recreate crossVec with a random vector. This process can be repeated to ensure a non-zero length as necessary (though this running even once is rare)
			while(crossVec.lengthSquared() == 0){
				crossVec = lengthVec.crossProduct(new Vec3d(1, 1, rand.nextInt(16)));
			}
			crossVec = crossVec.normalize();

			//The angle in radians that each bolt shall be offset from the adjacent bolts. Used to ensure consistent spacing
			double angle = 2D * Math.PI / count;

			for(int i = 0; i < count; i++){
				//Whether this bolt has "diverged" from its target, causing it to miss the end position
				boolean diverged = rand.nextFloat() < diffusionRate;

				for(int node = 0; node < states[0].length; node++){
					//portion is the the fraction of the total length this node traverses (from 0 to ~1), ignoring the random component. The function used is arbitrary, but was chosen due to a pleasant scaling.
					double portion = -0.012229D * Math.pow(node, 2) + 0.222835D * node - 0.0030303D;
					if(portion < 0){
						portion = 0D;
					}
					//Generate the next node position
					states[i][node] = lengthVec.scale(portion * length).add(crossVec.scale(length / 6D * (diverged ? Math.sqrt(portion) : Math.sqrt(portion) - Math.pow(portion, 2)))).add(rand.nextDouble() / 4D, rand.nextDouble() / 4D, rand.nextDouble() / 4D);
				}
				
				//Rotates crossVec angle radians about lengthVec
				//This formula is only valid because crossVec and lengthVec are perpendicular
				crossVec = crossVec.scale(Math.cos(angle)).add(crossVec.crossProduct(lengthVec).scale(Math.sin(angle)));
			}
		}

		//Render the arcs as stored in states
		for(int i = 0; i < count; i++){
			for(int node = 1; node < states[0].length; node++){
				//We generate a vector based on the player's perspective for use creating a thickness to rendered lines. The vector is chosen such to maximize the apparent thickness from the given view angle
				//The width vector is the vector from the player's eyes to the closest point on the link line (were it extended indefinitely) to the player's eyes, all cross the link line vector.
				//If you want to know where this formula comes from... I'm not cramming a quarter page of calculus into these comments
				Vec3d offsetVec = new Vec3d(states[i][node - 1].x + start.x - playerX, states[i][node - 1].y + start.y - playerY - Minecraft.getInstance().player.getEyeHeight(), states[i][node - 1].z + start.z - playerZ);
				Vec3d deltaVec = states[i][node].subtract(states[i][node - 1]);
				Vec3d vec = offsetVec.add(deltaVec.scale(-deltaVec.dotProduct(offsetVec) / deltaVec.lengthSquared())).crossProduct(deltaVec);
				vec = vec.scale(arcWidth / 2F / vec.length());

				buf.pos(states[i][node].x - vec.x, states[i][node].y - vec.y, states[i][node].z - vec.z).endVertex();
				buf.pos(states[i][node].x + vec.x, states[i][node].y + vec.y, states[i][node].z + vec.z).endVertex();
				buf.pos(states[i][node - 1].x + vec.x, states[i][node - 1].y + vec.y, states[i][node - 1].z + vec.z).endVertex();
				buf.pos(states[i][node - 1].x - vec.x, states[i][node - 1].y - vec.y, states[i][node - 1].z - vec.z).endVertex();
			}
		}

		tess.draw();
		GlStateManager.color3f(1, 1, 1);
		GlStateManager.enableTexture();

		if(lastTick != worldTime){
			lastTick = worldTime;
			return lifeTime-- < 0;
		}

		return false;
	}
}
