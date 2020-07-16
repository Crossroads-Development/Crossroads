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

	private static final ITag<Block> gravityBlocking = BlockTags.makeWrapperTag(Crossroads.MODID + ":gravity_blocking");

	private static final int RANGE = CRConfig.gravRange.get();

	public DensusPlateTileEntity(){
		super(type);
	}

	private Direction getFacing(){
		BlockState state = getBlockState();
		if(state.func_235901_b_(ESProperties.FACING)){
			return state.get(ESProperties.FACING);
		}
		return Direction.DOWN;
	}
	
	private boolean isAnti(){
		BlockState state = getBlockState();
		return state.getBlock() == CRBlocks.antiDensusPlate;
	}
	
	@Override
	public void tick(){
		if(world.isRemote){
			return;
		}
		
		Direction dir = getFacing().getOpposite();
		boolean inverse = isAnti();
		int effectiveRange = RANGE;

		//Check for cavorite shortening the range
		for(int i = 1; i <= RANGE; i++){
			BlockState state = world.getBlockState(pos.offset(dir, i));
			if(gravityBlocking.contains(state.getBlock())){
				effectiveRange = i;
				break;
			}
		}

		List<Entity> ents = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.getX() + (dir.getXOffset() == 1 ? 1 : 0), pos.getY() + (dir.getYOffset() == 1 ? 1 : 0), pos.getZ() + (dir.getZOffset() == 1 ? 1 : 0), pos.getX() + (dir.getXOffset() == -1 ? 0 : 1) + effectiveRange * dir.getXOffset(), pos.getY() + (dir.getYOffset() == -1 ? 0 : 1) + effectiveRange * dir.getYOffset(), pos.getZ() + (dir.getZOffset() == -1 ? 0 : 1) + effectiveRange * dir.getZOffset()), EntityPredicates.IS_STANDALONE);
		for(Entity ent : ents){
			if(ent.isSneaking() || ent.isSpectator()){
				continue;
			}
			switch(dir.getAxis()){
				case X:
					ent.addVelocity(0.6D * (ent.getPosX() < pos.getX() ^ inverse ? 1D : -1D), 0, 0);
					ent.velocityChanged = true;
					break;
				case Y:
					ent.addVelocity(0, 0.6D * (ent.getPosY() < pos.getY() ^ inverse ? 1D : -1D), 0);
					ent.velocityChanged = true;
					if(inverse && dir == Direction.UP || !inverse && dir == Direction.DOWN){
						ent.fallDistance = 0;
					}
					break;
				case Z:
					ent.addVelocity(0, 0, 0.6D * (ent.getPosZ() < pos.getZ() ^ inverse ? 1D : -1D));
					ent.velocityChanged = true;
					break;
				default:
					break;
			}
		}
	}
}
