package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.MathUtil;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.beams.BeamUnit;
import com.Da_Technomancer.crossroads.api.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.api.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.api.packets.CRPackets;
import com.Da_Technomancer.crossroads.api.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.api.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.api.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.api.technomancy.*;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.essentials.api.packets.SendLongToClient;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GatewayControllerTileEntity extends IFluxLink.FluxHelper implements IGateway{

	public static final BlockEntityType<GatewayControllerTileEntity> TYPE = CRTileEntity.createType(GatewayControllerTileEntity::new, CRBlocks.gatewayController);
	public static final int INERTIA = 0;//Moment of inertia
	public static final int FLUX_PER_CYCLE = 4;
	private static final float ROTATION_SPEED = (float) Math.PI / 40F;//Rate of convergence between angle and axle 'speed' in radians/tick. Yes, this terminology is confusing

	//These fields are only correct for the top center block of the multiblock (isActive() returns true)
	//They will not necessarily be null/empty/0 if this inactive- always check isActive()
	private GatewayAddress address = null;//The address of THIS gateway
	private double rotaryEnergy = 0;//Rotary energy
	private float angle = 0;//Used for rendering and dialing chevrons. Because it's used for logic, we don't use the master axis angle syncing, which is render-based
	private float clientAngle = 0;//Angle on the client. On the server, acts as a record of value sent to client
	private float clientW = 0;//Speed on the client (post adjustment). On the server, acts as a record of value sent to client
	private float referenceSpeed = 0;//Speed which angles will be defined relative to on the server
	//Visible for rendering
	public EnumBeamAlignments[] chevrons = new EnumBeamAlignments[4];//Current values locked into chevrons. Null for unset chevrons
	private boolean origin = false;//Whether this gateway started the connection in dialed (determines which side has flux)

	private IAxleHandler axleHandler = null;
	private LazyOptional<IAxleHandler> axleOpt = null;
	private LazyOptional<IBeamHandler> beamOpt = null;

	private int size = 0;//Diameter of the multiblock, from top center to bottom center
	private Direction.Axis plane = null;//Legal values are null (unformed), x (for structure in x-y plane), and z (for structure in y-z plane). This should never by y

	public GatewayControllerTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, null, Behaviour.SOURCE);
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
			genOptionals();
			RotaryUtil.addRotaryInfo(chat, axleHandler, true);
			FluxUtil.addFluxInfo(chat, this, chevrons[3] != null && origin ? FLUX_PER_CYCLE : 0);
			super.addInfo(chat, player, hit);
		}
	}

	/**
	 * Used for rendering
	 * @return The size of the formed multiblock. Only valid on the client for the top-center block of the formed multiblock
	 */
	public int getSize(){
		return size;
	}

	/**
	 * Used for rendering
	 * @return The plane of the formed multiblock. Only valid on the client for the top-center block of the formed multiblock
	 */
	@Nullable
	public Direction.Axis getPlane(){
		return plane;
	}

	/**
	 * Used for rendering
	 * @param partialTicks The partial ticks in [0, 1]
	 * @return The angle of the octagonal ring used for dialing
	 */
	public double getAngle(float partialTicks){
		return calcAngleChange(clientW, clientAngle) * partialTicks + clientAngle;
	}

	/**
	 * Determines whether this TE should do anything
	 * @return Whether this block is formed into a multiblock and is the top center block (which handles all the logic)
	 */
	public boolean isActive(){
		BlockState state = getBlockState();
		return state.getBlock() == CRBlocks.gatewayController && getBlockState().getValue(CRProperties.ACTIVE);
	}

	//Gateway connection management

	@Nullable
	@Override
	public GatewayAddress getAddress(){
		return address;
	}

	private void undialLinkedGateway(){
		GatewayAddress prevDialed = new GatewayAddress(chevrons);
		Location prevLinkLocation = GatewaySavedData.lookupAddress((ServerLevel) level, prevDialed);
		if(prevLinkLocation != null){
			MinecraftServer server = level.getServer();
			IGateway prevLink = GatewayAddress.evalTE(prevLinkLocation, server);
			if(prevLink != null){
				prevLink.undial(address);
			}
		}
	}

	@Override
	public void undial(GatewayAddress other){
		GatewayAddress prevDialed = new GatewayAddress(chevrons);
		if(prevDialed.fullAddress() && prevDialed.equals(other)){
			//Wipe the chevrons
			for(int i = 0; i < 4; i++){
				chevrons[i] = null;
			}
			origin = false;
			referenceSpeed = 0;
			resyncToClient();
			setChanged();
			CRPackets.sendPacketAround(level, worldPosition, new SendLongToClient(3, 0L, worldPosition));
		}
	}

	@Override
	public void dialTo(GatewayAddress other, boolean cost){
		//Disconnect from any previous connection
		GatewayAddress prevDialed = new GatewayAddress(chevrons);
		if(prevDialed.fullAddress() && !prevDialed.equals(other)){
			//Undial the connected gateway
			undialLinkedGateway();
			//Undial this gateway
			undial(prevDialed);
		}

		//Create the new connection
		//Set the chevrons
		for(int i = 0; i < 4; i++){
			chevrons[i] = other.getEntry(i);
		}
		origin = cost;
		playEffects(true);
		//Send chevrons to client
		CRPackets.sendPacketAround(level, worldPosition, new SendLongToClient(3, new GatewayAddress(chevrons).serialize(), worldPosition));
	}

	@Override
	public void teleportEntity(Entity entity, float horizontalRelPos, float verticalRelPos, Direction.Axis sourceAxis){
		Vec3 centerPos = new Vec3(worldPosition.getX() + 0.5D, worldPosition.getY() - size / 2D + 0.5D, worldPosition.getZ() + 0.5D);
		float scalingRadius = (size - 2) / 2F;
		if(plane == Direction.Axis.X){
			IGateway.teleportEntityTo(entity, (ServerLevel) level, centerPos.x + scalingRadius * horizontalRelPos, centerPos.y + scalingRadius * verticalRelPos, centerPos.z, sourceAxis == plane ? 0 : 90);
		}else{
			IGateway.teleportEntityTo(entity, (ServerLevel) level, centerPos.x, centerPos.y + scalingRadius * verticalRelPos, centerPos.z + scalingRadius * horizontalRelPos, sourceAxis == plane ? 0 : -90);
		}
		playTPEffect(level, entity.getX(), entity.getY(), entity.getZ());
	}

	@Override
	public AABB getRenderBoundingBox(){
		//Increase render BB to include links and the entire formed frame
		return new AABB(worldPosition).inflate(Math.max(getRange(), isActive() ? size : 0));
	}

	/**
	 * Creates purely aesthetic sounds/particles
	 * Virtual-server side only
	 * @param success Whether this is for a successful action (like connecting) or an unsucessful action (like dialing a fake address)
	 */
	private void playEffects(boolean success){
		level.playLocalSound(worldPosition.getX() + 0.5F, worldPosition.getY() - 1.5F, worldPosition.getZ() + 0.5F, success ? SoundEvents.END_PORTAL_FRAME_FILL : SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1F, level.random.nextFloat(), true);
	}

	//Multiblock management

	@Override
	public void dismantle(){
		if(!level.isClientSide && isActive()){
			//The head dismantles the entire multiblock, restoring inactive states

			//Cancel our connection
			undialLinkedGateway();
			undial(new GatewayAddress(chevrons));

			//Release our address back into the pool
			GatewaySavedData.releaseAddress((ServerLevel) level, address);

			BlockPos.MutableBlockPos mutPos = new BlockPos.MutableBlockPos(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
			Direction horiz = plane == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;//horizontal direction
			int preSize = size;//We have to store this, as the field will be modified in the loop
			mutPos.move(horiz, -preSize / 2);
			for(int i = 0; i < preSize; i++){
				for(int j = 0; j < preSize; j++){
					//Iterate over a size-by-size square (technically excessive as the multiblock is hollow) and disable each individually (including this)
					BlockState otherState = level.getBlockState(mutPos);
					if(otherState.getBlock() == CRBlocks.gatewayEdge){
						level.setBlockAndUpdate(mutPos, otherState.setValue(CRProperties.ACTIVE, false));
					}
					BlockEntity te = level.getBlockEntity(mutPos);
					if(te instanceof GatewayEdgeTileEntity otherTE){
						otherTE.reset();
					}
					mutPos.move(horiz, 1);
				}
				mutPos.move(horiz, -preSize);
				mutPos.move(Direction.DOWN, 1);
			}

			//Reset this block
			BlockState state = getBlockState();
			if(state.getBlock() == CRBlocks.gatewayController){
				level.setBlockAndUpdate(worldPosition, getBlockState().setValue(CRProperties.ACTIVE, false));
			}
			size = 0;
			plane = null;
			axleOpt = null;
			beamOpt = null;
			address = null;
			origin = false;
			setChanged();
//			clearCache();
		}
	}

	/**
	 * Attempts to assemble this into a multiblock
	 * This will only work if this is the top-center block
	 * @return Whether this succeeded at forming the multiblock
	 */
	public boolean assemble(Player player){
		if(level.isClientSide){
			return false;//Server side only
		}
		if(getBlockState().getValue(CRProperties.ACTIVE)){
			return false;//This is already part of a multiblock
		}

		//First step is to determine the size
		int newSize = 0;
		BlockPos.MutableBlockPos mutPos = new BlockPos.MutableBlockPos(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
		//Maximum size is a 63x63, odd sized squares only
		boolean foundAir = false;//Indicates we have passed the top section of the frame
		int foundThickness = 1;
		for(int i = 1; i < 63; i++){
			mutPos.move(Direction.DOWN);
			BlockState state = level.getBlockState(mutPos);
			if(legalForGateway(state)){
				if(foundAir){
					newSize = i + foundThickness;
					break;
				}else{
					foundThickness++;
				}
//			}else if(!state.isAir()){
//				return false;//There is an obstruction
			}else{
				foundAir = true;
			}
		}
		if(newSize < 5 || newSize % 2 == 0){
			MiscUtil.displayMessage(player, Component.translatable("tt.crossroads.gateway.size_wrong"));
			return false;//Even sizes are not allowed
		}

		Direction.Axis axis;
		if(legalForGateway(level.getBlockState(worldPosition.east())) && legalForGateway(level.getBlockState(worldPosition.west()))){
			axis = Direction.Axis.X;
		}else if(legalForGateway(level.getBlockState(worldPosition.south())) && legalForGateway(level.getBlockState(worldPosition.north()))){
			axis = Direction.Axis.Z;
		}else{
			return false;//There wasn't even enough of the structure to determine what orientation it's supposed to have
		}

		size = newSize;
		plane = axis;
		int thickness = Math.max(1, size / 5);//required thickness of frame blocks

		//First pass over the area is to confirm this is a legal structure
		Direction horiz = Direction.get(Direction.AxisDirection.POSITIVE, axis);
		mutPos.set(worldPosition).move(horiz, -size / 2);
		for(int i = 0; i < size; i++){
			for(int j = 0; j < size; j++){
				//Iterate over a size-by-size square and check each pos
				BlockState otherState = level.getBlockState(mutPos);

				if(i < thickness || size - i <= thickness || j < thickness || size - j <= thickness){
					//We are on the edges, and expect a frame block
					if((i != 0 || j != size / 2) && !legalForGateway(otherState)){
						MiscUtil.displayMessage(player, Component.translatable("tt.crossroads.gateway.thickness", thickness));
						return false;
					}
				}
				//Removed hollow requirement
//				else if(!otherState.isAir()){
//					return false;//We are on the inside, and expect air
//				}

				mutPos.move(horiz, 1);
			}
			mutPos.move(horiz, -size);
			mutPos.move(Direction.DOWN, 1);
		}

		//Configure this TE
		//Request an address- fail if we can't get one
		address = GatewaySavedData.requestAddress((ServerLevel) level, worldPosition);
		if(address == null){
			MiscUtil.displayMessage(player, Component.translatable("tt.crossroads.gateway.address_taken"));
			return false;
		}
		//Resetting the optionals to null forces the optional cache to regenerate
		axleOpt = null;
		beamOpt = null;

		//Second pass is to actually assemble the structure
		mutPos.set(worldPosition).move(horiz, -size / 2);
		for(int i = 0; i < size; i++){
			for(int j = 0; j < size; j++){
				//Iterate over a size-by-size square and modify each edge
				BlockState otherState = level.getBlockState(mutPos);
				if(i < thickness || size - i <= thickness || j < thickness || size - j <= thickness){
					//We are on the edges
					level.setBlockAndUpdate(mutPos, otherState.setValue(CRProperties.ACTIVE, true));
					BlockEntity te = level.getBlockEntity(mutPos);
					if(te instanceof GatewayEdgeTileEntity otherTE){
						otherTE.setKey(worldPosition.subtract(mutPos));
//						otherTE.clearCache();
					}
				}

				mutPos.move(horiz, 1);
			}
			mutPos.move(horiz, -size);
			mutPos.move(Direction.DOWN, 1);
		}

		//Update this block
		level.setBlockAndUpdate(worldPosition, getBlockState().setValue(CRProperties.ACTIVE, true));
//		clearCache();

		//Send a packet to the client with the size and orientation info
		CRPackets.sendPacketAround(level, worldPosition, new SendLongToClient(5, plane.ordinal() | ((long) size << 2), worldPosition));

		return true;
	}

	private static boolean legalForGateway(BlockState state){
		return state.getBlock() == CRBlocks.gatewayEdge && !state.getValue(CRProperties.ACTIVE);
	}

	@Override
	public void clientTick(){
		//This TE only ticks if it is active
		if(isActive()){
			//Perform angle movement on the client, and track what the client is probably doing on the server
			clientAngle += calcAngleChange(clientW, clientAngle);
			super.clientTick();
		}
	}

	@Override
	public void serverTick(){
		//This TE only ticks if it is active
		if(isActive()){
			genOptionals();

			//Perform angle movement on the server
			float angleTarget = (float) axleHandler.getSpeed() - referenceSpeed;
			angle += calcAngleChange(angleTarget, angle);

			//Check for resyncing angle data to client
			final double errorMargin = Math.PI / 32D;
			if(Math.abs(clientAngle - angle) >= errorMargin || Math.abs(clientW - angleTarget) >= errorMargin / 2D){
				//Resync the speed and angle to the client
				resyncToClient();
			}

			//Teleportation
			if(chevrons[3] != null && plane != null && !isShutDown()){
				Direction horiz = Direction.get(Direction.AxisDirection.POSITIVE, plane);
				AABB area = new AABB(worldPosition.below(size).relative(horiz, -size / 2), worldPosition.relative(horiz, size / 2 + 1));
				//We use the timeUntilPortal field in Entity to not spam TP entities between two portals
				//This is both not what it's for, and exactly what it's for
				List<Entity> entities = level.getEntitiesOfClass(Entity.class, area, EntitySelector.ENTITY_STILL_ALIVE.and(e -> IGateway.isAllowedToTeleport(e, level)));
				if(!entities.isEmpty()){
					Location loc = GatewaySavedData.lookupAddress((ServerLevel) level, new GatewayAddress(chevrons));
					IGateway otherTE;
					if(loc != null){
						MinecraftServer server = level.getServer();
						if((otherTE = GatewayAddress.evalTE(loc, server)) != null){
							Vec3 centerPos = new Vec3(worldPosition.getX() + 0.5D, worldPosition.getY() - size / 2D + 0.5D, worldPosition.getZ() + 0.5D);
							float scalingRadius = (size - 2) / 2F;
							for(Entity e : entities){
								float relPosH = Mth.clamp(plane == Direction.Axis.X ? ((float) (e.getX() - centerPos.x) / scalingRadius) : ((float) (e.getZ() - centerPos.z) / scalingRadius), -1, 1);
								float relPosV = Mth.clamp((float) (e.getY() - centerPos.y) / scalingRadius, -1, 1);
								playTPEffect(level, e.getX(), e.getY(), e.getZ());//Play effects at the start position
								otherTE.teleportEntity(e, relPosH, relPosV, plane);
							}
						}
					}
				}
			}

			//Handle flux
			if(level.getGameTime() % FluxUtil.FLUX_TIME == 0 && origin && lastTick != level.getGameTime() && !isShutDown()){
				addFlux(FLUX_PER_CYCLE);
			}

			super.serverTick();//We call the super method last, as we use lastTick to prevent time acceleration in the code above, and lastTick is updated in the super method
		}
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

	private static void playTPEffect(Level world, double xPos, double yPos, double zPos){
		//Spawn smoke particles
		for(int i = 0; i < 10; i++){
			world.addAlwaysVisibleParticle(ParticleTypes.SMOKE, xPos + Math.random() - 0.5D, yPos + Math.random() - 0.5D, zPos + Math.random() - 0.5D, Math.random() - 0.5F, Math.random() - 0.5F, Math.random() - 0.5F);
		}
		//play a sound
		world.playLocalSound(xPos, yPos, zPos, SoundEvents.PORTAL_TRAVEL, SoundSource.BLOCKS, 1, (float) Math.random(), true);
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		//Active only
		address = nbt.contains("address") ? GatewayAddress.deserialize(nbt.getInt("address")) : null;
		clientW = nbt.getFloat("client_speed");
		rotaryEnergy = nbt.getDouble("rot_1");
		for(int i = 0; i < 4; i++){
			chevrons[i] = nbt.contains("chev_" + i) ? EnumBeamAlignments.values()[nbt.getInt("chev_" + i)] : null;
		}
		angle = nbt.getFloat("angle");
		clientAngle = angle;
		referenceSpeed = nbt.getFloat("reference");
		origin = nbt.getBoolean("origin");

		//Generic
		size = nbt.getInt("size");
		plane = nbt.contains("plane") ? Direction.Axis.values()[nbt.getInt("plane")] : null;
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		//Active only
		if(address != null){
			nbt.putInt("address", address.serialize());
		}
		for(int i = 0; i < 4; i++){
			if(chevrons[i] != null){
				nbt.putInt("chev_" + i, chevrons[i].ordinal());
			}
		}
		nbt.putFloat("client_speed", axleHandler == null ? clientW : (float) axleHandler.getSpeed());
		nbt.putDouble("rot_1", rotaryEnergy);
		nbt.putFloat("angle", angle);
		nbt.putFloat("reference", referenceSpeed);
		nbt.putBoolean("origin", origin);

		//Generic
		nbt.putInt("size", size);
		if(plane != null){
			nbt.putInt("plane", plane.ordinal());
		}

	}

	@Override
	public CompoundTag getUpdateTag(){
		CompoundTag nbt = super.getUpdateTag();
		for(int i = 0; i < 4; i++){
			if(chevrons[i] != null){
				nbt.putInt("chev_" + i, chevrons[i].ordinal());
			}
		}
		nbt.putFloat("client_speed", clientW);
		nbt.putDouble("rot_1", rotaryEnergy);
		nbt.putFloat("angle", angle);

		nbt.putInt("size", size);
		if(plane != null){
			nbt.putInt("plane", plane.ordinal());
		}
		return nbt;
	}

	private void resyncToClient(){
		genOptionals();
		clientAngle = angle;
		clientW = (float) axleHandler.getSpeed() - referenceSpeed;
		long packet = (Integer.toUnsignedLong(Float.floatToRawIntBits(clientAngle)) << 32L) | Integer.toUnsignedLong(Float.floatToRawIntBits(clientW));
		CRPackets.sendPacketAround(level, worldPosition, new SendLongToClient(4, packet, worldPosition));
	}

	//Capabilities

	private void genOptionals(){
		if(axleOpt == null){
			if(isActive()){
				axleHandler = new AxleHandler();
				axleOpt = LazyOptional.of(() -> axleHandler);
				beamOpt = LazyOptional.of(BeamHandler::new);
			}else{
				axleOpt = LazyOptional.empty();
				beamOpt = LazyOptional.empty();
			}
		}
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		if(axleOpt != null){
			axleOpt.invalidate();
		}
		if(beamOpt != null){
			beamOpt.invalidate();
		}
	}

	@Nonnull
	@Override
	@SuppressWarnings("unchecked")
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side){
		if(isActive()){
			genOptionals();
			if(cap == Capabilities.AXLE_CAPABILITY && (side == null || side == Direction.UP)){
				return (LazyOptional<T>) axleOpt;
			}
			if(cap == Capabilities.BEAM_CAPABILITY){
				return (LazyOptional<T>) beamOpt;
			}
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayer player){
		super.receiveLong(identifier, message, player);
		switch(identifier){
			case 3:
				GatewayAddress add = GatewayAddress.deserialize((int) message);
				for(int i = 0; i < 4; i++){
					chevrons[i] = add.getEntry(i);
				}
				break;
			case 4:
				clientAngle = Float.intBitsToFloat((int) (message >>> 32L));
				clientW = Float.intBitsToFloat((int) (message & 0xFFFFFFFFL));
				break;
			case 5:
				//size and orientation for rendering
				plane = Direction.Axis.values()[(int) (message & 3)];
				size = (int) (message >>> 2);
				break;
		}
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
		public void disconnect(){
			axis = null;
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
			resyncToClient();//Force a resync of the speed and angle to the client
			CRPackets.sendPacketAround(level, worldPosition, new SendLongToClient(3, new GatewayAddress(chevrons).serialize(), worldPosition));
			setChanged();
		}
	}
}
