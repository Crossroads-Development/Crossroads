package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.blocks.alchemy.DensusPlate;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class DensusPlateTileEntity extends TileEntity implements ITickable{

	private EnumFacing facing = null;
	private Boolean anti = null;
	private static final int RANGE = ModConfig.getConfigInt(ModConfig.gravRange, false);

	private EnumFacing getFacing(){
		if(facing == null){
			IBlockState state = world.getBlockState(pos);
			if(!(state.getBlock() instanceof DensusPlate)){
				return EnumFacing.DOWN;
			}
			facing = state.getValue(EssentialsProperties.FACING);
			anti = state.getBlock() == ModBlocks.antiDensusPlate;
		}
		return facing;
	}
	
	private boolean isAnti(){
		if(anti == null){
			IBlockState state = world.getBlockState(pos);
			if(!(state.getBlock() instanceof DensusPlate)){
				return false;
			}
			facing = state.getValue(EssentialsProperties.FACING);
			anti = state.getBlock() == ModBlocks.antiDensusPlate;
		}
		return anti;
	}
	
	@Override
	public void update(){
		if(world.isRemote){
			return;
		}
		
		EnumFacing dir = getFacing().getOpposite();
		boolean inverse = isAnti();
		int effectiveRange = RANGE;

		for(int i = 1; i <= RANGE; i++){
			IBlockState state = world.getBlockState(pos.offset(dir, i));
			if(state.getBlock() == ModBlocks.cavorite){
				effectiveRange = i;
				break;
			}
		}

		List<Entity> ents = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.getX() + (dir.getXOffset() == 1 ? 1 : 0), pos.getY() + (dir.getYOffset() == 1 ? 1 : 0), pos.getZ() + (dir.getZOffset() == 1 ? 1 : 0), pos.getX() + (dir.getXOffset() == -1 ? 0 : 1) + effectiveRange * dir.getXOffset(), pos.getY() + (dir.getYOffset() == -1 ? 0 : 1) + effectiveRange * dir.getYOffset(), pos.getZ() + (dir.getZOffset() == -1 ? 0 : 1) + effectiveRange * dir.getZOffset()), EntitySelectors.IS_ALIVE);
		for(Entity ent : ents){
			if(ent.isSneaking() || (ent instanceof EntityPlayer && ((EntityPlayer) ent).isSpectator())){
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
					if(inverse && dir == EnumFacing.UP || !inverse && dir == EnumFacing.DOWN){
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
