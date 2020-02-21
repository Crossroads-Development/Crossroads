package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ObjectHolder;

@OnlyIn(value = Dist.CLIENT, _interface = IRendersAsItem.class)
@ObjectHolder(Crossroads.MODID)
public class EntityBullet extends ThrowableEntity implements IRendersAsItem{

	private static final ItemStack RENDER_STACK = new ItemStack(Items.IRON_NUGGET);

	@ObjectHolder("bullet")
	private static EntityType<EntityBullet> type = null;

	private int damage;

	public EntityBullet(EntityType<EntityBullet> type, World worldIn){
		super(type, worldIn);
	}

	public EntityBullet(World worldIn, LivingEntity throwerIn, int damage){
		super(type, throwerIn, worldIn);
		this.damage = damage;
	}

	@Override
	protected void onImpact(RayTraceResult result){
		if(!world.isRemote){
			if(result.getType() == RayTraceResult.Type.ENTITY){
				((EntityRayTraceResult) result).getEntity().attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), (float) damage);
			}
			world.setEntityState(this, (byte) 3);
			remove();
		}
	}

	@Override
	protected void registerData(){

	}

	@Override
	public void readAdditional(CompoundNBT nbt){
		super.readAdditional(nbt);
		damage = nbt.getInt("damage");
	}

	@Override
	public void writeAdditional(CompoundNBT nbt){
		super.writeAdditional(nbt);
		nbt.putInt("damage", damage);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemStack getItem(){
		return RENDER_STACK;
	}
}
