package com.Da_Technomancer.crossroads.render.TESR.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelPump extends ModelBase{

	private ModelRenderer core;
	private ModelRenderer screwA;
	private ModelRenderer screwB;
	private ModelRenderer screwC;

	public ModelPump(){
		textureWidth = 64;
		textureHeight = 32;

		core = new ModelRenderer(this, 56, 0);
		core.addBox(-1F, 0F, -1F, 2, 17, 2);
		core.setRotationPoint(0F, 0F, 0F);
		core.setTextureSize(64, 32);
		core.mirror = true;
		setRotation(core, 0F, 0F, 0F);
		screwA = new ModelRenderer(this, 5, 12);
		screwA.addBox(-3F, .5F, -.25F, 6, 1, 3);
		screwA.setRotationPoint(0F, 1F, 1.25F);
		screwA.setTextureSize(64, 32);
		screwA.mirror = true;
		setRotation(screwA, 0F, 0F, -0.3926991F);
		
		screwB = new ModelRenderer(this, 39, 0);
		screwB.addBox(-2.75F, 1.5F, -3.5F, 3, 1, 5);
		screwB.setRotationPoint(-1.25F, 2F, -1F);
		screwB.setTextureSize(64, 32);
		screwB.mirror = true;
		setRotation(screwB, 0.3926991F, 0, 0);
		
		screwC = new ModelRenderer(this, 5, 7);
		screwC.addBox(-1.5F, 2F, -2.75F, 5, 1, 3);
		screwC.setRotationPoint(1F, 3.5F, -1.25F);
		screwC.setTextureSize(64, 32);
		screwC.mirror = true;
		setRotation(screwC, 0F, 0F, 0.3926991F);
	}

	public void renderScrew(){
		float f = 1F / 16F;
		core.render(f);
		screwA.render(f);
		screwB.render(f);
		screwC.render(f);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z){
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}