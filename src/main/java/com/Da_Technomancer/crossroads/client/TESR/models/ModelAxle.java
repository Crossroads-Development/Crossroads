package com.Da_Technomancer.crossroads.client.TESR.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelAxle extends ModelBase {
    public ModelRenderer shape1;

    public ModelAxle() {
        this.textureWidth = 32;
        this.textureHeight = 16;
        this.shape1 = new ModelRenderer(this, 0, 0);
        this.shape1.setRotationPoint(-1.0F, -8F, -1.0F);
        this.shape1.addBox(0.0F, 0.0F, 0.0F, 2, 16, 2, 0.0F);
    }

    public void render() { 
    	float f = 1F / 16F;
    	shape1.render(f);
    }
}
