package com.Da_Technomancer.crossroads.render.bakedModel;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;

public class RenderUtil{

	private static void putVertex(UnpackedBakedQuad.Builder builder, Vec3d normal, double x, double y, double z, float u, float v, TextureAtlasSprite sprite){
		VertexFormat vf = builder.getVertexFormat();
		for(int e = 0; e < vf.getElementCount(); e++){
			switch(vf.getElement(e).getUsage()){
				case POSITION:
					builder.put(e, (float) x, (float) y, (float) z, 1.0f);
					break;
				case COLOR:
					builder.put(e, 1.0f, 1.0f, 1.0f, 1.0f);
					break;
				case UV:
					if(vf.getElement(e).getIndex() == 0){
						u = sprite.getInterpolatedU(u);
						v = sprite.getInterpolatedV(v);
						builder.put(e, u, v, 0f, 1f);
						break;
					}
				case NORMAL:
					builder.put(e, (float) normal.x, (float) normal.y, (float) normal.z, 0f);
					break;
				default:
					builder.put(e);
					break;
			}
		}
	}

	/**
	 * Creates a quad, with all four corners being custom
	 * @param v1 First corner pos
	 * @param v2 Second corner pos
	 * @param v3 Third corner pos
	 * @param v4 Fourth corner pos
	 * @param sprite The texture
	 * @param side The orientation of the plane
	 * @param vf The VertexFormat in use
	 * @return The created quad
	 */
	public static BakedQuad createQuad(Vec3d v1, Vec3d v2, Vec3d v3, Vec3d v4, TextureAtlasSprite sprite, EnumFacing side, VertexFormat vf){
		return createQuad(v1, v2, v3, v4, sprite, 0, 0, 16, 16, side, vf);
	}

	/**
	 * Creates a quad, with all four corners being custom
	 * @param v1 First corner pos
	 * @param v2 Second corner pos
	 * @param v3 Third corner pos
	 * @param v4 Fourth corner pos
	 * @param sprite The texture
	 * @param uStart Starting u texture coord
	 * @param vStart Starting v texture coord
	 * @param uEnd Ending u texture coord
	 * @param vEnd Ending v texture coord
	 * @param side The orientation of the plane
	 * @param vf The VertexFormat in use
	 * @return The created quad
	 */
	public static BakedQuad createQuad(Vec3d v1, Vec3d v2, Vec3d v3, Vec3d v4, TextureAtlasSprite sprite, int uStart, int vStart, int uEnd, int vEnd, EnumFacing side, VertexFormat vf){
		Vec3d normal = v3.subtract(v2).crossProduct(v1.subtract(v2));

		UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(vf);
		builder.setQuadOrientation(side);
		builder.setTexture(sprite);
		builder.setApplyDiffuseLighting(false);
		putVertex(builder, normal, v1.x, v1.y, v1.z, uStart, vStart, sprite);
		putVertex(builder, normal, v2.x, v2.y, v2.z, uStart, vEnd, sprite);
		putVertex(builder, normal, v3.x, v3.y, v3.z, uEnd, vEnd, sprite);
		putVertex(builder, normal, v4.x, v4.y, v4.z, uEnd, vStart, sprite);
		return builder.build();
	}
}
