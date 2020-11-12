package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.recipes.BeamTransmuteRec;
import com.Da_Technomancer.crossroads.particles.sounds.CRSounds;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class BeamEffect{

	/**
	 * Used for generic alignments with no special effects
	 */
	public static final BeamEffect INSTANCE = new BeamEffect();

	public void doBeamEffect(EnumBeamAlignments align, boolean voi, int power, World worldIn, BlockPos pos, @Nullable Direction dir){
		performTransmute(align, voi, power, worldIn, pos);
	}

	protected boolean performTransmute(EnumBeamAlignments align, boolean voi, int power, World worldIn, BlockPos pos){
		BlockState state = worldIn.getBlockState(pos);
		List<BeamTransmuteRec> recipes = worldIn.getRecipeManager().getRecipes(CRRecipes.BEAM_TRANSMUTE_TYPE, new Inventory(0), worldIn);
		Optional<BeamTransmuteRec> recipe = recipes.parallelStream().filter(rec -> rec.canApply(align, voi, power, state)).findAny();
		if(recipe.isPresent()){
			worldIn.setBlockState(pos, recipe.get().getOutput().getDefaultState());
			if(CRConfig.beamSounds.get()){
				//Play a sound
				CRSounds.playSoundServer(worldIn, pos, CRSounds.BEAM_TRANSMUTE, SoundCategory.BLOCKS, 0.5F, 1F);//TODO
			}
			return true;
		}
		return false;
	}
}
