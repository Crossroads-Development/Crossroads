package com.Da_Technomancer.crossroads.API.technomancy;

import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.CRConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public interface IGateway extends IInfoTE{

	/**
	 * Gets the address of this gateway
	 * Null indicates either an address-less gateway or an incomplete gateway- either way, it can not be dialed
	 * Virtual server side only
	 * @return Address of this gateway
	 */
	@Nullable
	GatewayAddress getAddress();

	/**
	 * Dials this gateway to another
	 * Should not modify or adjust the other gateway
	 * This method is responsible for disconnecting any previous dial on itself
	 * Virtual server side only
	 * @param other The other gateway
	 * @param cost Whether this gateway should assume the cost for the link/produce flux
	 */
	void dialTo(GatewayAddress other, boolean cost);

	/**
	 * Cancels/removes the connection of this gateway
	 * If this gateway is not currently dialed to the passed parameter, this method should do nothing
	 * Should not modify the other gateway
	 * Virtual server side only
	 * @param other The gateway that this gateway should undial from
	 */
	void undial(GatewayAddress other);

	/**
	 * Teleports an entity to this gateway, and rotates/orients them
	 * Virtual server side only
	 * @param entity The entity to teleport
	 * @param horizontalRelPos A value in [-1, 1] indicating position on the horizontal axis relative to the center
	 * @param verticalRelPos A value in [-1, 1] indicating position on the vertical axis relative to the center
	 * @param sourceAxis The horizontal axis parallel to the plane of the source gateway
	 */
	void teleportEntity(Entity entity, float horizontalRelPos, float verticalRelPos, Direction.Axis sourceAxis);

	/**
	 * Dismantles the multiblock
	 * Called by the frame when broken
	 */
	void dismantle();

	/**
	 * Teleports an entity to the specified position and dimension, and applies rotations and gateway cooldowns
	 * @param e The entity to teleport
	 * @param target The world to teleport it to
	 * @param posX The desired entity X position
	 * @param posY The desired entity Y position
	 * @param posZ The desired entity Z position
	 * @param yawRotation The amount (in degrees) to rotate the yaw of this entity
	 */
	static void teleportEntityTo(Entity e, ServerLevel target, double posX, double posY, double posZ, float yawRotation){
		//Moves an entity to any position in any dimension

		if(e instanceof ServerPlayer play){
			//Based on TeleportCommand

			//Load endpoint chunk
			target.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, new ChunkPos(new BlockPos(posX, posY, posZ)), 1, e.getId());

			e.stopRiding();
			if(play.isSleeping()){
				play.stopSleepInBed(true, true);
			}

			float prevHeadYaw = play.getYHeadRot();
			Vec3 prevVelocity = play.getDeltaMovement();
			if(target == e.level){
				play.connection.teleport(posX, posY, posZ, play.getViewYRot(1) + yawRotation, play.getViewXRot(1));
			}else{
				play.teleportTo(target, posX, posY, posZ, play.getViewYRot(1) + yawRotation, play.getViewXRot(1));
			}
			play.setYHeadRot(prevHeadYaw + yawRotation);
			play.setDeltaMovement(prevVelocity.yRot(yawRotation));
		}else{
			Vec3 prevVelocity = e.getDeltaMovement();
			if(target == e.level){
				float prevHeadYaw = e.getYHeadRot();
				e.moveTo(posX, posY, posZ, e.getViewYRot(1) + yawRotation, e.getViewXRot(1));
				e.setYHeadRot(prevHeadYaw + yawRotation);
			}else{
				//We clone the entity, and delete the original
				e.unRide();
				Entity entity = e;
				e = e.getType().create(target);
				if(e == null){
					return;
				}

				e.restoreFrom(entity);
				e.moveTo(posX, posY, posZ, entity.getViewYRot(1) + yawRotation, entity.getViewXRot(1));
				e.setYHeadRot(entity.getYHeadRot() + yawRotation);
				target.addDuringTeleport(e);
				entity.remove(Entity.RemovalReason.CHANGED_DIMENSION);//Remove the copy in the source dimension
			}
			e.setDeltaMovement(prevVelocity.yRot(yawRotation));
		}

		//Add a timestamp of when this entity was teleported by a gateway
		//Used to add a teleportation cooldown
		CompoundTag eNBT = e.getPersistentData();
		String dimName = MiscUtil.getDimensionName(target);
		long worldTime = target.getGameTime();
		eNBT.putString("cr_gateway_dim", dimName);
		eNBT.putLong("cr_gateway_time", worldTime);
	}

	static boolean isAllowedToTeleport(Entity e, Level sourceWorld){
		if(!CRConfig.allowGateway.get()){
			return false;
		}
		if(!(e instanceof Player) && !CRConfig.allowGatewayEntities.get()){
			return false;
		}

		CompoundTag nbt = e.getPersistentData();
		String dimName = MiscUtil.getDimensionName(sourceWorld);
		long worldTime = sourceWorld.getGameTime();
		//Effective teleportation cooldown of 20*3 ticks = 3 seconds
		return !nbt.getString("cr_gateway_dim").equals(dimName) || Math.abs(worldTime - nbt.getLong("cr_gateway_time")) >= 20 * 3;
	}
}
