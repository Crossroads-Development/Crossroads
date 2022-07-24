package com.Da_Technomancer.crossroads.effects.beam_effects;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.ambient.sounds.CRSounds;
import com.Da_Technomancer.crossroads.api.beams.BeamHit;
import com.Da_Technomancer.crossroads.api.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.crafting.BeamTransmuteRec;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
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
		doBeamEffect(align, voi, power, beamHit.getWorld(), beamHit.getPos(), beamHit.getDirection());
	}

	/**
	 * @deprecated Call and override the version with BeamHit.
	 */
	@Deprecated
	public void doBeamEffect(EnumBeamAlignments align, boolean voi, int power, Level worldIn, BlockPos pos, @Nullable Direction dir){
		performTransmute(align, voi, power, worldIn, pos);
	}

	@Deprecated
	protected boolean performTransmute(EnumBeamAlignments align, boolean voi, int power, Level worldIn, BlockPos pos){
		return performTransmute(align, voi, power, new BeamHit((ServerLevel) worldIn, pos, Direction.DOWN, worldIn.getBlockState(pos)));
	}

	protected boolean performTransmute(EnumBeamAlignments align, boolean voi, int power, BeamHit beamHit){
		List<BeamTransmuteRec> recipes = beamHit.getWorld().getRecipeManager().getRecipesFor(CRRecipes.BEAM_TRANSMUTE_TYPE, new SimpleContainer(0), beamHit.getWorld());
		BlockState state = beamHit.getEndState();
		Optional<BeamTransmuteRec> recipe = recipes.parallelStream().filter(rec -> rec.canApply(align, voi, power, state)).findAny();
		if(recipe.isPresent()){
			beamHit.getWorld().setBlockAndUpdate(beamHit.getPos(), recipe.get().getOutput().defaultBlockState());
			if(CRConfig.beamSounds.get()){
				//Play a sound
				CRSounds.playSoundServer(beamHit.getWorld(), beamHit.getPos(), CRSounds.BEAM_TRANSMUTE, SoundSource.BLOCKS, 0.5F, 1F);
			}
			return true;
		}
		return false;
	}
}
