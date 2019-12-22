package com.Da_Technomancer.crossroads.API.alchemy;

import com.Da_Technomancer.crossroads.items.crafting.recipes.AlchemyRec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.particles.IParticleData;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;

/**
 * Implementations must implement hasCapability and getCapability directly. 
 */
public abstract class AlchemyReactorTE extends AlchemyCarrierTE implements IReactionChamber{

	public AlchemyReactorTE(TileEntityType<? extends AlchemyReactorTE> type){
		super(type);
	}

	public AlchemyReactorTE(TileEntityType<? extends AlchemyReactorTE> type, boolean glass){
		super(type, glass);
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
	public <T extends IParticleData> void addVisualEffect(T particleType, double speedX, double speedY, double speedZ){
		if(!world.isRemote){
			Vec3d particlePos = getParticlePos();
			((ServerWorld) world).spawnParticle(particleType, particlePos.x, particlePos.y, particlePos.z, 0, speedX, speedY, speedZ, 1F);
		}
	}

	protected boolean broken = false;

	@Override
	public void destroyChamber(float strength){
		if(!broken){
			broken = true;
			BlockState state = world.getBlockState(pos);
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
			SoundType sound = state.getBlock().getSoundType(state, world, pos, null);
			world.playSound(null, pos, sound.getBreakSound(), SoundCategory.BLOCKS, sound.getVolume(), sound.getPitch());
			AlchemyUtil.releaseChemical(world, pos, contents);
			if(strength > 0F){
				world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), strength, Explosion.Mode.BREAK);//We will drop items, because an explosion in your lab is devastating enough without having to re-craft everything
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
	public void tick(){
		if(world.isRemote){
			return;
		}
		if(dirtyReag){
			correctReag();
		}

		if(world.getGameTime() % AlchemyUtil.ALCHEMY_TIME == 0){
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
		for(AlchemyRec react : AlchemyCore.getReactions(world)){
			if(react.performReaction(this)){
				correctReag();
				break;
			}
		}
	}
}
