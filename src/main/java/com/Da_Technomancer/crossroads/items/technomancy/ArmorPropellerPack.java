package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.tileentities.rotary.WindingTableTileEntity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ArmorPropellerPack extends TechnomancyArmor implements WindingTableTileEntity.IWindableItem{

	public static final double WIND_PER_BOOST = 0.005;

	public ArmorPropellerPack(){
		super(EquipmentSlotType.CHEST);
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
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items){
		if(isInGroup(group)){
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
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add(new TranslationTextComponent("tt.crossroads.boilerplate.spring_speed", CRConfig.formatVal(getWindLevel(stack)), CRConfig.formatVal(getMaxWind())));
		tooltip.add(new TranslationTextComponent("tt.crossroads.propeller_pack.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.propeller_pack.quip").setStyle(MiscUtil.TT_QUIP));
	}

	public static void applyMidairBoost(PlayerEntity player){
		Vector3d look = player.getLookVec();
		Vector3d motion = player.getMotion();
		player.setMotion(motion.add(look.x * 0.1D + (look.x * 1.5D - motion.x) * 0.5D, look.y * 0.1D + (look.y * 1.5D - motion.y) * 0.5D, look.z * 0.1D + (look.z * 1.5D - motion.z) * 0.5D));
	}
}
