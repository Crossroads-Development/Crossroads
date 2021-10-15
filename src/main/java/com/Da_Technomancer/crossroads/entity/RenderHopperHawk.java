package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderHopperHawk extends MobRenderer<EntityHopperHawk, ModelHopperHawk>{

	public static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/entities/hopper_hawk.png");

	protected RenderHopperHawk(EntityRendererProvider.Context context){
		super(context, new ModelHopperHawk(), 0.3F);
	}

	@Override
	public ResourceLocation getTextureLocation(EntityHopperHawk entity){
		return TEXTURE;
	}

	@Override
	public float getBob(EntityHopperHawk entity, float p_77044_2_){
		float f = Mth.lerp(p_77044_2_, entity.oFlap, entity.flap);
		float f1 = Mth.lerp(p_77044_2_, entity.oFlapSpeed, entity.flapSpeed);
		return (Mth.sin(f) + 1.0F) * f1;
	}
}
