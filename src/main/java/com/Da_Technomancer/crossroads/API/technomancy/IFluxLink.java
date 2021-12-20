package com.Da_Technomancer.crossroads.API.technomancy;

import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.packets.IIntArrayReceiver;
import com.Da_Technomancer.crossroads.API.packets.SendIntArrayToClient;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.ambient.sounds.CRSounds;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.essentials.packets.ILongReceiver;
import com.Da_Technomancer.essentials.tileentities.ILinkTE;
import com.Da_Technomancer.essentials.tileentities.ITickableTileEntity;
import com.Da_Technomancer.essentials.tileentities.LinkHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;

public interface IFluxLink extends ILongReceiver, ILinkTE, IInfoTE, IIntArrayReceiver{

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

	/**
	 * Will only be called on the virtual client
	 * Each int represents one entropy transfer arc
	 * Format for each int:
	 * bits[0,7]: relative x
	 * bits[8,15]: relative y
	 * bits[16,23]: relative z
	 * bits[24,31]: Unused
	 * @return An array of ints representing all transferred entropy to be rendered
	 */
	int[] getRenderedArcs();

	/**
	 * For rendering
	 * Only called on the virtual client side
	 * @return Whether this block should render effects for being near the failure point
	 */
	default boolean renderFluxWarning(){
		return false;
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

	/**
	 * A standard implementation of IFluxLink
	 * Can be used as either a superclass (extenders should pass null/themselves and their type to the constructor), or as an instantiated helper (instantiators should pass any non-null type and themselves to the constructor)
	 * When used as a helper, calls to the IFluxLink & tick methods should be passed to this class, and read() & write() should call readData() and writeData()
	 */
	class FluxHelper extends BlockEntity implements ITickableTileEntity, IFluxLink{

		private static final byte RENDER_ID = 6;

		private final BlockEntity owner;
		private final Behaviour behaviour;
		private final LinkHelper linkHelper;
		private int queuedFlux = 0;
		private int readingFlux = 0;
		public int flux = 0;
		public long lastTick = 0;
		protected Consumer<Integer> fluxTransferHandler;
		private boolean shutDown = false;//Only used if safe mode is enabled in the config
		private int[] rendered = new int[0];

		public FluxHelper(BlockEntityType<?> type, BlockPos pos, BlockState state, @Nullable BlockEntity owner, Behaviour behaviour){
			this(type, pos, state, owner, behaviour, null);
		}

		public FluxHelper(BlockEntityType<?> type, BlockPos pos, BlockState state, @Nullable BlockEntity owner, Behaviour behaviour, @Nullable Consumer<Integer> fluxTransferHandler){
			super(type, pos, state);
			this.owner = owner == null ? this : owner;
			this.behaviour = behaviour;
			linkHelper = new LinkHelper((ILinkTE) this.owner);
			this.fluxTransferHandler = fluxTransferHandler;
		}

		@Override
		public void load(CompoundTag nbt){
			super.load(nbt);
			readData(nbt);
		}

		@Override
		public void saveAdditional(CompoundTag nbt){
			super.saveAdditional(nbt);
			writeData(nbt);
		}

		@Override
		public CompoundTag getUpdateTag(){
			CompoundTag nbt = super.getUpdateTag();
			nbt.putIntArray("rendered_arcs", rendered);
			return nbt;
		}

		public void readData(CompoundTag nbt){
			linkHelper.readNBT(nbt);
			lastTick = nbt.getLong("last_tick");
			flux = nbt.getInt("flux");
			queuedFlux = nbt.getInt("queued_flux");
			readingFlux = nbt.getInt("reading_flux");
			shutDown = nbt.getBoolean("shutdown");
			rendered = nbt.getIntArray("rendered_arcs");
		}

		public void writeData(CompoundTag nbt){
			linkHelper.writeNBT(nbt);
			nbt.putLong("last_tick", lastTick);
			nbt.putInt("flux", flux);
			nbt.putInt("queued_flux", queuedFlux);
			nbt.putInt("reading_flux", readingFlux);
			nbt.putBoolean("shutdown", shutDown);
			nbt.putIntArray("rendered_arcs", rendered);
		}

		/**
		 * Ticks the flux handler
		 * Should be called every tick on the virtual server side
		 */
		@Override
		public void serverTick(){
			ITickableTileEntity.super.serverTick();
			Level world = owner.getLevel();
			long worldTime = world.getGameTime();
			if(worldTime != lastTick){
				lastTick = worldTime;
				if(lastTick % FluxUtil.FLUX_TIME == 0){
					int toTransfer = flux;
					readingFlux = flux;
					flux = queuedFlux;
					queuedFlux = 0;
					if(fluxTransferHandler == null){
						Pair<Integer, int[]> transferResult = FluxUtil.performTransfer(this, linkHelper.getLinksRelative(), toTransfer);
						flux += transferResult.getLeft();
						if(!Arrays.equals(transferResult.getRight(), rendered)){
							rendered = transferResult.getRight();
							CRPackets.sendPacketAround(world, owner.getBlockPos(), new SendIntArrayToClient(RENDER_ID, rendered, owner.getBlockPos()));
						}
					}else{
						fluxTransferHandler.accept(toTransfer);
					}
					owner.setChanged();
					shutDown = FluxUtil.checkFluxOverload(this);
				}
			}
		}

		/**
		 * Ticks the flux handler
		 * Should be called every tick on the virtual client side
		 */
		@Override
		public void clientTick(){
			ITickableTileEntity.super.clientTick();
			//Play sounds
			Level world = owner.getLevel();
			BlockPos pos = owner.getBlockPos();
			if(rendered.length != 0 && world.getGameTime() % FluxUtil.FLUX_TIME == 0 && CRConfig.fluxSounds.get()){
				CRSounds.playSoundClientLocal(world, pos, CRSounds.FLUX_TRANSFER, SoundSource.BLOCKS, 0.4F, 1F);
			}

			//This 5 is the lifetime of the render
			if(world.getGameTime() % 5 == 0 && renderFluxWarning()){
				CRRenderUtil.addArc(world, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, pos.getX() + 0.5F + 2.5F * (float) (Math.random() - 0.5F), pos.getY() + 0.5F + 2.5F * (float) (Math.random() - 0.5F), pos.getZ() + 0.5F + 2.5F * (float) (Math.random() - 0.5F), 3, 1F, FluxUtil.COLOR_CODES[(int) (world.getGameTime() % 3)]);
			}
		}

		public boolean isShutDown(){
			return shutDown;
		}

		@Override
		public boolean allowAccepting(){
			return !isShutDown();
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
			if(owner.getLevel().getGameTime() == lastTick){
				flux += deltaFlux;
			}else{
				queuedFlux += deltaFlux;
			}
			owner.setChanged();
		}

		@Override
		public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
			FluxUtil.addLinkInfo(chat, (ILinkTE) owner);
		}

		@Override
		public Set<BlockPos> getLinks(){
			return linkHelper.getLinksRelative();
		}

		@Override
		public boolean createLinkSource(ILinkTE endpoint, @Nullable Player player){
			return linkHelper.addLink(endpoint, player);
		}

		@Override
		public void removeLinkSource(BlockPos end){
			linkHelper.removeLink(end);
		}

		@Override
		public void receiveLong(byte id, long value, @Nullable ServerPlayer sender){
			linkHelper.handleIncomingPacket(id, value);
		}

		@Override
		public void receiveInts(byte context, int[] message, @Nullable ServerPlayer sendingPlayer){
			if(context == RENDER_ID){
				rendered = message == null ? new int[0] : message;
			}
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
		public BlockEntity getTE(){
			return owner;
		}

		@Override
		public int[] getRenderedArcs(){
			return rendered;
		}
	}
}
