package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.tileentities.rotary.WindingTableTileEntity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;

public class RecallDevice extends Item implements WindingTableTileEntity.IWindableItem{

	private static final double WIND_USE = 0.4;

	public RecallDevice(){
		super(new Properties().tab(CRItems.TAB_CROSSROADS).stacksTo(1));
		String name = "recall_device";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.boilerplate.spring_speed", CRConfig.formatVal(getWindLevel(stack)), CRConfig.formatVal(getMaxWind())));
		tooltip.add(new TranslationTextComponent("tt.crossroads.recall_device.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.recall_device.debuff"));
		if(CRConfig.recallTimeLimit.get() == 0){
			//Disabled
			tooltip.add(new TranslationTextComponent("tt.crossroads.recall_device.config.disabled"));
		}else{
			int limit = CRConfig.recallTimeLimit.get();
			if(limit < 0){
				//Unlimited recall
				tooltip.add(new TranslationTextComponent("tt.crossroads.recall_device.config.unlimited"));
			}else{
				tooltip.add(new TranslationTextComponent("tt.crossroads.recall_device.config", limit));
			}
			CompoundNBT nbt = stack.getOrCreateTagElement("recall_data");
			long timeElapsed;
			if(nbt.contains("timestamp") && (timeElapsed = worldIn.getGameTime() - nbt.getLong("timestamp")) < limit * 20){
				tooltip.add(new TranslationTextComponent("tt.crossroads.recall_device.current", (int) (timeElapsed / 20)));
			}else{
				tooltip.add(new TranslationTextComponent("tt.crossroads.recall_device.current.none"));
			}
		}
	}

	private static void storeData(CompoundNBT data, PlayerEntity player){
		//Data to store is:
		//Timestamp
		//Player username
		//Dimension
		//Position
		//Orientation
		//Health
		//Hunger
		//Velocity

		data.putLong("timestamp", player.level.getGameTime());
		String playerName = player.getGameProfile().getName();
		data.putString("username", playerName == null ? "NULL" : playerName);
		data.putString("dimension", player.level.dimension().location().toString());//World registry key is used
		data.putDouble("pos_x", player.getX());
		data.putDouble("pos_y", player.getY());
		data.putDouble("pos_z", player.getZ());
		data.putLong("position", player.blockPosition().asLong());
		data.putFloat("yaw", player.getViewYRot(1F));
		data.putFloat("yaw_head", player.getYHeadRot());
		data.putFloat("pitch", player.getViewXRot(1F));
		data.putFloat("health", player.getHealth());
		data.putInt("hunger", player.getFoodData().getFoodLevel());
		data.putFloat("saturation", player.getFoodData().getSaturationLevel());
		data.putDouble("vel_x", player.getDeltaMovement().x());
		data.putDouble("vel_y", player.getDeltaMovement().y());
		data.putDouble("vel_z", player.getDeltaMovement().z());

		if(player.level.isClientSide()){
			//Player only sound for setting a position
			player.playSound(SoundEvents.BELL_BLOCK, 2F, 1F);
		}
	}

	private void recall(CompoundNBT data, PlayerEntity player, ItemStack held){
		if(!data.contains("timestamp")){
			if(player.level.isClientSide){
				MiscUtil.chatMessage(player, new TranslationTextComponent("tt.crossroads.recall_device.none"));
			}
			return;//No data stored
		}
		//Check time delay and that it's the same player
		long delay = player.level.getGameTime() - data.getLong("timestamp");
		int limit = CRConfig.recallTimeLimit.get() * 20;//In ticks
		if(limit >= 0 && delay > limit){
			if(player.level.isClientSide){
				MiscUtil.chatMessage(player, new TranslationTextComponent("tt.crossroads.recall_device.expired"));
			}
			return;//Too old- do nothing
		}

		double wind = getWindLevel(held);

		if(wind < WIND_USE){
			if(player.level.isClientSide){
				MiscUtil.chatMessage(player, new TranslationTextComponent("tt.crossroads.recall_device.not_wound"));
			}
			return;//Insufficiently wound
		}else{
			setWindLevel(held, wind - WIND_USE);
		}

		String playerName = player.getGameProfile().getName();
		if(playerName == null || !playerName.equals(data.getString("username"))){
			if(player.level.isClientSide){
				MiscUtil.chatMessage(player, new TranslationTextComponent("tt.crossroads.recall_device.wrong_player"));
			}
			return;//Wrong player or null profile
		}

		if(CRConfig.allowStatRecall.get()){
			//Only restore health and hunger if enabled in config
			player.setHealth(data.getFloat("health"));
			MiscUtil.setPlayerFood(player, data.getInt("hunger"), data.getFloat("saturation"));
		}

		if(!player.level.isClientSide){
			ServerPlayerEntity playerServ = (ServerPlayerEntity) player;
			ResourceLocation targetDimension = new ResourceLocation(data.getString("dimension"));
			ServerWorld targetWorld;//World we are recalling to. Almost always the same as current dimension. Null if something went wrong
			if(targetDimension.equals(player.level.dimension().location())){
				targetWorld = (ServerWorld) player.level;
			}else{
				try{
					targetWorld = MiscUtil.getWorld(MiscUtil.getWorldKey(targetDimension, null), playerServ.server);
				}catch(Exception e){
					targetWorld = null;
				}
			}
			if(targetWorld == player.level){
				playerServ.connection.teleport(data.getDouble("pos_x"), data.getDouble("pos_y"), data.getDouble("pos_z"), data.getFloat("yaw"), data.getFloat("pitch"));
			}else if(targetWorld != null){
				playerServ.teleportTo(targetWorld, data.getDouble("pos_x"), data.getDouble("pos_y"), data.getDouble("pos_z"), data.getFloat("yaw"), data.getFloat("pitch"));
			}
		}

		player.setYHeadRot(data.getFloat("yaw_head"));
		player.setDeltaMovement(new Vector3d(data.getDouble("vel_x"), data.getDouble("vel_y"), data.getDouble("vel_z")));

		applySickness(player, delay, limit);
	}

	private static void applySickness(PlayerEntity player, long delay, long delayLimit){
		//Penalty of nausea (time scaling with delay), and poison for very long delays
		//Durations are in ticks
		long poisonStTime = delayLimit / 2L;
		player.addEffect(new EffectInstance(Effects.CONFUSION, (int) MathHelper.clampedLerp(20 * 5, 20 * 15, (float) delay / poisonStTime), 0));
		if(delay > poisonStTime){
			//For unlimited delay config setting, a constant 10 second poison is applied instead of basing it on the portion of the delay limit expended
			int poisonDuration = delayLimit < 0 ? 20 * 10 : (int) MathHelper.clampedLerp(20 * 5, 20 * 30, (float) (delay - poisonStTime) / (delayLimit - poisonStTime));
			player.addEffect(new EffectInstance(Effects.POISON, poisonDuration, 0));
		}

		//Also plays sound
		player.level.playSound(null, player.blockPosition(), SoundEvents.BELL_RESONATE, SoundCategory.PLAYERS, 1F, 1F);
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand hand){

		//If shift right clicking, set current data
		//If normal right clicking, revert to last save and set current position as data
		//Apply sickness based on time between uses

		ItemStack held = playerIn.getItemInHand(hand);
		CompoundNBT nbt = held.getOrCreateTagElement("recall_data");

		CompoundNBT newStored = new CompoundNBT();
		storeData(newStored, playerIn);

		if(!playerIn.isShiftKeyDown()){
			//World sound for recalling
			//Played at source and destination
			worldIn.playSound(null, playerIn.blockPosition(), SoundEvents.BELL_RESONATE, SoundCategory.PLAYERS, 1F, 1F);
			recall(nbt, playerIn, held);//Will do nothing if over time limit, wrong player, or no data stored
		}

		held.getTag().put("recall_data", newStored);

		return ActionResult.success(held);
	}

	@Override
	public double getMaxWind(){
		return 10;
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
