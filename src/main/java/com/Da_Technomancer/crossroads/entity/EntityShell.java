package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.api.alchemy.AlchemyUtil;
import com.Da_Technomancer.crossroads.api.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class EntityShell extends ThrowableProjectile implements ItemSupplier{

	public static EntityType<EntityShell> type;

	private static final EntityDataAccessor<ItemStack> item = SynchedEntityData.defineId(EntityShell.class, EntityDataSerializers.ITEM_STACK);

	private ReagentMap contents;//Technically redundant with the itemstack in data manager, but meh

	public EntityShell(EntityType<EntityShell> type, Level worldIn){
		super(type, worldIn);
	}

	public EntityShell(Level worldIn, ReagentMap contents, ItemStack stack){
		this(type, worldIn);
		this.contents = contents;
		entityData.set(item, stack);
	}

	public EntityShell(Level worldIn, LivingEntity throwerIn, ReagentMap contents, ItemStack stack){
		super(type, throwerIn, worldIn);
		this.contents = contents;
		entityData.set(item, stack);
	}

	@Override
	protected void onHit(HitResult result){
		if(!level.isClientSide){
			if(contents != null){
				Vec3 hit = result.getLocation();
				AlchemyUtil.releaseChemical(level, new BlockPos(hit.x, hit.y, hit.z), contents);
			}
			level.playSound(null, getX(), getY(), getZ(), SoundEvents.GLASS_BREAK, SoundSource.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
			level.broadcastEntityEvent(this, (byte) 3);
			remove(RemovalReason.KILLED);
		}
	}

	@Override
	public void tick(){
		//EntityShell has movement physics like items, but collision behavior like projectiles

		super.baseTick();

		//Movement physics
		//Copied from ItemEntity
		xo = getX();
		yo = getY();
		zo = getZ();
		Vec3 vec3 = getDeltaMovement();
		if(!isNoGravity()){
			setDeltaMovement(getDeltaMovement().add(0.0D, -0.04D, 0.0D));
		}

		if(!onGround || getDeltaMovement().horizontalDistanceSqr() > 1.0E-5D || (tickCount + getId()) % 4 == 0){
			move(MoverType.SELF, this.getDeltaMovement());
			float f1 = 0.98F;

			if(onGround){
				f1 = level.getBlockState(new BlockPos(getX(), getY() - 1.0D, getZ())).getFriction(level, new BlockPos(getX(), getY() - 1.0D, getZ()), this) * 0.98F;
			}

			setDeltaMovement(getDeltaMovement().multiply(f1, 0.98D, f1));
			if(onGround){
				Vec3 vec31 = getDeltaMovement();
				if(vec31.y < 0.0D){
					setDeltaMovement(vec31.multiply(1.0D, -0.5D, 1.0D));
				}
			}
		}

		hasImpulse |= updateInWaterStateAndDoFluidPushing();
		if(!level.isClientSide){
			double d0 = getDeltaMovement().subtract(vec3).lengthSqr();
			if(d0 > 0.01D){
				hasImpulse = true;
			}
		}

		//Impact handling
		//Copied from ThrowableProjectile
		HitResult hitresult = ProjectileUtil.getHitResult(this, this::canHitEntity);
		boolean flag = false;
		if(hitresult.getType() == HitResult.Type.BLOCK){
			BlockPos blockpos = ((BlockHitResult) hitresult).getBlockPos();
			BlockState blockstate = level.getBlockState(blockpos);
			if(blockstate.is(Blocks.NETHER_PORTAL)){
				this.handleInsidePortal(blockpos);
				flag = true;
			}else if(blockstate.is(Blocks.END_GATEWAY)){
				BlockEntity blockentity = level.getBlockEntity(blockpos);
				if(blockentity instanceof TheEndGatewayBlockEntity && TheEndGatewayBlockEntity.canEntityTeleport(this)){
					TheEndGatewayBlockEntity.teleportEntity(level, blockpos, blockstate, this, (TheEndGatewayBlockEntity) blockentity);
				}

				flag = true;
			}
		}

		if(hitresult.getType() != HitResult.Type.MISS && !flag && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)){
			onHit(hitresult);
		}
	}

	@Override
	protected void defineSynchedData(){
		entityData.define(item, new ItemStack(CRItems.shellGlass));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag nbt){
		super.readAdditionalSaveData(nbt);
		contents = ReagentMap.readFromNBT(nbt);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag nbt){
		super.addAdditionalSaveData(nbt);
		if(contents != null){
			contents.write(nbt);
		}
	}

	@Override
	public Packet<?> getAddEntityPacket(){
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public ItemStack getItem(){
		return entityData.get(item);
	}
}
