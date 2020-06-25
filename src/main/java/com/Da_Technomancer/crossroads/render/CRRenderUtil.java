package com.Da_Technomancer.crossroads.render;

import com.Da_Technomancer.crossroads.API.packets.AddVisualToClient;
import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.packets.SafeCallable;
import com.Da_Technomancer.essentials.render.RenderUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
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
	 * Adds a vertex to the builder using the BLOCK vertex format
	 * @param builder The active builder
	 * @param matrix The reference matrix
	 * @param x The x position of this vertex
	 * @param y The y position of this vertex
	 * @param z The z position of this vertex
	 * @param u The u coord of this vertex texture mapping
	 * @param v The v coord of this vertex texture mapping
	 * @param normalX The normal x component to this vertex
	 * @param normalY The normal y component to this vertex
	 * @param normalZ The normal z component to this vertex
	 * @param light The light value
	 * @param col A size 4 array (r, g, b, a) defining the color, scale [0, 255]
	 */
	@OnlyIn(Dist.CLIENT)
	public static void addVertexBlock(IVertexBuilder builder, MatrixStack matrix, float x, float y, float z, float u, float v, float normalX, float normalY, float normalZ, int light, int[] col){
		builder.pos(matrix.getLast().getMatrix(), x, y, z).color(col[0], col[1], col[2], col[3]).tex(u, v).lightmap(light).normal(normalX, normalY, normalZ).endVertex();
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
	 * Converts a color to a size 4 integer array of values [0, 255], in order r,g,b,a
	 * @param color The source color
	 * @return A size 4 array
	 */
	@Nonnull
	public static int[] convertColor(@Nonnull Color color){
		return new int[] {color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()};
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
	 * Gets the position of the player's camera
	 * Client side only
	 * @return The camera position relative to the world
	 */
	@OnlyIn(Dist.CLIENT)
	public static Vec3d getCameraPos(){
		return Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
	}

	/**
	 * Draws a long, thin triangular prism '3d line'
	 * Expects the buffer to be drawing in QUADS mode, DefaultVertexFormats.POSITION
	 * @param builder A builder with QUADS mode in DefaultVertexFormats.POSITION_COLOR_LIGHTMAP
	 * @param matrix Matrix translated to world origin (0, 0, 0). Will not be modified
	 * @param start Vector position to start from
	 * @param end Vector position to end at
	 * @param width Width of the line to draw- same scale as position vectors
	 * @param col A size 4 integer array representing color (r, g, b, a) values [0, 255]
	 * @param light The combined light coordinate to render with
	 */
	@OnlyIn(Dist.CLIENT)
	public static void draw3dLine(IVertexBuilder builder, MatrixStack matrix, Vec3d start, Vec3d end, double width, int[] col, int light){
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
			builder.pos(matrix.getLast().getMatrix(), (float) (start.getX() + offsetPrev.getX()), (float) (start.getY() + offsetPrev.getY()), (float) (start.getZ() + offsetPrev.getZ())).color(col[0], col[1], col[2], col[3]).lightmap(light).endVertex();
			builder.pos(matrix.getLast().getMatrix(), (float) (end.getX() + offsetPrev.getX()), (float) (end.getY() + offsetPrev.getY()), (float) (end.getZ() + offsetPrev.getZ())).color(col[0], col[1], col[2], col[3]).lightmap(light).endVertex();
			builder.pos(matrix.getLast().getMatrix(), (float) (end.getX() + offsetNext.getX()), (float) (end.getY() + offsetNext.getY()), (float) (end.getZ() + offsetNext.getZ())).color(col[0], col[1], col[2], col[3]).lightmap(light).endVertex();
			builder.pos(matrix.getLast().getMatrix(), (float) (start.getX() + offsetNext.getX()), (float) (start.getY() + offsetNext.getY()), (float) (start.getZ() + offsetNext.getZ())).color(col[0], col[1], col[2], col[3]).lightmap(light).endVertex();
		}
	}
}
