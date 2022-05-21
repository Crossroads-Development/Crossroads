package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.ambient.sounds.CRSounds;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.recipes.BeamTransmuteRec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

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
	 * @param worldIn World
	 * @param pos Position of the effect
	 * @param dir The side of the blockspace the beam hit. Can be left null if this is non-directional
	 * @param angle A (normalized) vector of the direction of the beam effect, typically visualized as the direction the beam is travelling. If null but dir is nonnull, the vector of dir will be used as a fallback. Can also be null if this is non-directional.
	 */
	public void doBeamEffect(EnumBeamAlignments align, boolean voi, int power, Level worldIn, BlockPos pos, @Nullable Direction dir, @Nullable Vec3 angle){
		doBeamEffect(align, voi, power, worldIn, pos, dir);
	}

	/**
	 * @deprecated Call and override the version with angle.
	 */
	@Deprecated
	public void doBeamEffect(EnumBeamAlignments align, boolean voi, int power, Level worldIn, BlockPos pos, @Nullable Direction dir){
		performTransmute(align, voi, power, worldIn, pos);
	}

	protected boolean performTransmute(EnumBeamAlignments align, boolean voi, int power, Level worldIn, BlockPos pos){
		BlockState state = worldIn.getBlockState(pos);
		List<BeamTransmuteRec> recipes = worldIn.getRecipeManager().getRecipesFor(CRRecipes.BEAM_TRANSMUTE_TYPE, new SimpleContainer(0), worldIn);
		Optional<BeamTransmuteRec> recipe = recipes.parallelStream().filter(rec -> rec.canApply(align, voi, power, state)).findAny();
		if(recipe.isPresent()){
			worldIn.setBlockAndUpdate(pos, recipe.get().getOutput().defaultBlockState());
			if(CRConfig.beamSounds.get()){
				//Play a sound
				CRSounds.playSoundServer(worldIn, pos, CRSounds.BEAM_TRANSMUTE, SoundSource.BLOCKS, 0.5F, 1F);
			}
			return true;
		}
		return false;
	}

	@Nullable
	protected Vec3 getAngleVec(@Nullable Direction dir, @Nullable Vec3 angle){
		if(angle != null){
			return null;
		}
		if(dir != null){
			Vec3i normalVec = dir.getOpposite().getNormal();
			return new Vec3(normalVec.getX(), normalVec.getY(), normalVec.getZ());
		}
		return null;
	}
}
