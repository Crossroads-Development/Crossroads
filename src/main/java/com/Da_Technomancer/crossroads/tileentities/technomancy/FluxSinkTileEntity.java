package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.API.technomancy.IFluxLink;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.particles.sounds.CRSounds;
import com.Da_Technomancer.essentials.packets.SendLongToClient;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;

@ObjectHolder(Crossroads.MODID)
public class FluxSinkTileEntity extends IFluxLink.FluxHelper{

	@ObjectHolder("flux_sink")
	public static TileEntityType<FluxSinkTileEntity> type = null;

	private static final int CAPACITY = 256;

	private boolean running = false;
	private long runningStartTime;//Used for rendering
	public static final float STARTUP_TIME = 60;//Used for rendering
	public final int[] renderPortals = new int[] {-1, -1};//Used for rendering; indices of the floating portals to render an entropy transfer into, -1 means no transfer

	public FluxSinkTileEntity(){
		super(type, null, Behaviour.SINK, null);
		this.fluxTransferHandler = this::consumeFlux;
	}

	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		chat.add(new TranslationTextComponent("tt.crossroads.flux_sink.desc"));
		FluxUtil.addFluxInfo(chat, this, -1);
	}

	@Override
	public void tick(){
		super.tick();
		if(world.isRemote){
			//Create client-side entropy effects
			//By doing this on the individual clients, we avoid needing extra packets
			if(world.getGameTime() % FluxUtil.FLUX_TIME == 0){
				//Sound
				CRSounds.playSoundClientLocal(world, pos, CRSounds.FLUX_TRANSFER, SoundCategory.BLOCKS, 0.4F, 1F);
				//Rendered arcs
				if(world.getGameTime() % (FluxUtil.FLUX_TIME * 4) == 0){
					if(getRunDuration() > STARTUP_TIME){
						renderPortals[0] = world.rand.nextInt(8);
						renderPortals[1] = world.rand.nextInt(8);
						if(renderPortals[0] == renderPortals[1]){
							renderPortals[1] = (renderPortals[0] + 1) % 8;
						}
					}else{
						renderPortals[0] = renderPortals[1] = -1;
					}
				}
			}
		}
	}

	private void consumeFlux(int fluxIn){
		if(isRunning()){
			int remainder = fluxIn - Math.min(CAPACITY, fluxIn);
			this.flux += remainder;
		}else{
			this.flux += fluxIn;
		}
	}

	/**
	 * Used for rendering. Doesn't tamper with the cache- unlike isRunning()
	 * @return Gets the time since this started running, or -1 if this is not running
	 */
	public float getRunDuration(){
		return running ? world.getGameTime() - runningStartTime : -1;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox(){
		return new AxisAlignedBB(pos.add(-3, -3, -3), pos.add(4, 4, 4));
	}

	private boolean isRunning(){
		//We cache the value of whether this is running, and only recheck once every 5 seconds
		if(world.getGameTime() % 100 == 0){
			boolean prevRunning = running;
			running = false;
			//expects a beacon below it, with any number of air gaps
			BlockPos.Mutable mutPos = new BlockPos.Mutable(pos.getX(), pos.getY(), pos.getZ());
			do{
				mutPos.move(Direction.DOWN);
				BlockState state = world.getBlockState(mutPos);
				if(state.getBlock() == Blocks.BEACON){
					running = true;
				}else if(!canBeaconBeamPass(state, world, mutPos)){
					return false;
				}
			}while(!running && mutPos.getY() > 1);
			if(prevRunning != running){
				//Notify the clients
				CRPackets.sendPacketAround(world, pos, new SendLongToClient(1, running ? world.getGameTime() : 0, pos));
				markDirty();
			}
		}
		return running;
	}

	/**
	 * Finds whether a beacon beam can pass through a block
	 * Based on BeaconTileEntity.tick()
	 * @param state The blockstate to check if beacons can pass
	 * @param world The world
	 * @param pos The position of the state- not of the calling flux sink
	 * @return Whether beacons can treat a block as air
	 */
	private static boolean canBeaconBeamPass(BlockState state, World world, BlockPos pos){
		//We don't actually know where the beacon is.
		//pos.down() is an incorrect value, but all current implementations ignore it (and should have sanity checking anyway)
		float[] colMult = state.getBeaconColorMultiplier(world, pos, pos.down());
		return colMult != null || state.getOpacity(world, pos) < 15 || state.getBlock() == Blocks.BEDROCK;
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt){
		super.read(state, nbt);
		running = nbt.getBoolean("running");
		runningStartTime = nbt.getLong("run_time");
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putBoolean("running", running);
		nbt.putLong("run_time", runningStartTime);
		return nbt;
	}

	@Override
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
		nbt.putBoolean("running", running);
		nbt.putLong("run_time", runningStartTime);
		return nbt;
	}

	@Override
	public int getMaxFlux(){
		return CAPACITY;
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayerEntity sendingPlayer){
		super.receiveLong(identifier, message, sendingPlayer);
		//Receive running info
		if(identifier == 1){
			runningStartTime = message;
			running = message != 0;
		}
	}
}
