package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;

public class RotaryDrillTileEntity extends TileEntity implements ITickable{

	private static final DamageSource DRILL = new DamageSource("drill").setDamageBypassesArmor();
	
	private int ticksExisted = 0;
	private final double ENERGYUSE = .5D;
	private final double SPEEDPERHARDNESS = .1D;

	private float angle = 0;

	@Override
	public void update(){
		if(world.isRemote){
			EnumFacing facing = world.getBlockState(pos).getValue(Properties.FACING);
			if(world.getTileEntity(pos.offset(facing.getOpposite())) != null && world.getTileEntity(pos.offset(facing.getOpposite())).hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing) && (facing.getOpposite() != EnumFacing.UP || !(world.getTileEntity(pos.offset(EnumFacing.UP)) instanceof ToggleGearTileEntity) || world.getBlockState(pos.offset(EnumFacing.UP)).getValue(Properties.REDSTONE_BOOL))){
				angle = (float) world.getTileEntity(pos.offset(facing.getOpposite())).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing).getAngle();
			}

			return;
		}

		EnumFacing facing = world.getBlockState(pos).getValue(Properties.FACING);
		IAxleHandler handler;
		if(world.getTileEntity(pos.offset(facing.getOpposite())) != null && world.getTileEntity(pos.offset(facing.getOpposite())).hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing) && (facing.getOpposite() != EnumFacing.UP || !(world.getTileEntity(pos.offset(EnumFacing.UP)) instanceof ToggleGearTileEntity) || world.getBlockState(pos.offset(EnumFacing.UP)).getValue(Properties.REDSTONE_BOOL)) && Math.abs((handler = world.getTileEntity(pos.offset(facing.getOpposite())).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing)).getMotionData()[1]) >= ENERGYUSE){
			handler.addEnergy(-ENERGYUSE, false, false);
			if(++ticksExisted % 10 == 0){
				world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.BLOCKS, .2F, .5F);
				if(!world.isAirBlock(pos.offset(facing))){
					if(Math.abs(handler.getMotionData()[0]) >= world.getBlockState(pos.offset(facing)).getBlockHardness(world, pos.offset(facing)) * SPEEDPERHARDNESS){
						world.destroyBlock(pos.offset(facing), true);
					}
				}else if(world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos.offset(facing)), EntitySelectors.IS_ALIVE) != null){
					for(EntityLivingBase ent : world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos.offset(facing)), EntitySelectors.IS_ALIVE)){
						ent.attackEntityFrom(DRILL, (float) Math.abs(handler.getMotionData()[0] / SPEEDPERHARDNESS));
					}
				}
			}
		}
	}

	public float getAngle(){
		return angle;
	}
}
