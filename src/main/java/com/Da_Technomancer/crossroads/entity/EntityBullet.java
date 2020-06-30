package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ObjectHolder;

@OnlyIn(value = Dist.CLIENT, _interface = IRendersAsItem.class)
@ObjectHolder(Crossroads.MODID)
public class EntityBullet extends ThrowableEntity implements IRendersAsItem{

	@ObjectHolder("cr_bullet")
	public static EntityType<EntityBullet> type = null;

	private static final DataParameter<Float> damagePar = EntityDataManager.createKey(EntityBullet.class, DataSerializers.FLOAT);
	private static final DataParameter<ItemStack> itemPar = EntityDataManager.createKey(EntityBullet.class, DataSerializers.ITEMSTACK);

	public EntityBullet(EntityType<EntityBullet> type, World worldIn){
		super(type, worldIn);
	}

	public EntityBullet(World worldIn, float damage, ItemStack item){
		this(type, worldIn);
		dataManager.set(damagePar, damage);
		dataManager.set(itemPar, item);
	}

	public EntityBullet(World worldIn, LivingEntity throwerIn, float damage, ItemStack item){
		super(type, throwerIn, worldIn);
		dataManager.set(damagePar, damage);
		dataManager.set(itemPar, item);
	}

	@Override
	protected void onImpact(RayTraceResult result){
		if(!world.isRemote && result.getType() == RayTraceResult.Type.ENTITY){
			EntityRayTraceResult res = (EntityRayTraceResult) result;
			LivingEntity attacker = getThrower();
			DamageSource source = new EntityDamageSource("cr_bullet", attacker).setProjectile();
			res.getEntity().attackEntityFrom(source, dataManager.get(damagePar));
		}
	}

	@Override
	protected void registerData(){
		dataManager.register(damagePar, 0F);
		dataManager.register(itemPar, new ItemStack(Items.IRON_NUGGET, 1));
	}

	@Override
	public void readAdditional(CompoundNBT nbt){
		super.readAdditional(nbt);
	}

	@Override
	public void writeAdditional(CompoundNBT nbt){
		super.writeAdditional(nbt);
	}

	@Override
	public IPacket<?> createSpawnPacket(){
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemStack getItem(){
		return dataManager.get(itemPar);
	}
}
