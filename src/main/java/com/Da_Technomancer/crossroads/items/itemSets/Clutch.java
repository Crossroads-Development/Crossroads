package com.Da_Technomancer.crossroads.items.itemSets;

import com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms.IMechanism;
import com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms.MechanismTileEntity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class Clutch extends Axle{

	private final boolean inverted;

	public Clutch(boolean inverted){
		super("clutch" + (inverted ? "_inv" : ""));
		this.inverted = inverted;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		super.addInformation(stack, world, tooltip, advanced);
		tooltip.add(new TranslationTextComponent("tt.crossroads.clutch.redstone"));
	}

	@Override
	protected IMechanism mechanismToPlace(){
		return MechanismTileEntity.MECHANISMS.get(inverted ? 3 : 2);
	}
}
