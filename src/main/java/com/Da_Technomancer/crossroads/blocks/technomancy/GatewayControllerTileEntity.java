package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.beams.BeamUnit;
import com.Da_Technomancer.crossroads.api.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.api.beams.IBeamCapable;
import com.Da_Technomancer.crossroads.api.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.api.packets.CRPackets;
import com.Da_Technomancer.crossroads.api.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.api.rotary.IAxleCapable;
import com.Da_Technomancer.crossroads.api.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.api.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.api.technomancy.*;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.essentials.api.ILinkTE;
import com.Da_Technomancer.essentials.api.MathUtil;
import com.Da_Technomancer.essentials.api.packets.SendLongToTE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Set;

public class GatewayControllerTileEntity extends GatewayControllerAbstractTileEntity implements IFluxLink, IBeamCapable, IAxleCapable{

	public static final BlockEntityType<GatewayControllerTileEntity> TYPE = CRTileEntity.createType(GatewayControllerTileEntity::new, CRBlocks.gatewayController);
	public static final int INERTIA = 0;//Moment of inertia
	public static final int FLUX_PER_CYCLE = 4;
	private static final float ROTATION_SPEED = (float) Math.PI / 40F;//Rate of convergence between angle and axle 'speed' in radians/tick. Yes, this terminology is confusing

	//These fields are only correct for isActive() is true
	//They will not necessarily be null/empty/0 if this inactive- always check isActive()
	private double rotaryEnergy = 0;//Rotary energy
	private float angle = 0;//Used for rendering and dialing chevrons. Because it's used for logic, we don't use the master axis angle syncing, which is render-based
	private float clientAngle = 0;//Angle on the client. On the server, acts as a record of value sent to client
	private float clientW = 0;//Speed on the client (post adjustment). On the server, acts as a record of value sent to client
	private float referenceSpeed = 0;//Speed which angles will be defined relative to on the server
	//Flux related fields
	private final SimpleFluxLink fluxHelper;

	private IAxleHandler axleHandler = null;
	private IBeamHandler beamHandler = null;

	public GatewayControllerTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
		fluxHelper = new SimpleFluxLink(TYPE, pos, state, this, Behaviour.SOURCE);
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		if(isActive() && address != null){
			//Address of this gateway
			String[] names = new String[4];
			for(int i = 0; i < 4; i++){
				EnumBeamAlignments align = address.getEntry(i);
				if(align == null){
					align = EnumBeamAlignments.NO_MATCH;//Should never ahppen
				}
				names[i] = align.getLocalName(false);
			}
			chat.add(Component.translatable("tt.crossroads.gateway.chevron.address", names[0], names[1], names[2], names[3]));

			//Chevrons
			boolean addedPotential = false;//Whether we have added the name of the potentially next alignment to dial
			for(int i = 0; i < 4; i++){
				if(chevrons[i] == null){
					if(addedPotential){
						names[i] = MiscUtil.localize("tt.crossroads.gateway.chevron.none");
					}else{
						addedPotential = true;
						names[i] = String.format("[%s]", GatewayAddress.getLegalEntry(Math.round(angle * 8F / 2F / (float) Math.PI)).getLocalName(false));
					}
				}else{
					names[i] = chevrons[i].getLocalName(false);
				}
			}
			chat.add(Component.translatable("tt.crossroads.gateway.chevron.dialed", names[0], names[1], names[2], names[3]));
			resetHandlers();
			RotaryUtil.addRotaryInfo(chat, axleHandler, true);
			FluxUtil.addFluxInfo(chat, this, chevrons[3] != null && origin ? FLUX_PER_CYCLE : 0);
		}
	}

	/**
	 * Used for rendering
	 * @param partialTicks The partial ticks in [0, 1]
	 * @return The angle of the octagonal ring used for dialing
	 */
	public double getAngle(float partialTicks){
		return calcAngleChange(clientW, clientAngle) * partialTicks + clientAngle;
	}

	//Gateway connection management

	@Override
	public void undial(GatewayAddress other){
		GatewayAddress prevDialed = new GatewayAddress(chevrons);
		if(prevDialed.fullAddress() && prevDialed.equals(other)){
			resyncRotaryToClient();
			referenceSpeed = 0;
		}
		super.undial(other);
	}

	//Multiblock management

	@Override
	public void dismantle(){
		if(!level.isClientSide && isActive()){
			axleHandler = null;
			beamHandler = null;
		}
		super.dismantle();
	}

	/**
	 * Attempts to assemble this into a multiblock
	 * This will only work if this is the top-center block
	 * @return Whether this succeeded at forming the multiblock
	 */
	public boolean assemble(Player player){
		if(super.assemble(player)){
			//Resetting the handlers to null forces the cache to regenerate
			axleHandler = null;
			beamHandler = null;
			return true;
		}
		return false;
	}

	@Override
	public void clientTick(){
		fluxHelper.clientTick();
		if(isActive()){
			//Perform angle movement on the client, and track what the client is probably doing on the server
			clientAngle += calcAngleChange(clientW, clientAngle);
			super.clientTick();
		}
	}

	@Override
	public void serverTick(){
		if(isActive()){
			resetHandlers();

			//Perform angle movement on the server
			float angleTarget = (float) axleHandler.getSpeed() - referenceSpeed;
			angle += calcAngleChange(angleTarget, angle);

			//Check for resyncing angle data to client
			final double errorMargin = Math.PI / 32D;
			if(Math.abs(clientAngle - angle) >= errorMargin || Math.abs(clientW - angleTarget) >= errorMargin / 2D){
				//Resync the speed and angle to the client
				resyncRotaryToClient();
			}

			//Handle flux
			if(level.getGameTime() % FluxUtil.FLUX_TIME == 0 && origin && fluxHelper.lastTick != level.getGameTime() && !isShutDown()){
				addFlux(FLUX_PER_CYCLE);
			}
		}
		fluxHelper.serverTick();
		super.serverTick();
	}

	/**
	 * Calculates the change in angle each tick, based on the target angle and current angle
	 * Takes the shortest path, has a maximum angle change per tick
	 * @param target The target angle
	 * @param current The current angle
	 * @return The change in angle to occur this tick. Positive is counter-clockwise, negative is clockwise
	 */
	private static float calcAngleChange(float target, float current){
		final float pi2 = (float) Math.PI * 2F;
		//Due to circular path, the two routes to the target need to be compared, and the shortest taken
		float angleChange = MathUtil.clockModulus(target, pi2) - MathUtil.clockModulus(current, pi2);
		if(angleChange > Math.PI || angleChange < -Math.PI){
			if(angleChange > 0){
				angleChange -= pi2;
			}else{
				angleChange += pi2;
			}
		}
		angleChange = Mth.clamp(angleChange, -ROTATION_SPEED, ROTATION_SPEED);
		return angleChange;
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		fluxHelper.loadAdditional(nbt, registries);
		clientW = nbt.getFloat("client_speed");
		rotaryEnergy = nbt.getDouble("rot_1");
		angle = nbt.getFloat("angle");
		clientAngle = angle;
		referenceSpeed = nbt.getFloat("reference");
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		fluxHelper.writeData(nbt);
		nbt.putFloat("client_speed", axleHandler == null ? clientW : (float) axleHandler.getSpeed());
		nbt.putDouble("rot_1", rotaryEnergy);
		nbt.putFloat("angle", angle);
		nbt.putFloat("reference", referenceSpeed);

	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries){
		CompoundTag nbt = super.getUpdateTag(pRegistries);
		fluxHelper.writeData(nbt);
		nbt.putFloat("client_speed", clientW);
		nbt.putDouble("rot_1", rotaryEnergy);
		nbt.putFloat("angle", angle);
		return nbt;
	}

	private void resyncRotaryToClient(){
		resetHandlers();
		clientAngle = angle;
		clientW = (float) axleHandler.getSpeed() - referenceSpeed;
		long packet = (Integer.toUnsignedLong(Float.floatToRawIntBits(clientAngle)) << 32L) | Integer.toUnsignedLong(Float.floatToRawIntBits(clientW));
		CRPackets.sendPacketAround(level, worldPosition, new SendLongToTE(4, packet, worldPosition));
	}

	//Capabilities

	private void resetHandlers(){
		if(axleHandler == null){
			if(isActive()){
				axleHandler = new AxleHandler();
				beamHandler = new BeamHandler();
			}else{
				axleHandler = null;
				beamHandler = null;
			}
		}
	}

	@Nullable
	@Override
	public IAxleHandler getAxleHandler(Direction dir){
		if(dir == null || dir == Direction.UP){
			return axleHandler;
		}
		return null;
	}

	@Nullable
	@Override
	public IBeamHandler getBeamHandler(Direction dir){
		return beamHandler;
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayer player){
		super.receiveLong(identifier, message, player);
		fluxHelper.receiveLong(identifier, message, player);
		if(identifier == 4){
			clientAngle = Float.intBitsToFloat((int) (message >>> 32L));
			clientW = Float.intBitsToFloat((int) (message & 0xFFFFFFFFL));
		}
	}

	@Override
	public boolean canBeginLinking(){
		return fluxHelper.canBeginLinking();
	}

	@Override
	public boolean canLink(ILinkTE otherTE){
		return fluxHelper.canLink(otherTE);
	}

	@Override
	public Set<BlockPos> getLinks(){
		return fluxHelper.getLinks();
	}

	@Override
	public boolean createLinkSource(ILinkTE endpoint, @Nullable Player player){
		return fluxHelper.createLinkSource(endpoint, player);
	}

	@Override
	public void removeLinkSource(BlockPos end){
		fluxHelper.removeLinkSource(end);
	}

	@Override
	public int getFlux(){
		return fluxHelper.getFlux();
	}

	@Override
	public void receiveInts(byte context, int[] message, @Nullable ServerPlayer sendingPlayer){
		fluxHelper.receiveInts(context, message, sendingPlayer);
	}

	@Override
	public int[] getRenderedArcs(){
		return fluxHelper.getRenderedArcs();
	}

	@Override
	public boolean isShutDown(){
		return fluxHelper.isShutDown();
	}

	@Override
	public int getReadingFlux(){
		return fluxHelper.getReadingFlux();
	}

	@Override
	public void addFlux(int deltaFlux){
		fluxHelper.addFlux(deltaFlux);
	}

	@Override
	public boolean canAcceptLinks(){
		return fluxHelper.canAcceptLinks();
	}

	private class AxleHandler implements IAxleHandler{

		//Fairly generic implementation that leaves angle management to tick()

		public double rotRatio;
		public byte updateKey;
		public IAxisHandler axis;

		@Override
		public void propagate(IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius, boolean renderOffset){
			//If true, this has already been checked.
			if(key == updateKey || masterIn.addToList(this)){
				return;
			}

			rotRatio = rotRatioIn == 0 ? 1 : rotRatioIn;
			updateKey = key;
			axis = masterIn;
		}

		@Override
		public double getRotationRatio(){
			return rotRatio;
		}

		@Override
		public float getAngle(float partialTicks){
			return clientAngle + partialTicks * clientW / 20F;
		}

		@Override
		public void disconnect(IAxisHandler disconnectingAxis){
			if(disconnectingAxis == null || axis == disconnectingAxis){
				axis = null;
			}
		}

		@Override
		public double getSpeed(){
			return axis == null ? 0 : rotRatio * axis.getBaseSpeed();
		}

		@Override
		public double getEnergy(){
			return rotaryEnergy;
		}

		@Override
		public void setEnergy(double newEnergy){
			rotaryEnergy = newEnergy;
			setChanged();
		}

		@Override
		public double getMoInertia(){
			return INERTIA;
		}
	}

	private class BeamHandler implements IBeamHandler{

		@Override
		public void setBeam(@Nonnull BeamUnit mag){
			if(mag.isEmpty()){
				return;
			}

			if(chevrons[3] != null){
				//We're dialed into something. Reset
				undialLinkedGateway();
				undial(new GatewayAddress(chevrons));
				return;
			}

			int index = 0;//Find the first undialed chevron
			for(int i = 0; i < 4; i++){
				if(chevrons[i] == null){
					index = i;
					break;
				}
			}

			EnumBeamAlignments alignment = GatewayAddress.getLegalEntry(Math.round(angle * 8F / 2F / (float) Math.PI));

			if(CRConfig.hardGateway.get() && alignment != EnumBeamAlignments.getAlignment(mag)){
				//Optional hardmode (off by default)
				chevrons[0] = chevrons[1] = chevrons[2] = chevrons[3] = null;
				syncChevrons();
				return;
			}

			chevrons[index] = alignment;//Dial in a new chevron
			referenceSpeed = (float) axleHandler.getSpeed();//Re-define our reference to the current input speed
			if(index == 3){
				//If this is the final chevron, make the connection and reset the target
				GatewayAddress targetAddress = new GatewayAddress(chevrons);
				Location location = GatewaySavedData.lookupAddress((ServerLevel) level, targetAddress);
				IGateway otherGateway;
				MinecraftServer server = level.getServer();
				if(location != null && (otherGateway = GatewayAddress.evalTE(location, server)) != null){
					otherGateway.dialTo(address, false);
					dialTo(targetAddress, true);
				}else{
					//Invalid address; reset
					chevrons[0] = chevrons[1] = chevrons[2] = chevrons[3] = null;
				}

				//Reset reference speed state
				referenceSpeed = 0;
			}
			resyncRotaryToClient();//Force a resync of the speed and angle to the client
			syncChevrons();
			setChanged();
		}
	}
}
