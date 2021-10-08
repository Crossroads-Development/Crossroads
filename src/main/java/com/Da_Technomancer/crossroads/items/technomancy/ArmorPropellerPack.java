package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.tileentities.rotary.WindingTableTileEntity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ArmorPropellerPack extends TechnomancyArmor implements WindingTableTileEntity.IWindableItem{

	public static final double WIND_PER_BOOST = 0.005;

	public ArmorPropellerPack(){
		super(EquipmentSlot.CHEST);
		String name = "propeller_pack";
		setRegistryName(name);
		CRItems.toRegister.add(this);
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
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items){
		if(allowdedIn(group)){
			items.add(new ItemStack(this, 1));
			ItemStack unarmoredWound = new ItemStack(this, 1);
			setWindLevel(unarmoredWound, getMaxWind());
			items.add(unarmoredWound);
			items.add(setReinforced(new ItemStack(this, 1), true));
			ItemStack armoredWound = new ItemStack(this, 1);
			setWindLevel(armoredWound, getMaxWind());
			items.add(setReinforced(armoredWound, true));
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		tooltip.add(new TranslatableComponent("tt.crossroads.boilerplate.spring_speed", CRConfig.formatVal(getWindLevel(stack)), CRConfig.formatVal(getMaxWind())));
		tooltip.add(new TranslatableComponent("tt.crossroads.propeller_pack.desc"));
		tooltip.add(new TranslatableComponent("tt.crossroads.propeller_pack.quip").setStyle(MiscUtil.TT_QUIP));
	}

	public static void applyMidairBoost(Player player){
		Vec3 look = player.getLookAngle();
		Vec3 motion = player.getDeltaMovement();
		player.setDeltaMovement(motion.add(look.x * 0.1D + (look.x * 1.5D - motion.x) * 0.5D, look.y * 0.1D + (look.y * 1.5D - motion.y) * 0.5D, look.z * 0.1D + (look.z * 1.5D - motion.z) * 0.5D));
	}
}
