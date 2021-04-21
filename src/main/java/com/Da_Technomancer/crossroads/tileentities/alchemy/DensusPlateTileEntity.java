package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.registries.ObjectHolder;

import java.util.List;

@ObjectHolder(Crossroads.MODID)
public class DensusPlateTileEntity extends TileEntity implements ITickableTileEntity{

	@ObjectHolder("densus_plate")
	private static TileEntityType<DensusPlateTileEntity> type = null;

	private static final ITag<Block> gravityBlocking = BlockTags.bind(Crossroads.MODID + ":gravity_blocking");

	private static final int RANGE = CRConfig.gravRange.get();

	public DensusPlateTileEntity(){
		super(type);
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
	public void tick(){
		if(level.isClientSide){
			return;
		}
		
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

		List<Entity> ents = level.getEntitiesOfClass(Entity.class, new AxisAlignedBB(worldPosition.getX() + (dir.getStepX() == 1 ? 1 : 0), worldPosition.getY() + (dir.getStepY() == 1 ? 1 : 0), worldPosition.getZ() + (dir.getStepZ() == 1 ? 1 : 0), worldPosition.getX() + (dir.getStepX() == -1 ? 0 : 1) + effectiveRange * dir.getStepX(), worldPosition.getY() + (dir.getStepY() == -1 ? 0 : 1) + effectiveRange * dir.getStepY(), worldPosition.getZ() + (dir.getStepZ() == -1 ? 0 : 1) + effectiveRange * dir.getStepZ()), EntityPredicates.ENTITY_NOT_BEING_RIDDEN);
		for(Entity ent : ents){
			if(ent.isShiftKeyDown() || ent.isSpectator()){
				continue;
			}
			switch(dir.getAxis()){
				case X:
					ent.push(0.6D * (ent.getX() < worldPosition.getX() ^ inverse ? 1D : -1D), 0, 0);
					ent.hurtMarked = true;
					break;
				case Y:
					ent.push(0, 0.6D * (ent.getY() < worldPosition.getY() ^ inverse ? 1D : -1D), 0);
					ent.hurtMarked = true;
					if(inverse && dir == Direction.UP || !inverse && dir == Direction.DOWN){
						ent.fallDistance = 0;
					}
					break;
				case Z:
					ent.push(0, 0, 0.6D * (ent.getZ() < worldPosition.getZ() ^ inverse ? 1D : -1D));
					ent.hurtMarked = true;
					break;
				default:
					break;
			}
		}
	}
}
