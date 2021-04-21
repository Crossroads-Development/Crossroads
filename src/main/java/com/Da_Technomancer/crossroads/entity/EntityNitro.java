package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ObjectHolder;

@OnlyIn(value = Dist.CLIENT, _interface = IRendersAsItem.class)
@ObjectHolder(Crossroads.MODID)
public class EntityNitro extends ThrowableEntity implements IRendersAsItem{

	@ObjectHolder("nitro")
	public static EntityType<EntityNitro> type = null;
	private static final ItemStack RENDER_STACK = new ItemStack(CRItems.nitroglycerin);

	public EntityNitro(EntityType<EntityNitro> type, World worldIn){
		super(type, worldIn);
	}

	public EntityNitro(World worldIn, LivingEntity throwerIn){
		super(type, throwerIn, worldIn);
	}

	@Override
	public void setSecondsOnFire(int seconds){
		if(seconds > 0){
			onHit(new BlockRayTraceResult(new Vector3d(getX(), getY(), getZ()), Direction.UP, blockPosition(), true));
		}
	}

	@Override
	protected void onHit(RayTraceResult result){
		if(!level.isClientSide){
			Vector3d vec = result.getLocation();
			level.explode(null, vec.x, vec.y, vec.z, 5F, Explosion.Mode.BREAK);
			level.playSound(null, getX(), getY(), getZ(), SoundEvents.GLASS_BREAK, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
			level.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE, SoundCategory.NEUTRAL, 1F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
			level.broadcastEntityEvent(this, (byte) 3);
			remove();
		}
	}

	@Override
	protected void defineSynchedData(){

	}

	@Override
	public IPacket<?> getAddEntityPacket(){
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemStack getItem(){
		return RENDER_STACK;
	}
}
