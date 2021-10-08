package com.Da_Technomancer.crossroads.render;

import com.Da_Technomancer.crossroads.API.packets.AddVisualToClient;
import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.essentials.render.RenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.function.BiFunction;

public class CRRenderUtil extends RenderUtil{

	public static final Vec3 VEC_I = new Vec3(1, 0, 0);
	public static final Vec3 VEC_J = new Vec3(0, 1, 0);
	public static final Vec3 VEC_K = new Vec3(0, 0, 1);
	private static final int[] WHITE_COLOR = {255, 255, 255, 255};

	/**
	 * Used internally. Public only for packet use
	 */
	@SuppressWarnings("unchecked")
	public static final BiFunction<Level, CompoundTag, IVisualEffect>[] visualFactories = (BiFunction<Level, CompoundTag, IVisualEffect>[]) new BiFunction[3];

	static{
		visualFactories[0] = LooseBeamRenderable::readFromNBT;
		visualFactories[1] = LooseArcRenderable::readFromNBT;
		visualFactories[2] = LooseEntropyRenderable::readFromNBT;
	}

	//Pre-made render effects

	public static void addBeam(Level world, double x, double y, double z, double length, float angleX, float angleY, byte width, int color){
		CompoundTag nbt = new CompoundTag();
		nbt.putInt("id", 0);
		nbt.putDouble("x", x);
		nbt.putDouble("y", y);
		nbt.putDouble("z", z);
		nbt.putDouble("length", length);
		nbt.putFloat("angle_x", angleX);
		nbt.putFloat("angle_y", angleY);
		nbt.putByte("width", width);
		nbt.putInt("color", color);
		CRPackets.sendEffectPacketAround(world, new BlockPos(x, y, z), new AddVisualToClient(nbt));
	}

	public static void addArc(Level world, Vec3 start, Vec3 end, int count, float diffusionRate, int color){
		addArc(world, (float) start.x, (float) start.y, (float) start.z, (float) end.x, (float) end.y, (float) end.z, count, diffusionRate, color);
	}

	public static void addArc(Level world, float xSt, float ySt, float zSt, float xEn, float yEn, float zEn, int count, float diffusionRate, int color){
		addArc(world, xSt, ySt, zSt, xEn, yEn, zEn, count, diffusionRate, (byte) 5, color, true);
	}

	public static void addArc(Level world, float xSt, float ySt, float zSt, float xEn, float yEn, float zEn, int count, float diffusionRate, byte lifespan, int color, boolean playSound){
		boolean sound = playSound && CRConfig.electricSounds.get();
		CompoundTag nbt = new CompoundTag();
		nbt.putInt("id", 1);
		nbt.putFloat("x", xSt);
		nbt.putFloat("y", ySt);
		nbt.putFloat("z", zSt);
		nbt.putFloat("x_e", xEn);
		nbt.putFloat("y_e", yEn);
		nbt.putFloat("z_e", zEn);
//		nbt.putFloat("x_f", xSt);
//		nbt.putFloat("y_f", ySt);
//		nbt.putFloat("z_f", zSt);
		nbt.putInt("count", count);
		nbt.putFloat("diffu", diffusionRate);
		nbt.putInt("color", color);
		nbt.putByte("lif", lifespan);
		nbt.putBoolean("sound", sound);

		//I have decided I hate this sound on loop
		//world.playSound(null, xSt, ySt, zSt, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 1F, 1.6F);
		if(world.isClientSide){
			AddVisualToClient.effectsToRender.add(visualFactories[1].apply(world, nbt));
		}else{
			CRPackets.sendEffectPacketAround(world, new BlockPos((xSt + xEn) / 2F, (ySt + yEn) / 2F, (zSt + zEn) / 2F), new AddVisualToClient(nbt));
		}
	}

	@Deprecated
	public static void addEntropyBeam(Level world, float xSt, float ySt, float zSt, float xEn, float yEn, float zEn, int qty, byte lifespan, boolean playSound){
		boolean sound = playSound && CRConfig.fluxSounds.get();
		CompoundTag nbt = new CompoundTag();
		nbt.putInt("id", 2);
		nbt.putFloat("x_st", xSt);
		nbt.putFloat("y_st", ySt);
		nbt.putFloat("z_st", zSt);
		nbt.putFloat("x_en", xEn);
		nbt.putFloat("y_en", yEn);
		nbt.putFloat("z_en", zEn);
		nbt.putByte("lifespan", lifespan);
		nbt.putInt("quantity", qty);
		nbt.putBoolean("sound", sound);

		if(world.isClientSide){
			AddVisualToClient.effectsToRender.add(visualFactories[2].apply(world, nbt));
		}else{
			CRPackets.sendEffectPacketAround(world, new BlockPos((xSt + xEn) / 2F, (ySt + yEn) / 2F, (zSt + zEn) / 2F), new AddVisualToClient(nbt));
		}
	}

	//Tessellation utilities

	/**
	 * Gets the texture atlas sprite for a texture in the blocks texture map. Does not work for other texture maps
	 * @param location The location of the texture (including file ending)
	 * @return The corresponding texture atlas sprite
	 */
	public static TextureAtlasSprite getTextureSprite(ResourceLocation location){
		return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(location);
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
	public static void addVertexBlock(VertexConsumer builder, PoseStack matrix, float x, float y, float z, float u, float v, float normalX, float normalY, float normalZ, int light, int[] col){
		builder.vertex(matrix.last().pose(), x, y, z).color(col[0], col[1], col[2], col[3]).uv(u, v).uv2(light).normal(matrix.last().normal(), normalX, normalY, normalZ).endVertex();
	}

	/**
	 * Adds a vertex to the builder using the ENTITY vertex format
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
	public static void addVertexEntity(VertexConsumer builder, PoseStack matrix, float x, float y, float z, float u, float v, float normalX, float normalY, float normalZ, int light, int[] col){
		builder.vertex(matrix.last().pose(), x, y, z).color(col[0], col[1], col[2], col[3]).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix.last().normal(), normalX, normalY, normalZ).endVertex();
	}

	/**
	 * Adds a vertex to the builder using the ENTITY vertex format
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
	 */
	@OnlyIn(Dist.CLIENT)
	public static void addVertexEntity(VertexConsumer builder, PoseStack matrix, float x, float y, float z, float u, float v, float normalX, float normalY, float normalZ, int light){
		addVertexEntity(builder, matrix, x, y, z, u, v, normalX, normalY, normalZ, light, WHITE_COLOR);
	}

	/**
	 * Gets the time to be used for rendering animations
	 * @param partialTicks The partial ticks [0, 1]
	 * @param world Any world reference
	 * @return The animation render time
	 */
	public static float getRenderTime(float partialTicks, @Nullable Level world){
		if(world == null){
			return 0;//We really don't want to crash for this
		}else{
			//Caps the returned gametime at 1 real day, to reduce floating point error
			return world.getGameTime() % (20 * 60 * 60 * 24) + partialTicks;
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
	public static Vec3 findNormal(Vec3 point0, Vec3 point1, Vec3 point2){
		point1 = point1.subtract(point0);
		point2 = point2.subtract(point0);
		return point1.cross(point2);
	}

	/**
	 * Finds the combined packed light at a position, for rendering
	 * @param world The world
	 * @param pos The position to check
	 * @return The light value, as a packed lightmap coordinate
	 */
	public static int getLightAtPos(Level world, BlockPos pos){
		if(world != null){
			return LevelRenderer.getLightColor(world, pos);
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
	public static Vec3 getCameraPos(){
		return Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
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
	public static void draw3dLine(VertexConsumer builder, PoseStack matrix, Vec3 start, Vec3 end, double width, int[] col, int light){
		Vec3 lineVec = end.subtract(start);
		//Find any perpendicular vector using a deterministic method
		Vec3[] perpVec = new Vec3[3];
		perpVec[0] = lineVec.cross(VEC_I);
		if(perpVec[0].lengthSqr() == 0){
			perpVec[0] = lineVec.cross(VEC_J);
		}
		//width * sqrt(3) / 4 is the length for center-to-tip distance of equilateral triangle that will be formed by the 3 quads
		perpVec[0] = perpVec[0].scale(width * Math.sqrt(3) / 4 / perpVec[0].length());
		//Rotate perVecA +-120deg about lineVec using a simplified form of Rodrigues' rotation formula
		Vec3 compA = perpVec[0].scale(-.5D);//perpVecA * cos(120deg)
		Vec3 compB = perpVec[0].cross(lineVec.normalize()).scale(Math.sqrt(3) / 2D);//(perpVecA x unit vector of lineVec) * sin(120deg)
		perpVec[1] = compA.add(compB);
		perpVec[2] = compA.subtract(compB);
		//perpVec 0, 1, & 2 represent the vertices of the triangular ends of the triangular prism formed, relative to start (or end)

		for(int i = 0; i < 3; i++){
			Vec3 offsetPrev = perpVec[i];
			Vec3 offsetNext = perpVec[(i + 1) % perpVec.length];
			builder.vertex(matrix.last().pose(), (float) (start.x() + offsetPrev.x()), (float) (start.y() + offsetPrev.y()), (float) (start.z() + offsetPrev.z())).color(col[0], col[1], col[2], col[3]).uv2(light).endVertex();
			builder.vertex(matrix.last().pose(), (float) (end.x() + offsetPrev.x()), (float) (end.y() + offsetPrev.y()), (float) (end.z() + offsetPrev.z())).color(col[0], col[1], col[2], col[3]).uv2(light).endVertex();
			builder.vertex(matrix.last().pose(), (float) (end.x() + offsetNext.x()), (float) (end.y() + offsetNext.y()), (float) (end.z() + offsetNext.z())).color(col[0], col[1], col[2], col[3]).uv2(light).endVertex();
			builder.vertex(matrix.last().pose(), (float) (start.x() + offsetNext.x()), (float) (start.y() + offsetNext.y()), (float) (start.z() + offsetNext.z())).color(col[0], col[1], col[2], col[3]).uv2(light).endVertex();
		}
	}
}
