package com.Da_Technomancer.crossroads.API.magic;

import java.awt.Color;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.effects.IEffect;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class BeamManager{

	public static int beamStage = 2;
	public static boolean resetVisual = false;
	public static long cycleNumber;

	private final EnumFacing dir;
	private final BlockPos pos;

	private BlockPos end;
	private int dist;//This can be calculated from pos and end, but this way it doesn't require using Math.sqrt().
	private MagicUnit lastSent;
	private MagicUnit lastFullSent;
	public static final int MAX_DISTANCE = 16;
	public static final int BEAM_TIME = 5;

	public BeamManager(@Nonnull EnumFacing dir, @Nonnull BlockPos pos){
		this.dir = dir;
		this.pos = pos.toImmutable();
	}

	public void emit(@Nullable MagicUnit mag, World world){
		for(int i = 1; i <= BeamManager.MAX_DISTANCE; i++){
			TileEntity checkTE = world.getTileEntity(pos.offset(dir, i));
			if(checkTE != null && checkTE.hasCapability(Capabilities.MAGIC_HANDLER_CAPABILITY, dir.getOpposite())){
				if(!pos.offset(dir, i).equals(end)){
					wipe(world);
					end = pos.offset(dir, i);
				}

				checkTE.getCapability(Capabilities.MAGIC_HANDLER_CAPABILITY, dir.getOpposite()).setMagic(mag);
				if(dist != i || (mag == null ? lastSent != null : !mag.equals(lastSent))){
					dist = i;
					lastSent = mag;
					if(lastSent != null){
						lastFullSent = lastSent;
					}

					ModPackets.network.sendToAllAround(new SendIntToClient(dir.getIndex(), getPacket(), pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
					return;
				}else{
					if(resetVisual){
						ModPackets.network.sendToAllAround(new SendIntToClient(dir.getIndex(), getPacket(), pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
					}
					return;
				}
			}

			IBlockState checkState = world.getBlockState(pos.offset(dir, i));
			if(i == BeamManager.MAX_DISTANCE || !checkState.getBlock().isAir(checkState, world, pos.offset(dir, i))){
				wipe(world);
				end = pos.offset(dir, i);
				if(mag != null && mag.getRGB() != null){
					IEffect e = EnumMagicElements.getElement(mag).getMixEffect(mag.getRGB());
					if(e != null){
						e.doEffect(world, pos.offset(dir, i), Math.min(64, mag.getPower()));
					}
				}
				boolean holder = dist != i || (mag == null ? lastSent != null : !mag.equals(lastSent));
				dist = i;
				lastSent = mag;
				if(lastSent != null){
					lastFullSent = lastSent;
				}
				if(holder || resetVisual){
					ModPackets.network.sendToAllAround(new SendIntToClient(dir.getIndex(), getPacket(), pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
				}
				return;
			}
		}

		if(resetVisual){
			ModPackets.network.sendToAllAround(new SendIntToClient(dir.getIndex(), getPacket(), pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		}
	}

	private void wipe(World world){
		if(end != null){
			TileEntity te = world.getTileEntity(end);
			if(te != null && te.hasCapability(Capabilities.MAGIC_HANDLER_CAPABILITY, dir.getOpposite())){
				te.getCapability(Capabilities.MAGIC_HANDLER_CAPABILITY, dir.getOpposite()).setMagic(null);
			}
		}
	}

	public int getPacket(){
		return lastSent == null || lastSent.getRGB() == null ? 0 : ((dist - 1) << 24) + (lastSent.getRGB().getRGB() & 0xFFFFFF) + (Math.min(7, (int) Math.round(Math.sqrt(lastSent.getPower())) - 1) << 28);
	}

	@Nullable
	public static Triple<Color, Integer, Integer> getTriple(int packet){
		return packet == 0 ? null : Triple.of(Color.decode(Integer.toString(packet & 0xFFFFFF)), ((packet >> 24) & 15) + 1, (packet >> 28) + 1);
	}

	@Nullable
	public MagicUnit getLastFullSent(){
		return lastFullSent;
	}

	@Nullable
	public MagicUnit getLastSent(){
		return lastSent;
	}
}
