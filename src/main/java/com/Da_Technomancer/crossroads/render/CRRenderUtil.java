package com.Da_Technomancer.crossroads.render;

import com.Da_Technomancer.crossroads.API.packets.AddVisualToClient;
import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.packets.SafeCallable;
import com.Da_Technomancer.essentials.render.RenderUtil;
import jdk.internal.jline.internal.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

public class CRRenderUtil extends RenderUtil{

	public static final Vec3d VEC_I = new Vec3d(1, 0, 0);
	public static final Vec3d VEC_J = new Vec3d(0, 1, 0);
	public static final Vec3d VEC_K = new Vec3d(0, 0, 1);

	/**
	 * Used internally. Public only for packet use
	 */
	@SuppressWarnings("unchecked")
	public static final Function<CompoundNBT, IVisualEffect>[] visualFactories = (Function<CompoundNBT, IVisualEffect>[]) new Function[2];

	static{
		visualFactories[0] = LooseBeamRenderable::readFromNBT;
		visualFactories[1] = LooseArcRenderable::readFromNBT;
	}

	//Pre-made render effects

	public static void addBeam(World world, double x, double y, double z, double length, float angleX, float angleY, byte width, int color){
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt("id", 0);
		nbt.putDouble("x", x);
		nbt.putDouble("y", y);
		nbt.putDouble("z", z);
		nbt.putDouble("length", length);
		nbt.putFloat("angle_x", angleX);
		nbt.putFloat("angle_y", angleY);
		nbt.putByte("width", width);
		nbt.putInt("color", color);
		CRPackets.sendPacketAround(world, new BlockPos(x, y, z), new AddVisualToClient(nbt));
	}

	public static void addArc(World world, Vec3d start, Vec3d end, int count, float diffusionRate, int color){
		addArc(world, (float) start.x, (float) start.y, (float) start.z, (float) end.x, (float) end.y, (float) end.z, count, diffusionRate, color);
	}

	public static void addArc(World world, float xSt, float ySt, float zSt, float xEn, float yEn, float zEn, int count, float diffusionRate, int color){
		addArc(world, xSt, ySt, zSt, xEn, yEn, zEn, xSt, ySt, zSt, count, diffusionRate, (byte) 10, color);
	}

	public static void addArc(World world, float xSt, float ySt, float zSt, float xEn, float yEn, float zEn, float xStFin, float yStFin, float zStFin, int count, float diffusionRate, byte lifespan, int color){
		//I have decided I hate this sound on loop
		//		world.playSound(null, xSt, ySt, zSt, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 1F, 1.6F);

		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt("id", 1);
		nbt.putFloat("x", xSt);
		nbt.putFloat("y", ySt);
		nbt.putFloat("z", zSt);
		nbt.putFloat("x_e", xEn);
		nbt.putFloat("y_e", yEn);
		nbt.putFloat("z_e", zEn);
		nbt.putFloat("x_f", xStFin);
		nbt.putFloat("y_f", yStFin);
		nbt.putFloat("z_f", zStFin);
		nbt.putInt("count", count);
		nbt.putFloat("diffu", diffusionRate);
		nbt.putInt("color", color);
		nbt.putByte("lif", lifespan);

		if(world.isRemote){
			SafeCallable.effectsToRender.add(visualFactories[1].apply(nbt));
		}else{
			CRPackets.sendPacketAround(world, new BlockPos(xSt, ySt, zSt), new AddVisualToClient(nbt));
		}
	}

	//Tessellation utilities

	/**
	 * Gets the texture atlas sprite for a texture in the blocks texture map. Does not work for other texture maps
	 * @param location The location of the texture (including file ending)
	 * @return The corresponding texture atlas sprite
	 */
	public static TextureAtlasSprite getTextureSprite(ResourceLocation location){
		return Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(location);
	}

	/**
	 * Gets the time to be used for rendering animations
	 * @param partialTicks The partial ticks [0, 1]
	 * @param world Any world reference
	 * @return The animation render time
	 */
	public static float getRenderTime(float partialTicks, @Nullable World world){
		if(world == null){
			return 0;//We really don't want to crash for this
		}else{
			return world.getGameTime() % Integer.MAX_VALUE + partialTicks;
		}
	}

	/**
	 * Finds the normal vector for a vertex
	 * If all vertices in a polygon are in the same plane, this result will apply to all
	 * @param point0 The position of the vertex this normal is for
	 * @param point1 The position of an adjacent vertex
	 * @param point2 The position of the other adjacent vertex
	 * @return The normal vector
	 */
	public static Vec3d findNormal(Vec3d point0, Vec3d point1, Vec3d point2){
		point1 = point1.subtract(point0);
		point2 = point2.subtract(point0);
		return point1.crossProduct(point2);
	}

	/**
	 * Finds the combined packed light at a position, for rendering
	 * @param world The world
	 * @param pos The position to check
	 * @return The light value, as a packed lightmap coordinate
	 */
	public static int getLightAtPos(World world, BlockPos pos){
		if(world != null){
			return WorldRenderer.getCombinedLight(world, pos);
		}else{
			return BRIGHT_LIGHT;
		}
	}

	/**
	 * Calculates a combined light coordinate for things that should 'slightly' glow in the dark
	 * Returns the world light if above a minimum value, otherwise brings it to the minimum
	 * @param worldLight The combined light coordinate in the world
	 * @return The light value to use for a moderate glow-in-the-dark effect
	 */
	public static int calcMediumLighting(int worldLight){
		int skyLight = (worldLight >> 16) & 0xF0;
		int blockLight = worldLight & 0xF0;
		if(blockLight < 0x80){
			blockLight = 0x80;//Minimum block light level of 8.
			return (skyLight << 16) | blockLight;
		}

		return worldLight;
	}

	/**
	 * Adds a long, thin triangular prism '3d line' to the vertex buffer
	 * Expects the buffer to be drawing in QUADS mode, DefaultVertexFormats.POSITION
	 * Will not finalize the draw
	 * @param buf The buffer to write vertices to
	 * @param start Vector position to start from
	 * @param end Vector position to end at
	 * @param width Width of the line to draw- same scale as position vectors
	 */
	@OnlyIn(Dist.CLIENT)
	public static void draw3dLine(BufferBuilder buf, Vec3d start, Vec3d end, double width){
		Vec3d lineVec = end.subtract(start);
		//Find any perpendicular vector using a deterministic method
		Vec3d[] perpVec = new Vec3d[3];
		perpVec[0] = lineVec.crossProduct(VEC_I);
		if(perpVec[0].lengthSquared() == 0){
			perpVec[0] = lineVec.crossProduct(VEC_J);
		}
		//width * sqrt(3) / 4 is the length for center-to-tip distance of equilateral triangle that will be formed by the 3 quads
		perpVec[0] = perpVec[0].scale(width * Math.sqrt(3) / 4 / perpVec[0].length());
		//Rotate perVecA +-120deg about lineVec using a simplified form of Rodrigues' rotation formula
		Vec3d compA = perpVec[0].scale(-.5D);//perpVecA * cos(120deg)
		Vec3d compB = perpVec[0].crossProduct(lineVec.normalize()).scale(Math.sqrt(3) / 2D);//(perpVecA x unit vector of lineVec) * sin(120deg)
		perpVec[1] = compA.add(compB);
		perpVec[2] = compA.subtract(compB);
		//perpVec 0, 1, & 2 represent the vertices of the triangular ends of the triangular prism formed, relative to start (or end)

		for(int i = 0; i < 3; i++){
			Vec3d offsetPrev = perpVec[i];
			Vec3d offsetNext = perpVec[(i + 1) % perpVec.length];
			buf.pos(start.getX() + offsetPrev.getX(), start.getY() + offsetPrev.getY(), start.getZ() + offsetPrev.getZ()).endVertex();
			buf.pos(end.getX() + offsetPrev.getX(), end.getY() + offsetPrev.getY(), end.getZ() + offsetPrev.getZ()).endVertex();
			buf.pos(end.getX() + offsetNext.getX(), end.getY() + offsetNext.getY(), end.getZ() + offsetNext.getZ()).endVertex();
			buf.pos(start.getX() + offsetNext.getX(), start.getY() + offsetNext.getY(), start.getZ() + offsetNext.getZ()).endVertex();
		}
	}
}
