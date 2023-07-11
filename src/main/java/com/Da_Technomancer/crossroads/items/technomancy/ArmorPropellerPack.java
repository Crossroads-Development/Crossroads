package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.rotary.WindingTableTileEntity;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class ArmorPropellerPack extends TechnomancyArmor implements WindingTableTileEntity.IWindableItem{

	public static final double WIND_PER_BOOST = 0.005;

	public ArmorPropellerPack(){
		super(Type.CHESTPLATE);
		String name = "propeller_pack";
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
	public ItemStack[] populateCreativeTab(){
		ItemStack[] result = new ItemStack[4];
		result[0] = new ItemStack(this, 1);
		ItemStack unarmoredWound = new ItemStack(this, 1);
		setWindLevel(unarmoredWound, getMaxWind());
		result[1] = unarmoredWound;
		result[2] = setReinforced(new ItemStack(this, 1), true);
		ItemStack armoredWound = new ItemStack(this, 1);
		setWindLevel(armoredWound, getMaxWind());
		result[3] = setReinforced(armoredWound, true);
		return result;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		appendTooltip(stack, tooltip, flagIn);
		tooltip.add(Component.translatable("tt.crossroads.propeller_pack.desc"));
		tooltip.add(Component.translatable("tt.crossroads.propeller_pack.quip").setStyle(MiscUtil.TT_QUIP));
	}

	public static void applyMidairBoost(Player player){
		Vec3 look = player.getLookAngle();
		Vec3 motion = player.getDeltaMovement();
		player.setDeltaMovement(motion.add(look.x * 0.1D + (look.x * 1.5D - motion.x) * 0.5D, look.y * 0.1D + (look.y * 1.5D - motion.y) * 0.5D, look.z * 0.1D + (look.z * 1.5D - motion.z) * 0.5D));
	}
}
