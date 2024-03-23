package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.api.packets.CRPackets;
import com.Da_Technomancer.crossroads.api.technomancy.GatewayAddress;
import com.Da_Technomancer.crossroads.api.technomancy.GatewaySavedData;
import com.Da_Technomancer.crossroads.api.technomancy.IGateway;
import com.Da_Technomancer.crossroads.api.technomancy.Location;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import com.Da_Technomancer.essentials.api.packets.ILongReceiver;
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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GatewayControllerDestinationTileEntity extends BlockEntity implements IGateway, ITickableTileEntity, ILongReceiver{

	public static final BlockEntityType<GatewayControllerDestinationTileEntity> TYPE = CRTileEntity.createType(GatewayControllerDestinationTileEntity::new, CRBlocks.gatewayControllerDestination);

	//These fields are only correct for the top center block of the multiblock (isActive() returns true)
	//They will not necessarily be null/empty/0 if this inactive- always check isActive()
	private GatewayAddress address = null;//The address of THIS gateway
	private GatewayAddress lastDialed = new GatewayAddress(new EnumBeamAlignments[4]);
	//Visible for rendering
	public EnumBeamAlignments[] chevrons = new EnumBeamAlignments[4];//Current values locked into chevrons. Null for unset chevrons

	private int size = 0;//Diameter of the multiblock, from top center to bottom center
	private Direction.Axis plane = null;//Legal values are null (unformed), x (for structure in x-y plane), and z (for structure in y-z plane). This should never by y

	public GatewayControllerDestinationTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		if(isActive() && address != null){
			//Address of this gateway
			String[] names = new String[4];
			for(int i = 0; i < 4; i++){
				EnumBeamAlignments align = address.getEntry(i);
				if(align == null){
					align = EnumBeamAlignments.NO_MATCH;//Should never happen
				}
				names[i] = align.getLocalName(false);
			}
			chat.add(Component.translatable("tt.crossroads.gateway.chevron.address", names[0], names[1], names[2], names[3]));

			//Chevrons
			boolean dialed = chevrons[3] != null;
			for(int i = 0; i < 4; i++){
				if(dialed){
					names[i] = chevrons[i].getLocalName(false);
				}else{
					if(lastDialed.fullAddress()){
						names[i] = lastDialed.getEntry(i).getLocalName(false);
					}else{
						names[i] = MiscUtil.localize("tt.crossroads.gateway.chevron.none");
					}
				}
			}
			if(dialed){
				chat.add(Component.translatable("tt.crossroads.gateway.chevron.dialed", names[0], names[1], names[2], names[3]));
			}else{
				chat.add(Component.translatable("tt.crossroads.gateway.chevron.prev_dialed", names[0], names[1], names[2], names[3]));
			}
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
	 * Determines whether this TE should do anything
	 * @return Whether this block is formed into a multiblock and is the top center block (which handles all the logic)
	 */
	public boolean isActive(){
		BlockState state = getBlockState();
		return state.getBlock() == CRBlocks.gatewayControllerDestination && getBlockState().getValue(CRProperties.ACTIVE);
	}

	public void redstoneInput(){
		//If we are not currently dialed to something,
		//connect to the previous gateway with a redstone signal
		if(chevrons[3] == null && lastDialed.fullAddress()){
			Location location = GatewaySavedData.lookupAddress((ServerLevel) level, lastDialed);
			IGateway otherGateway;
			MinecraftServer server = level.getServer();
			if(location != null && (otherGateway = GatewayAddress.evalTE(location, server)) != null){
				otherGateway.dialTo(address, true);//The other gateway assumes the cost
				dialTo(lastDialed, false);
			}else{
				//Invalid address; reset
				chevrons[0] = chevrons[1] = chevrons[2] = chevrons[3] = null;
				lastDialed = new GatewayAddress(chevrons);
				setChanged();
			}
		}
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
			//Save to last dialed
			lastDialed = other;
			//Wipe the chevrons
			for(int i = 0; i < 4; i++){
				chevrons[i] = null;
			}
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
		//Save to last dialed
		lastDialed = other;
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
		return new AABB(worldPosition).inflate(isActive() ? size : 0);
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
			if(state.getBlock() == CRBlocks.gatewayControllerDestination){
				level.setBlockAndUpdate(worldPosition, getBlockState().setValue(CRProperties.ACTIVE, false));
			}
			size = 0;
			plane = null;
			address = null;
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
		CRPackets.sendPacketAround(level, worldPosition, new SendLongToClient(5, plane.ordinal() | (size << 2), worldPosition));

		return true;
	}

	private static boolean legalForGateway(BlockState state){
		return state.getBlock() == CRBlocks.gatewayEdge && !state.getValue(CRProperties.ACTIVE);
	}

	@Override
	public void serverTick(){
		//This TE only ticks if it is active
		if(isActive() && chevrons[3] != null && plane != null){
			ITickableTileEntity.super.serverTick();

			//Teleportation
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
		EnumBeamAlignments[] lastDialedChev = new EnumBeamAlignments[4];
		for(int i = 0; i < 4; i++){
			chevrons[i] = nbt.contains("chev_" + i) ? EnumBeamAlignments.values()[nbt.getInt("chev_" + i)] : null;
			lastDialedChev[i] = nbt.contains("dialed_" + i) ? EnumBeamAlignments.values()[nbt.getInt("dialed_" + i)] : null;
		}
		lastDialed = new GatewayAddress(lastDialedChev);
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
			if(lastDialed.getEntry(i) != null){
				nbt.putInt("dialed_" + i, lastDialed.getEntry(i).ordinal());
			}
		}

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

		nbt.putInt("size", size);
		if(plane != null){
			nbt.putInt("plane", plane.ordinal());
		}
		return nbt;
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayer player){
		switch(identifier){
			case 3:
				GatewayAddress add = GatewayAddress.deserialize((int) message);
				for(int i = 0; i < 4; i++){
					chevrons[i] = add.getEntry(i);
				}
				break;
			case 5:
				//size and orientation for rendering
				plane = Direction.Axis.values()[(int) (message & 3)];
				size = (int) (message >>> 2);
				break;
		}
	}
}
