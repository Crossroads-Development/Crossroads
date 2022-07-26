package com.Da_Technomancer.crossroads.api.beams;

import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.effects.beam_effects.BeamEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * Represents a beam output from a given start position along a given direction
 * Handles outputting the beam and assists in syncing rendering data
 */
public class BeamHelper{

	protected final Direction dir;
	protected final BlockPos pos;

	protected int dist;
	@Nonnull
	protected BeamUnit lastSent = BeamUnit.EMPTY;

	public BeamHelper(@Nonnull Direction dir, @Nonnull BlockPos pos){
		this.dir = dir;
		this.pos = pos.immutable();
	}

	/**
	 * Emit a beam
	 * @param mag The beam to transmit
	 * @param world The world
	 * @return Whether the rendered beam has changed and a new update packet needs to be sent
	 */
	public boolean emit(@Nonnull BeamUnit mag, Level world){
		BeamHit beamHit = BeamUtil.rayTraceBeamSimple(mag, world, pos, dir, BeamUtil.MAX_DISTANCE, false);
		int newDist = beamHit.getPos().distManhattan(pos);

		//Check for machine receiving beams
		BlockEntity checkTE = beamHit.getEndBlockEntity();
		LazyOptional<IBeamHandler> opt;
		if(checkTE != null && (opt = checkTE.getCapability(Capabilities.BEAM_CAPABILITY, dir.getOpposite())).isPresent()){
			opt.orElseThrow(NullPointerException::new).setBeam(mag);
			return updateBeamRender(mag, newDist);
		}

		//Do beam effect
		if(!mag.isEmpty() && !world.isClientSide){
			EnumBeamAlignments align = EnumBeamAlignments.getAlignment(mag);
			BeamEffect e = align.getEffect();
			e.doBeamEffect(align, mag.getVoid() != 0, Math.min(BeamUtil.MAX_EFFECT_POWER, mag.getPower()), beamHit);
		}
		return updateBeamRender(mag, newDist);
	}

	protected boolean updateBeamRender(BeamUnit newBeam, int newDist){
		if(dist != newDist || !newBeam.equals(lastSent)){
			dist = newDist;
			lastSent = newBeam;
			return true;
		}else{
			return false;
		}
	}

	@Nonnull
	public BeamUnit getLastSent(){
		return lastSent;
	}

	/**
	 * Serializes information needed by this class on the client side into an integer
	 * @param mag The beam unit to render
	 * @param dist The length of the last beam sent
	 * @return A serialized form of the arguments
	 */
	public static int toPacket(BeamUnit mag, int dist){
		if(mag == null){
			return 0;
		}
		int packet = 0;
		if(mag.getPower() != 0){
			packet |= mag.getRGB().getRGB() & 0xFFFFFF;//Encode color, Remove the alpha bits
			packet |= ((dist - 1) & 0xF) << 24;//Encode length
			packet |= ((BeamUtil.getBeamRadius(mag.getPower()) - 1) & 0xF) << 28;//Encode beam radius
		}
		return packet;
	}

	public int genPacket(){
		return toPacket(lastSent, dist);
	}

	/**
	 * Deserializes an integer packet from the server into information needed for rendering
	 * @param packet The packet
	 * @return A triple with the color to render, the length of the beam to render, and the size (adjusted for rendering, not the original power of the beam) of the beam to render
	 */
	public static Triple<Color, Integer, Integer> getTriple(int packet){
		return packet == 0 ? Triple.of(Color.BLACK, 0, 0) : Triple.of(Color.decode(Integer.toString(packet & 0xFFFFFF)), ((packet >>> 24) & 0xF) + 1, (packet >>> 28) + 1);
	}
}
