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
		super(new Properties().group(CRItems.TAB_CROSSROADS).maxStackSize(1));
		String name = "recall_device";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
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
			CompoundNBT nbt = stack.getOrCreateChildTag("recall_data");
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

		data.putLong("timestamp", player.world.getGameTime());
		String playerName = player.getGameProfile().getName();
		data.putString("username", playerName == null ? "NULL" : playerName);
		data.putString("dimension", player.world.func_234923_W_().func_240901_a_().toString());//World registry key is used
		data.putDouble("pos_x", player.getPosX());
		data.putDouble("pos_y", player.getPosY());
		data.putDouble("pos_z", player.getPosZ());
		data.putLong("position", player.func_233580_cy_().toLong());
		data.putFloat("yaw", player.getYaw(1F));
		data.putFloat("yaw_head", player.getRotationYawHead());
		data.putFloat("pitch", player.getPitch(1F));
		data.putFloat("health", player.getHealth());
		data.putInt("hunger", player.getFoodStats().getFoodLevel());
		data.putFloat("saturation", player.getFoodStats().getSaturationLevel());
		data.putDouble("vel_x", player.getMotion().getX());
		data.putDouble("vel_y", player.getMotion().getY());
		data.putDouble("vel_z", player.getMotion().getZ());

		if(player.world.isRemote()){
			//Player only sound for setting a position
			player.playSound(SoundEvents.BLOCK_BELL_USE, 2F, 1F);
		}
	}

	private void recall(CompoundNBT data, PlayerEntity player, ItemStack held){
		if(!data.contains("timestamp")){
			if(player.world.isRemote){
				MiscUtil.chatMessage(player, new TranslationTextComponent("tt.crossroads.recall_device.none"));
			}
			return;//No data stored
		}
		//Check time delay and that it's the same player
		long delay = player.world.getGameTime() - data.getLong("timestamp");
		int limit = CRConfig.recallTimeLimit.get() * 20;//In ticks
		if(limit >= 0 && delay > limit){
			if(player.world.isRemote){
				MiscUtil.chatMessage(player, new TranslationTextComponent("tt.crossroads.recall_device.expired"));
			}
			return;//Too old- do nothing
		}

		double wind = getWindLevel(held);

		if(wind < WIND_USE){
			if(player.world.isRemote){
				MiscUtil.chatMessage(player, new TranslationTextComponent("tt.crossroads.recall_device.not_wound"));
			}
			return;//Insufficiently wound
		}else{
			setWindLevel(held, wind - WIND_USE);
		}

		String playerName = player.getGameProfile().getName();
		if(playerName == null || !playerName.equals(data.getString("username"))){
			if(player.world.isRemote){
				MiscUtil.chatMessage(player, new TranslationTextComponent("tt.crossroads.recall_device.wrong_player"));
			}
			return;//Wrong player or null profile
		}

		if(CRConfig.allowStatRecall.get()){
			//Only restore health and hunger if enabled in config
			player.setHealth(data.getFloat("health"));
			MiscUtil.setPlayerFood(player, data.getInt("hunger"), data.getFloat("saturation"));
		}

		if(!player.world.isRemote){
			ServerPlayerEntity playerServ = (ServerPlayerEntity) player;
			ResourceLocation targetDimension = new ResourceLocation(data.getString("dimension"));
			ServerWorld targetWorld;//World we are recalling to. Almost always the same as current dimension. Null if something went wrong
			if(targetDimension.equals(player.world.func_234923_W_().func_240901_a_())){
				targetWorld = (ServerWorld) player.world;
			}else{
				try{
					targetWorld = MiscUtil.getWorld(MiscUtil.getWorldKey(targetDimension, null), playerServ.server);
				}catch(Exception e){
					targetWorld = null;
				}
			}
			if(targetWorld == player.world){
				playerServ.connection.setPlayerLocation(data.getDouble("pos_x"), data.getDouble("pos_y"), data.getDouble("pos_z"), data.getFloat("yaw"), data.getFloat("pitch"));
			}else if(targetWorld != null){
				playerServ.teleport(targetWorld, data.getDouble("pos_x"), data.getDouble("pos_y"), data.getDouble("pos_z"), data.getFloat("yaw"), data.getFloat("pitch"));
			}
		}

		player.setRotationYawHead(data.getFloat("yaw_head"));
		player.setMotion(new Vector3d(data.getDouble("vel_x"), data.getDouble("vel_y"), data.getDouble("vel_z")));

		applySickness(player, delay, limit);
	}

	private static void applySickness(PlayerEntity player, long delay, long delayLimit){
		//Penalty of nausea (time scaling with delay), and poison for very long delays
		//Durations are in ticks
		long poisonStTime = delayLimit / 2L;
		player.addPotionEffect(new EffectInstance(Effects.NAUSEA, (int) MathHelper.clampedLerp(20 * 5, 20 * 15, (float) delay / poisonStTime), 0));
		if(delay > poisonStTime){
			//For unlimited delay config setting, a constant 10 second poison is applied instead of basing it on the portion of the delay limit expended
			int poisonDuration = delayLimit < 0 ? 20 * 10 : (int) MathHelper.clampedLerp(20 * 5, 20 * 30, (float) (delay - poisonStTime) / (delayLimit - poisonStTime));
			player.addPotionEffect(new EffectInstance(Effects.POISON, poisonDuration, 0));
		}

		//Also plays sound
		player.world.playSound(null, player.func_233580_cy_(), SoundEvents.BLOCK_BELL_RESONATE, SoundCategory.PLAYERS, 1F, 1F);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand hand){

		//If shift right clicking, set current data
		//If normal right clicking, revert to last save and set current position as data
		//Apply sickness based on time between uses

		ItemStack held = playerIn.getHeldItem(hand);
		CompoundNBT nbt = held.getOrCreateChildTag("recall_data");

		CompoundNBT newStored = new CompoundNBT();
		storeData(newStored, playerIn);

		if(!playerIn.isSneaking()){
			//World sound for recalling
			//Played at source and destination
			worldIn.playSound(null, playerIn.func_233580_cy_(), SoundEvents.BLOCK_BELL_RESONATE, SoundCategory.PLAYERS, 1F, 1F);
			recall(nbt, playerIn, held);//Will do nothing if over time limit, wrong player, or no data stored
		}

		held.getTag().put("recall_data", newStored);

		return ActionResult.resultSuccess(held);
	}

	@Override
	public double getMaxWind(){
		return 10;
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
}
