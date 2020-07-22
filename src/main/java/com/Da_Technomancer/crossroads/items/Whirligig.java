package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.tileentities.rotary.WindingTableTileEntity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class Whirligig extends Item implements WindingTableTileEntity.IWindableItem{

	public static final double WIND_USE_RATE = 10D / (20 * 60 * 8);//Rate at which the charge is drained, rad/s /tick

	protected Whirligig(){
		super(new Properties().group(CRItems.TAB_CROSSROADS).maxStackSize(1));
		String name = "whirligig";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		double wind = getWindLevel(stack);
		double maxWind = getMaxWind();
		tooltip.add(new TranslationTextComponent("tt.crossroads.boilerplate.spring_speed", CRConfig.formatVal(wind), CRConfig.formatVal(maxWind)));
		tooltip.add(new TranslationTextComponent("tt.crossroads.whirligig.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.whirligig.elevate", CRConfig.whirligigHover.get()));
		tooltip.add(new TranslationTextComponent("tt.crossroads.whirligig.quip").func_230530_a_(MiscUtil.TT_QUIP));
	}

	@Override
	public double getMaxWind(){
		return 10;
	}

	@Override
	public int getUseDuration(ItemStack stack){
		return 72000;//Arbitrary large number used by vanilla items- 1 hour
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn){
		//Starts using the item if there is sufficient charge
		ItemStack held = playerIn.getHeldItem(handIn);
		double wind = getWindLevel(held);
		if(wind > 0){
			playerIn.setActiveHand(handIn);
			return ActionResult.resultSuccess(held);
		}
		return ActionResult.resultFail(held);
	}

	@Override
	public void onUsingTick(ItemStack stack, LivingEntity player, int count){
		//Called on both sides every tick while the item is being actively used
		if(player.isServerWorld()){
			double wind = getWindLevel(stack);
			if(wind > 0){

				final double SLOWFALL_WIND = CRConfig.whirligigSafe.get();//Minimum charge level to eliminate fall damage
				final double HOVER_WIND = CRConfig.whirligigHover.get();//Minimum charge level to hover

				//Fall damage
				if(wind >= SLOWFALL_WIND){
					//Eliminate fall damage
					player.fallDistance = 0;
				}else{
					//Reduce fall damage by a portion by slowing the accumulation of fall distance
					//The coefficient used is in effect much higher than stated due to the 'compounding' effect of this being applied every tick
					player.fallDistance *= 1D - 0.2D * (wind / SLOWFALL_WIND);
				}

				//Upward thrust
				if(player.getPosY() < 250){//Safety limit to prevent ridiculous heights being attained
					//Vanilla gravity is applied as constant change in y-velocity every tick
					final double gravity = 0.08;
					double thrust = gravity * (wind / HOVER_WIND);
					player.addVelocity(0, thrust, 0);
					player.velocityChanged = true;
				}

				//Consume charge
				wind = Math.max(wind - WIND_USE_RATE, 0);
				setWindLevel(stack, wind);
			}else{
				player.resetActiveHand();//Insufficient charge
			}
		}
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items){
		if(isInGroup(group)){
			items.add(new ItemStack(this, 1));
			ItemStack stack = new ItemStack(this, 1);
			setWindLevel(stack, getMaxWind());
			items.add(stack);
		}
	}

	@Override
	public UseAction getUseAction(ItemStack stack){
		return UseAction.BLOCK;
	}
}
