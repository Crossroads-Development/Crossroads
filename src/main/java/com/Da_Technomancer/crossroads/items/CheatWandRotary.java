package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.List;

public class CheatWandRotary extends Item{

	private static final int RATE = 10_000;

	protected CheatWandRotary(){
		super(CRItems.itemProp.maxStackSize(1));
		String name = "cheat_wand_rotary";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context){
		TileEntity te = context.getWorld().getTileEntity(context.getPos());
		LazyOptional<IAxleHandler> axleOpt;
		if(te != null && (axleOpt = te.getCapability(Capabilities.AXLE_CAPABILITY, null)).isPresent()){
			IAxleHandler axle = axleOpt.orElseThrow(NullPointerException::new);
			if(context.isPlacerSneaking()){
				axle.addEnergy(-RATE, true, true);
			}else{
				axle.addEnergy(RATE, true, true);
			}
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.boilerplate.creative"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.cheat_rotary.desc", RATE));
		tooltip.add(new TranslationTextComponent("tt.crossroads.cheat_rotary.back", RATE));
	}
}
