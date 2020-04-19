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
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.List;

@ObjectHolder(Crossroads.MODID)
public class EntityFlyingMachine extends Entity{

	@ObjectHolder("flying_machine")
	public static EntityType<EntityFlyingMachine> type = null;

	//In radians, 0 is down, pi/2 is forward
	private static final DataParameter<Float> GRAV_PLATE_ANGLE = EntityDataManager.createKey(EntityFlyingMachine.class, DataSerializers.FLOAT);
	private static final float ACCEL = 0.12F;
	private int damage = 0;

	public EntityFlyingMachine(EntityType<EntityFlyingMachine> type, World worldIn){
		super(type, worldIn);
		preventEntitySpawning = true;
	}

	@Override
	protected void registerData(){
		dataManager.register(GRAV_PLATE_ANGLE, 0F);
	}

	protected float getAngle(){
		return dataManager.get(GRAV_PLATE_ANGLE);
	}

	private void setAngle(float newAngle){
		dataManager.set(GRAV_PLATE_ANGLE, newAngle);
	}

	@Override
	public void tick(){
		Entity controller = getControllingPassenger();

		//Regen health similar to boats and minecarts
		if(damage > 0){
			damage--;
		}

		fallDistance = 0;//Prevent fall damage

		prevPosX = this.posX;
		prevPosY = this.posY;
		prevPosZ = this.posZ;

		double[] vel = new double[3];
		vel[0] = getMotion().getX();
		vel[1] = getMotion().getY();
		vel[2] = getMotion().getZ();

		//Gravity
		vel[1] -= 0.08D;

		//When there is a player riding, let the client control movement
		//When there isn't a player, let the server control movement
		//Never let both sides control movement
		if(controller instanceof PlayerEntity){

			//Do movement on the client ONLY. The wheel angle isn't correct on the server
			if(world.isRemote && controller == Minecraft.getInstance().player){
				//Rotate the wheel based on player control
				GameSettings settings = Minecraft.getInstance().gameSettings;
				if(settings.keyBindForward.isKeyDown()){
					setAngle(getAngle() - (float) Math.PI / 20F);
				}else if(settings.keyBindBack.isKeyDown()){
					setAngle(getAngle() + (float) Math.PI / 20F);
				}

				float angle = 0;
				//Apply acceleration based on wheel angle. Total acceleration is ACCEL, in direction of wheel
				angle = -getAngle();
				rotationYaw = controller.getRotationYawHead();
				controller.velocityChanged = true;

				vel[0] += Math.sin(angle) * Math.sin(-Math.toRadians(rotationYaw) - Math.PI) * ACCEL;
				vel[1] += -Math.cos(angle) * ACCEL;
				vel[2] += Math.sin(angle) * Math.cos(-Math.toRadians(rotationYaw) - Math.PI) * ACCEL;

				//Apply our calculated velocity and move
				setMotion(vel[0], vel[1], vel[2]);
				markVelocityChanged();
				move(MoverType.SELF, getMotion());

				//Air resistance/friction
				final double min = 0.003D;
				for(int i = 0; i < 3; i++){
					vel[i] *= 0.8D;
					if(Math.abs(vel[i]) < min){
						vel[i] = 0;
					}
				}
				setMotion(vel[0], vel[1], vel[2]);
			}
		}else if(!world.isRemote){
			//When we have no rider, just go down
			setAngle(0);
			vel[1] -= ACCEL;

			markVelocityChanged();
			move(MoverType.SELF, getMotion());

			//Air resistance/friction
			final double min = 0.003D;
			for(int i = 0; i < 3; i++){
				vel[i] *= 0.8D;
				if(Math.abs(vel[i]) < min){
					vel[i] = 0;
				}
			}
			setMotion(vel[0], vel[1], vel[2]);
		}

		super.tick();
	}

	@Override
	@Nullable
	public AxisAlignedBB getCollisionBox(Entity entityIn){
		return entityIn.canBePushed() ? entityIn.getBoundingBox() : null;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		//Boat/minecart style breaking

		if(isInvulnerableTo(source)){
			return false;
		}else if(!world.isRemote && isAlive()){
			if(source instanceof IndirectEntityDamageSource && source.getTrueSource() != null && isPassenger(source.getTrueSource())){
				return false;
			}else{
				damage += amount * 10F;
				markVelocityChanged();
				boolean flag = source.getTrueSource() instanceof PlayerEntity && ((PlayerEntity) source.getTrueSource()).isCreative();

				if(flag || damage > 40){
					if(!flag && world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)){
						entityDropItem(CRItems.flyingMachine);
					}

					remove();
				}
			}
		}
		return true;
	}

	@Override
	protected boolean canTriggerWalking(){
		return false;
	}

	public boolean processInitialInteract(PlayerEntity player, Hand hand){
		if(!player.isSneaking()){
			if(!world.isRemote){
				player.startRiding(this);
			}
			return true;
		}
		return false;
	}

	@Override
	public Entity getControllingPassenger(){
		List<Entity> list = getPassengers();
		return list.isEmpty() ? null : list.get(0);
	}

	@Override
	public IPacket<?> createSpawnPacket(){
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public double getMountedYOffset(){
		return 1.1D;
	}

	@Override
	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(){
		return getBoundingBox();
	}

	@Override
	public boolean canBePushed(){
		return true;
	}

	@Override
	public boolean canBeCollidedWith(){
		return isAlive();
	}

	@Override
	public void readAdditional(CompoundNBT nbt){
		damage = nbt.getInt("dam");
	}

	@Override
	public void writeAdditional(CompoundNBT nbt){
		nbt.putInt("dam", damage);
	}
}
