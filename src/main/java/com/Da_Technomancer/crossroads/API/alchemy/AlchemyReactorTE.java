package com.Da_Technomancer.crossroads.API.alchemy;

import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.crafting.recipes.AlchemyRec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;

/**
 * Implementations must implement getCapability directly.
 */
public abstract class AlchemyReactorTE extends AlchemyCarrierTE implements IReactionChamber{

	public AlchemyReactorTE(BlockEntityType<? extends AlchemyReactorTE> type, BlockPos pos, BlockState state){
		super(type, pos, state);
	}

	public AlchemyReactorTE(BlockEntityType<? extends AlchemyReactorTE> type, BlockPos pos, BlockState state, boolean glass){
		super(type, pos, state, glass);
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
	public <T extends ParticleOptions> void addVisualEffect(T particleType, double speedX, double speedY, double speedZ){
		if(!level.isClientSide){
			Vec3 particlePos = getParticlePos();
			((ServerLevel) level).sendParticles(particleType, particlePos.x, particlePos.y, particlePos.z, 0, speedX, speedY, speedZ, 1F);
		}
	}

	@Override
	public void destroyChamber(float strength){
		destroyCarrier(strength);//Use the destruction method in AlchemyCarrierTE
	}

	@Override
	public void serverTick(){
		//Note that we do NOT have a super call

		if(!init && useCableHeat()){
			cableTemp = HeatUtil.convertBiomeTemp(level, worldPosition);
		}
		init = true;

		if(dirtyReag){
			correctReag();
		}

		if(level.getGameTime() % AlchemyUtil.ALCHEMY_TIME == 0){
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
		for(AlchemyRec react : ReagentManager.getReactions(level)){
			if(react.performReaction(this)){
				correctReag();
				break;
			}
		}
	}
}
