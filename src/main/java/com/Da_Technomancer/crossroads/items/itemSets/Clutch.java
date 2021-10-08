package com.Da_Technomancer.crossroads.items.itemSets;

import com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms.IMechanism;
import com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms.MechanismTileEntity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class Clutch extends Axle{

	private final boolean inverted;

	public Clutch(boolean inverted){
		super("clutch" + (inverted ? "_inv" : ""));
		this.inverted = inverted;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag advanced){
		super.appendHoverText(stack, world, tooltip, advanced);
		tooltip.add(new TranslatableComponent("tt.crossroads.clutch.redstone"));
	}

	@Override
	protected IMechanism mechanismToPlace(){
		return MechanismTileEntity.MECHANISMS.get(inverted ? 3 : 2);
	}
}
