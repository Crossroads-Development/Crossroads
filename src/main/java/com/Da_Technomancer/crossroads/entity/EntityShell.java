package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.API.alchemy.AlchemyUtil;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.Crossroads;
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
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ObjectHolder;

@OnlyIn(value = Dist.CLIENT, _interface = ItemSupplier.class)
@ObjectHolder(Crossroads.MODID)
public class EntityShell extends ThrowableProjectile implements ItemSupplier{

	@ObjectHolder("shell")
	public static EntityType<EntityShell> type = null;
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
	@OnlyIn(Dist.CLIENT)
	public ItemStack getItem(){
		return entityData.get(item);
	}
}
