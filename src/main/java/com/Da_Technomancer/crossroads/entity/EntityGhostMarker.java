package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.API.beams.BeamUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

@ObjectHolder(Crossroads.MODID)
public class EntityGhostMarker extends Entity{

	@ObjectHolder("ghost_marker")
	public static EntityType<EntityGhostMarker> type = null;

	private long time;
	private int lifespan;
	private EnumMarkerType markType;
	public CompoundNBT data;

	public EntityGhostMarker(EntityType<EntityGhostMarker> type, World worldIn){
		super(type, worldIn);
		setNoGravity(true);
		noClip = true;
	}

	public EntityGhostMarker(World worldIn, @Nonnull EnumMarkerType markerType){
		this(worldIn, markerType, markerType.defaultLifespan);
	}

	public EntityGhostMarker(World worldIn, @Nonnull EnumMarkerType markerType, int lifespan){
		super(type, worldIn);
		this.markType = markerType;
		this.lifespan = lifespan;
		time = worldIn.getGameTime();
		setNoGravity(true);
		noClip = true;
	}

	@Override
	protected void readAdditional(CompoundNBT nbt){
		nbt.putString("type", markType.name());
		nbt.putInt("life", lifespan);
		nbt.putLong("time", time);
		if(data != null){
			nbt.put("data", data);
		}
	}

	@Nullable
	public EnumMarkerType getMarkerType(){
		return markType;
	}

	@Override
	protected void writeAdditional(CompoundNBT nbt){
		try{
			markType = EnumMarkerType.valueOf(nbt.getString("type"));
		}catch(IllegalArgumentException | NullPointerException e){
			Crossroads.logger.error("Failed to load EntityGhostMarker at " + getPosition().toString() + "; dim: " + world.getDimension().getType().getId() + "; with type: " + nbt.getString("type") + ". Removing.");
			remove();
		}
		lifespan = nbt.getInt("life");
		time = nbt.getLong("time");
		if(nbt.contains("data")){
			data = nbt.getCompound("data");
		}
	}

	@Override
	public IPacket<?> createSpawnPacket(){
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected void registerData(){
		//
	}

	@Override
	public void tick(){
		super.tick();
		if(!world.isRemote && lifespan >= 0 && time != world.getGameTime()){
			time = world.getGameTime();//World time check to avoid tick-acceleration
			if(--lifespan == 0){
				if(markType != null && markType.expireEffect != null){
					markType.expireEffect.accept(this);
				}
				remove();
			}
		}
	}

	public enum EnumMarkerType{

		EQUILIBRIUM(BeamUtil.BEAM_TIME + 1, null),
		VOID_EQUILIBRIUM(BeamUtil.BEAM_TIME + 1, null),
		DELAYED_EXPLOSION(BeamUtil.BEAM_TIME, (EntityGhostMarker marker) -> {if(marker.data != null && marker.data.contains("power")) marker.world.createExplosion(marker, marker.getPosX(), marker.getPosY(), marker.getPosZ(), marker.data.getFloat("power"), marker.data.getBoolean("flaming"), Explosion.Mode.valueOf(marker.data.getString("blast_type")));}),
		BLOCK_SPAWNING(BeamUtil.BEAM_TIME + 1, null);

		private final int defaultLifespan;
		private final Consumer<EntityGhostMarker> expireEffect;

		EnumMarkerType(int defaultLifespan, @Nullable Consumer<EntityGhostMarker> expireEffect){
			this.defaultLifespan = defaultLifespan;
			this.expireEffect = expireEffect;
		}
	}
}
