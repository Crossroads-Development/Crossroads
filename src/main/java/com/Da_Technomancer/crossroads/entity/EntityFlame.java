package com.Da_Technomancer.crossroads.entity;

import java.awt.Color;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityFlame extends Entity{

	public EntityFlame(World worldIn){
		super(worldIn);
		noClip = true;
		setNoGravity(true);
		setSize(0.25F, 0.25F);
	}

	private int range;//Internally in 20th of a block. Relies on total velocity being 1 block/second 
	private boolean temperedFlame;
	private boolean hasAether;
	private double sulfurRatio;
	private double qsilvrRatio;

	public EntityFlame(World worldIn, double range, boolean temperedFlame, boolean hasAether, double sulfurRatio, double qsilvrRatio){
		this(worldIn);
		this.range = (int) (range * 20D);
		this.temperedFlame = temperedFlame;
		this.hasAether = hasAether;
		this.sulfurRatio = sulfurRatio;
		this.qsilvrRatio = qsilvrRatio;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		range = nbt.getInteger("range");
		temperedFlame = nbt.getBoolean("tempered");
		hasAether = nbt.getBoolean("hasAeth");
		sulfurRatio = nbt.getDouble("sulf");
		qsilvrRatio = nbt.getDouble("qsilvr");
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		nbt.setInteger("range", range);
		nbt.setBoolean("tempered", temperedFlame);
		nbt.setBoolean("hasAeth", hasAether);
		nbt.setDouble("sulf", sulfurRatio);
		nbt.setDouble("qsilvr", qsilvrRatio);
	}

	@Override
	protected void entityInit(){

	}

	@Override
	public void onUpdate(){
		lastTickPosX = posX;
		lastTickPosY = posY;
		lastTickPosZ = posZ;
		super.onUpdate();
		this.posX += this.motionX;
		this.posY += this.motionY;
		this.posZ += this.motionZ;

		if(!world.isRemote){
			
			BlockPos pos = new BlockPos(posX, posY, posZ);
			if(world.getBlockState(pos).getBlock() == Blocks.AIR){
				world.setBlockState(pos, Blocks.COBBLESTONE.getDefaultState());//TODO temp for testing
			}
			//TODO effect
			if(--range <= 0){
				setDead();
			}
		}
	}

	public Color getColor(){
		return Color.RED;//TODO
	}
}
