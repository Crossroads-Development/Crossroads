package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.tileentities.rotary.WindingTableTileEntity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class SpringGun extends ShootableItem implements WindingTableTileEntity.IWindableItem{

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
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand hand){
		ItemStack held = playerIn.getItemInHand(hand);
		double wind = getWindLevel(held);
		ItemStack ammo = playerIn.getProjectile(held);
		if(ammo.isEmpty() && playerIn.isCreative()){
			ammo = new ItemStack(Items.ARROW, 1);
		}
		if(wind > MIN_SPEED && !ammo.isEmpty() && ammo.getItem() instanceof ArrowItem){
			if(!worldIn.isClientSide){
				//Shoot
				AbstractArrowEntity arrow = ((ArrowItem) ammo.getItem()).createArrow(worldIn, ammo, playerIn);
				float speed = (float) wind * 0.5F;
				arrow.shootFromRotation(playerIn, playerIn.xRot, playerIn.yRot, 0.0F, speed, 0.2F);
				//Despite the method being named setDamage, it actually sets a damage multiplier
				//The actual damage dealt of an arrow is (damage * speed)
				float damageMult = calcDamage(wind) / speed;
				arrow.setBaseDamage(damageMult);
				//Don't set critical, as that changes the damage dealt
				arrow.setSoundEvent(SoundEvents.CROSSBOW_HIT);
				arrow.setShotFromCrossbow(true);
				if(playerIn.isCreative()){
					arrow.pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
				}
				worldIn.addFreshEntity(arrow);

				//Consume ammo
				if(!playerIn.abilities.instabuild){
					ammo.shrink(1);
				}

				worldIn.playSound(playerIn, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1F, 1F);//Play sound for other players
			}

			playerIn.playNotifySound(SoundEvents.CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1F, 1F);//Play sound on the client
			if(!playerIn.abilities.instabuild){
				//Return the discharged weapon
				setWindLevel(held, 0);
			}
			return ActionResult.success(held);
		}

		return ActionResult.fail(held);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		double wind = getWindLevel(stack);
		double maxWind = getMaxWind();
		tooltip.add(new TranslationTextComponent("tt.crossroads.boilerplate.spring_speed", CRConfig.formatVal(wind), CRConfig.formatVal(maxWind)));
		tooltip.add(new TranslationTextComponent("tt.crossroads.spring_gun.winding", CRConfig.formatVal(calcDamage(wind)), CRConfig.formatVal(calcDamage(maxWind))));
		tooltip.add(new TranslationTextComponent("tt.crossroads.spring_gun.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.spring_gun.quip").setStyle(MiscUtil.TT_QUIP));
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
	public UseAction getUseAnimation(ItemStack stack){
		return UseAction.CROSSBOW;//Doesn't really have any effect as arm posing is hard coded for crossbows
	}

	@Override
	public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items){
		if(allowdedIn(group)){
			items.add(new ItemStack(this, 1));
			ItemStack stack = new ItemStack(this, 1);
			setWindLevel(stack, getMaxWind());
			items.add(stack);
		}
	}
}
