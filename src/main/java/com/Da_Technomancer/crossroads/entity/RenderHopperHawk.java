package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderHopperHawk extends MobRenderer<EntityHopperHawk, ModelHopperHawk>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/entity/hopper_hawk.png");

	protected RenderHopperHawk(EntityRendererManager manager){
		super(manager, new ModelHopperHawk(), 0.3F);
	}

	@Override
	public ResourceLocation getTextureLocation(EntityHopperHawk entity){
		return TEXTURE;
	}

	@Override
	public float getBob(EntityHopperHawk entity, float p_77044_2_){
		float f = MathHelper.lerp(p_77044_2_, entity.oFlap, entity.flap);
		float f1 = MathHelper.lerp(p_77044_2_, entity.oFlapSpeed, entity.flapSpeed);
		return (MathHelper.sin(f) + 1.0F) * f1;
	}
}
