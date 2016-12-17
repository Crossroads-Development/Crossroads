package com.Da_Technomancer.crossroads.client.TESR.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;

public class ModelSteamTurbine extends ModelBase{
	// fields
	ModelRenderer glassA;
	ModelRenderer glassB;
	ModelRenderer glassC;
	ModelRenderer glassD;
	ModelRenderer top;
	ModelRenderer lowerA;
	ModelRenderer lowerB;
	ModelRenderer lowerC;
	ModelRenderer lowerD;
	ModelRenderer upperA;
	ModelRenderer upperB;
	ModelRenderer upperC;
	ModelRenderer upperD;
	ModelRenderer sideA;
	ModelRenderer sideB;
	ModelRenderer sideC;
	ModelRenderer sideD;
	ModelRenderer base;
	ModelRenderer core;
	ModelRenderer screwA;
	ModelRenderer screwB;
	ModelRenderer screwC;

	ModelRenderer upperSolidA;
	ModelRenderer upperSolidB;
	ModelRenderer upperSolidC;
	ModelRenderer upperSolidD;

	ModelRenderer connectA;
	ModelRenderer connectB;
	ModelRenderer connectC;
	ModelRenderer connectD;

	public ModelSteamTurbine(){
		textureWidth = 64;
		textureHeight = 32;

		glassA = new ModelRenderer(this, 0, 25);
		glassA.addBox(0F, 0F, 0F, 10, 6, 1);
		glassA.setRotationPoint(-5F, 17F, 5F);

		glassA.setTextureSize(64, 32);
		glassA.mirror = true;
		setRotation(glassA, 0F, 0F, 0F);

		upperSolidA = new ModelRenderer(this, 0, 17);
		upperSolidA.addBox(0F, 0F, 0F, 10, 8, 1);
		upperSolidA.setRotationPoint(-5F, 9F, 5F);
		upperSolidA.setTextureSize(64, 32);
		upperSolidA.mirror = true;
		setRotation(upperSolidA, 0F, 0F, 0F);

		glassB = new ModelRenderer(this, 0, 25);
		glassB.addBox(0F, 0F, 0F, 10, 6, 1);
		glassB.setRotationPoint(-5F, 17F, -6F);
		glassB.setTextureSize(64, 32);
		glassB.mirror = true;
		setRotation(glassB, 0F, 0F, 0F);

		upperSolidB = new ModelRenderer(this, 0, 17);
		upperSolidB.addBox(0F, 0F, 0F, 10, 8, 1);
		upperSolidB.setRotationPoint(-5F, 9F, -6F);
		upperSolidB.setTextureSize(64, 32);
		upperSolidB.mirror = true;
		setRotation(upperSolidB, 0F, 0F, 0F);

		glassC = new ModelRenderer(this, 0, 25);
		glassC.addBox(0F, 0F, 0F, 10, 6, 1);
		glassC.setRotationPoint(-6F, 17F, 5F);
		glassC.setTextureSize(64, 32);
		glassC.mirror = true;
		setRotation(glassC, 0F, 1.570796F, 0F);

		upperSolidC = new ModelRenderer(this, 0, 17);
		upperSolidC.addBox(0F, 0F, 0F, 10, 8, 1);
		upperSolidC.setRotationPoint(-6F, 9F, 5F);
		upperSolidC.setTextureSize(64, 32);
		upperSolidC.mirror = true;
		setRotation(upperSolidC, 0F, 1.570796F, 0F);

		glassD = new ModelRenderer(this, 0, 25);
		glassD.addBox(0F, 0F, 0F, 10, 6, 1);
		glassD.setRotationPoint(5F, 17F, 5F);
		glassD.setTextureSize(64, 32);
		glassD.mirror = true;
		setRotation(glassD, 0F, 1.570796F, 0F);

		upperSolidD = new ModelRenderer(this, 0, 17);
		upperSolidD.addBox(0F, 0F, 0F, 10, 8, 1);
		upperSolidD.setRotationPoint(5F, 9F, 5F);
		upperSolidD.setTextureSize(64, 32);
		upperSolidD.mirror = true;
		setRotation(upperSolidD, 0F, 1.570796F, 0F);

		top = new ModelRenderer(this, 24, 21);
		top.addBox(0F, 0F, 0F, 10, 1, 10);
		top.setRotationPoint(-5F, 8F, -5F);
		top.setTextureSize(64, 32);
		top.mirror = true;
		setRotation(top, 0F, 0F, 0F);
		lowerA = new ModelRenderer(this, 0, 0);
		lowerA.addBox(0F, 0F, 0F, 12, 1, 1);
		lowerA.setRotationPoint(-6F, 23F, 5F);
		lowerA.setTextureSize(64, 32);
		lowerA.mirror = true;
		setRotation(lowerA, 0F, 0F, 0F);
		lowerB = new ModelRenderer(this, 0, 0);
		lowerB.addBox(0F, 0F, 0F, 12, 1, 1);
		lowerB.setRotationPoint(-6F, 23F, -6F);
		lowerB.setTextureSize(64, 32);
		lowerB.mirror = true;
		setRotation(lowerB, 0F, 0F, 0F);
		lowerC = new ModelRenderer(this, 26, 0);
		lowerC.addBox(0F, 0F, 0F, 1, 1, 10);
		lowerC.setRotationPoint(-6F, 23F, -5F);
		lowerC.setTextureSize(64, 32);
		lowerC.mirror = true;
		setRotation(lowerC, 0F, 0F, 0F);
		lowerD = new ModelRenderer(this, 26, 0);
		lowerD.addBox(0F, 0F, 0F, 1, 1, 10);
		lowerD.setRotationPoint(5F, 23F, -5F);
		lowerD.setTextureSize(64, 32);
		lowerD.mirror = true;
		setRotation(lowerD, 0F, 0F, 0F);
		upperA = new ModelRenderer(this, 0, 0);
		upperA.addBox(0F, 0F, 0F, 12, 1, 1);
		upperA.setRotationPoint(-6F, 8F, 5F);
		upperA.setTextureSize(64, 32);
		upperA.mirror = true;
		setRotation(upperA, 0F, 0F, 0F);
		upperB = new ModelRenderer(this, 0, 0);
		upperB.addBox(0F, 0F, 0F, 12, 1, 1);
		upperB.setRotationPoint(-6F, 8F, -6F);
		upperB.setTextureSize(64, 32);
		upperB.mirror = true;
		setRotation(upperB, 0F, 0F, 0F);
		upperC = new ModelRenderer(this, 26, 0);
		upperC.addBox(0F, 0F, 0F, 1, 1, 10);
		upperC.setRotationPoint(-6F, 8F, -5F);
		upperC.setTextureSize(64, 32);
		upperC.mirror = true;
		setRotation(upperC, 0F, 0F, 0F);
		upperD = new ModelRenderer(this, 26, 0);
		upperD.addBox(0F, 0F, 0F, 1, 1, 10);
		upperD.setRotationPoint(5F, 8F, -5F);
		upperD.setTextureSize(64, 32);
		upperD.mirror = true;
		setRotation(upperD, 0F, 0F, 0F);
		sideA = new ModelRenderer(this, 0, 2);
		sideA.addBox(0F, 0F, 0F, 1, 14, 1);
		sideA.setRotationPoint(-6F, 9F, 5F);
		sideA.setTextureSize(64, 32);
		sideA.mirror = true;
		setRotation(sideA, 0F, 0F, 0F);
		sideB = new ModelRenderer(this, 0, 2);
		sideB.addBox(0F, 0F, 0F, 1, 14, 1);
		sideB.setRotationPoint(5F, 9F, 5F);
		sideB.setTextureSize(64, 32);
		sideB.mirror = true;
		setRotation(sideB, 0F, 0F, 0F);
		sideC = new ModelRenderer(this, 0, 2);
		sideC.addBox(0F, 0F, 0F, 1, 14, 1);
		sideC.setRotationPoint(5F, 9F, -6F);
		sideC.setTextureSize(64, 32);
		sideC.mirror = true;
		setRotation(sideC, 0F, 0F, 0F);
		sideD = new ModelRenderer(this, 0, 2);
		sideD.addBox(0F, 0F, 0F, 1, 14, 1);
		sideD.setRotationPoint(-6F, 9F, -6F);
		sideD.setTextureSize(64, 32);
		sideD.mirror = true;
		setRotation(sideD, 0F, 0F, 0F);
		base = new ModelRenderer(this, 24, 21);
		base.addBox(0F, 0F, 0F, 10, 1, 10);
		base.setRotationPoint(-5F, 16F, -5F);
		base.setTextureSize(64, 32);
		base.mirror = true;
		setRotation(base, 0F, 0F, 0F);
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

		connectA = new ModelRenderer(this, 27, 12);
		connectA.addBox(0F, 0F, 0F, 6, 6, 2);
		connectA.setRotationPoint(-3F, 13F, 6F);
		connectA.setTextureSize(64, 32);
		connectA.mirror = true;
		setRotation(connectA, 0F, 0F, 0F);

		connectB = new ModelRenderer(this, 27, 12);
		connectB.addBox(0F, 0F, 0F, 6, 6, 2);
		connectB.setRotationPoint(-3F, 13F, -8F);
		connectB.setTextureSize(64, 32);
		connectB.mirror = true;
		setRotation(connectB, 0F, 0F, 0F);

		connectC = new ModelRenderer(this, 27, 12);
		connectC.addBox(0F, 0F, 0F, 6, 6, 2);
		connectC.setRotationPoint(-8F, 13F, 3F);
		connectC.setTextureSize(64, 32);
		connectC.mirror = true;
		setRotation(connectC, 0F, 1.5707965F, 0F);

		connectD = new ModelRenderer(this, 27, 12);
		connectD.addBox(0F, 0F, 0F, 6, 6, 2);
		connectD.setRotationPoint(6F, 13F, 3F);
		connectD.setTextureSize(64, 32);
		connectD.mirror = true;
		setRotation(connectD, 0F, 1.5707965F, 0F);

	}

	public void renderMain(){
		float f = 1F / 16F;
		glassA.render(f);
		glassB.render(f);
		glassC.render(f);
		glassD.render(f);
		top.render(f);
		lowerA.render(f);
		lowerB.render(f);
		lowerC.render(f);
		lowerD.render(f);
		upperA.render(f);
		upperB.render(f);
		upperC.render(f);
		upperD.render(f);
		sideA.render(f);
		sideB.render(f);
		sideC.render(f);
		sideD.render(f);
		base.render(f);

		upperSolidA.render(f);
		upperSolidB.render(f);
		upperSolidC.render(f);
		upperSolidD.render(f);

		// South
		GlStateManager.color(.4F, 0F, 0F, 1.0F);
		connectA.render(f);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		connectB.render(f);
		connectC.render(f);
		connectD.render(f);
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
