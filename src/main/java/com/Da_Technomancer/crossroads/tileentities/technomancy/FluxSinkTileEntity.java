package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.API.technomancy.IFluxLink;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.packets.SendLongToClient;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@ObjectHolder(Crossroads.MODID)
public class FluxSinkTileEntity extends TileEntity implements IFluxLink, ITickableTileEntity{

	@ObjectHolder("flux_sink")
	private static TileEntityType<FluxSinkTileEntity> type = null;

	private static final int CAPACITY = 10_000;
	private int flux = 0;
	private boolean running = false;

	public FluxSinkTileEntity(){
		super(type);
	}

	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		chat.add(new TranslationTextComponent("tt.crossroads.flux_sink.desc"));
		FluxUtil.addFluxInfo(chat, this, -1);
	}

	@Override
	public void tick(){
		if(world.getGameTime() % FluxUtil.FLUX_TIME == 0 && isRunning() && flux != 0){
			flux = 0;
			markDirty();
		}
	}

	public boolean isRunningForRender(){
		//We don't use the isRunning() method for rendering, as at 60FPS, that would invalidate the cache 3 times in one tick
		return running;
	}

	private boolean isRunning(){
		//We cache the value of whether this is running, and only recheck once every 5 seconds
		if(world.getGameTime() % 100 == 0){
			boolean prevRunning = running;
			running = false;
			//expects a beacon below it, with any number of air gaps
			BlockPos.MutableBlockPos mutPos = new BlockPos.MutableBlockPos(pos);
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
				CRPackets.sendPacketAround(world, pos, new SendLongToClient(1, running ? 1 : 0, pos));
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
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayerEntity sendingPlayer){
		//We don't start links, only receive them

		//Receive running info
		if(identifier == 1){
			running = message != 0;
		}
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		flux = nbt.getInt("flux");
		running = nbt.getBoolean("running");
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putInt("flux", flux);
		nbt.putBoolean("running", running);
		return nbt;
	}

	@Override
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
		nbt.putBoolean("running", running);
		return nbt;
	}

	@Override
	public int getFlux(){
		return flux;
	}

	@Override
	public void setFlux(int newFlux){
		flux = newFlux;
		markDirty();
	}

	@Override
	public Set<BlockPos> getLinks(){
		return new HashSet<>(0);
	}

	@Override
	public int getMaxFlux(){
		return CAPACITY;
	}

	@Override
	public Behaviour getBehaviour(){
		return Behaviour.SINK;//This will trigger almost all the behaviour changes we need without overriding
	}
}
