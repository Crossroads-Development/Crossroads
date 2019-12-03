package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

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
	public void setFire(int seconds){
		if(seconds > 0){
			onImpact(new BlockRayTraceResult(new Vec3d(posX, posY, posZ), Direction.UP, getPosition(), true));
		}
	}

	@Override
	protected void onImpact(RayTraceResult result){
		if(!world.isRemote){
			Vec3d vec = result.getHitVec();
			world.createExplosion(null, vec.x, vec.y, vec.z, 5F, Explosion.Mode.BREAK);
			world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.NEUTRAL, 0.5F, 0.4F / (rand.nextFloat() * 0.4F + 0.8F));
			world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.NEUTRAL, 1F, 0.4F / (rand.nextFloat() * 0.4F + 0.8F));
			world.setEntityState(this, (byte) 3);
			remove();
		}
	}

	@Override
	protected void registerData(){

	}

	@Override
	public ItemStack getItem(){
		return RENDER_STACK;
	}
}
