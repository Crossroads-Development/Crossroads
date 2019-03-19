package com.Da_Technomancer.crossroads.API.alchemy;

import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;

import java.util.ArrayList;

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
	public ReagentMap getReagants(){
		return contents;
	}

	@Override
	public EnumContainerType getChannel(){
		return EnumContainerType.NONE;
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
	public void destroyChamber(float strength){
		if(!broken){
			broken = true;
			IBlockState state = world.getBlockState(pos);
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
			SoundType sound = state.getBlock().getSoundType(state, world, pos, null);
			world.playSound(null, pos, sound.getBreakSound(), SoundCategory.BLOCKS, sound.getVolume(), sound.getPitch());
			AlchemyUtil.releaseChemical(world, pos, contents);
			if(strength > 0F){
				world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), strength, true);
			}
		}
	}

	@Override
	protected void correctReag(){
		super.correctReag();

		boolean destroy = false;

		ArrayList<IReagent> toRemove = new ArrayList<>(1);//Rare that there is more than 1 at a time

		for(IReagent type : contents.keySet()){
			if(contents.getQty(type) == 0){
				continue;
			}
			if(glass && !type.canGlassContain()){
				destroy |= type.destroysBadContainer();
				toRemove.add(type);
			}
		}

		if(destroy){
			destroyChamber(0);
		}else{
			for(IReagent type : toRemove){
				contents.removeReagent(type, contents.get(type));
			}
		}
	}

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}
		if(dirtyReag){
			correctReag();
		}

		if(world.getTotalWorldTime() % AlchemyUtil.ALCHEMY_TIME == 0){
			spawnParticles();
			performReaction();
			performTransfer();
		}
	}

	@Override
	public int getReactionCapacity(){
		return transferCapacity() * 2;
	}

	protected void performReaction(){
		for(IReaction react : AlchemyCore.REACTIONS){
			if(react.performReaction(this)){
				correctReag();
				break;
			}
		}
	}
}
