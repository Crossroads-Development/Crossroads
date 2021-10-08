package com.Da_Technomancer.crossroads.API.beams;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.effects.BeamEffect;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nonnull;
import java.awt.*;

public class BeamManager{

	private final Direction dir;
	private final BlockPos pos;

	private int dist;
	@Nonnull
	private BeamUnit lastSent = BeamUnit.EMPTY;

	public BeamManager(@Nonnull Direction dir, @Nonnull BlockPos pos){
		this.dir = dir;
		this.pos = pos.immutable();
	}

	public boolean emit(@Nonnull BeamUnit mag, Level world){
		for(int i = 1; i <= BeamUtil.MAX_DISTANCE; i++){
			BlockEntity checkTE = world.getBlockEntity(pos.relative(dir, i));
			LazyOptional<IBeamHandler> opt;
			if(checkTE != null && (opt = checkTE.getCapability(Capabilities.BEAM_CAPABILITY, dir.getOpposite())).isPresent()){
				opt.orElseThrow(NullPointerException::new).setBeam(mag);
				if(dist != i || !mag.equals(lastSent)){
					dist = i;
					lastSent = mag;
					return true;
				}else{
					return false;
				}
			}

			BlockState checkState = world.getBlockState(pos.relative(dir, i));
			if(i == BeamUtil.MAX_DISTANCE || BeamUtil.solidToBeams(checkState, world, pos.relative(dir, i), dir, mag.getPower())){
				if(!mag.isEmpty()){
					EnumBeamAlignments align = EnumBeamAlignments.getAlignment(mag);
					BeamEffect e = align.getEffect();
					e.doBeamEffect(align, mag.getVoid() != 0, Math.min(64, mag.getPower()), world, pos.relative(dir, i), dir.getOpposite());
				}
				if(dist != i || !mag.equals(lastSent)){
					dist = i;
					lastSent = mag;
					return true;
				}
				return false;
			}
		}

		return false;
	}

	@Nonnull
	public BeamUnit getLastSent(){
		return lastSent;
	}

	/**
	 * Serializes information needed by this class on the client side into an integer
	 * @param mag The beam unit to render
	 * @param dist The length of the last beam send
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
