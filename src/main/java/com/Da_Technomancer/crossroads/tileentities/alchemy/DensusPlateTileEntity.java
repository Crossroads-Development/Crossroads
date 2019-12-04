package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
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

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}
		
		if(facing == null || anti == null){
			IBlockState state = world.getBlockState(pos);
			if(state.getBlock() != ModBlocks.densusPlate){
				return;
			}
			facing = state.getValue(Properties.FACING);
			anti = state.getValue(Properties.CONTAINER_TYPE);
		}
		List<Entity> ents = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.getX() + facing.getFrontOffsetX() * 0.5D, pos.getY() + facing.getFrontOffsetY() * 0.5D, pos.getZ() + facing.getFrontOffsetZ() * 0.5D, pos.getX() + 64 * facing.getFrontOffsetX() + (facing.getFrontOffsetX() == 0 ? 1 : 0), pos.getY() + 64 * facing.getFrontOffsetY() + (facing.getFrontOffsetY() == 0 ? 1 : 0), pos.getZ() + 64 * facing.getFrontOffsetZ() + (facing.getFrontOffsetZ() == 0 ? 1 : 0)), EntitySelectors.IS_ALIVE);
		for(Entity ent : ents){
			if(ent.isSneaking() || (ent instanceof EntityPlayer) && ((EntityPlayer) ent).isSpectator()){
				continue;
			}
			switch(facing.getAxis()){
				case X:
					ent.addVelocity(0.6D * (ent.posX < pos.getX() ? 1D : -1D) * (anti ? -1D : 1D), 0, 0);
					ent.velocityChanged = true;
					break;
				case Y:
					ent.addVelocity(0, 0.6D * (ent.posY < pos.getY() ? 1D : -1D) * (anti ? -1D : 1D), 0);
					ent.velocityChanged = true;
					if(anti && facing == EnumFacing.UP || !anti && facing == EnumFacing.DOWN){
						ent.fallDistance = 0;
					}
					break;
				case Z:
					ent.addVelocity(0, 0, 0.6D * (ent.posZ < pos.getZ() ? 1D : -1D) * (anti ? -1D : 1D));
					ent.velocityChanged = true;
					break;
				default:
					break;
			}
		}
	}
}
