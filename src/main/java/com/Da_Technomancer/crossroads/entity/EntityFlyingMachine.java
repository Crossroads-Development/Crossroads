package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.BoatEntity;
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

	private static final DataParameter<Float> GRAV_PLATE_ANGLE = EntityDataManager.createKey(EntityFlyingMachine.class, DataSerializers.FLOAT);//0 is down, radians, pi/2 is forward
	private static final float ACCEL = 0.12F;
	private int damage = 0;

	public EntityFlyingMachine(EntityType<EntityFlyingMachine> type, World worldIn){
		super(type, worldIn);
		preventEntitySpawning = true; 
	}

	protected float getAngle(){
		return dataManager.get(GRAV_PLATE_ANGLE);
	}

	@Override
	protected void registerData(){
		dataManager.register(GRAV_PLATE_ANGLE, 0F);
	}

	@Override
	public void tick(){
		Entity controller = getControllingPassenger();

		if(world.isRemote){
			GameSettings settings = Minecraft.getInstance().gameSettings;
			if(controller != null && controller == Minecraft.getInstance().player){
				if(settings.keyBindForward.isKeyDown()){
					dataManager.set(GRAV_PLATE_ANGLE, dataManager.get(GRAV_PLATE_ANGLE) - (float) Math.PI / 20F);
				}else if(settings.keyBindBack.isKeyDown()){
					dataManager.set(GRAV_PLATE_ANGLE, dataManager.get(GRAV_PLATE_ANGLE) + (float) Math.PI / 20F);
				}
			}
		}
		if(damage > 0){
			damage--;
		}

		fallDistance = 0;
		prevPosX = this.posX;
		prevPosY = this.posY;
		prevPosZ = this.posZ;

		double[] vel = new double[3];
		vel[0] = getMotion().getX();
		vel[1] = getMotion().getY();
		vel[2] = getMotion().getZ();

		vel[1] -= 0.08D;//Gravity

		if(controller == null){
			dataManager.set(GRAV_PLATE_ANGLE, 0F);
		}else{
			float angle = -dataManager.get(GRAV_PLATE_ANGLE);
			rotationYaw = controller.getRotationYawHead();
			vel[0] += Math.sin(angle) * Math.sin(-Math.toRadians(rotationYaw) - Math.PI) * ACCEL;
			vel[1] += -Math.cos(angle) * ACCEL;
			vel[2] += Math.sin(angle) * Math.cos(-Math.toRadians(rotationYaw) - Math.PI) * ACCEL;
			controller.velocityChanged = true;
		}
		setMotion(vel[0], vel[1], vel[2]);
		markVelocityChanged();
		move(MoverType.SELF, getMotion());

		final double min = 0.003D;
		for(int i = 0; i < 3; i++){
			vel[i] *= 0.8D;//air resistance
			if(Math.abs(vel[i]) < min){
				vel[i] = 0;
			}
		}
		setMotion(vel[0], vel[1], vel[2]);
		super.tick();
	}

	@Override
	@Nullable
	public AxisAlignedBB getCollisionBox(Entity entityIn){
		return entityIn.canBePushed() ? entityIn.getBoundingBox() : null;
	}

	public boolean attackEntityFrom(DamageSource source, float amount){
		if(isInvulnerableTo(source)){
			return false;
		}else if(!world.isRemote && isAlive()){
			if(source instanceof IndirectEntityDamageSource && source.getTrueSource() != null && this.isPassenger(source.getTrueSource())){
				return false;
			}else{
				damage += amount * 10F;
				markVelocityChanged();
				boolean flag = source.getTrueSource() instanceof PlayerEntity && ((PlayerEntity) source.getTrueSource()).isCreative();

				if(flag || damage > 40){
					if(!flag && world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)){
						entityDropItem(CRItems.flyingMachine, 0);
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
		if(player.isSneaking()){
			return false;
		}else{
			if(!this.world.isRemote){
				player.startRiding(this);
			}
			return true;
		}
	}

	@Override
	public Entity getControllingPassenger(){
		List<Entity> list = this.getPassengers();
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
	public void applyEntityCollision(Entity entityIn){
		if(entityIn instanceof BoatEntity){
			if(entityIn.getBoundingBox().minY < getBoundingBox().maxY){
				super.applyEntityCollision(entityIn);
			}
		}else if(entityIn.getBoundingBox().minY <= getBoundingBox().minY){
			super.applyEntityCollision(entityIn);
		}
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
