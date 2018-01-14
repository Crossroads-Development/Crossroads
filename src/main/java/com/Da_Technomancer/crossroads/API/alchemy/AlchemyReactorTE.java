package com.Da_Technomancer.crossroads.API.alchemy;

import javax.annotation.Nullable;

import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.WorldServer;

/**
 * Implementations must implement hasCapability and getCapability directly. 
 */
public abstract class AlchemyReactorTE extends AlchemyCarrierTE implements IReactionChamber{

	public AlchemyReactorTE(){
		super();
	}

	public AlchemyReactorTE(boolean glass){
		super(glass);
	}

	@Override
	public ReagentStack[] getReagants(){
		return contents;
	}

	@Override
	public double getHeat(){
		return heat;
	}

	@Override
	public void setHeat(double heatIn){
		heat = heatIn;
	}

	@Override
	public void addVisualEffect(EnumParticleTypes particleType, double speedX, double speedY, double speedZ, int... particleArgs){
		if(!world.isRemote){
			((WorldServer) world).spawnParticle(particleType, false, (float) pos.getX() + .5F, (float) pos.getY() + .5F, (float) pos.getZ() + .5F, 0, speedX, speedY, speedZ, 1F, particleArgs);
		}
	}

	protected boolean broken = false;

	@Override
	public void destroyChamber(){
		if(!broken){
			broken = true;
			double temp = getTemp();
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
			for(ReagentStack r : contents){
				if(r != null){
					r.getType().onRelease(world, pos, r.getAmount(), r.getPhase(temp));
				}
			}
		}
	}

	@Override
	@Nullable
	protected boolean[] correctReag(){
		dirtyReag = false;
		amount = 0;
		for(ReagentStack r : contents){
			if(r != null){
				amount += r.getAmount();
			}
		}
		if(amount == 0){
			return null;
		}

		double endTemp = correctTemp();

		boolean[] solvents = new boolean[EnumSolventType.values().length];

		for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
			ReagentStack reag = contents[i];
			if(reag != null){
				if(reag.getAmount() >= AlchemyCore.MIN_QUANTITY){
					IReagent type = reag.getType();
					solvents[EnumSolventType.AQUA_REGIA.ordinal()] |= i == 11;//Aqua regia is a special case where it works no matter the phase, but ONLY works at all if a polar solvent is present. 

					if(type.getMeltingPoint() <= endTemp && type.getBoilingPoint() > endTemp){
						solvents[type.solventType().ordinal()] = true;
					}
				}else{
					heat -= (endTemp + 273D) * reag.getAmount();
					contents[i] = null;
				}
			}
		}

		solvents[EnumSolventType.AQUA_REGIA.ordinal()] &= solvents[EnumSolventType.POLAR.ordinal()];

		boolean destroy = false;

		for(int i = 0; i < contents.length; i++){
			ReagentStack reag = contents[i];
			if(reag == null){
				continue;
			}
			reag.updatePhase(endTemp, solvents);
			if(glass && !reag.getType().canGlassContain()){
				destroy |= reag.getType().destroysBadContainer();
				contents[i] = null;
			}
		}

		if(destroy){
			destroyChamber();
			return null;
		}
		return solvents;
	}

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}
		if(dirtyReag){
			correctReag();
		}

		if(world.getTotalWorldTime() % AlchemyCore.ALCHEMY_TIME == 0){
			spawnParticles();
			performReaction();
			performTransfer();
		}
	}

	@Override
	public double getReactionCapacity(){
		return transferCapacity() * 1.5D;
	}

	protected void performReaction(){
		boolean[] solvents = new boolean[EnumSolventType.values().length];

		for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
			ReagentStack reag = contents[i];
			if(reag != null){
				IReagent type = reag.getType();
				solvents[EnumSolventType.AQUA_REGIA.ordinal()] |= i == 11;//Aqua regia is a special case where it works no matter the phase, but ONLY works at all if a polar solvent is present. 

				if(type.getMeltingPoint() <= correctTemp() && type.getBoilingPoint() > correctTemp()){
					solvents[type.solventType().ordinal()] = true;
				}
			}
		}

		solvents[EnumSolventType.AQUA_REGIA.ordinal()] &= solvents[EnumSolventType.POLAR.ordinal()];

		for(IReaction react : AlchemyCore.REACTIONS){
			if(react.performReaction(this, solvents)){
				solvents = correctReag();
				if(solvents == null){
					return;
				}
				break;
			}
		}
	}
}
