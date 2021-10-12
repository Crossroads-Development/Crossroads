package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.API.technomancy.IFluxLink;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.ambient.sounds.CRSounds;
import com.Da_Technomancer.essentials.packets.SendLongToClient;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;

import com.Da_Technomancer.crossroads.API.technomancy.IFluxLink.Behaviour;

@ObjectHolder(Crossroads.MODID)
public class FluxSinkTileEntity extends IFluxLink.FluxHelper{

	@ObjectHolder("flux_sink")
	public static BlockEntityType<FluxSinkTileEntity> type = null;

	private static final int CAPACITY = 256;

	private boolean running = false;
	private long runningStartTime;//Used for rendering
	public static final float STARTUP_TIME = 60;//Used for rendering
	public final int[] renderPortals = new int[] {-1, -1};//Used for rendering; indices of the floating portals to render an entropy transfer into, -1 means no transfer

	public FluxSinkTileEntity(BlockPos pos, BlockState state){
		super(type, null, Behaviour.SINK, null);
		this.fluxTransferHandler = this::consumeFlux;
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		chat.add(new TranslatableComponent("tt.crossroads.flux_sink.desc"));
		FluxUtil.addFluxInfo(chat, this, -1);
	}

	@Override
	public void tick(){
		super.tick();
		if(level.isClientSide){
			//Create client-side entropy effects
			//By doing this on the individual clients, we avoid needing extra packets
			if(level.getGameTime() % FluxUtil.FLUX_TIME == 0){
				//Sound
				CRSounds.playSoundClientLocal(level, worldPosition, CRSounds.FLUX_TRANSFER, SoundSource.BLOCKS, 0.4F, 1F);
				//Rendered arcs
				if(level.getGameTime() % (FluxUtil.FLUX_TIME * 4) == 0){
					if(getRunDuration() > STARTUP_TIME){
						renderPortals[0] = level.random.nextInt(8);
						renderPortals[1] = level.random.nextInt(8);
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
		return running ? level.getGameTime() - runningStartTime : -1;
	}

	@Override
	public AABB getRenderBoundingBox(){
		return new AABB(worldPosition.offset(-3, -3, -3), worldPosition.offset(4, 4, 4));
	}

	private boolean isRunning(){
		//We cache the value of whether this is running, and only recheck once every 5 seconds
		if(level.getGameTime() % 100 == 0){
			boolean prevRunning = running;
			running = false;
			//expects a beacon below it, with any number of air gaps
			BlockPos.MutableBlockPos mutPos = new BlockPos.MutableBlockPos(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
			do{
				mutPos.move(Direction.DOWN);
				BlockState state = level.getBlockState(mutPos);
				if(state.getBlock() == Blocks.BEACON){
					running = true;
				}else if(!canBeaconBeamPass(state, level, mutPos)){
					return false;
				}
			}while(!running && mutPos.getY() > 1);
			if(prevRunning != running){
				//Notify the clients
				CRPackets.sendPacketAround(level, worldPosition, new SendLongToClient(1, running ? level.getGameTime() : 0, worldPosition));
				setChanged();
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
	private static boolean canBeaconBeamPass(BlockState state, Level world, BlockPos pos){
		//We don't actually know where the beacon is.
		//pos.down() is an incorrect value, but all current implementations ignore it (and should have sanity checking anyway)
		float[] colMult = state.getBeaconColorMultiplier(world, pos, pos.below());
		return colMult != null || state.getLightBlock(world, pos) < 15 || state.getBlock() == Blocks.BEDROCK;
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		running = nbt.getBoolean("running");
		runningStartTime = nbt.getLong("run_time");
	}

	@Override
	public CompoundTag save(CompoundTag nbt){
		super.save(nbt);
		nbt.putBoolean("running", running);
		nbt.putLong("run_time", runningStartTime);
		return nbt;
	}

	@Override
	public CompoundTag getUpdateTag(){
		CompoundTag nbt = super.getUpdateTag();
		nbt.putBoolean("running", running);
		nbt.putLong("run_time", runningStartTime);
		return nbt;
	}

	@Override
	public int getMaxFlux(){
		return CAPACITY;
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayer sendingPlayer){
		super.receiveLong(identifier, message, sendingPlayer);
		//Receive running info
		if(identifier == 1){
			runningStartTime = message;
			running = message != 0;
		}
	}
}
