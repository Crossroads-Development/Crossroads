package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.packets.StreamCodecUtils;
import com.Da_Technomancer.crossroads.blocks.rotary.WindingTableTileEntity;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class RecallDevice extends Item implements WindingTableTileEntity.IWindableItem{

	private static final double WIND_USE = 0.4;

	public RecallDevice(){
		super(new Properties().stacksTo(1));
		String name = "recall_device";
		CRItems.queueForRegister(name, this);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag){
		appendTooltip(stack, tooltip, flag);
		tooltip.add(Component.translatable("tt.crossroads.recall_device.desc"));
		tooltip.add(Component.translatable("tt.crossroads.recall_device.debuff"));
		if(CRConfig.recallTimeLimit.get() == 0){
			//Disabled
			tooltip.add(Component.translatable("tt.crossroads.recall_device.config.disabled"));
		}else{
			int limit = CRConfig.recallTimeLimit.get();
			if(limit < 0){
				//Unlimited recall
				tooltip.add(Component.translatable("tt.crossroads.recall_device.config.unlimited"));
			}else{
				tooltip.add(Component.translatable("tt.crossroads.recall_device.config", limit));
			}
			RecallData recallData = stack.get(CRItems.TIME_RECALL_DATA);
			long timeElapsed;

			Level level = context.level();
			if(level != null){
				if(recallData != null && (timeElapsed = context.level().getGameTime() - recallData.timestamp) < limit * 20){
					tooltip.add(Component.translatable("tt.crossroads.recall_device.current", (int) (timeElapsed / 20)));
				}else{
					tooltip.add(Component.translatable("tt.crossroads.recall_device.current.none"));
				}
			}
		}
	}

	private static RecallData storeData(Player player){
		//Data to store is:
		//Timestamp
		//Player username
		//Dimension
		//Position
		//Orientation
		//Health
		//Hunger
		//Velocity

		long timestamp = player.level().getGameTime();
		String playerName = player.getGameProfile().getName();
		if(playerName == null){
			playerName = "NULL";
		}
		String dimension = player.level().dimension().location().toString();//World registry key is used
		double posX = player.getX();
		double posY = player.getY();
		double posZ = player.getZ();
		long blockPos = player.blockPosition().asLong();
		float yaw = player.getViewYRot(1F);
		float yawHead = player.getYHeadRot();
		float pitch = player.getViewXRot(1F);
		float health = player.getHealth();
		int hunger = player.getFoodData().getFoodLevel();
		float saturation = player.getFoodData().getSaturationLevel();
		double velX = player.getDeltaMovement().x();
		double velY = player.getDeltaMovement().y();
		double velZ = player.getDeltaMovement().z();

		if(player.level().isClientSide()){
			//Player only sound for setting a position
			player.playSound(SoundEvents.BELL_BLOCK, 2F, 1F);
		}
		return new RecallData(timestamp, playerName, dimension, posX, posY, posY, blockPos, yaw, yawHead, pitch, health, hunger, saturation, velX, velY, velZ);
	}

	private void recall(@Nullable RecallData data, Player player, ItemStack held){
		if(data == null){
			if(player.level().isClientSide){
				MiscUtil.displayMessage(player, Component.translatable("tt.crossroads.recall_device.none"));
			}
			return;//No data stored
		}
		//Check time delay and that it's the same player
		long delay = player.level().getGameTime() - data.timestamp;
		int limit = CRConfig.recallTimeLimit.get() * 20;//In ticks
		if(limit >= 0 && delay > limit){
			if(player.level().isClientSide){
				MiscUtil.displayMessage(player, Component.translatable("tt.crossroads.recall_device.expired"));
			}
			return;//Too old- do nothing
		}

		double wind = getWindLevel(held);

		if(wind < WIND_USE){
			if(player.level().isClientSide){
				MiscUtil.displayMessage(player, Component.translatable("tt.crossroads.recall_device.not_wound"));
			}
			return;//Insufficiently wound
		}else{
			setWindLevel(held, wind - WIND_USE);
		}

		String playerName = player.getGameProfile().getName();
		if(playerName == null || !playerName.equals(data.playerName)){
			if(player.level().isClientSide){
				MiscUtil.displayMessage(player, Component.translatable("tt.crossroads.recall_device.wrong_player"));
			}
			return;//Wrong player or null profile
		}

		if(CRConfig.allowStatRecall.get()){
			//Only restore health and hunger if enabled in config
			player.setHealth(data.health);
			MiscUtil.setPlayerFood(player, data.hunger, data.saturation);
		}

		if(!player.level().isClientSide){
			ServerPlayer playerServ = (ServerPlayer) player;
			ResourceLocation targetDimension = ResourceLocation.parse(data.dimension);
			ServerLevel targetWorld;//World we are recalling to. Almost always the same as current dimension. Null if something went wrong
			if(targetDimension.equals(player.level().dimension().location())){
				targetWorld = (ServerLevel) player.level();
			}else{
				try{
					targetWorld = MiscUtil.getWorld(MiscUtil.getWorldKey(targetDimension, null), playerServ.server);
				}catch(Exception e){
					targetWorld = null;
				}
			}
			if(targetWorld == player.level()){
				playerServ.connection.teleport(data.posX, data.posY, data.posZ, data.yaw, data.pitch);
			}else if(targetWorld != null){
				playerServ.teleportTo(targetWorld, data.posX, data.posY, data.posZ, data.yaw, data.pitch);
			}
		}

		player.setYHeadRot(data.yawHead);
		player.setDeltaMovement(new Vec3(data.velX, data.velY, data.velZ));

		applySickness(player, delay, limit);
	}

	private static void applySickness(Player player, long delay, long delayLimit){
		//Penalty of nausea (time scaling with delay), and poison for very long delays
		//Durations are in ticks
		long poisonStTime = delayLimit / 2L;
		player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, (int) Mth.clampedLerp(20 * 5, 20 * 15, (float) delay / poisonStTime), 0));
		if(delay > poisonStTime){
			//For unlimited delay config setting, a constant 10 second poison is applied instead of basing it on the portion of the delay limit expended
			int poisonDuration = delayLimit < 0 ? 20 * 10 : (int) Mth.clampedLerp(20 * 5, 20 * 30, (float) (delay - poisonStTime) / (delayLimit - poisonStTime));
			player.addEffect(new MobEffectInstance(MobEffects.POISON, poisonDuration, 0));
		}

		//Also plays sound
		player.level().playSound(null, player.blockPosition(), SoundEvents.BELL_RESONATE, SoundSource.PLAYERS, 1F, 1F);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand){

		//If shift right clicking, set current data
		//If normal right clicking, revert to last save and set current position as data
		//Apply sickness based on time between uses

		ItemStack held = playerIn.getItemInHand(hand);
		RecallData oldData = held.getOrDefault(CRItems.TIME_RECALL_DATA, null);

		RecallData newData = storeData(playerIn);

		if(!playerIn.isShiftKeyDown()){
			//World sound for recalling
			//Played at source and destination
			worldIn.playSound(null, playerIn.blockPosition(), SoundEvents.BELL_RESONATE, SoundSource.PLAYERS, 1F, 1F);
			recall(oldData, playerIn, held);//Will do nothing if over time limit, wrong player, or no data stored
		}

		held.set(CRItems.TIME_RECALL_DATA, newData);

		return InteractionResultHolder.success(held);
	}

	@Override
	public double getMaxWind(){
		return 10;
	}

	public static record RecallData(long timestamp, String playerName, String dimension, double posX, double posY, double posZ, long blockPosition, float yaw, float yawHead, float pitch, float health, int hunger, float saturation, double velX, double velY, double velZ){

		public static final Codec<RecallData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.LONG.fieldOf("timestamp").forGetter(RecallData::timestamp),
				Codec.STRING.fieldOf("playerName").forGetter(RecallData::playerName),
				Codec.STRING.fieldOf("stability").forGetter(RecallData::dimension),
				Codec.DOUBLE.fieldOf("posX").forGetter(RecallData::posX),
				Codec.DOUBLE.fieldOf("posY").forGetter(RecallData::posY),
				Codec.DOUBLE.fieldOf("posZ").forGetter(RecallData::posZ),
				Codec.LONG.fieldOf("blockPosition").forGetter(RecallData::blockPosition),
				Codec.FLOAT.fieldOf("yaw").forGetter(RecallData::yaw),
				Codec.FLOAT.fieldOf("yawHead").forGetter(RecallData::yawHead),
				Codec.FLOAT.fieldOf("pitch").forGetter(RecallData::pitch),
				Codec.FLOAT.fieldOf("health").forGetter(RecallData::health),
				Codec.INT.fieldOf("hunger").forGetter(RecallData::hunger),
				Codec.FLOAT.fieldOf("saturation").forGetter(RecallData::saturation),
				Codec.DOUBLE.fieldOf("velX").forGetter(RecallData::velX),
				Codec.DOUBLE.fieldOf("velY").forGetter(RecallData::velY),
				Codec.DOUBLE.fieldOf("velZ").forGetter(RecallData::velZ)
		).apply(instance, RecallData::new));

		public static final StreamCodec<ByteBuf, RecallData> STREAM_CODEC = StreamCodecUtils.composite(
				ByteBufCodecs.VAR_LONG, RecallData::timestamp,
				ByteBufCodecs.STRING_UTF8, RecallData::playerName,
				ByteBufCodecs.STRING_UTF8, RecallData::dimension,
				ByteBufCodecs.DOUBLE, RecallData::posX,
				ByteBufCodecs.DOUBLE, RecallData::posY,
				ByteBufCodecs.DOUBLE, RecallData::posZ,
				ByteBufCodecs.VAR_LONG, RecallData::blockPosition,
				ByteBufCodecs.FLOAT, RecallData::yaw,
				ByteBufCodecs.FLOAT, RecallData::yawHead,
				ByteBufCodecs.FLOAT, RecallData::pitch,
				ByteBufCodecs.FLOAT, RecallData::health,
				ByteBufCodecs.VAR_INT, RecallData::hunger,
				ByteBufCodecs.FLOAT, RecallData::saturation,
				ByteBufCodecs.DOUBLE, RecallData::velX,
				ByteBufCodecs.DOUBLE, RecallData::velY,
				ByteBufCodecs.DOUBLE, RecallData::velZ,
			RecallData::new
		);
	}
}
