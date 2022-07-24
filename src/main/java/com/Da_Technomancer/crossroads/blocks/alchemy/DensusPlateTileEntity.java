package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class DensusPlateTileEntity extends BlockEntity implements ITickableTileEntity{

	public static final BlockEntityType<DensusPlateTileEntity> TYPE = CRTileEntity.createType(DensusPlateTileEntity::new, CRBlocks.antiDensusPlate, CRBlocks.densusPlate);

	private static final TagKey<Block> gravityBlocking = CraftingUtil.getTagKey(ForgeRegistries.Keys.BLOCKS, new ResourceLocation(Crossroads.MODID, "gravity_blocking"));

	private final int RANGE = CRConfig.gravRange.get();
	private final double ACCEL = CRConfig.gravAccel.get();

	public DensusPlateTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	private Direction getFacing(){
		BlockState state = getBlockState();
		if(state.hasProperty(CRProperties.FACING)){
			return state.getValue(CRProperties.FACING);
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
			if(CraftingUtil.tagContains(gravityBlocking, state.getBlock())){
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
