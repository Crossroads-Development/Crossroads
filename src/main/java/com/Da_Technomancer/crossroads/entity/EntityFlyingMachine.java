package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ObjectHolder;

import java.util.List;

@ObjectHolder(Crossroads.MODID)
public class EntityFlyingMachine extends Entity{

	@ObjectHolder("flying_machine")
	public static EntityType<EntityFlyingMachine> type = null;

	//In radians, 0 is down, pi/2 is forward
	private static final DataParameter<Float> GRAV_PLATE_ANGLE = EntityDataManager.defineId(EntityFlyingMachine.class, DataSerializers.FLOAT);
	private static final float ACCEL = 0.12F;
	private int damage = 0;

	public EntityFlyingMachine(EntityType<EntityFlyingMachine> type, World worldIn){
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
		if(controller instanceof PlayerEntity){

			//Do movement on the client ONLY. The wheel angle isn't correct on the server
			if(level.isClientSide && controller == Minecraft.getInstance().player){
				//Rotate the wheel based on player control
				GameSettings settings = Minecraft.getInstance().options;
				if(settings.keyUp.isDown()){
					setAngle(getAngle() - (float) Math.PI / 20F);
				}else if(settings.keyDown.isDown()){
					setAngle(getAngle() + (float) Math.PI / 20F);
				}

				float angle = 0;
				//Apply acceleration based on wheel angle. Total acceleration is ACCEL, in direction of wheel
				angle = -getAngle();
				yRot = controller.getYHeadRot();
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
		}else if(!level.isClientSide){
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
		}else if(!level.isClientSide && isAlive()){
			if(source instanceof IndirectEntityDamageSource && source.getEntity() != null && hasPassenger(source.getEntity())){
				return false;
			}else{
				damage += amount * 10F;
				markHurt();
				boolean flag = source.getEntity() instanceof PlayerEntity && ((PlayerEntity) source.getEntity()).isCreative();

				if(flag || damage > 40){
					if(!flag && level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)){
						spawnAtLocation(CRItems.flyingMachine);
					}

					remove();
				}
			}
		}
		return true;
	}

	@Override
	protected boolean isMovementNoisy(){
		return false;
	}

	public ActionResultType interact(PlayerEntity player, Hand hand){
		if(!player.isShiftKeyDown()){
			if(!level.isClientSide){
				player.startRiding(this);
			}
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}

	@Override
	public Entity getControllingPassenger(){
		List<Entity> list = getPassengers();
		return list.isEmpty() ? null : list.get(0);
	}

	@Override
	public IPacket<?> getAddEntityPacket(){
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
	public void readAdditionalSaveData(CompoundNBT nbt){
		damage = nbt.getInt("dam");
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT nbt){
		nbt.putInt("dam", damage);
	}
}
