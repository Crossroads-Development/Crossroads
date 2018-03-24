package com.Da_Technomancer.crossroads.API.alchemy;

import javax.annotation.Nullable;

import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
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
			Vec3d particlePos = getParticlePos();
			((WorldServer) world).spawnParticle(particleType, false, particlePos.x, particlePos.y, particlePos.z, 0, speedX, speedY, speedZ, 1F, particleArgs);
		}
	}

	protected boolean broken = false;

	@Override
	public void destroyChamber(){
		if(!broken){
			broken = true;
			double temp = getTemp();
			IBlockState state = world.getBlockState(pos);
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
			SoundType sound = state.getBlock().getSoundType(state, world, pos, null);
			world.playSound(null, pos, sound.getBreakSound(), SoundCategory.BLOCKS, sound.getVolume(), sound.getPitch());
			for(ReagentStack r : contents){
				if(r != null){
					r.getType().onRelease(world, pos, r.getAmount(), temp, r.getPhase(temp), contents);
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

					if(type.getMeltingPoint() <= endTemp && type.getBoilingPoint() > endTemp && type.solventType() != null){
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
	
	@Override
	public void dropItem(ItemStack stack){
		InventoryHelper.spawnItemStack(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack);
	}

	protected void performReaction(){
		boolean[] solvents = new boolean[EnumSolventType.values().length];

		for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
			ReagentStack reag = contents[i];
			if(reag != null){
				IReagent type = reag.getType();
				solvents[EnumSolventType.AQUA_REGIA.ordinal()] |= i == 11;//Aqua regia is a special case where it works no matter the phase, but ONLY works at all if a polar solvent is present. 

				if(type.getMeltingPoint() <= correctTemp() && type.getBoilingPoint() > correctTemp() && type.solventType() != null){
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
