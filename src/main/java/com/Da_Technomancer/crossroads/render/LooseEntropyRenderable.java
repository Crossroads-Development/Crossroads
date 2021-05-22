package com.Da_Technomancer.crossroads.render;

import com.Da_Technomancer.crossroads.ambient.sounds.CRSounds;
import com.Da_Technomancer.crossroads.render.TESR.EntropyRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

import java.util.Random;

@Deprecated
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
		matrix.mulPose(Vector3f.YP.rotation(-angleY));
		matrix.mulPose(Vector3f.XP.rotation(angleX + (float) Math.PI / 2F));

		IVertexBuilder builder = buffer.getBuffer(CRRenderTypes.FLUX_TRANSFER_TYPE);
		EntropyRenderer.renderArc(length, matrix, builder, worldTime, partialTicks);

		if(lastTick != worldTime){
			lastTick = worldTime;
			return --lifeTime < 0;
		}

		return false;
	}
}
