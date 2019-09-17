package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.API.packets.CrossroadsPackets;
import com.Da_Technomancer.crossroads.API.packets.NbtToEntityServer;
import com.Da_Technomancer.crossroads.items.CrossroadsItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class EntityFlyingMachine extends Entity implements INbtReceiver{

	private static final DataParameter<Float> GRAV_PLATE_ANGLE = EntityDataManager.createKey(EntityFlyingMachine.class, DataSerializers.FLOAT);//0 is down, radians, pi/2 is forward
	private static final float ACCEL = 0.12F;
	private int damage = 0;

	public EntityFlyingMachine(World worldIn){
		super(worldIn);
		preventEntitySpawning = true;
		setSize(1F, 1.3F);
	}

	protected float getAngle(){
		return dataManager.get(GRAV_PLATE_ANGLE);
	}

	@Override
	public void onUpdate(){
		Entity controller = getControllingPassenger();

		if(world.isRemote){
			if(controller != null && controller == Minecraft.getInstance().player){
				if(GameSettings.isKeyDown(Minecraft.getInstance().gameSettings.keyBindForward)){
					dataManager.set(GRAV_PLATE_ANGLE, dataManager.get(GRAV_PLATE_ANGLE) - (float) Math.PI / 20F);
					CompoundNBT nbt = new CompoundNBT();
					nbt.putFloat("ang", dataManager.get(GRAV_PLATE_ANGLE));
					CrossroadsPackets.network.sendToServer(new NbtToEntityServer(getUniqueID(), world.provider.getDimension(), nbt));
				}else if(GameSettings.isKeyDown(Minecraft.getInstance().gameSettings.keyBindBack)){
					dataManager.set(GRAV_PLATE_ANGLE, dataManager.get(GRAV_PLATE_ANGLE) + (float) Math.PI / 20F);
					CompoundNBT nbt = new CompoundNBT();
					nbt.putFloat("ang", dataManager.get(GRAV_PLATE_ANGLE));
					CrossroadsPackets.network.sendToServer(new NbtToEntityServer(getUniqueID(), world.provider.getDimension(), nbt));
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
		motionY -= 0.08D;

		if(controller == null){
			dataManager.set(GRAV_PLATE_ANGLE, 0F);
		}else{
			float angle = -dataManager.get(GRAV_PLATE_ANGLE);
			rotationYaw = controller.getRotationYawHead();
			//Fun fact that isn't documented: The server and client have different definitions of angle in the x-z plane. This is weird and annoying, and a pain to work out. Vanilla server definition: Counter clockwise is positive, 0 degrees at -x axis. Vanilla client definition: Clockwise is positive, 0 degrees at +z axis. Someone stab the people at Mojang, please
			motionY -= Math.cos(angle) * ACCEL;
			motionX += Math.sin(angle) * Math.sin(-Math.toRadians(rotationYaw) - Math.PI) * ACCEL;
			motionZ += Math.sin(angle) * Math.cos(-Math.toRadians(rotationYaw) - Math.PI) * ACCEL;
			controller.velocityChanged = true;
		}
		markVelocityChanged();
		move(MoverType.SELF, motionX, motionY, motionZ);

		motionX *= 0.8D;
		motionY *= 0.8D;
		motionZ *= 0.8D;


		if(Math.abs(motionX) < 0.003D){
			motionX = 0.0D;
		}

		if(Math.abs(motionY) < 0.003D){
			motionY = 0.0D;
		}

		if(Math.abs(motionZ) < 0.003D){
			motionZ = 0.0D;
		}
		super.onUpdate();
	}

	@Override
	@Nullable
	public AxisAlignedBB getCollisionBox(Entity entityIn){
		return entityIn.canBePushed() ? entityIn.getEntityBoundingBox() : null;
	}

	public boolean attackEntityFrom(DamageSource source, float amount){
		if(isEntityInvulnerable(source)){
			return false;
		}else if(!world.isRemote && !isDead){
			if(source instanceof IndirectEntityDamageSource && source.getTrueSource() != null && this.isPassenger(source.getTrueSource())){
				return false;
			}else{
				damage += amount * 10F;
				markVelocityChanged();
				boolean flag = source.getTrueSource() instanceof PlayerEntity && ((PlayerEntity) source.getTrueSource()).capabilities.isCreativeMode;

				if(flag || damage > 40){
					if(!flag && world.getGameRules().getBoolean("doEntityDrops")){
						dropItemWithOffset(CrossroadsItems.flyingMachine, 1, 0.0F);
					}

					setDead();
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
	protected void entityInit(){
		dataManager.register(GRAV_PLATE_ANGLE, 0F);
	}

	@Override
	public double getMountedYOffset(){
		return 1.1D;
	}

	@Override
	public void applyEntityCollision(Entity entityIn){
		if(entityIn instanceof BoatEntity){
			if(entityIn.getEntityBoundingBox().minY < getEntityBoundingBox().maxY){
				super.applyEntityCollision(entityIn);
			}
		}else if(entityIn.getEntityBoundingBox().minY <= getEntityBoundingBox().minY){
			super.applyEntityCollision(entityIn);
		}
	}

	@Override
	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(){
		return getEntityBoundingBox();
	}

	@Override
	public boolean canBePushed(){
		return true;
	}

	@Override
	public boolean canBeCollidedWith(){
		return !isDead;
	}

	@Override
	protected void readEntityFromNBT(CompoundNBT nbt){
		damage = nbt.getInt("dam");
	}

	@Override
	protected void writeEntityToNBT(CompoundNBT nbt){
		nbt.putInt("dam", damage);
	}

	@Override
	public void receiveNBT(CompoundNBT nbt){
		if(!world.isRemote){
			dataManager.set(GRAV_PLATE_ANGLE, nbt.getFloat("ang"));
		}
	}
}
