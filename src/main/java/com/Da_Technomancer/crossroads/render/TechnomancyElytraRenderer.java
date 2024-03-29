package com.Da_Technomancer.crossroads.render;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class TechnomancyElytraRenderer<T extends LivingEntity, M extends EntityModel<T>> extends ElytraLayer<T, M>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/entities/techno_elytra.png");

	public TechnomancyElytraRenderer(RenderLayerParent<T, M> rendererIn, EntityModelSet modelSet){
		super(rendererIn, modelSet);
	}

	@Override
	public boolean shouldRender(ItemStack stack, T entity){
		return stack.getItem() == CRItems.propellerPack;
	}

	@Override
	public ResourceLocation getElytraTexture(ItemStack stack, T entity){
		return TEXTURE;
	}
}
