package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.CrossroadsConfig;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.blocks.alchemy.DensusPlate;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.ITickableTileEntity;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class DensusPlateTileEntity extends TileEntity implements ITickableTileEntity{

	private Direction facing = null;
	private Boolean anti = null;
	private static final int RANGE = CrossroadsConfig.gravRange.get();

	private Direction getFacing(){
		if(facing == null){
			BlockState state = world.getBlockState(pos);
			if(!(state.getBlock() instanceof DensusPlate)){
				return Direction.DOWN;
			}
			facing = state.get(EssentialsProperties.FACING);
			anti = state.getBlock() == CrossroadsBlocks.antiDensusPlate;
		}
		return facing;
	}
	
	private boolean isAnti(){
		if(anti == null){
			BlockState state = world.getBlockState(pos);
			if(!(state.getBlock() instanceof DensusPlate)){
				return false;
			}
			facing = state.get(EssentialsProperties.FACING);
			anti = state.getBlock() == CrossroadsBlocks.antiDensusPlate;
		}
		return anti;
	}
	
	@Override
	public void update(){
		if(world.isRemote){
			return;
		}
		
		Direction dir = getFacing().getOpposite();
		boolean inverse = isAnti();
		int effectiveRange = RANGE;

		for(int i = 1; i <= RANGE; i++){
			BlockState state = world.getBlockState(pos.offset(dir, i));
			if(state.getBlock() == CrossroadsBlocks.cavorite){
				effectiveRange = i;
				break;
			}
		}

		List<Entity> ents = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.getX() + (dir.getXOffset() == 1 ? 1 : 0), pos.getY() + (dir.getYOffset() == 1 ? 1 : 0), pos.getZ() + (dir.getZOffset() == 1 ? 1 : 0), pos.getX() + (dir.getXOffset() == -1 ? 0 : 1) + effectiveRange * dir.getXOffset(), pos.getY() + (dir.getYOffset() == -1 ? 0 : 1) + effectiveRange * dir.getYOffset(), pos.getZ() + (dir.getZOffset() == -1 ? 0 : 1) + effectiveRange * dir.getZOffset()), EntityPredicates.IS_ALIVE);
		for(Entity ent : ents){
			if(ent.isSneaking() || (ent instanceof PlayerEntity && ((PlayerEntity) ent).isSpectator())){
				continue;
			}
			switch(dir.getAxis()){
				case X:
					ent.addVelocity(0.6D * (ent.posX < pos.getX() ^ inverse ? 1D : -1D), 0, 0);
					ent.velocityChanged = true;
					break;
				case Y:
					ent.addVelocity(0, 0.6D * (ent.posY < pos.getY() ^ inverse ? 1D : -1D), 0);
					ent.velocityChanged = true;
					if(inverse && dir == Direction.UP || !inverse && dir == Direction.DOWN){
						ent.fallDistance = 0;
					}
					break;
				case Z:
					ent.addVelocity(0, 0, 0.6D * (ent.posZ < pos.getZ() ^ inverse ? 1D : -1D));
					ent.velocityChanged = true;
					break;
				default:
					break;
			}
		}
	}
}
