package com.Da_Technomancer.crossroads.API.templates;

import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.awt.*;
import java.util.ArrayList;

public interface IBeamRenderTE extends IInfoTE{

	@Override
	default void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		BeamUnit[] mag = getLastSent();
		boolean output = false;
		if(mag != null){
			for(int i = 0; i < mag.length; i++){
				BeamUnit check = mag[i];
				if(!check.isEmpty()){
					output = true;
					EnumBeamAlignments.getAlignment(check).discover(player, true);
					String dir = Direction.from3DDataValue(i).toString();
					dir = Character.toUpperCase(dir.charAt(0)) + dir.substring(1);
					Color col = check.getRGB();
					chat.add(new TranslatableComponent("tt.crossroads.meter.beam", dir, check.toString(), EnumBeamAlignments.getAlignment(check).getLocalName(check.getVoid() != 0), col.getRed(), col.getGreen(), col.getBlue(), check.getEnergy(), check.getPotential(), check.getStability(), check.getVoid(), check.getPower()));
				}
			}
		}
		if(!output){
			//Generic message so it doesn't output nothing to the user
			chat.add(new TranslatableComponent("tt.crossroads.meter.beam.none"));
		}
	}

	/**
	 *
	 * @return A size six array (with null elements) with integers that BeamManager.getTriple will convert into rendering instructions
	 */
	int[] getRenderedBeams();

	/**
	 * For informational displays.
	 * Must not contain null elements
	 */
	BeamUnit[] getLastSent();

	/* Recommended override for subclasses
	@Override
	public AxisAlignedBB getRenderBoundingBox(){
		return INFINITE_EXTENT_AABB;
	}
	 */
}
