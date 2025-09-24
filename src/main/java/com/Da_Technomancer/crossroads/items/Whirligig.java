package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.rotary.WindingTableTileEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;

import java.util.List;

public class Whirligig extends Item implements WindingTableTileEntity.IWindableItem{

	public static final double WIND_USE_RATE = 10D / (20 * 60 * 8);//Rate at which the charge is drained, rad/s /tick

	protected Whirligig(){
		super(new Properties().stacksTo(1)
				.attributes(ItemAttributeModifiers.builder()
						.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 5, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
						.add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -3.1D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build()));
		String name = "whirligig";
		CRItems.queueForRegister(name, this);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag){
		appendTooltip(stack, tooltip, flag);
		tooltip.add(Component.translatable("tt.crossroads.whirligig.desc"));
		tooltip.add(Component.translatable("tt.crossroads.whirligig.elevate", CRConfig.whirligigHover.get()));
		tooltip.add(Component.translatable("tt.crossroads.whirligig.quip").setStyle(MiscUtil.TT_QUIP));
	}

	@Override
	public double getMaxWind(){
		return 10;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity){
		return 72000;//Arbitrary large number used by vanilla items- 1 hour
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn){
		//Starts using the item if there is sufficient charge
		ItemStack held = playerIn.getItemInHand(handIn);
		double wind = getWindLevel(held);
		if(wind > 0 || murderEasterEgg.equals(playerIn.getGameProfile().getName())){
			playerIn.startUsingItem(handIn);
			return InteractionResultHolder.success(held);
		}
		return InteractionResultHolder.fail(held);
	}

	private static final String murderEasterEgg = "Talcosa";

	@Override
	public void onUseTick(Level world, LivingEntity player, ItemStack stack, int count){
		//Called on both sides every tick while the item is being actively used
		if(!world.isClientSide()){
			double wind = getWindLevel(stack);

			if(player instanceof Player && murderEasterEgg.equals(((Player) player).getGameProfile().getName()))
			//Semi-apology for the easter egg that instakills a certain player if they touch a wind turbine where they still get windmill-murked, but also don't need to charge whirligigs
			{
				wind = Math.max(wind, 8);
			}
			if(wind > 0){

				final double SLOWFALL_WIND = CRConfig.whirligigSafe.get();//Minimum charge level to eliminate fall damage
				final double HOVER_WIND = CRConfig.whirligigHover.get();//Minimum charge level to hover

				//Target the player's mount instead of the player if they have one
				Entity targetEntity = player.getVehicle() == null ? player : player.getVehicle();

				//Fall damage
				if(wind >= SLOWFALL_WIND){
					//Eliminate fall damage
					targetEntity.fallDistance = 0;
				}else{
					//Reduce fall damage by a portion by slowing the accumulation of fall distance
					//The coefficient used is in effect much higher than stated due to the 'compounding' effect of this being applied every tick
					targetEntity.fallDistance *= 1D - 0.2D * (wind / SLOWFALL_WIND);
				}

				//Upward thrust
				if(player.getY() < world.getMaxBuildHeight() + 10){//Safety limit to prevent ridiculous heights being attained
					//Vanilla gravity is applied as constant change in y-velocity every tick
					final double gravity = 0.08;
					double thrust = gravity * (wind / HOVER_WIND);
					targetEntity.push(0, thrust, 0);
					targetEntity.hurtMarked = true;
				}

				//Consume charge
				wind = Math.max(wind - WIND_USE_RATE, 0);
				setWindLevel(stack, wind);
			}else{
				player.stopUsingItem();//Insufficient charge
			}
		}
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack){
		return UseAnim.BLOCK;
	}
}
