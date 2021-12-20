package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.tileentities.rotary.WindingTableTileEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class SpringGun extends ProjectileWeaponItem implements WindingTableTileEntity.IWindableItem{

	private static final double MIN_SPEED = 1;

	protected SpringGun(){
		super(new Properties().tab(CRItems.TAB_CROSSROADS).stacksTo(1));
		String name = "spring_gun";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	private float calcDamage(double wind){
		//Values based on regression between (1, 5) and (10, 50)
		return Math.max(Math.round(20.8D * Math.sqrt(wind) - 15.8D), 0);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand){
		ItemStack held = playerIn.getItemInHand(hand);
		double wind = getWindLevel(held);
		ItemStack ammo = playerIn.getProjectile(held);
		if(ammo.isEmpty() && playerIn.isCreative()){
			ammo = new ItemStack(Items.ARROW, 1);
		}
		if(wind > MIN_SPEED && !ammo.isEmpty() && ammo.getItem() instanceof ArrowItem){
			if(!worldIn.isClientSide){
				//Shoot
				AbstractArrow arrow = ((ArrowItem) ammo.getItem()).createArrow(worldIn, ammo, playerIn);
				float speed = (float) wind * 0.5F;
				arrow.shootFromRotation(playerIn, playerIn.getXRot(), playerIn.getYRot(), 0.0F, speed, 0.2F);
				//Despite the method being named setDamage, it actually sets a damage multiplier
				//The actual damage dealt of an arrow is (damage * speed)
				float damageMult = calcDamage(wind) / speed;
				arrow.setBaseDamage(damageMult);
				//Don't set critical, as that changes the damage dealt
				arrow.setSoundEvent(SoundEvents.CROSSBOW_HIT);
				arrow.setShotFromCrossbow(true);
				if(playerIn.isCreative()){
					arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
				}
				worldIn.addFreshEntity(arrow);

				//Consume ammo
				if(!playerIn.getAbilities().instabuild){
					ammo.shrink(1);
				}

				worldIn.playSound(playerIn, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1F, 1F);//Play sound for other players
			}

			playerIn.playNotifySound(SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1F, 1F);//Play sound on the client
			if(!playerIn.getAbilities().instabuild){
				//Return the discharged weapon
				setWindLevel(held, 0);
			}
			return InteractionResultHolder.success(held);
		}

		return InteractionResultHolder.fail(held);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		double wind = getWindLevel(stack);
		double maxWind = getMaxWind();
		tooltip.add(new TranslatableComponent("tt.crossroads.boilerplate.spring_speed", CRConfig.formatVal(wind), CRConfig.formatVal(maxWind)));
		tooltip.add(new TranslatableComponent("tt.crossroads.spring_gun.winding", CRConfig.formatVal(calcDamage(wind)), CRConfig.formatVal(calcDamage(maxWind))));
		tooltip.add(new TranslatableComponent("tt.crossroads.spring_gun.desc"));
		tooltip.add(new TranslatableComponent("tt.crossroads.spring_gun.quip").setStyle(MiscUtil.TT_QUIP));
	}

	@Override
	public double getMaxWind(){
		return CRConfig.springGunCap.get();
	}

	@Override
	public Predicate<ItemStack> getAllSupportedProjectiles(){
		return ARROW_ONLY;
	}

	@Override
	public int getDefaultProjectileRange(){
		return 15;//Don't actually know what this does- looks mob AI related?
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack){
		return UseAnim.CROSSBOW;//Doesn't really have any effect as arm posing is hard coded for crossbows
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items){
		if(allowdedIn(group)){
			items.add(new ItemStack(this, 1));
			ItemStack stack = new ItemStack(this, 1);
			setWindLevel(stack, getMaxWind());
			items.add(stack);
		}
	}
}
