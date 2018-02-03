package com.Da_Technomancer.crossroads.entity;

import java.awt.Color;

import com.Da_Technomancer.crossroads.API.effects.alchemy.AetherEffect;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendFlameInfoToClient;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class EntityFlame extends Entity{

	public EntityFlame(World worldIn){
		super(worldIn);
		noClip = true;
		setNoGravity(true);
		setSize(0.25F, 0.25F);
	}

	private int range;//Internally in 20th of a block. Relies on total velocity being 1 block/second 
	public boolean temperedFlame;
	public boolean hasAether;
	public double sulfurRatio;
	public double qsilvrRatio;
	private boolean synced = false;

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
			if(!synced){
				synced = true;
				ModPackets.network.sendToAllAround(new SendFlameInfoToClient(getUniqueID(), temperedFlame, hasAether, (float) sulfurRatio, (float) qsilvrRatio), new TargetPoint(world.provider.getDimension(), posX, posY, posZ, 512));
			}
			
			
			BlockPos pos = new BlockPos(posX, posY, posZ);
			if(hasAether){
				AetherEffect.doTransmute(world, pos, sulfurRatio, qsilvrRatio);
			}
			if(!temperedFlame){
				float hardness = world.getBlockState(pos).getBlockHardness(world, pos);
				if(hardness >= 0 && hardness <= 3F){
					world.setBlockState(pos, Blocks.FIRE.getDefaultState());
				}
			}
			if(--range <= 0){
				setDead();
			}
		}
	}

	public Color getColor(){
		return hasAether ? new Color(((int) (255D * sulfurRatio)), ((int) (128D * (1D - sulfurRatio))), ((int) (255D * (1D - sulfurRatio)))) : temperedFlame ? Color.CYAN : Color.RED;
	}
}
