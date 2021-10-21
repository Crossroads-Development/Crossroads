package com.Da_Technomancer.crossroads.render;

import com.Da_Technomancer.crossroads.ambient.sounds.CRSounds;
import com.Da_Technomancer.crossroads.render.TESR.EntropyRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import com.mojang.math.Vector3f;
import net.minecraft.world.level.Level;

import java.util.Random;

/**
 * @deprecated Due to the main usecase being blocks, and it generally being inefficient for blocks to send packets to render entropy from the server instead of handling rendering on the client
 * Use EntropyRenderer::renderArc from the client side instead
 */
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

	public LooseEntropyRenderable(Level world, float xSt, float ySt, float zSt, float xEn, float yEn, float zEn, int qty, byte lifeTime, boolean sound){
		this.xSt = xSt;
		this.ySt = ySt;
		this.zSt = zSt;
		this.length = (float) Math.sqrt(Math.pow(xEn - xSt, 2F) + Math.pow(yEn - ySt, 2F) + Math.pow(zEn - zSt, 2F));
		this.angleX = (float) Math.atan2(-(yEn - ySt), Math.sqrt(Math.pow(xEn - xSt, 2F) + Math.pow(zEn - zSt, 2F)));
		this.angleY = (float) Math.atan2(-(xEn - xSt), zEn - zSt);
		this.qty = qty;
		this.lifeTime = lifeTime;
		if(sound){
			CRSounds.playSoundClientLocal(world, new BlockPos((xSt + xEn) / 2F, (ySt + yEn) / 2F, (zSt + zEn) / 2F), CRSounds.FLUX_TRANSFER, SoundSource.BLOCKS, 0.4F, 1F);
		}
	}

	public static LooseEntropyRenderable readFromNBT(Level world, CompoundTag nbt){
		return new LooseEntropyRenderable(world, nbt.getFloat("x_st"), nbt.getFloat("y_st"), nbt.getFloat("z_st"), nbt.getFloat("x_en"), nbt.getFloat("y_en"), nbt.getFloat("z_en"), nbt.getInt("quantity"), nbt.getByte("lifespan"), nbt.getBoolean("sound"));
	}

	@Override
	public boolean render(PoseStack matrix, MultiBufferSource buffer, long worldTime, float partialTicks, Random rand){
		matrix.translate(xSt, ySt, zSt);
		matrix.mulPose(Vector3f.YP.rotation(-angleY));
		matrix.mulPose(Vector3f.XP.rotation(angleX + (float) Math.PI / 2F));

		VertexConsumer builder = buffer.getBuffer(CRRenderTypes.FLUX_TRANSFER_TYPE);
		EntropyRenderer.renderArc(length, matrix, builder, worldTime, partialTicks);

		if(lastTick != worldTime){
			lastTick = worldTime;
			return --lifeTime < 0;
		}

		return false;
	}
}
