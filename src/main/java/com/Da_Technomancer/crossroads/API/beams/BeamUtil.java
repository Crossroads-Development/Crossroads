package com.Da_Technomancer.crossroads.API.beams;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BeamUtil{

	public static final int MAX_DISTANCE = 16;
	public static final int BEAM_TIME = 4;
	public static final int POWER_LIMIT = 64_000;

	private static final Tag<Block> PASSABLE = BlockTags.bind(Crossroads.MODID + ":beam_passable");
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
		if(state.isAir() || PASSABLE.contains(state.getBlock())){
			return false;
		}

		//Return false if any portion of the shape intersects with the mask for that direction
		VoxelShape shape = state.getBlockSupportShape(world, pos);
		VoxelShape mask;
		if(CRConfig.beamPowerCollision.get()){
			int radius = getBeamRadius(power);
			switch(toDir.getAxis()){
				case X:
					mask = Block.box(0, 8 - radius, 8 - radius, 16, 8 + radius, 8 + radius);
					break;
				case Y:
					mask = Block.box(8 - radius, 0, 8 - radius, 8 + radius, 16, 8 + radius);
					break;
				case Z:
				default:
					mask = Block.box(8 - radius, 8 - radius, 0, 8 + radius, 8 + radius, 16);
					break;
			}
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
}
