package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.tileentities.rotary.WindingTableTileEntity;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class Whirligig extends Item implements WindingTableTileEntity.IWindableItem{

	public static final double WIND_USE_RATE = 10D / (20 * 60 * 8);//Rate at which the charge is drained, rad/s /tick
	private final Multimap<Attribute, AttributeModifier> attributeModifiers;

	protected Whirligig(){
		super(new Properties().tab(CRItems.TAB_CROSSROADS).stacksTo(1));
		String name = "whirligig";
		setRegistryName(name);
		CRItems.toRegister.add(this);

		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", 5, AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -3.1D, AttributeModifier.Operation.ADDITION));
		attributeModifiers = builder.build();
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		double wind = getWindLevel(stack);
		double maxWind = getMaxWind();
		tooltip.add(new TranslationTextComponent("tt.crossroads.boilerplate.spring_speed", CRConfig.formatVal(wind), CRConfig.formatVal(maxWind)));
		tooltip.add(new TranslationTextComponent("tt.crossroads.whirligig.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.whirligig.elevate", CRConfig.whirligigHover.get()));
		tooltip.add(new TranslationTextComponent("tt.crossroads.whirligig.quip").setStyle(MiscUtil.TT_QUIP));
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
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn){
		//Starts using the item if there is sufficient charge
		ItemStack held = playerIn.getItemInHand(handIn);
		double wind = getWindLevel(held);
		if(wind > 0 || murderEasterEgg.equals(playerIn.getGameProfile().getName())){
			playerIn.startUsingItem(handIn);
			return ActionResult.success(held);
		}
		return ActionResult.fail(held);
	}

	private static final String murderEasterEgg = "dinidini";

	@Override
	public void onUsingTick(ItemStack stack, LivingEntity player, int count){
		//Called on both sides every tick while the item is being actively used
		if(!player.level.isClientSide()){
			double wind = getWindLevel(stack);

			if(player instanceof PlayerEntity && murderEasterEgg.equals(((PlayerEntity) player).getGameProfile().getName()))
				//Semi-apology for the easter egg that instakills a certain player if they touch a wind turbine where they still get windmill-murked, but also don't need to charge whirligigs
				wind = Math.max(wind, 8);
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
				if(player.getY() < 250){//Safety limit to prevent ridiculous heights being attained
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
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack){
		//Acts as a melee weapon
		return slot == EquipmentSlotType.MAINHAND ? attributeModifiers : super.getAttributeModifiers(slot, stack);
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

	@Override
	public UseAction getUseAnimation(ItemStack stack){
		return UseAction.BLOCK;
	}
}
