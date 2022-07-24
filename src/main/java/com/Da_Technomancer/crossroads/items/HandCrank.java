package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.api.rotary.RotaryUtil;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.List;

public class HandCrank extends Item{

	protected HandCrank(){
		this("hand_crank", Rarity.COMMON);
	}

	protected HandCrank(String name, Rarity rarity){
		super(new Properties().tab(CRItems.TAB_CROSSROADS).stacksTo(1).rarity(rarity));
		CRItems.toRegister.put(name, this);
	}

	protected int getRate(){
		return 100;
	}

	@Override
	public InteractionResult useOn(UseOnContext context){
		BlockEntity te = context.getLevel().getBlockEntity(context.getClickedPos());
		LazyOptional<IAxleHandler> axleOpt;
		Direction side = context.getClickedFace().getOpposite();
		if(te != null && (axleOpt = te.getCapability(Capabilities.AXLE_CAPABILITY, side)).isPresent()){
			double signMult = -1;
			if(context.getPlayer() != null && context.getPlayer().isShiftKeyDown()){
				signMult *= -1;
			}
			signMult *= RotaryUtil.getCCWSign(side);
			axleOpt.orElseThrow(NullPointerException::new).addEnergy(getRate() * signMult, true);
			context.getPlayer().getCooldowns().addCooldown(this, 4);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(Component.translatable("tt.crossroads.crank.desc", getRate()));
		tooltip.add(Component.translatable("tt.crossroads.crank.back", getRate()));
	}
}
