package com.Da_Technomancer.crossroads.api.beams;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.entity.EntityGhostMarker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class BeamUtil{

	public static final int MAX_DISTANCE = 16;
	public static final int BEAM_TIME = 4;
	public static final int POWER_LIMIT = 64_000;
	public static final int MAX_EFFECT_POWER = 64;

	public static final Predicate<Entity> BEAM_COLLIDE_ENTITY = EntitySelector.ENTITY_STILL_ALIVE.and(EntitySelector.NO_SPECTATORS).and(ent -> !(ent instanceof EntityGhostMarker));

	private static final TagKey<Block> PASSABLE = CraftingUtil.getTagKey(ForgeRegistries.Keys.BLOCKS, new ResourceLocation(Crossroads.MODID, "beam_passable"));
	private static final VoxelShape[] COLLISION_MASK = new VoxelShape[3];

	static{
		COLLISION_MASK[0] = Block.box(0, 5, 5, 16, 11, 11);
		COLLISION_MASK[1] = Block.box(5, 0, 5, 11, 16, 11);
		COLLISION_MASK[2] = Block.box(5, 5, 0, 11, 11, 16);
	}

	/**
	 * Determines whether beams should collide with a given block
	 * @param state The state to check
	 * @param world The world this state exists in (Use any non-null world if this is unavailable)
	 * @param pos The position of the passed state in the world (use the origin if this is unavailable)
	 * @param toDir The direction the beam is travelling towards
	 * @param power The beam power
	 * @return Whether beams should collide with this block
	 */
	public static boolean solidToBeams(BlockState state, Level world, BlockPos pos, Direction toDir, int power){
		return solidToBeams(state, world, pos, toDir, power, false);
	}

	/**
	 * Determines whether beams should collide with a given block
	 * @param state The state to check
	 * @param world The world this state exists in (Use any non-null world if this is unavailable)
	 * @param pos The position of the passed state in the world (use the origin if this is unavailable)
	 * @param toDir The direction the beam is travelling towards
	 * @param power The beam power
	 * @param sensitive If this beam should use sensitive collision which collides more easily
	 * @return Whether beams should collide with this block
	 */
	public static boolean solidToBeams(BlockState state, Level world, BlockPos pos, Direction toDir, int power, boolean sensitive){
		if(state.isAir() || CraftingUtil.tagContains(PASSABLE, state.getBlock())){
			return false;
		}

		if(sensitive){
			return true;//Collide with anything that isn't air or explicitly marked as passable by beams
		}

		//Return false if any portion of the shape intersects with the mask for that direction
		VoxelShape shape = state.getBlockSupportShape(world, pos);
		VoxelShape mask;
		if(CRConfig.beamPowerCollision.get()){
			int radius = getBeamRadius(power);
			mask = switch(toDir.getAxis()){
				case X -> Block.box(0, 8 - radius, 8 - radius, 16, 8 + radius, 8 + radius);
				case Y -> Block.box(8 - radius, 0, 8 - radius, 8 + radius, 16, 8 + radius);
				case Z -> Block.box(8 - radius, 8 - radius, 0, 8 + radius, 8 + radius, 16);
			};
		}else{
			mask = COLLISION_MASK[toDir.getAxis().ordinal()];
		}
		return Shapes.joinIsNotEmpty(shape, mask, BooleanOp.AND);
	}

	/**
	 * Calculates the radius of beam sides (for rendering) based on the beam power
	 * The radius is half the diagonal length
	 * @param power The beam power
	 * @return Half the rendering radius, in pixels
	 */
	public static int getBeamRadius(int power){
		if(power <= 0){
			return 0;
		}
		return Math.min(8, (int) Math.round(Math.sqrt(power)));
	}

	/**
	 * Finds the location a beam would hit along a direction. Does not collide with entities.
	 * Does not perform any rendering or beam effects
	 * @param beam The beam unit being fired
	 * @param world The world
	 * @param startPos The starting point of the beam
	 * @param dir Direction of the beam path of travel
	 * @param maxRange The maximum range of the beam
	 * @param sensitive Whether to use sensitive block collision
	 * @return BeamHit describing where the beam would hit
	 */
	@Nonnull
	public static BeamHit rayTraceBeamSimple(@Nonnull BeamUnit beam, Level world, BlockPos startPos, Direction dir, int maxRange, boolean sensitive){
		BlockPos checkPos = startPos;
		BlockState checkState = Blocks.AIR.defaultBlockState();
		for(int i = 1; i <= maxRange; i++){
			checkPos = startPos.relative(dir, i);
			checkState = world.getBlockState(checkPos);

//			//Check for machine receiving beams
//			BlockEntity checkTE = world.getBlockEntity(checkPos);
//			if(checkTE != null && checkTE.getCapability(Capabilities.BEAM_CAPABILITY, dir.getOpposite()).isPresent()){
//				return new BeamHit((ServerLevel) world, checkPos, dir.getOpposite(), checkState, beam);
//			}

			//Check for collision or machine receiving beams
			BlockEntity checkTE;
			if(i == maxRange || solidToBeams(checkState, world, checkPos, dir, beam.getPower(), sensitive) || (checkTE = world.getBlockEntity(checkPos)) != null && checkTE.getCapability(Capabilities.BEAM_CAPABILITY, dir.getOpposite()).isPresent()){
				return new BeamHit((ServerLevel) world, checkPos, dir.getOpposite(), checkState, beam);
			}
		}
		return new BeamHit((ServerLevel) world, checkPos, dir.getOpposite(), checkState, beam);
	}

	/**
	 * Finds the location a beam would hit along an arbitrary ray, colliding with both blocks and entities
	 * Does not perform any rendering or beam effects
	 * @param beam The beam unit being fired
	 * @param world The world
	 * @param startPos The starting point of the beam
	 * @param endSourcePos The end point of the beam will be along the ray extending from endSourcePos. This value is often the same as startPos.
	 * @param ray A normalized ray vector pointing from endSourcePos; the end point of the beam will be along this line. For endSourcePos == startPos, this is the direction of beam travel
	 * @param excludedEntity An entity to ignore collisions with (null for none)
	 * @param ignorePos A block position to ignore block collisions with (null for none)
	 * @param maxRange The maximum range of the beam
	 * @param sensitive Whether to use sensitive block collision
	 * @return BeamHit describing where the beam would hit
	 */
	@Nonnull
	public static BeamHit rayTraceBeams(@Nonnull BeamUnit beam, Level world, Vec3 startPos, Vec3 endSourcePos, Vec3 ray, @Nullable Entity excludedEntity, @Nullable BlockPos ignorePos, double maxRange, boolean sensitive){
		final double stepSize = CRConfig.beamRaytraceStep.get();
		final double halfStep = stepSize / 2D;
		Direction collisionDir = Direction.getNearest(ray.x, ray.y, ray.z);//Used for beam collision detection
		Vec3 stepRay = ray.scale(stepSize);
		//effect direction is nonnull. Use the direction of the beam if no block collision occurred
		Direction effectDir = Direction.getNearest(-ray.x, -ray.y, -ray.z);
		double[] end = new double[] {endSourcePos.x, endSourcePos.y, endSourcePos.z};
		BlockPos.MutableBlockPos endPos = new BlockPos.MutableBlockPos(end[0], end[1], end[2]);
		BlockPos.MutableBlockPos prevEndPos = new BlockPos.MutableBlockPos(end[0], end[1], end[2]);
		BlockState state = world.getBlockState(endPos);

		//Raytrace manually along the look direction
		for(double d = 0; d < maxRange; d += stepSize){
			end[0] += stepRay.x;
			end[1] += stepRay.y;
			end[2] += stepRay.z;
			endPos = endPos.set(end[0], end[1], end[2]);
			boolean didPosChange = !endPos.equals(prevEndPos);
			if(didPosChange){
				prevEndPos = prevEndPos.set(endPos);
				state = world.getBlockState(endPos);
			}

			//Check for entity collisions
			List<Entity> ents = world.getEntities(excludedEntity, new AABB(end[0] - halfStep, end[1] - halfStep, end[2] - halfStep, end[0] + halfStep, end[1] + halfStep, end[2] + halfStep), BEAM_COLLIDE_ENTITY);
			if(!ents.isEmpty()){
				Vec3 entVec = ents.get(0).position();
				//Vector component of entity position (relative to beam source) onto beam ray direction, added back to beam source position
				//Gives the point on the beam-path line closest to the entity (the hitVec isn't necessarily on the actual line of the beam)
				Vec3 lineVec = startPos.add(ray.scale(entVec.subtract(startPos).dot(ray)));
				end[0] = lineVec.x;
				end[1] = lineVec.y;
				end[2] = lineVec.z;
				return new BeamHit((ServerLevel) world, endPos.immutable(), effectDir, state, beam, ray, new Vec3(end[0], end[1], end[2]));
			}

			//Check for block collisions
			//Speed things up a bit by not rechecking blocks
			if(didPosChange && !world.isOutsideBuildHeight(endPos) && !endPos.equals(ignorePos)){
				if(solidToBeams(state, world, endPos, collisionDir, beam.getPower(), sensitive)){
					//Note: this VoxelShape has no offset
					//Sensitive collision uses a full block shape to guarantee collision
					VoxelShape shape = sensitive ? Shapes.block() : state.getBlockSupportShape(world, endPos);
					BlockHitResult res = shape.clip(startPos, new Vec3(end[0] + ray.x, end[1] + ray.y, end[2] + ray.z), endPos);
					if(res != null){
						Vec3 hitVec = res.getLocation();
						end[0] = hitVec.x;
						end[1] = hitVec.y;
						end[2] = hitVec.z;
						effectDir = res.getDirection();
						return new BeamHit((ServerLevel) world, endPos.immutable(), effectDir, state, beam, ray, new Vec3(end[0], end[1], end[2]));
					}
				}
			}
		}

		return new BeamHit((ServerLevel) world, endPos.immutable(), effectDir, state, beam, ray, new Vec3(end[0], end[1], end[2]));
	}
}
