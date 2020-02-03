package com.Da_Technomancer.crossroads.API.beams;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.effects.BeamEffect;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nonnull;
import java.awt.*;

public class BeamManager{

	private static final Tag<Block> PASSABLE = new BlockTags.Wrapper(new ResourceLocation(Crossroads.MODID, "beam_passable"));
	public static final int MAX_DISTANCE = 16;
	public static final int BEAM_TIME = 5;

	private final Direction dir;
	private final BlockPos pos;

	private int dist;
	@Nonnull
	private BeamUnit lastSent = BeamUnit.EMPTY;

	public BeamManager(@Nonnull Direction dir, @Nonnull BlockPos pos){
		this.dir = dir;
		this.pos = pos.toImmutable();
	}

	public boolean emit(@Nonnull BeamUnit mag, World world){
		for(int i = 1; i <= BeamManager.MAX_DISTANCE; i++){
			TileEntity checkTE = world.getTileEntity(pos.offset(dir, i));
			LazyOptional<IBeamHandler> opt;
			if(checkTE != null && (opt = checkTE.getCapability(Capabilities.BEAM_CAPABILITY, dir.getOpposite())).isPresent()){
				opt.orElseThrow(NullPointerException::new).setBeam(mag);
				if(dist != i * i || !mag.equals(lastSent)){
					dist = i;
					lastSent = mag;
					return true;
				}else{
					return false;
				}
			}

			BlockState checkState = world.getBlockState(pos.offset(dir, i));
			if(i == BeamManager.MAX_DISTANCE || solidToBeams(checkState, world, pos.offset(dir, i))){
				if(!mag.isEmpty()){
					EnumBeamAlignments align = EnumBeamAlignments.getAlignment(mag);
					BeamEffect e = align.getEffect();
					e.doBeamEffect(align, mag.getVoid() != 0, Math.min(64, mag.getPower()), world, pos.offset(dir, i), dir.getOpposite());
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

	/**
	 * Determines whether beams should collide with a given block
	 * @param state The state to check
	 * @param world The world this state exists in (Use any non-null world if this is unavailable)
	 * @param pos The position of the passed state in the world (use the origin if this is unavailable)
	 * @return Whether beams should collide with this block
	 */
	public static boolean solidToBeams(BlockState state, World world, BlockPos pos){
		return !state.isAir(world, pos) && !PASSABLE.contains(state.getBlock());
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
		return ((dist - 1) << 24) + (mag.getRGB().getRGB() & 0xFFFFFF) + (Math.min(7, (int) Math.round(Math.sqrt(mag.getPower())) - 1) << 28);
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
		return packet == 0 ? Triple.of(Color.BLACK, 0, 0) : Triple.of(Color.decode(Integer.toString(packet & 0xFFFFFF)), ((packet >> 24) & 15) + 1, (packet >> 28) + 1);
	}

	@Nonnull
	public BeamUnit getLastSent(){
		return lastSent;
	}
}
