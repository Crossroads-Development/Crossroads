package com.Da_Technomancer.crossroads.client.TESR.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelPump extends ModelBase{
	ModelRenderer core;
	ModelRenderer screwA;
	ModelRenderer screwB;
	ModelRenderer screwC;

	public ModelPump(){
		textureWidth = 64;
		textureHeight = 32;

		core = new ModelRenderer(this, 56, 0);
		core.addBox(0F, 0F, 0F, 2, 17, 2);
		core.setRotationPoint(-1F, 7F, -1F);
		core.setTextureSize(64, 32);
		core.mirror = true;
		setRotation(core, 0F, 0F, 0F);
		screwA = new ModelRenderer(this, 5, 12);
		screwA.addBox(0F, 0F, 0F, 6, 1, 3);
		screwA.setRotationPoint(-3F, 21F, 1F);
		screwA.setTextureSize(64, 32);
		screwA.mirror = true;
		setRotation(screwA, 0F, 0F, 0.3926991F);
		screwB = new ModelRenderer(this, 39, 0);
		screwB.addBox(0F, 0F, 0F, 3, 1, 5);
		screwB.setRotationPoint(-4F, 19F, -3F);
		screwB.setTextureSize(64, 32);
		screwB.mirror = true;
		setRotation(screwB, -0.3926991F, 0, 0);
		screwC = new ModelRenderer(this, 5, 7);
		screwC.addBox(0F, 0F, 0F, 5, 1, 3);
		screwC.setRotationPoint(3F, 17F, -1F);
		screwC.setTextureSize(64, 32);
		screwC.mirror = true;
		setRotation(screwC, 0F, -3.141593F, -0.3926991F);
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