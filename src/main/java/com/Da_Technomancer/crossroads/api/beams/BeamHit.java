package com.Da_Technomancer.crossroads.api.beams;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

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
	 * The cardinal direction side of the block this beam hit, or opposite the cardinal direction the beam is travelling if N/A
	 * Used as a fallback for ray if not specified.
	 */
	@Nonnull
	private final Direction direction;
	/**
	 * The blockstate at the end position of the beam.
	 */
	@Nullable
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

	/**
	 * The beam unit being applied
	 * This should NOT be used to determine the beam effect (should be based on alignment/power/void only); it should only be used when the beam is being re-absorbed by a machine/item/etc
	 */
	private final BeamUnit beamUnit;

	public BeamHit(@Nonnull ServerLevel world, @Nonnull BlockPos pos, @Nonnull Direction direction, @Nullable BlockState endState, @Nonnull BeamUnit beamUnit){
		this.world = world;
		this.pos = pos;
		this.direction = direction;
		this.endState = endState;
		this.beamUnit = beamUnit;
	}

	public BeamHit(@Nonnull ServerLevel world, @Nonnull BlockPos pos, @Nonnull Direction direction, @Nullable BlockState endState, @Nonnull BeamUnit beamUnit, @Nonnull Vec3 ray, @Nonnull Vec3 hitPos){
		this(world, pos, direction, endState, beamUnit);
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
	 * This should NOT be used to determine the beam effect (should be based on alignment/power/void only); it should only be used when the beam is being re-absorbed by a machine/item/etc
	 * @return The beam unit being applied
	 */
	@Nonnull
	public BeamUnit getBeamUnit(){
		return beamUnit;
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
		return world.equals(beamHit.world) && pos.equals(beamHit.pos) && direction == beamHit.direction && getEndState().equals(beamHit.getEndState()) && beamUnit.equals(beamHit.beamUnit) && getRay().equals(beamHit.getRay()) && getHitPos().equals(beamHit.getHitPos());
	}

	@Override
	public int hashCode(){
		return Objects.hash(world, pos, direction, getEndState(), beamUnit, getRay(), getHitPos());
	}

	@Override
	public String toString(){
		return "BeamHit{" +
				"world=" + world +
				", pos=" + pos +
				", direction=" + direction +
				", endState=" + getEndState() +
				", beamUnit=" + beamUnit +
				", ray=" + getRay() +
				", hitPos=" + getHitPos() +
				'}';
	}

	/**
	 * The range to pass to getNearbyEntities for targeting entities 'hit directly' by the beam
	 */
	public static final double WITHIN_BLOCK_RANGE = 0.48D;

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

	@Nullable
	public BlockEntity getEndBlockEntity(){
		return getWorld().getBlockEntity(getPos());
	}

	@Nullable
	public <T> T getEndCapability(Capability<T> capability){
		return getEndCapability(capability, true);
	}

	@Nullable
	public <T> T getEndCapability(Capability<T> capability, boolean allowNull){
		BlockEntity te = getEndBlockEntity();
		if(te == null){
			return null;
		}
		//Try hit face first
		LazyOptional<T> opt = te.getCapability(capability, getDirection());
		if(opt.isPresent()){
			return opt.orElseThrow(NullPointerException::new);
		}else if(allowNull){
			//Try the null side as a fallback
			opt = te.getCapability(capability, null);
			if(opt.isPresent()){
				return opt.orElseThrow(NullPointerException::new);
			}
		}
		return null;
	}
}
