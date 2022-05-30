package com.Da_Technomancer.crossroads.API.beams;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class BeamHit{

	/**
	 * The world. Virtual server side only
	 */
	@Nonnull
	private final ServerLevel world;
	/**
	 * BlockPos which contains the collision/end point of the beam.
	 */
	@Nonnull
	private final BlockPos pos;
	/**
	 * The cardinal direction towards which the beam is travelling.
	 * Used as a fallback for ray if not specified.
	 */
	@Nonnull
	private final Direction direction;
	/**
	 * The blockstate at the end position of the beam.
	 */
	@Nonnull
	private BlockState endState;
	/**
	 * A normalized vector pointing in the direction of the travel path of the beam.
	 */
	@Nullable
	private Vec3 ray;
	/**
	 * The point at which the beam terminates. Should be within pos. Should be somewhere along the line of the beam; 'nearby' is not enough
	 */
	@Nullable
	private Vec3 hitPos;

	public BeamHit(@Nonnull ServerLevel world, @Nonnull BlockPos pos, @Nonnull Direction direction, @Nonnull BlockState endState){
		this.world = world;
		this.pos = pos;
		this.direction = direction;
		this.endState = endState;
	}

	public BeamHit(@Nonnull ServerLevel world, @Nonnull BlockPos pos, @Nonnull Direction direction, @Nonnull BlockState endState, @Nonnull Vec3 ray, @Nonnull Vec3 hitPos){
		this(world, pos, direction, endState);
		this.ray = ray;
		this.hitPos = hitPos;
	}

	@Nonnull
	public ServerLevel getWorld(){
		return world;
	}

	@Nonnull
	public BlockPos getPos(){
		return pos;
	}

	@Nonnull
	public Direction getDirection(){
		return direction;
	}

	@Nonnull
	public BlockState getEndState(){
		if(endState == null){
			endState = world.getBlockState(pos);
		}
		return endState;
	}

	@Nonnull
	public Vec3 getRay(){
		if(ray == null){
			Vec3i normalVec = getDirection().getOpposite().getNormal();
			ray = new Vec3(normalVec.getX(), normalVec.getY(), normalVec.getZ());
		}
		return ray;
	}

	@Nonnull
	public Vec3 getHitPos(){
		if(hitPos == null){
			hitPos = Vec3.atCenterOf(pos);
		}
		return hitPos;
	}

	/**
	 * Gets entities near this point. Result is not cached.
	 * @param entityClass Only finds entities which extend this class
	 * @param range Radius from this point to search
	 * @param filter An additional filter to apply. A basic filter for alive, etc is always applied regardless of this value.
	 * @param <T> Entities of this class and subclasses will be found
	 * @return List of all valid matches in range. May be empty.
	 */
	@Nonnull
	public <T extends Entity> List<T> getNearbyEntities(Class<T> entityClass, double range, @Nullable Predicate<? super Entity> filter){
		if(filter == null){
			filter = BeamUtil.BEAM_COLLIDE_ENTITY;
		}else{
			filter = BeamUtil.BEAM_COLLIDE_ENTITY.and(filter);
		}
		Vec3 core = getHitPos();
		AABB aabb = AABB.ofSize(core, range * 2D, range * 2D, range * 2D);
		return world.getEntitiesOfClass(entityClass, aabb, filter);
	}

	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		BeamHit beamHit = (BeamHit) o;
		return world.equals(beamHit.world) && pos.equals(beamHit.pos) && direction == beamHit.direction && getEndState().equals(beamHit.getEndState()) && getRay().equals(beamHit.getRay()) && getHitPos().equals(beamHit.getHitPos());
	}

	@Override
	public int hashCode(){
		return Objects.hash(world, pos, direction, getEndState(), getRay(), getHitPos());
	}

	@Override
	public String toString(){
		return "BeamHit{" +
				"world=" + world +
				", pos=" + pos +
				", direction=" + direction +
				", endState=" + getEndState() +
				", ray=" + getRay() +
				", hitPos=" + getHitPos() +
				'}';
	}
}
