package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class EntityNitro extends ThrowableProjectile implements ItemSupplier{

	@ObjectHolder("nitro")
	public static EntityType<EntityNitro> type = null;
	private static final ItemStack RENDER_STACK = new ItemStack(CRItems.nitroglycerin);

	public EntityNitro(EntityType<EntityNitro> type, Level worldIn){
		super(type, worldIn);
	}

	public EntityNitro(Level worldIn, LivingEntity throwerIn){
		super(type, throwerIn, worldIn);
	}

	@Override
	public void setSecondsOnFire(int seconds){
		if(seconds > 0){
			onHit(new BlockHitResult(new Vec3(getX(), getY(), getZ()), Direction.UP, blockPosition(), true));
		}
	}

	@Override
	protected void onHit(HitResult result){
		if(!level.isClientSide){
			Vec3 vec = result.getLocation();
			level.explode(null, vec.x, vec.y, vec.z, 5F, Explosion.BlockInteraction.BREAK);
			level.playSound(null, getX(), getY(), getZ(), SoundEvents.GLASS_BREAK, SoundSource.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
			level.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 1F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
			level.broadcastEntityEvent(this, (byte) 3);
			remove(RemovalReason.KILLED);
		}
	}

	@Override
	protected void defineSynchedData(){

	}

	@Override
	public Packet<?> getAddEntityPacket(){
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public ItemStack getItem(){
		return RENDER_STACK;
	}
}
