package com.Da_Technomancer.crossroads.client.TESR.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelDrill extends ModelBase{
	public ModelRenderer base;
	public ModelRenderer upperBase;
	public ModelRenderer lowerTip;
	public ModelRenderer midTip;
	public ModelRenderer topTip;

	public ModelDrill(){
		this.textureWidth = 64;
		this.textureHeight = 32;
		this.lowerTip = new ModelRenderer(this, 30, 0);
		this.lowerTip.setRotationPoint(-3.0F, 15.0F, -3.0F);
		this.lowerTip.addBox(0.0F, 0.0F, 0.0F, 6, 3, 6, 0.0F);
		this.midTip = new ModelRenderer(this, 36, 9);
		this.midTip.setRotationPoint(-2.0F, 12.0F, -2.0F);
		this.midTip.addBox(0.0F, 0.0F, 0.0F, 4, 3, 4, 0.0F);
		this.topTip = new ModelRenderer(this, 0, 0);
		this.topTip.setRotationPoint(-1.0F, 8.0F, -1.0F);
		this.topTip.addBox(0.0F, 0.0F, 0.0F, 2, 4, 2, 0.0F);
		this.base = new ModelRenderer(this, 0, 0);
		this.base.setRotationPoint(-5.0F, 21.0F, -5.0F);
		this.base.addBox(0.0F, 0.0F, 0.0F, 10, 3, 10, 0.0F);
		this.upperBase = new ModelRenderer(this, 0, 13);
		this.upperBase.setRotationPoint(-4.0F, 18.0F, -4.0F);
		this.upperBase.addBox(0.0F, 0.0F, 0.0F, 8, 3, 8, 0.0F);
	}

	public void render(){
		float f = 1F / 16F;
		this.lowerTip.render(f);
		this.midTip.render(f);
		this.topTip.render(f);
		this.base.render(f);
		this.upperBase.render(f);
	}
}
