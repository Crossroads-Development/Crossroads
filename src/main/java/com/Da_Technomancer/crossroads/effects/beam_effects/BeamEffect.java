package com.Da_Technomancer.crossroads.effects.beam_effects;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.ambient.sounds.CRSounds;
import com.Da_Technomancer.crossroads.api.CRCapabilities;
import com.Da_Technomancer.crossroads.api.beams.BeamHit;
import com.Da_Technomancer.crossroads.api.beams.BeamUnit;
import com.Da_Technomancer.crossroads.api.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.api.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.crafting.BeamTransmuteRec;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.integration.curios.CurioHelper;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.technomancy.BeamCage;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;

public class BeamEffect{

	/**
	 * Used for generic alignments with no special effects
	 */
	public static final BeamEffect INSTANCE = new BeamEffect();

	/**
	 * Performs the beam effect. Call on the virtual server side only.
	 * @param align This beam alignment
	 * @param voi Whether this is a void variant
	 * @param power Total beam power
	 * @param beamHit Information about the collision point and path of the beam
	 */
	public void doBeamEffect(EnumBeamAlignments align, boolean voi, int power, BeamHit beamHit){
		performTransmute(align, voi, power, beamHit);
	}

	/**
	 * Performs basic beam effects which all alignments should perform
	 * All beam effects should call this method first
	 * @param align This beam alignment
	 * @param voi Whether this is a void variant
	 * @param power Total beam power
	 * @param beamHit Information about the collision point and path of the beam
	 * @return Whether this did anything (in which case, don't run the normal beam effect)
	 */
	protected boolean performTransmute(EnumBeamAlignments align, boolean voi, int power, BeamHit beamHit){
		if(align == EnumBeamAlignments.NO_MATCH){
			return false;//Optimization; empty beams don't do anything
		}

		//Go into a machine
		IBeamHandler receivingMachine = beamHit.getEndCapability(com.Da_Technomancer.crossroads.api.CRCapabilities.BEAM_CAPABILITY, false);
		if(receivingMachine != null){
			receivingMachine.setBeam(beamHit.getBeamUnit(), beamHit);
			return true;
		}

		//Try converting the block according to a recipe
		// TODO: Passing a SingleRecipeInput with null ItemStack as a replacement for previous SimpleContainer(0); the matches method of BeamTransmuteRec ignores the
		//  RecipeInput, so this shouldn't become relevant, but there may be a better way to do this.
		List<RecipeHolder<BeamTransmuteRec>> recipes = beamHit.getWorld().getRecipeManager().getRecipesFor(CRRecipes.BEAM_TRANSMUTE_TYPE, new SingleRecipeInput(null), beamHit.getWorld());
		BlockState state = beamHit.getEndState();
		Optional<RecipeHolder<BeamTransmuteRec>> recipe = recipes.parallelStream().filter(rec -> rec.value().canApply(align, voi, power, state)).findAny();
		if(recipe.isPresent()){
			beamHit.getWorld().setBlockAndUpdate(beamHit.getPos(), recipe.get().value().getOutput().defaultBlockState());
			if(CRConfig.beamSounds.get()){
				//Play a sound
				CRSounds.playSoundServer(beamHit.getWorld(), beamHit.getPos(), CRSounds.BEAM_TRANSMUTE, SoundSource.BLOCKS, 0.5F, 1F);
			}
			return true;
		}

		//If we hit someone holding a beam cage, charge the cage
		List<LivingEntity> hitEntities = beamHit.getNearbyEntities(LivingEntity.class, BeamHit.WITHIN_BLOCK_RANGE, null);
		for(LivingEntity ent : hitEntities){
			ItemStack beamCage = CurioHelper.getEquipped(CRItems.beamCage, ent);
			if(!beamCage.isEmpty()){
				//Charge the beam cage, consuming the beam
				BeamUnit cageBeam = BeamCage.getStored(beamCage);
				int energy = cageBeam.getEnergy();
				int potential = cageBeam.getPotential();
				int stability = cageBeam.getStability();
				int cageVoi = cageBeam.getVoid();

				BeamUnit mag = beamHit.getBeamUnit();
				energy += mag.getEnergy();
				potential += mag.getPotential();
				stability += mag.getStability();
				cageVoi += mag.getVoid();
				cageBeam = new BeamUnit(energy, potential, stability, cageVoi);
				BeamCage.storeBeam(beamCage, cageBeam);
				return true;
			}
		}

		return false;
	}
}
