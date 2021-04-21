package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.API.alchemy.AlchemyUtil;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ObjectHolder;

@OnlyIn(value = Dist.CLIENT, _interface = IRendersAsItem.class)
@ObjectHolder(Crossroads.MODID)
public class EntityShell extends ThrowableEntity implements IRendersAsItem{

	@ObjectHolder("shell")
	public static EntityType<EntityShell> type = null;
	private static final DataParameter<ItemStack> item = EntityDataManager.defineId(EntityShell.class, DataSerializers.ITEM_STACK);

	private ReagentMap contents;//Technically redundant with the itemstack in data manager, but meh

	public EntityShell(EntityType<EntityShell> type, World worldIn){
		super(type, worldIn);
	}

	public EntityShell(World worldIn, ReagentMap contents, ItemStack stack){
		this(type, worldIn);
		this.contents = contents;
		entityData.set(item, stack);
	}

	public EntityShell(World worldIn, LivingEntity throwerIn, ReagentMap contents, ItemStack stack){
		super(type, throwerIn, worldIn);
		this.contents = contents;
		entityData.set(item, stack);
	}

	@Override
	protected void onHit(RayTraceResult result){
		if(!level.isClientSide){
			if(contents != null){
				Vector3d hit = result.getLocation();
				AlchemyUtil.releaseChemical(level, new BlockPos(hit.x, hit.y, hit.z), contents);
			}
			level.playSound(null, getX(), getY(), getZ(), SoundEvents.GLASS_BREAK, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
			level.broadcastEntityEvent(this, (byte) 3);
			remove();
		}
	}

	@Override
	protected void defineSynchedData(){
		entityData.define(item, new ItemStack(CRItems.shellGlass));
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT nbt){
		super.readAdditionalSaveData(nbt);
		contents = ReagentMap.readFromNBT(nbt);
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT nbt){
		super.addAdditionalSaveData(nbt);
		if(contents != null){
			contents.write(nbt);
		}
	}

	@Override
	public IPacket<?> getAddEntityPacket(){
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemStack getItem(){
		return entityData.get(item);
	}
}
