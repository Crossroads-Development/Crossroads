package com.Da_Technomancer.crossroads.API.alchemy;

import com.Da_Technomancer.crossroads.crafting.recipes.AlchemyRec;
import net.minecraft.particles.IParticleData;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

/**
 * Implementations must implement getCapability directly.
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
			Vector3d particlePos = getParticlePos();
			((ServerWorld) world).spawnParticle(particleType, particlePos.x, particlePos.y, particlePos.z, 0, speedX, speedY, speedZ, 1F);
		}
	}

	@Override
	public void destroyChamber(float strength){
		destroyCarrier(strength);//Use the destruction method in AlchemyCarrierTE
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
		for(AlchemyRec react : ReagentManager.getReactions(world)){
			if(react.performReaction(this)){
				correctReag();
				break;
			}
		}
	}
}
