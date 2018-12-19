package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendPlayerTickCountToClient;
import com.Da_Technomancer.crossroads.API.technomancy.FluxTE;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.blocks.technomancy.TemporalAccelerator;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Random;

public class TemporalAcceleratorTileEntity extends FluxTE{

	private static final Random RAND = new Random();
	private final IBeamHandler magicHandler = new BeamHandler();
	private EnumFacing facing;
	private int intensity = 0;
	private int size = 1;
	private Region region;
	private int duration = 0;
	//Used to prevent accelerators affecting each other
	private long lastRunTick;

	public void resetCache(){
		facing = null;
		region = null;
	}

	public Region getRegion(){
		if(region == null){
			region = new Region(size, pos, getFacing());
		}

		return region;
	}

	public boolean stoppingTime(){
		return intensity < 0 && RAND.nextInt(16) < -intensity;//Random is used to (on average) slow time instead of stopping it at small intensities
	}

	public int adjustSize(){
		size += 2;
		if(size > 7){
			size = 1;
		}
		region = null;
		markDirty();
		return size;
	}

	private EnumFacing getFacing(){
		if(facing == null){
			IBlockState state = world.getBlockState(pos);
			if(!(state.getBlock() instanceof TemporalAccelerator)){
				invalidate();
				return EnumFacing.DOWN;
			}
			facing = state.getValue(EssentialsProperties.FACING);
		}

		return facing;
	}

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, @Nullable EnumFacing side, float hitX, float hitY, float hitZ){
		super.addInfo(chat, player, side, hitX, hitY, hitZ);
		chat.add("Size: " + size);
		chat.add("Applied Boost: " + (intensity < 0 ? Math.max(intensity, -16) + "/16" : "+" + (int) (Math.pow(2, (int) (intensity / 4)) - 1)));
	}

	@Override
	public void update(){
		super.update();

		if(!world.isRemote && world.getTotalWorldTime() != lastRunTick){
			lastRunTick = world.getTotalWorldTime();
			int extraTicks = (int) Math.pow(2, (int) (intensity / 4)) - 1;
			if(extraTicks > 0){
				//Perform entity effect
				ArrayList<Entity> ents = (ArrayList<Entity>) world.getEntitiesWithinAABB(Entity.class, getRegion().getBB());

				for(Entity ent : ents){
					if(ent.updateBlocked){
						continue;
					}
					if(ent instanceof EntityPlayerMP){
						ModPackets.network.sendTo(new SendPlayerTickCountToClient(extraTicks + 1), (EntityPlayerMP) ent);
					}
					for(int i = 0; i < extraTicks; i++){
						ent.onUpdate();
					}
				}


				//Perform tile entity effect
				AxisAlignedBB bb = getRegion().getBB();
				for(int x = (int) bb.minX; x < (int) bb.maxX; x++){
					for(int y = (int) bb.minY; y < (int) bb.maxY; y++){
						for(int z = (int) bb.minZ; z < (int) bb.maxZ; z++){
							TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
							if(te instanceof ITickable){
								for(int run = 0; run < extraTicks; run++){
									((ITickable) te).update();
								}
							}
						}
					}
				}
			}

			//Create flux
			if(world.getTotalWorldTime() % FluxUtil.FLUX_TIME == 0){
				if(intensity < 0){
					if(intensity <= -16){
						//The effect of this is that while time is fully stopped, the flux produced increases, but the flux creation is reset once time is again allowed to flow
						duration += 1;
						addFlux(size * duration);
					}else{
						duration = 0;
						addFlux(size);
					}
				}else{
					duration = 0;
					addFlux(size * extraTicks);
				}
				intensity = 0;
				markDirty();
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.MAGIC_CAPABILITY && (side == null || side == getFacing().getOpposite())){
			return (T) magicHandler;
		}

		return super.getCapability(cap, side);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("intensity", intensity);
		nbt.setInteger("size", size);
		nbt.setInteger("duration", duration);
		nbt.setLong("last_run", lastRunTick);

		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		intensity = nbt.getInteger("intensity");
		size = nbt.getInteger("size");
		duration = nbt.getInteger("duration");
		lastRunTick = nbt.getLong("last_run");
	}

	@Override
	public boolean isFluxEmitter(){
		return true;
	}

	@Override
	public boolean isFluxReceiver(){
		return false;
	}

	private class BeamHandler implements IBeamHandler{

		@Override
		public void setMagic(BeamUnit mag){
			if(mag != null && (true || EnumBeamAlignments.getAlignment(mag) == EnumBeamAlignments.TIME)){//TODO temp for testing
				if(mag.getVoid() == 0){
					intensity += mag.getPower();//Speed up time
				}else{
					intensity -= mag.getPower();//Slow down time
				}
				markDirty();
			}
		}
	}

	public static class Region{

		private final int range;
		private final BlockPos center;
		private final AxisAlignedBB bb;

		public Region(int size, BlockPos pos, EnumFacing dir){
			center = pos.offset(dir, size / 2 + 1);
			range = size / 2;
			bb = new AxisAlignedBB(center.add(-range, -range, -range), center.add(range + 1, range + 1, range + 1));
		}

		public boolean inRegion(BlockPos pos){
			return Math.abs(pos.getX() - center.getX()) <= range && Math.abs(pos.getY() - center.getY()) <= range && Math.abs(pos.getZ() - center.getZ()) <= range;
		}

		public AxisAlignedBB getBB(){
			return bb;
		}
	}
}
