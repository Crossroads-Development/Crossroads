package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.Da_Technomancer.essentials.tileentities.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ObjectHolder;

import java.util.List;

@ObjectHolder(Crossroads.MODID)
public class DensusPlateTileEntity extends BlockEntity implements ITickableTileEntity{

	@ObjectHolder("densus_plate")
	public static BlockEntityType<DensusPlateTileEntity> TYPE = null;

	private static final Tag<Block> gravityBlocking = BlockTags.bind(Crossroads.MODID + ":gravity_blocking");

	private final int RANGE = CRConfig.gravRange.get();
	private final double ACCEL = CRConfig.gravAccel.get();

	public DensusPlateTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	private Direction getFacing(){
		BlockState state = getBlockState();
		if(state.hasProperty(ESProperties.FACING)){
			return state.getValue(ESProperties.FACING);
		}
		return Direction.DOWN;
	}
	
	private boolean isAnti(){
		BlockState state = getBlockState();
		return state.getBlock() == CRBlocks.antiDensusPlate;
	}
	
	@Override
	public void serverTick(){
		Direction dir = getFacing().getOpposite();
		boolean inverse = isAnti();
		int effectiveRange = RANGE;

		//Check for cavorite shortening the range
		for(int i = 1; i <= RANGE; i++){
			BlockState state = level.getBlockState(worldPosition.relative(dir, i));
			if(gravityBlocking.contains(state.getBlock())){
				effectiveRange = i;
				break;
			}
		}

		double acceleration = getBlockState().getValue(CRProperties.LAYERS) * ACCEL;

		List<Entity> ents = level.getEntitiesOfClass(Entity.class, new AABB(worldPosition.getX() + (dir.getStepX() == 1 ? 1 : 0), worldPosition.getY() + (dir.getStepY() == 1 ? 1 : 0), worldPosition.getZ() + (dir.getStepZ() == 1 ? 1 : 0), worldPosition.getX() + (dir.getStepX() == -1 ? 0 : 1) + effectiveRange * dir.getStepX(), worldPosition.getY() + (dir.getStepY() == -1 ? 0 : 1) + effectiveRange * dir.getStepY(), worldPosition.getZ() + (dir.getStepZ() == -1 ? 0 : 1) + effectiveRange * dir.getStepZ()), EntitySelector.ENTITY_NOT_BEING_RIDDEN);
		for(Entity ent : ents){
			if(ent.isShiftKeyDown() || ent.isSpectator()){
				continue;
			}

			switch(dir.getAxis()){
				case X:
					ent.push(acceleration * (ent.getX() < worldPosition.getX() ^ inverse ? 1D : -1D), 0, 0);
					ent.hurtMarked = true;
					break;
				case Y:
					ent.push(0, acceleration * (ent.getY() < worldPosition.getY() ^ inverse ? 1D : -1D), 0);
					ent.hurtMarked = true;
					if(inverse && dir == Direction.UP || !inverse && dir == Direction.DOWN){
						ent.fallDistance = 0;
					}
					break;
				case Z:
					ent.push(0, 0, acceleration * (ent.getZ() < worldPosition.getZ() ^ inverse ? 1D : -1D));
					ent.hurtMarked = true;
					break;
				default:
					break;
			}
		}
	}
}
