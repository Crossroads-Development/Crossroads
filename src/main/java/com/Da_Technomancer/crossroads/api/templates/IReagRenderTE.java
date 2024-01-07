package com.Da_Technomancer.crossroads.api.templates;

import com.Da_Technomancer.crossroads.api.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.api.alchemy.IReagent;
import com.Da_Technomancer.crossroads.api.alchemy.ReagentMap;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Vector3f;

import java.awt.*;

public interface IReagRenderTE{

	/**
	 * Called on client side
	 * @return Each array entry represents one rectangular prism volume. Start and end coordinate, relative to the blockpos origin. Left vector should be minimum coordinate value on all 3 axes, right vector the maximum
	 */
	public Pair<Vector3f, Vector3f>[] getRenderVolumes();

	/**
	 * Called on client side
	 * @return Outer array is phase, inner array is color as r,g,b,a, or null for phase not present
	 */
	public int[][] getReagentColorClient();

	public static int[] encodeReagMapToColors(ReagentMap reagMap){
		int[] phaseQty = new int[EnumMatterPhase.values().length];
		int[][] colorTotals = new int[EnumMatterPhase.values().length][4];

		double temp = reagMap.getTempC();
		for(IReagent reag : reagMap.keySetReag()){
			EnumMatterPhase phase = reag.getPhase(temp);
			int ordinal = phase.ordinal();
			Color col = reag.getColor(phase);
			int qty = reagMap.getQty(reag);
			colorTotals[ordinal][0] += col.getRed() * qty;
			colorTotals[ordinal][1] += col.getGreen() * qty;
			colorTotals[ordinal][2] += col.getBlue() * qty;
			colorTotals[ordinal][3] += col.getAlpha() * qty;
			phaseQty[ordinal] += qty;
		}
		int[] result = new int[EnumMatterPhase.values().length];
		for(int i = 0; i < result.length; i++){
			if(phaseQty[i] != 0){
				result[i] = (((colorTotals[i][0] / phaseQty[i]) & 0xFF) << 16) | (((colorTotals[i][1] / phaseQty[i]) & 0xFF) << 8) | (((colorTotals[i][2] / phaseQty[i]) & 0xFF)) | (((colorTotals[i][3] / phaseQty[i]) & 0xFF) << 24);
			}
		}

		return result;
	}

	public static int[][] splitColorComponents(int[] colors){
		int[][] result = new int[EnumMatterPhase.values().length][];
		for(int i = 0; i < colors.length; i++){
			int alpha = colors[i] >>> 24;
			if(alpha != 0){
				//Alpha non-zero
				result[i] = new int[4];
				result[i][0] = colors[i] >>> 16 & 0xFF;
				result[i][1] = colors[i] >>> 8 & 0xFF;
				result[i][2] = colors[i] & 0xFF;
				result[i][3] = alpha;
			}
		}
		return result;
	}
}
