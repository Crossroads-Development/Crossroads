package com.Da_Technomancer.crossroads.API.magic;

import java.awt.Color;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.effects.IEffect;
import com.Da_Technomancer.crossroads.API.enums.MagicElements;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BeamManager{
	
	private final EnumFacing dir;
	private final World world;
	private final BlockPos pos;
	
	private BlockPos end;
	
	/**I know this can be calculated from pos and end,
	 * But this is simpler and doesn't require using Math.sqrt
	 */
	private int dist;
	private MagicUnit lastSent;
	
	public BeamManager(EnumFacing dir, BlockPos pos, World world){
		this.dir = dir;
		this.world = world;
		this.pos = pos.toImmutable();
	}
	
	public boolean emit(@Nullable MagicUnit mag){
		for(int i = 1; i <= IMagicHandler.MAX_DISTANCE; i++){
			if(world.getTileEntity(pos.offset(dir, i)) != null && world.getTileEntity(pos.offset(dir, i)).hasCapability(Capabilities.MAGIC_HANDLER_CAPABILITY, dir.getOpposite())){
				if(!pos.offset(dir, i).equals(end)){
					wipe();
					end = pos.offset(dir, i);
				}
				
				world.getTileEntity(end).getCapability(Capabilities.MAGIC_HANDLER_CAPABILITY, dir.getOpposite()).setMagic(mag);
				if(dist != i || (mag == null ? lastSent != null : !mag.equals(lastSent))){
					dist = i;
					lastSent = mag;
					return true;
				}else{
					return false;
				}
			}
			
			if(i == IMagicHandler.MAX_DISTANCE || !world.getBlockState(pos.offset(dir, i)).getBlock().isAir(world.getBlockState(pos.offset(dir, i)), world, pos.offset(dir, i))){
				wipe();
				if(mag != null && mag.getRGB() != null){
					IEffect e = MagicElements.getElement(mag).getMixEffect(mag.getRGB());
					if(e != null){
						e.doEffect(world, pos.offset(dir, i), mag.getPower());
					}
				}
				boolean holder = dist != i || (mag == null ? lastSent != null : !mag.equals(lastSent));
				dist = i;
				lastSent = mag;
				return holder;
			}
		}
		
		return false;
	}
	
	private void wipe(){
		if(end != null && world.getTileEntity(end) != null && world.getTileEntity(end).hasCapability(Capabilities.MAGIC_HANDLER_CAPABILITY, dir.getOpposite())){
			world.getTileEntity(end).getCapability(Capabilities.MAGIC_HANDLER_CAPABILITY, dir.getOpposite()).setMagic(null);
		}
	}
	
	public int getPacket(){
		return lastSent == null || lastSent.getRGB() == null ? 0 : ((dist - 1) << 24) + (lastSent.getRGB().getRGB() & 16777215) + (Math.min((int) Math.sqrt(lastSent.getPower()) - 1, 7) << 28);
	}

	@Nullable
	public static Triple<Color, Integer, Integer> getTriple(int packet){
		return packet == 0 ? null : Triple.of(Color.decode(Integer.toString(packet & 16777215)), ((packet >> 24) & 15) + 1, (packet >> 28) + 1);
	}
}
