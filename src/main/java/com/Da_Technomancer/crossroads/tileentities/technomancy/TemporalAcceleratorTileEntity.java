package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.API.packets.ILongReceiver;
import com.Da_Technomancer.crossroads.API.packets.CrossroadsPackets;
import com.Da_Technomancer.crossroads.API.packets.SendLongToClient;
import com.Da_Technomancer.crossroads.API.packets.SendPlayerTickCountToClient;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.blocks.technomancy.TemporalAccelerator;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ITickableTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Random;

public class TemporalAcceleratorTileEntity extends TileEntity implements ITickableTileEntity, IInfoTE, ILongReceiver{

	public static final int FLUX_MULT = 1;
	private static final Random RAND = new Random();
	private final IBeamHandler magicHandler = new BeamHandler();
	private Direction facing;
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

	@Override
	public void addInfo(ArrayList<String> chat, PlayerEntity player, @Nullable Direction side, BlockRayTraceResult hit){
		chat.add("Temporal Entropy: " + EntropySavedData.getEntropy(world) + "%");
		chat.add("Size: " + size);
		chat.add("Applied Boost: " + (intensity < 0 ? Math.max(intensity, -16) + "/16" : "+" + (int) (Math.pow(2, intensity / 4D) - 1)));
	}

	public Region getRegion(){
		if(region == null){
			region = new Region(size, pos, getFacing());
		}

		return region;
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayerEntity sendingPlayer){
		if(identifier == 4){
			size = (int) message;
		}
	}

	@OnlyIn(Dist.CLIENT)
	public int getSize(){
		return size;
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
		CrossroadsPackets.network.sendToAllAround(new SendLongToClient((byte) 4, size, pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		return size;
	}

	private Direction getFacing(){
		if(facing == null){
			BlockState state = world.getBlockState(pos);
			if(!(state.getBlock() instanceof TemporalAccelerator)){
				invalidate();
				return Direction.DOWN;
			}
			facing = state.get(EssentialsProperties.FACING);
		}

		return facing;
	}

	@Override
	public void tick(){
		if(!world.isRemote && world.getGameTime() != lastRunTick){
			if(EntropySavedData.getSeverity(world).getRank() >= EntropySavedData.Severity.DESTRUCTIVE.getRank()){
				FluxUtil.overloadFlux(world, pos);
				return;
			}

			lastRunTick = world.getGameTime();
			int extraTicks = (int) Math.pow(2, intensity / 4D) - 1;
			if(extraTicks > 0){
				//Perform entity effect
				ArrayList<Entity> ents = (ArrayList<Entity>) world.getEntitiesWithinAABB(Entity.class, getRegion().getBB());

				for(Entity ent : ents){
					if(ent.updateBlocked){
						continue;
					}
					if(ent instanceof ServerPlayerEntity){
						CrossroadsPackets.network.sendTo(new SendPlayerTickCountToClient(extraTicks + 1), (ServerPlayerEntity) ent);
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
							BlockPos effectPos = new BlockPos(x, y, z);
							BlockState state = world.getBlockState(effectPos);
							TileEntity te = world.getTileEntity(effectPos);
							if(te instanceof ITickableTileEntity){
								for(int run = 0; run < extraTicks; run++){
									((ITickableTileEntity) te).update();
								}
							}
							//Blocks have a 16^3/randomTickSpeed chance of a random tick each game tick in vanilla
							if(state.getBlock().getTickRandomly() && RAND.nextInt(16 * 16 * 16 / world.getGameRules().getInt("randomTickSpeed")) < extraTicks){
								state.getBlock().randomTick(world, effectPos, state, world.rand);
							}
						}
					}
				}
			}

			//Create flux
			if(world.getGameTime() % FluxUtil.FLUX_TIME == 0){
				if(intensity < 0){
					//Stopped time
					if(intensity <= -16){
						//The effect of this is that while time is fully stopped, the flux produced increases, but the flux creation is reset once time is again allowed to flow
						duration += 1;
						EntropySavedData.addEntropy(world, FLUX_MULT * size * duration);
					}else{
						//Slowed time
						duration = 0;
						EntropySavedData.addEntropy(world, FLUX_MULT * size);
					}
				}else{
					//Sped up time
					duration = 0;
					EntropySavedData.addEntropy(world, FLUX_MULT * size * extraTicks);
				}
				intensity = 0;
				markDirty();
			}
		}
	}

	@Override
	public boolean hasCapability(Capability<?> cap, Direction side){
		if(cap == Capabilities.BEAM_CAPABILITY && (side == null || side == getFacing().getOpposite())){
			return true;
		}

		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.BEAM_CAPABILITY && (side == null || side == getFacing().getOpposite())){
			return (T) magicHandler;
		}

		return super.getCapability(cap, side);
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putInt("intensity", intensity);
		nbt.putInt("size", size);
		nbt.putInt("duration", duration);
		nbt.putLong("last_run", lastRunTick);

		return nbt;
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		intensity = nbt.getInt("intensity");
		size = nbt.getInt("size");
		duration = nbt.getInt("duration");
		lastRunTick = nbt.getLong("last_run");
	}

	@Override
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
		nbt.putInt("size", size);
		return nbt;
	}

	private class BeamHandler implements IBeamHandler{

		@Override
		public void setMagic(BeamUnit mag){
			if(mag != null && EnumBeamAlignments.getAlignment(mag) == EnumBeamAlignments.TIME){
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

		public Region(int size, BlockPos pos, Direction dir){
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
