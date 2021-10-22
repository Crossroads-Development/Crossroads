package com.Da_Technomancer.crossroads.render;

import com.Da_Technomancer.crossroads.entity.EntityHopperHawk;
import com.Da_Technomancer.crossroads.entity.ModelHopperHawk;
import com.Da_Technomancer.crossroads.entity.RenderHopperHawk;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HopperHawkShoulderRenderer<T extends Player> extends RenderLayer<T, PlayerModel<T>>{

	private final ModelHopperHawk model;

	public HopperHawkShoulderRenderer(RenderLayerParent<T, PlayerModel<T>> p_i50929_1_, EntityModelSet modelSet){
		super(p_i50929_1_);
		model = new ModelHopperHawk(modelSet.bakeLayer(RenderHopperHawk.HOPPER_HAWK_MODEL_LAYER));
	}

	@Override
	public void render(PoseStack p_225628_1_, MultiBufferSource p_225628_2_, int p_225628_3_, T p_225628_4_, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_){
		this.render(p_225628_1_, p_225628_2_, p_225628_3_, p_225628_4_, p_225628_5_, p_225628_6_, p_225628_9_, p_225628_10_, true);
		this.render(p_225628_1_, p_225628_2_, p_225628_3_, p_225628_4_, p_225628_5_, p_225628_6_, p_225628_9_, p_225628_10_, false);
	}

	private void render(PoseStack p_229136_1_, MultiBufferSource p_229136_2_, int p_229136_3_, T p_229136_4_, float p_229136_5_, float p_229136_6_, float p_229136_7_, float p_229136_8_, boolean p_229136_9_){
		CompoundTag compoundnbt = p_229136_9_ ? p_229136_4_.getShoulderEntityLeft() : p_229136_4_.getShoulderEntityRight();
		EntityType.byString(compoundnbt.getString("id")).filter((p_215344_0_) -> {
			return p_215344_0_ == EntityHopperHawk.type;
		}).ifPresent((p_229137_11_) -> {
			p_229136_1_.pushPose();
			p_229136_1_.translate(p_229136_9_ ? (double) 0.4F : (double) -0.4F, p_229136_4_.isCrouching() ? (double) -1.3F : -1.5D, 0.0D);
			VertexConsumer ivertexbuilder = p_229136_2_.getBuffer(this.model.renderType(RenderHopperHawk.TEXTURE));
			this.model.renderOnShoulder(p_229136_1_, ivertexbuilder, p_229136_3_, OverlayTexture.NO_OVERLAY, p_229136_5_, p_229136_6_, p_229136_7_, p_229136_8_, p_229136_4_.tickCount);
			p_229136_1_.popPose();
		});
	}
}