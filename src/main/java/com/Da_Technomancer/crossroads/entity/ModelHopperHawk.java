package com.Da_Technomancer.crossroads.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelHopperHawk extends ListModel<EntityHopperHawk>{
	private final ModelPart body;
	private final ModelPart tail;
	private final ModelPart wingLeft;
	private final ModelPart wingRight;
	private final ModelPart head;
	private final ModelPart head2;
	private final ModelPart beak1;
	private final ModelPart beak2;
	private final ModelPart feather;
	private final ModelPart legLeft;
	private final ModelPart legRight;

	public ModelHopperHawk(){
		this.texWidth = 32;
		this.texHeight = 32;
		this.body = new ModelPart(this, 2, 8);
		this.body.addBox(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F);
		this.body.setPos(0.0F, 16.5F, -3.0F);
		this.tail = new ModelPart(this, 22, 1);
		this.tail.addBox(-1.5F, -1.0F, -1.0F, 3.0F, 4.0F, 1.0F);
		this.tail.setPos(0.0F, 21.07F, 1.16F);
		this.wingLeft = new ModelPart(this, 19, 8);
		this.wingLeft.addBox(-0.5F, 0.0F, -1.5F, 1.0F, 5.0F, 3.0F);
		this.wingLeft.setPos(1.5F, 16.94F, -2.76F);
		this.wingRight = new ModelPart(this, 19, 8);
		this.wingRight.addBox(-0.5F, 0.0F, -1.5F, 1.0F, 5.0F, 3.0F);
		this.wingRight.setPos(-1.5F, 16.94F, -2.76F);
		this.head = new ModelPart(this, 2, 2);
		this.head.addBox(-1.0F, -1.5F, -1.0F, 2.0F, 3.0F, 2.0F);
		this.head.setPos(0.0F, 15.69F, -2.76F);
		this.head2 = new ModelPart(this, 10, 0);
		this.head2.addBox(-1.0F, -0.5F, -2.0F, 2.0F, 1.0F, 4.0F);
		this.head2.setPos(0.0F, -2.0F, -1.0F);
		this.head.addChild(this.head2);
		this.beak1 = new ModelPart(this, 11, 7);
		this.beak1.addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F);
		this.beak1.setPos(0.0F, -0.5F, -1.5F);
		this.head.addChild(this.beak1);
		this.beak2 = new ModelPart(this, 16, 7);
		this.beak2.addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F);
		this.beak2.setPos(0.0F, -1.75F, -2.45F);
		this.head.addChild(this.beak2);
		this.feather = new ModelPart(this, 2, 18);
		this.feather.addBox(0.0F, -4.0F, -2.0F, 0.0F, 5.0F, 4.0F);
		this.feather.setPos(0.0F, -2.15F, 0.15F);
		this.head.addChild(this.feather);
		this.legLeft = new ModelPart(this, 14, 18);
		this.legLeft.addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F);
		this.legLeft.setPos(1.0F, 22.0F, -1.05F);
		this.legRight = new ModelPart(this, 14, 18);
		this.legRight.addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F);
		this.legRight.setPos(-1.0F, 22.0F, -1.05F);
	}

	public Iterable<ModelPart> parts(){
		return ImmutableList.of(this.body, this.wingLeft, this.wingRight, this.tail, this.head, this.legLeft, this.legRight);
	}

	public void setupAnim(EntityHopperHawk p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_){
		this.setupAnim(getState(p_225597_1_), p_225597_1_.tickCount, p_225597_2_, p_225597_3_, p_225597_4_, p_225597_5_, p_225597_6_);
	}

	public void prepareMobModel(EntityHopperHawk p_212843_1_, float p_212843_2_, float p_212843_3_, float p_212843_4_){
		this.prepare(getState(p_212843_1_));
	}

	public void renderOnShoulder(PoseStack p_228284_1_, VertexConsumer p_228284_2_, int p_228284_3_, int p_228284_4_, float p_228284_5_, float p_228284_6_, float p_228284_7_, float p_228284_8_, int p_228284_9_){
		this.prepare(State.ON_SHOULDER);
		this.setupAnim(State.ON_SHOULDER, p_228284_9_, p_228284_5_, p_228284_6_, 0.0F, p_228284_7_, p_228284_8_);
		this.parts().forEach((p_228285_4_) -> {
			p_228285_4_.render(p_228284_1_, p_228284_2_, p_228284_3_, p_228284_4_);
		});
	}

	private void setupAnim(State p_217162_1_, int p_217162_2_, float p_217162_3_, float p_217162_4_, float p_217162_5_, float p_217162_6_, float p_217162_7_){
		this.head.xRot = p_217162_7_ * ((float) Math.PI / 180F);
		this.head.yRot = p_217162_6_ * ((float) Math.PI / 180F);
		this.head.zRot = 0.0F;
		this.head.x = 0.0F;
		this.body.x = 0.0F;
		this.tail.x = 0.0F;
		this.wingRight.x = -1.5F;
		this.wingLeft.x = 1.5F;
		switch(p_217162_1_){
			case SITTING:
				break;
			case STANDING:
				this.legLeft.xRot += Mth.cos(p_217162_3_ * 0.6662F) * 1.4F * p_217162_4_;
				this.legRight.xRot += Mth.cos(p_217162_3_ * 0.6662F + (float) Math.PI) * 1.4F * p_217162_4_;
			case FLYING:
			case ON_SHOULDER:
			default:
				float f2 = p_217162_5_ * 0.3F;
				this.head.y = 15.69F + f2;
				this.tail.xRot = 1.015F + Mth.cos(p_217162_3_ * 0.6662F) * 0.3F * p_217162_4_;
				this.tail.y = 21.07F + f2;
				this.body.y = 16.5F + f2;
				this.wingLeft.zRot = -0.0873F - p_217162_5_;
				this.wingLeft.y = 16.94F + f2;
				this.wingRight.zRot = 0.0873F + p_217162_5_;
				this.wingRight.y = 16.94F + f2;
				this.legLeft.y = 22.0F + f2;
				this.legRight.y = 22.0F + f2;
		}

	}

	private void prepare(State state){
		this.feather.xRot = -0.2214F;
		this.body.xRot = 0.4937F;
		this.wingLeft.xRot = -0.6981F;
		this.wingLeft.yRot = -(float) Math.PI;
		this.wingRight.xRot = -0.6981F;
		this.wingRight.yRot = -(float) Math.PI;
		this.legLeft.xRot = -0.0299F;
		this.legRight.xRot = -0.0299F;
		this.legLeft.y = 22.0F;
		this.legRight.y = 22.0F;
		this.legLeft.zRot = 0.0F;
		this.legRight.zRot = 0.0F;
		switch(state){
			case SITTING:
				float f = 1.9F;
				this.head.y = 17.59F;
				this.tail.xRot = 1.5388988F;
				this.tail.y = 22.97F;
				this.body.y = 18.4F;
				this.wingLeft.zRot = -0.0873F;
				this.wingLeft.y = 18.84F;
				this.wingRight.zRot = 0.0873F;
				this.wingRight.y = 18.84F;
				++this.legLeft.y;
				++this.legRight.y;
				++this.legLeft.xRot;
				++this.legRight.xRot;
				break;
			case STANDING:
			case ON_SHOULDER:
			default:
				break;
			case FLYING:
				this.legLeft.xRot += 0.6981317F;
				this.legRight.xRot += 0.6981317F;
		}

	}

	private static State getState(EntityHopperHawk mob){
		if(mob.isInSittingPose()){
			return State.SITTING;
		}else{
			return mob.isFlying() ? State.FLYING : State.STANDING;
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static enum State{
		FLYING,
		STANDING,
		SITTING,
		ON_SHOULDER;
	}
}