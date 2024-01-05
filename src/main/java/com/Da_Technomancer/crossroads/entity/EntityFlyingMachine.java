package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class EntityFlyingMachine extends Entity{

	public static EntityType<EntityFlyingMachine> type;

	//In radians, 0 is down, pi/2 is forward
	private static final EntityDataAccessor<Float> GRAV_PLATE_ANGLE = SynchedEntityData.defineId(EntityFlyingMachine.class, EntityDataSerializers.FLOAT);
	private static final float ACCEL = 0.12F;
	private int damage = 0;

	public EntityFlyingMachine(EntityType<EntityFlyingMachine> type, Level worldIn){
		super(type, worldIn);
		blocksBuilding = true;
	}

	@Override
	protected void defineSynchedData(){
		entityData.define(GRAV_PLATE_ANGLE, 0F);
	}

	protected float getAngle(){
		return entityData.get(GRAV_PLATE_ANGLE);
	}

	private void setAngle(float newAngle){
		entityData.set(GRAV_PLATE_ANGLE, newAngle);
	}

	@Override
	public void tick(){
		Entity controller = getControllingPassenger();

		//Regen health similar to boats and minecarts
		if(damage > 0){
			damage--;
		}

		fallDistance = 0;//Prevent fall damage

		xo = this.getX();
		yo = this.getY();
		zo = this.getZ();

		double[] vel = new double[3];
		vel[0] = getDeltaMovement().x();
		vel[1] = getDeltaMovement().y();
		vel[2] = getDeltaMovement().z();

		//Gravity
		vel[1] -= 0.08D;

		//When there is a player riding, let the client control movement
		//When there isn't a player, let the server control movement
		//Never let both sides control movement
		if(controller instanceof Player){

			//Do movement on the client ONLY. The wheel angle isn't correct on the server
			if(level().isClientSide && controller == Minecraft.getInstance().player){
				//Rotate the wheel based on player control
				Options settings = Minecraft.getInstance().options;
				if(settings.keyUp.isDown()){
					setAngle(getAngle() - (float) Math.PI / 20F);
				}else if(settings.keyDown.isDown()){
					setAngle(getAngle() + (float) Math.PI / 20F);
				}

				float angle = 0;
				//Apply acceleration based on wheel angle. Total acceleration is ACCEL, in direction of wheel
				angle = -getAngle();
				float yRot = controller.getYHeadRot();
				setYRot(yRot);
				controller.hurtMarked = true;
				vel[0] += Math.sin(angle) * Math.sin(-Math.toRadians(yRot) - Math.PI) * ACCEL;
				vel[1] += -Math.cos(angle) * ACCEL;
				vel[2] += Math.sin(angle) * Math.cos(-Math.toRadians(yRot) - Math.PI) * ACCEL;

				//Apply our calculated velocity and move
				setDeltaMovement(vel[0], vel[1], vel[2]);
				markHurt();
				move(MoverType.SELF, getDeltaMovement());

				//Air resistance/friction
				final double min = 0.003D;
				for(int i = 0; i < 3; i++){
					vel[i] *= 0.8D;
					if(Math.abs(vel[i]) < min){
						vel[i] = 0;
					}
				}
				setDeltaMovement(vel[0], vel[1], vel[2]);
			}
		}else if(!level().isClientSide){
			//When we have no rider, just go down
			setAngle(0);
			vel[1] -= ACCEL;

			markHurt();
			move(MoverType.SELF, getDeltaMovement());

			//Air resistance/friction
			final double min = 0.003D;
			for(int i = 0; i < 3; i++){
				vel[i] *= 0.8D;
				if(Math.abs(vel[i]) < min){
					vel[i] = 0;
				}
			}
			setDeltaMovement(vel[0], vel[1], vel[2]);
		}

		super.tick();
	}

	@Override
	public boolean hurt(DamageSource source, float amount){
		//Boat/minecart style breaking

		if(isInvulnerableTo(source)){
			return false;
		}else if(!level().isClientSide && isAlive()){
			damage += amount * 10F;
			markHurt();
			boolean flag = source.getEntity() instanceof Player && ((Player) source.getEntity()).isCreative();

			if(flag || damage > 40){
				if(!flag && level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)){
					spawnAtLocation(CRItems.flyingMachine);
				}

				remove(RemovalReason.KILLED);
			}
		}
		return true;
	}

	@Override
	protected MovementEmission getMovementEmission(){
		return MovementEmission.EVENTS;
	}

	public InteractionResult interact(Player player, InteractionHand hand){
		if(!player.isShiftKeyDown()){
			if(!level().isClientSide){
				player.startRiding(this);
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public LivingEntity getControllingPassenger(){
		Entity entity = getFirstPassenger();
		if(entity instanceof LivingEntity living){
			return living;
		}else{
			return null;
		}
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket(){
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public double getPassengersRidingOffset(){
		return 1.1D;
	}

	@Override
	public boolean isPushable(){
		return true;
	}

	@Override
	public boolean isPickable(){
		return isAlive();
	}

	@Override
	public void readAdditionalSaveData(CompoundTag nbt){
		damage = nbt.getInt("dam");
	}

	@Override
	public void addAdditionalSaveData(CompoundTag nbt){
		nbt.putInt("dam", damage);
	}
}
