package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCore;
import com.Da_Technomancer.crossroads.API.alchemy.EnumSolventType;
import com.Da_Technomancer.crossroads.API.alchemy.IReagent;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityShell extends EntityThrowable{

	private ReagentStack[] contents;
	private double temp;

	public EntityShell(World worldIn){
		super(worldIn);
	}

	public EntityShell(World worldIn, EntityLivingBase throwerIn, ReagentStack[] contents, double temp){
		super(worldIn, throwerIn);
		this.contents = contents;
		this.temp = temp;
	}

	@Override
	protected void onImpact(RayTraceResult result){
		if(!world.isRemote){
			if(contents != null){
				BlockPos targetPos = result.getBlockPos();
				for(ReagentStack r : contents){
					if(r != null){
						r.getType().onRelease(world, targetPos, r.getAmount(), temp, r.getPhase(temp), contents);
					}
				}
			}
			world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.NEUTRAL, 0.5F, 0.4F / (rand.nextFloat() * 0.4F + 0.8F));
			world.setEntityState(this, (byte) 3);
			setDead();
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		temp = nbt.getDouble("temp");

		boolean[] solvents = new boolean[EnumSolventType.values().length];
		
		for(int i = 0; i < solvents.length; i++){
			solvents[i] = nbt.getBoolean(i + "_solv");
		}
		for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
			if(nbt.hasKey(i + "_am")){
				contents[i] = new ReagentStack(AlchemyCore.REAGENTS[i], nbt.getDouble(i + "_am"));
				contents[i].updatePhase(temp);
			}
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		nbt.setDouble("temp", temp);

		if(contents != null){
			boolean[] solvents = new boolean[EnumSolventType.values().length];

			for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
				ReagentStack reag = contents[i];
				if(reag != null){
					nbt.setDouble(i + "_am", reag.getAmount());
					
					IReagent type = reag.getType();
					solvents[EnumSolventType.AQUA_REGIA.ordinal()] |= i == 11;//Aqua regia is a special case where it works no matter the phase, but ONLY works at all if a polar solvent is present. 

					if(type.getMeltingPoint() <= temp && type.getBoilingPoint() > temp && type.solventType() != null){
						solvents[type.solventType().ordinal()] = true;
					}
				}
			}

			solvents[EnumSolventType.AQUA_REGIA.ordinal()] &= solvents[EnumSolventType.POLAR.ordinal()];
			
			for(int i = 0; i < solvents.length; i++){
				nbt.setBoolean(i + "_solv", solvents[i]);
			}
		}
	}
}
