package com.Da_Technomancer.crossroads.items.witchcraft;

import com.Da_Technomancer.crossroads.api.packets.CRPackets;
import com.Da_Technomancer.crossroads.api.packets.SendCompassTargetToClient;
import com.Da_Technomancer.crossroads.api.witchcraft.IPerishable;
import com.Da_Technomancer.crossroads.integration.curios.CurioHelper;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class BloodCompass extends Item{

	/**
	 * There is no guarantee that the tracked entity exists on the client
	 * Therefore, the compass item syncs the location of the tracked entity to the relevant client (the client of the player using the compass)
	 * This field is null on the server side. On the client side, it tracks the most recently synced entity position
	 * Don't make this field static
	 */
	public EntitySyncRecord syncedEntity = null;

	public BloodCompass(){
		super(new Item.Properties());
		String name = "blood_compass";
		CRItems.queueForRegister(name, this);
	}

	private static GlobalPos getTargetServer(UUID uuid, ServerLevel level){
		if(uuid != null){
			//This will be null if the entity is unloaded or in another dimension
			Entity ent = level.getEntity(uuid);
			if(ent != null){
				return GlobalPos.of(level.dimension(), ent.blockPosition());
			}
		}
		return null;
	}

	@Nullable
	public GlobalPos getTargetClient(@Nullable ClientLevel world, ItemStack stack, @Nullable Entity holder){
		if(!(holder instanceof LivingEntity player) || world == null){
			return null;
		}
		UUID uuid = getTargetUUID(stack, player, world);
		if(uuid != null){
			if(syncedEntity != null && syncedEntity.validRecord(uuid, world.getGameTime())){
				return syncedEntity.entityPos;
			}
		}
		return null;
	}

	@Nullable
	private static UUID getTargetUUID(ItemStack stack, @Nonnull LivingEntity player, @Nonnull Level world){
		ItemStack targetItem = CurioHelper.getEquipped(filtStack -> filtStack.getItem() instanceof BloodSample, player);
		if(!targetItem.isEmpty() && !IPerishable.isSpoiled(targetItem, world)){
			BloodSample.EntitySourceData sourceData = targetItem.get(CRItems.ENTITY_SOURCE_DATA);
			return sourceData == null ? null : sourceData.effectiveUUID();
		}
		return null;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity holdingEnt, int slot, boolean selected){
		if(world.isClientSide){
			return;
		}

		if(holdingEnt instanceof ServerPlayer player && world instanceof ServerLevel serverWorld && world.getGameTime() % 10 == 0){
			if(selected || player.getOffhandItem() == stack){
				//Every few ticks, send the location of the targeted entity to the client while the compass is held
				UUID uuid = getTargetUUID(stack, player, world);
				GlobalPos target = getTargetServer(uuid, serverWorld);
				if(uuid != null && target != null){
					CRPackets.sendPacketToPlayer(player, new SendCompassTargetToClient(target, uuid));
				}
			}
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag){
		tooltip.add(Component.translatable("tt.crossroads.blood_compass.desc"));
		tooltip.add(Component.translatable("tt.crossroads.blood_compass.spoil"));
	}

	public record EntitySyncRecord(UUID entityUUID, GlobalPos entityPos, long updateTime){

		public boolean validRecord(UUID targetEntity, long currentTime){
			return entityUUID.equals(targetEntity) && currentTime - updateTime < 200;
		}
	}
}
