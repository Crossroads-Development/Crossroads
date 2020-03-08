package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.List;

public class HandCrank extends Item{

	private static final int RATE = 100;

	protected HandCrank(){
		super(new Properties().group(CRItems.TAB_CROSSROADS).maxStackSize(1));
		String name = "hand_crank";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context){
		TileEntity te = context.getWorld().getTileEntity(context.getPos());
		LazyOptional<IAxleHandler> axleOpt;
		Direction side = context.getFace().getOpposite();
		if(te != null && (axleOpt = te.getCapability(Capabilities.AXLE_CAPABILITY, side)).isPresent()){
			double signMult = 1;
			if(context.isPlacerSneaking()){
				signMult *= -1;
			}
			if(side.getAxisDirection() == Direction.AxisDirection.POSITIVE){
				signMult *= -1;//Makes things seem consistent to the player
			}
			axleOpt.orElseThrow(NullPointerException::new).addEnergy(RATE * signMult, true);
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.crank.desc", RATE));
		tooltip.add(new TranslationTextComponent("tt.crossroads.crank.back", RATE));
	}
}
