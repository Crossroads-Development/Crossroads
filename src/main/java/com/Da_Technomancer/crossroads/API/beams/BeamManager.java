package com.Da_Technomancer.crossroads.API.beams;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.effects.IEffect;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

public class BeamManager{

	public static int beamStage = 2;
	public static long cycleNumber;

	private final EnumFacing dir;
	private final BlockPos pos;

	private int dist;
	private BeamUnit lastSent;
	public static final int MAX_DISTANCE = 16;
	public static final int BEAM_TIME = 5;

	public BeamManager(@Nonnull EnumFacing dir, @Nonnull BlockPos pos){
		this.dir = dir;
		this.pos = pos.toImmutable();
	}

	public boolean emit(@Nullable BeamUnit mag, World world){
		for(int i = 1; i <= BeamManager.MAX_DISTANCE; i++){
			TileEntity checkTE = world.getTileEntity(pos.offset(dir, i));
			if(checkTE != null && checkTE.hasCapability(Capabilities.MAGIC_HANDLER_CAPABILITY, dir.getOpposite())){
				checkTE.getCapability(Capabilities.MAGIC_HANDLER_CAPABILITY, dir.getOpposite()).setMagic(mag);
				if(dist != i * i || (mag == null ? lastSent != null : !mag.equals(lastSent))){
					dist = i;
					lastSent = mag;
					return true;
				}else{
					return false;
				}
			}

			IBlockState checkState = world.getBlockState(pos.offset(dir, i));
			if(i == BeamManager.MAX_DISTANCE || solidToBeams(checkState, world, pos.offset(dir, i))){
				if(mag != null){
					IEffect e = EnumBeamAlignments.getAlignment(mag).getMixEffect(mag.getRGB());
					if(e != null){
						e.doEffect(world, pos.offset(dir, i), Math.min(64, mag.getPower()));
					}
				}
				if(dist != i || (mag == null ? lastSent != null : !mag.equals(lastSent))){
					dist = i;
					lastSent = mag;
					return true;
				}
				return false;
			}
		}

		return false;
	}

	public static boolean solidToBeams(IBlockState state, World world, BlockPos pos){
		return !state.getBlock().isAir(state, world, pos) && !(state.getBlock() instanceof IBeamTransparent);
	}

	public static int toPacket(BeamUnit mag, int dist){
		if(mag == null){
			return 0;
		}
		return ((dist - 1) << 24) + (mag.getRGB().getRGB() & 0xFFFFFF) + (Math.min(7, (int) Math.round(Math.sqrt(mag.getPower())) - 1) << 28);
	}

	public int genPacket(){
		return toPacket(lastSent, dist);
	}

	public static Triple<Color, Integer, Integer> getTriple(int packet){
		return packet == 0 ? Triple.of(Color.BLACK, 0, 0) : Triple.of(Color.decode(Integer.toString(packet & 0xFFFFFF)), ((packet >> 24) & 15) + 1, (packet >> 28) + 1);
	}

	@Nullable
	public BeamUnit getLastSent(){
		return lastSent;
	}
}
