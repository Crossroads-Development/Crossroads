package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.rotary.WindingTableTileEntity;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ArmorPropellerPack extends TechnomancyArmor implements WindingTableTileEntity.IWindableItem{

	public static final double WIND_PER_BOOST = 0.005;

	public ArmorPropellerPack(boolean reinforced){
		super(Type.CHESTPLATE, reinforced);
		String name = reinforced ? "propeller_pack_reinforced" : "propeller_pack";
		CRItems.queueForRegister(name, this);
	}

	@Override
	public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks){
		return true;
	}

	@Override
	public boolean canElytraFly(ItemStack stack, LivingEntity entity){
		return true;
	}

	@Override
	public double getMaxWind(){
		return 10;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag){
		super.appendHoverText(stack, context, tooltip, flag);
		appendTooltip(stack, tooltip, flag);
		tooltip.add(Component.translatable("tt.crossroads.propeller_pack.desc"));
		tooltip.add(Component.translatable("tt.crossroads.propeller_pack.quip").setStyle(MiscUtil.TT_QUIP));
	}

	public static void applyMidairBoost(Player player){
		Vec3 look = player.getLookAngle();
		Vec3 motion = player.getDeltaMovement();
		player.setDeltaMovement(motion.add(look.x * 0.1D + (look.x * 1.5D - motion.x) * 0.5D, look.y * 0.1D + (look.y * 1.5D - motion.y) * 0.5D, look.z * 0.1D + (look.z * 1.5D - motion.z) * 0.5D));
	}
}
