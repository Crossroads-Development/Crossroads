package com.Da_Technomancer.crossroads.render;

import com.Da_Technomancer.crossroads.particles.sounds.CRSounds;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

import java.util.Random;

public class LooseEntropyRenderable implements IVisualEffect{

	private final float xSt;
	private final float ySt;
	private final float zSt;
	private final float length;
	private final float angleX;
	private final float angleY;
	private final int qty;
	private byte lifeTime;
	private long lastTick = 0;

	public LooseEntropyRenderable(World world, float xSt, float ySt, float zSt, float xEn, float yEn, float zEn, int qty, byte lifeTime, boolean sound){
		this.xSt = xSt;
		this.ySt = ySt;
		this.zSt = zSt;
		this.length = (float) Math.sqrt(Math.pow(xEn - xSt, 2F) + Math.pow(yEn - ySt, 2F) + Math.pow(zEn - zSt, 2F));
		this.angleX = (float) Math.atan2(-(yEn - ySt), Math.sqrt(Math.pow(xEn - xSt, 2F) + Math.pow(zEn - zSt, 2F)));
		this.angleY = (float) Math.atan2(-(xEn - xSt), zEn - zSt);
		this.qty = qty;
		this.lifeTime = lifeTime;
		if(sound){
			CRSounds.playSoundClientLocal(world, new BlockPos((xSt + xEn) / 2F, (ySt + yEn) / 2F, (zSt + zEn) / 2F), CRSounds.FLUX_TRANSFER, SoundCategory.BLOCKS, 0.4F, 1F);
		}
	}

	public static LooseEntropyRenderable readFromNBT(World world, CompoundNBT nbt){
		return new LooseEntropyRenderable(world, nbt.getFloat("x_st"), nbt.getFloat("y_st"), nbt.getFloat("z_st"), nbt.getFloat("x_en"), nbt.getFloat("y_en"), nbt.getFloat("z_en"), nbt.getInt("quantity"), nbt.getByte("lifespan"), nbt.getBoolean("sound"));
	}

	@Override
	public boolean render(MatrixStack matrix, IRenderTypeBuffer buffer, long worldTime, float partialTicks, Random rand){
		matrix.translate(xSt, ySt, zSt);
		matrix.rotate(Vector3f.YP.rotation(-angleY));
		matrix.rotate(Vector3f.XP.rotation(angleX + (float) Math.PI / 2F));

		IVertexBuilder builder = buffer.getBuffer(CRRenderTypes.FLUX_TRANSFER_TYPE);

		float unitLen = 0.5F;
		int lenCount = (int) (length / unitLen);

		matrix.scale(1, length / (unitLen * lenCount), 1);//As lenCount is an integer, this scale factor may slightly stretch the entire render to account for rounding error

		for(int i = 0; i < 3; i++){
			matrix.rotate(Vector3f.YP.rotationDegrees(12.5F + i * (i == 2 ? -1 : 1) * (worldTime + partialTicks) * 20F));
			float lenOffset = i * 0.2F;
			float radius = i / 20F + 0.03F;
			int circumCount = (int) (radius * 64F);
			float angle = (float) Math.PI * 2F / circumCount;
			Quaternion stepRotation = Vector3f.YP.rotation(angle);
			Quaternion lengthRotation = Vector3f.YP.rotation(angle / 3F);
			for(int j = 0; j < circumCount; j++){
				matrix.rotate(stepRotation);
				matrix.push();
				for(int k = 0; k < lenCount; k++){
					matrix.rotate(lengthRotation);
					float sideRad = ((i + j + k) % 3) * 0.007F + 0.005F;
					float pieceLen = 0.3F + ((i + j * 3 + k * 2) % 4) * 0.05F;
					int[] color = ((i + j * 2 + k) % 7) == 0 ? new int[] {255, 255, 255, 64} : new int[] {0, 0, 0, 255};
					builder.pos(matrix.getLast().getMatrix(), radius, k * unitLen + lenOffset, -sideRad).color(color[0], color[1], color[2], color[3]).tex(0, 0).endVertex();
					builder.pos(matrix.getLast().getMatrix(), radius, k * unitLen + lenOffset, sideRad).color(color[0], color[1], color[2], color[3]).tex(0, 1).endVertex();
					builder.pos(matrix.getLast().getMatrix(), radius, k * unitLen + pieceLen + lenOffset, sideRad).color(color[0], color[1], color[2], color[3]).tex(1, 1).endVertex();
					builder.pos(matrix.getLast().getMatrix(), radius, k * unitLen + pieceLen + lenOffset, -sideRad).color(color[0], color[1], color[2], color[3]).tex(1, 0).endVertex();
				}
				matrix.pop();
			}
		}

		if(lastTick != worldTime){
			lastTick = worldTime;
			return --lifeTime < 0;
		}

		return false;
	}
}
