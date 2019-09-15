package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class EntityGhostMarker extends Entity{

	private long time;
	private int lifespan;
	private EnumMarkerType type;
	public CompoundNBT data;

	public EntityGhostMarker(World worldIn){
		super(worldIn);
		setSize(1F, 1F);
		setNoGravity(true);
		noClip = true;
	}

	public EntityGhostMarker(World worldIn, @Nonnull EnumMarkerType type){
		this(worldIn, type, type.defaultLifespan);
	}

	public EntityGhostMarker(World worldIn, @Nonnull EnumMarkerType type, int lifespan){
		super(worldIn);
		this.type = type;
		this.lifespan = lifespan;
		time = worldIn.getTotalWorldTime();

		setSize(1F, 1F);
		setNoGravity(true);
		noClip = true;
	}

	@Override
	protected void entityInit(){

	}

	@Override
	protected void readEntityFromNBT(CompoundNBT nbt){
		nbt.setString("type", type.name());
		nbt.setInteger("life", lifespan);
		nbt.setLong("time", time);
		if(data != null){
			nbt.setTag("data", data);
		}
	}

	@Nullable
	public EnumMarkerType getType(){
		return type;
	}

	@Override
	protected void writeEntityToNBT(CompoundNBT nbt){
		try{
			type = EnumMarkerType.valueOf(nbt.getString("type"));
		}catch(IllegalArgumentException | NullPointerException e){
			Crossroads.logger.error("Failed to load EntityGhostMarker at " + getPosition().toString() + "; dim: " + world.provider.getDimension() + "; with type: " + nbt.getString("type") + ". Removing.");
			setDead();
		}
		lifespan = nbt.getInteger("life");
		time = nbt.getLong("time");
		if(nbt.hasKey("data")){
			data = nbt.getCompoundTag("data");
		}
	}

	@Override
	public void onUpdate(){
		super.onUpdate();
		if(!world.isRemote && lifespan >= 0 && time != world.getTotalWorldTime()){
			time = world.getTotalWorldTime();
			if(--lifespan == 0){
				if(type != null && type.expireEffect != null){
					type.expireEffect.accept(this);
				}
				setDead();
			}
		}
	}

	public enum EnumMarkerType{

		EQUALIBRIUM(5, null),
		VOID_EQUALIBRIUM(5, null),
		DELAYED_EXPLOSION(5, (EntityGhostMarker marker) -> {if(marker.data != null && marker.data.hasKey("power")) marker.world.createExplosion(marker, marker.posX, marker.posY, marker.posZ, marker.data.getFloat("power"), marker.data.getBoolean("smoking"));}),
		BLOCK_SPAWNING(5, null);

		private final int defaultLifespan;
		private final Consumer<EntityGhostMarker> expireEffect;

		EnumMarkerType(int defaultLifespan, @Nullable Consumer<EntityGhostMarker> expireEffect){
			this.defaultLifespan = defaultLifespan;
			this.expireEffect = expireEffect;
		}
	}
}
