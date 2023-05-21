package com.Da_Technomancer.crossroads.items.item_sets;

import com.Da_Technomancer.crossroads.api.MathUtil;
import com.Da_Technomancer.crossroads.api.CRMaterialLibrary;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public abstract class GearMatItem extends OreProfileItem{

	protected GearMatItem(String name){
		super(CRItems.baseItemProperties());
		CRItems.queueForRegister(name, this, CRItems.GEAR_CREATIVE_TAB_ID);
	}

	/**
	 * @param stack A stack with a GearMatItem
	 * @return The configured GearMaterial. Null if it has a non-existent material, iron if not configured (default)
	 */
	public static CRMaterialLibrary.GearMaterial getMaterial(ItemStack stack){
		String matKey;
		if(!stack.hasTag()){
			return CRMaterialLibrary.getDefaultMaterial();
		}else{
			matKey = stack.getTag().getString(KEY);
		}
		return CRMaterialLibrary.findMaterial(matKey);
	}

	@Override
	protected CRMaterialLibrary.OreProfile getSelfProfile(ItemStack stack){
		//Use the gear factory registry instead of the ore processing registry
		return getMaterial(stack);
	}

	protected abstract double shapeFactor();

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag advanced){
		CRMaterialLibrary.GearMaterial mat = getMaterial(stack);
		if(mat != null){
			tooltip.add(Component.translatable("tt.crossroads.boilerplate.inertia", MathUtil.preciseRound(mat.getDensity() * shapeFactor(), 3)));
		}
	}
}
