package com.Da_Technomancer.crossroads.render;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class TechnomancyElytraRenderer<T extends LivingEntity, M extends EntityModel<T>> extends ElytraLayer<T, M>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/entities/techno_elytra.png");

	public TechnomancyElytraRenderer(IEntityRenderer<T, M> rendererIn){
		super(rendererIn);
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
