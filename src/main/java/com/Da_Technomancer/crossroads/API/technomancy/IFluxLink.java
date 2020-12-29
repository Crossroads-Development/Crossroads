package com.Da_Technomancer.crossroads.API.technomancy;

import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.essentials.packets.ILongReceiver;
import com.Da_Technomancer.essentials.tileentities.ILinkTE;
import com.Da_Technomancer.essentials.tileentities.LinkHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Set;
import java.util.function.Consumer;

public interface IFluxLink extends ILongReceiver, ILinkTE, IInfoTE{

	int getFlux();

	/**
	 * Used for things like omnimeter readouts and rendering- anything the player would see to smooth out the technical fluctuations in getFlux()
	 * @return The total flux that should be represented to the player.
	 */
	int getReadingFlux();

	/**
	 * Inserts flux to this. Positive values only
	 * @param deltaFlux The increase in flux
	 */
	void addFlux(int deltaFlux);

	/**
	 * Maximum flux this machine can safely contain
	 * @return Flux limit before overloading
	 */
	default int getMaxFlux(){
		return 64;
	}

	@Override
	default int getRange(){
		return 16;
	}

	/**
	 * Will not be called if canAcceptLinks() is false
	 * @return Whether this machine can currently accept flux
	 */
	default boolean allowAccepting(){
		return true;
	}

	/**
	 * Should be state independent
	 * @return Whether this machine can ever accept flux
	 */
	boolean canAcceptLinks();

	@Override
	default Color getColor(){
		return Color.BLACK;
	}

	enum Behaviour{

		SOURCE(1, false),//Flux should be routed away from this TE
		SINK(0, true),//Flux should be routed towards this TE
		NODE(16, true);//Connect in a large network of these TEs

		private final int maxLinks;
		private final boolean allowedToAccept;

		Behaviour(int maxLinks, boolean canAccept){
			this.maxLinks = maxLinks;
			this.allowedToAccept = canAccept;
		}
	}

	class FluxHelper implements IFluxLink{

		private final TileEntity owner;
		private final Behaviour behaviour;
		private final LinkHelper linkHelper;
		private int queuedFlux = 0;
		private int readingFlux = 0;
		public int flux = 0;
		public long lastTick = 0;
		private final Consumer<Integer> fluxTransferHandler;
		private boolean shutDown = false;//Only used if safe mode is enabled in the config

		public FluxHelper(TileEntity owner, Behaviour behaviour){
			this(owner, behaviour, null);
		}

		public FluxHelper(TileEntity owner, Behaviour behaviour, @Nullable Consumer<Integer> fluxTransferHandler){
			this.owner = owner;
			this.behaviour = behaviour;
			linkHelper = new LinkHelper((ILinkTE) owner);
			this.fluxTransferHandler = fluxTransferHandler;
		}

		public void read(CompoundNBT nbt){
			if(nbt.contains("link")){
				//TODO remove: backwards compatibility nbt format
				//Convert from the pre-2.6.0 format used by several flux machines to the format used by LinkHelper
				nbt.putLong("link_0", nbt.getLong("link"));
			}
			linkHelper.readNBT(nbt);
			lastTick = nbt.getLong("last_tick");
			flux = nbt.getInt("flux");
			queuedFlux = nbt.getInt("queued_flux");
			readingFlux = nbt.getInt("reading_flux");
			shutDown = nbt.getBoolean("shutdown");
		}

		public void write(CompoundNBT nbt){
			linkHelper.writeNBT(nbt);
			nbt.putLong("last_tick", lastTick);
			nbt.putInt("flux", flux);
			nbt.putInt("queued_flux", queuedFlux);
			nbt.putInt("reading_flux", readingFlux);
			nbt.putBoolean("shutdown", shutDown);
		}

		/**
		 * Ticks the flux handler
		 * Should be called every tick
		 */
		public void tick(){
			long worldTime = owner.getWorld().getGameTime();
			if(worldTime != lastTick){
				lastTick = worldTime;
				if(lastTick % FluxUtil.FLUX_TIME == 0){
					int toTransfer = flux;
					readingFlux = flux;
					flux = queuedFlux;
					queuedFlux = 0;
					if(fluxTransferHandler == null){
						flux += FluxUtil.performTransfer(this, linkHelper.getLinksRelative(), toTransfer);
					}else{
						fluxTransferHandler.accept(toTransfer);
					}
					owner.markDirty();
					shutDown = FluxUtil.checkFluxOverload(this);
				}
			}
		}

		public boolean isShutDown(){
			return shutDown;
		}

		@Override
		public int getFlux(){
			return flux;
		}

		@Override
		public int getReadingFlux(){
			return readingFlux;
		}

		@Override
		public void addFlux(int deltaFlux){
			if(owner.getWorld().getGameTime() == lastTick){
				flux += deltaFlux;
			}else{
				queuedFlux += deltaFlux;
			}
			owner.markDirty();
		}

		@Override
		public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
			FluxUtil.addLinkInfo(chat, (ILinkTE) owner);
		}

		@Override
		public Set<BlockPos> getLinks(){
			return linkHelper.getLinksRelative();
		}

		@Override
		public boolean createLinkSource(ILinkTE endpoint, @Nullable PlayerEntity player){
			return linkHelper.addLink(endpoint, player);
		}

		@Override
		public void removeLinkSource(BlockPos end){
			linkHelper.removeLink(end);
		}

		@Override
		public void receiveLong(byte id, long value, @Nullable ServerPlayerEntity sender){
			linkHelper.handleIncomingPacket(id, value);
		}

		@Override
		public boolean canBeginLinking(){
			return behaviour != Behaviour.SINK;
		}

		@Override
		public boolean canAcceptLinks(){
			return behaviour.allowedToAccept;
		}

		@Override
		public boolean canLink(ILinkTE otherTE){
			return otherTE instanceof IFluxLink && ((IFluxLink) otherTE).canAcceptLinks();
		}

		@Override
		public int getMaxLinks(){
			return behaviour.maxLinks;
		}

		@Override
		public TileEntity getTE(){
			return owner;
		}
	}
}
