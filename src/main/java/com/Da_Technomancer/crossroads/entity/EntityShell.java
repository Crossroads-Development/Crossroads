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
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundEvents;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class EntityShell extends ThrowableEntity implements IRendersAsItem{

	@ObjectHolder("shell")
	private static EntityType<EntityShell> type = null;
	private static final DataParameter<Boolean> crystal = EntityDataManager.createKey(EntityShell.class, DataSerializers.BOOLEAN);
	private static final ItemStack[] RENDER_STACK = new ItemStack[] {new ItemStack(CRItems.shellGlass), new ItemStack(CRItems.shellCrystal)};

	private ReagentMap contents;

	public EntityShell(EntityType<EntityShell> type, World worldIn){
		super(type, worldIn);
	}

	public EntityShell(World worldIn, ReagentMap contents, boolean isCrystal){
		this(type, worldIn);
		this.contents = contents;
		dataManager.set(crystal, isCrystal);
	}

	public EntityShell(World worldIn, LivingEntity throwerIn, ReagentMap contents, boolean isCrystal){
		super(type, throwerIn, worldIn);
		this.contents = contents;
		dataManager.set(crystal, isCrystal);
	}

	@Override
	protected void onImpact(RayTraceResult result){
		if(!world.isRemote){
			if(contents != null){
				Vec3d hit = result.getHitVec();
				AlchemyUtil.releaseChemical(world, new BlockPos(hit.x, hit.y, hit.z), contents);
			}
			world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.NEUTRAL, 0.5F, 0.4F / (rand.nextFloat() * 0.4F + 0.8F));
			world.setEntityState(this, (byte) 3);
			remove();
		}
	}

	@Override
	protected void registerData(){
		dataManager.register(crystal, false);
	}

	@Override
	public void readAdditional(CompoundNBT nbt){
		super.readAdditional(nbt);
		contents = ReagentMap.readFromNBT(nbt);
	}

	@Override
	public void writeAdditional(CompoundNBT nbt){
		super.writeAdditional(nbt);
		if(contents != null){
			contents.write(nbt);
		}
	}

	@Override
	public ItemStack getItem(){
		return dataManager.get(crystal) ? RENDER_STACK[1] : RENDER_STACK[0];
	}
}
