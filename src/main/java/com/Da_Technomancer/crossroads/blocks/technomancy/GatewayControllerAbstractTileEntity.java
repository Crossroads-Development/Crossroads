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
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import com.Da_Technomancer.essentials.api.packets.ILongReceiver;
import com.Da_Technomancer.essentials.api.packets.SendLongToTE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
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
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public abstract class GatewayControllerAbstractTileEntity extends BlockEntity implements IGateway, ITickableTileEntity, ILongReceiver{

	//These fields are only correct for the top center block of the multiblock (isActive() returns true)
	//They will not necessarily be null/empty/0 if this inactive- always check isActive()
	protected GatewayAddress address = null;//The address of THIS gateway
	protected GatewayAddress lastDialed = new GatewayAddress(new EnumBeamAlignments[4]);
	protected boolean origin = false;//Whether this gateway initiated the connection
	//Visible for rendering
	public EnumBeamAlignments[] chevrons = new EnumBeamAlignments[4];//Current values locked into chevrons. Null for unset chevrons

	protected int size = 0;//Diameter of the multiblock, from top center to bottom center

	public GatewayControllerAbstractTileEntity(BlockEntityType<?> beType, BlockPos pos, BlockState state){
		super(beType, pos, state);
	}

	/**
	 * Used for rendering
	 * @return The size of the formed multiblock. Only valid on the client for the top-center block of the formed multiblock
	 */
	public int getSize(){
		return size;
	}

	/**
	 * Determines whether this TE should do anything
	 * @return Whether this block is formed into a multiblock and is the top center block (which handles all the logic)
	 */
	public boolean isActive(){
		return getBlockState().hasProperty(CRProperties.ACTIVE) && getBlockState().getValue(CRProperties.ACTIVE);
	}

	public Direction getFacing(){
		return getBlockState().hasProperty(CRProperties.HORIZ_FACING) ? getBlockState().getValue(CRProperties.HORIZ_FACING) : Direction.NORTH;
	}

	protected void syncChevrons(){
		if(!level.isClientSide){
			//Update circuit outputs
			doForEachEdgePosition(pos -> {
				level.updateNeighbourForOutputSignal(pos, CRBlocks.gatewayEdge);
			});
			//Update on client
			CRPackets.sendPacketAround(level, worldPosition, new SendLongToTE(3, new GatewayAddress(chevrons).serialize(), worldPosition));
		}
	}

	protected void doForEachEdgePosition(Consumer<BlockPos> edgePosConsumer){
		if(size > 0){
			Direction horiz = getFacing().getClockWise();//horizontal direction
			final int preSize = size;//We have to store this, as the field may be modified in the loop
			BlockPos.MutableBlockPos mutPos = new BlockPos.MutableBlockPos(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());

			mutPos.move(horiz, -preSize / 2);
			for(int i = 0; i < preSize; i++){
				for(int j = 0; j < preSize; j++){
					BlockState otherState = level.getBlockState(mutPos);
					if(otherState.getBlock() == CRBlocks.gatewayEdge){
						edgePosConsumer.accept(mutPos);
					}
					mutPos.move(horiz, 1);
				}
				mutPos.move(horiz, -preSize);
				mutPos.move(Direction.DOWN, 1);
			}
		}
	}

	//Gateway connection management

	@Nullable
	@Override
	public GatewayAddress getAddress(){
		return address;
	}

	protected void undialLinkedGateway(){
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
			origin = false;
			setChanged();
			syncChevrons();
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
		origin = cost;
		playEffects(true);
		//Send chevrons to client
		syncChevrons();
	}

	@Override
	public void teleportEntity(Entity entity, float horizontalRelPos, float verticalRelPos, Direction sourceDirection){
		Vec3 centerPos = new Vec3(worldPosition.getX() + 0.5D, worldPosition.getY() - (size-2D) / 2D , worldPosition.getZ() + 0.5D);
		float scalingRadius = (size - 2) / 2F;
		Direction facing = getFacing();
		IGateway.teleportEntityTo(entity, (ServerLevel) level, centerPos.x + scalingRadius * horizontalRelPos * facing.getStepZ(), centerPos.y + scalingRadius * verticalRelPos, centerPos.z - scalingRadius * horizontalRelPos * facing.getStepX(), 180 - (sourceDirection.toYRot() - facing.toYRot()), address);
		playTPEffect(level, entity.getX(), entity.getY(), entity.getZ());
	}

	/**
	 * Creates purely aesthetic sounds/particles
	 * Virtual-server side only
	 * @param success Whether this is for a successful action (like connecting) or an unsuccessful action (like dialing a fake address)
	 */
	protected void playEffects(boolean success){
		level.playLocalSound(worldPosition.getX() + 0.5F, worldPosition.getY() - 1.5F, worldPosition.getZ() + 0.5F, success ? SoundEvents.END_PORTAL_FRAME_FILL : SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1F, level.random.nextFloat(), true);
	}

	//Multiblock management

	@Override
	public void dismantle(){
		if(!level.isClientSide && size > 0){
			//The head dismantles the entire multiblock, restoring inactive states

			//Remove this controller from the edges
			doForEachEdgePosition(edgePosition -> {
				BlockState otherState = level.getBlockState(edgePosition);
				if(otherState.getBlock() == CRBlocks.gatewayEdge){
					level.setBlockAndUpdate(edgePosition, otherState.setValue(CRProperties.ACTIVE, false));
				}
				BlockEntity te = level.getBlockEntity(edgePosition);
				if(te instanceof GatewayEdgeTileEntity otherTE){
					otherTE.reset();
				}
			});

			//Cancel our connection
			undialLinkedGateway();
			undial(new GatewayAddress(chevrons));

			//Release our address back into the pool
			GatewaySavedData.releaseAddress((ServerLevel) level, address);

			//Reset this block
			BlockState state =level.getBlockState(worldPosition);//They may have already broken this block
			if(state.hasProperty(CRProperties.ACTIVE)){
				level.setBlockAndUpdate(worldPosition, getBlockState().setValue(CRProperties.ACTIVE, false));
			}
			size = 0;
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
		if(newSize == 0){
			MiscUtil.displayMessage(player, Component.translatable("tt.crossroads.gateway.missing_frame"));
			return false;
		}else if(newSize < 5 || newSize % 2 == 0){
			MiscUtil.displayMessage(player, Component.translatable("tt.crossroads.gateway.size_wrong"));
			return false;//Even sizes are not allowed
		}

		int thickness = Math.max(1, newSize / 5);//required thickness of frame blocks

		//First pass over the area is to confirm this is a legal structure
		Direction horiz = getFacing().getClockWise();
		mutPos.set(worldPosition).move(horiz, -newSize / 2);
		for(int i = 0; i < newSize; i++){
			for(int j = 0; j < newSize; j++){
				//Iterate over a size-by-size square and check each pos
				BlockState otherState = level.getBlockState(mutPos);

				if(i < thickness || newSize - i <= thickness || j < thickness || newSize - j <= thickness){
					//We are on the edges, and expect a frame block
					if((i != 0 || j != newSize / 2) && !legalForGateway(otherState)){
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
			mutPos.move(horiz, -newSize);
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
		mutPos.set(worldPosition).move(horiz, -newSize / 2);
		for(int i = 0; i < newSize; i++){
			for(int j = 0; j < newSize; j++){
				//Iterate over a size-by-size square and modify each edge
				BlockState otherState = level.getBlockState(mutPos);
				if(i < thickness || newSize - i <= thickness || j < thickness || newSize - j <= thickness){
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
			mutPos.move(horiz, -newSize);
			mutPos.move(Direction.DOWN, 1);
		}

		//Update this block
		size = newSize;
		level.setBlockAndUpdate(worldPosition, getBlockState().setValue(CRProperties.ACTIVE, true));
//		clearCache();

		//Send a packet to the client with the size and orientation info
		CRPackets.sendPacketAround(level, worldPosition, new SendLongToTE(5, size, worldPosition));

		return true;
	}

	private static boolean legalForGateway(BlockState state){
		return state.getBlock() == CRBlocks.gatewayEdge && !state.getValue(CRProperties.ACTIVE);
	}

	@Override
	public void serverTick(){
		//This TE only ticks if it is active
		if(isActive() && chevrons[3] != null){
			ITickableTileEntity.super.serverTick();

			//Teleportation
			Direction facing = getFacing();
			Direction horiz = facing.getClockWise();
			AABB area = AABB.encapsulatingFullBlocks(worldPosition.below(1).relative(horiz, -size / 2), worldPosition.below(size - 2).relative(horiz, size / 2));
			//We use the timeUntilPortal field in Entity to not spam TP entities between two portals
			//This is both not what it's for, and exactly what it's for
			List<Entity> entities = level.getEntitiesOfClass(Entity.class, area, EntitySelector.ENTITY_STILL_ALIVE.and(e -> IGateway.isAllowedToTeleport(e, level, address)));
			if(!entities.isEmpty()){
				Location loc = GatewaySavedData.lookupAddress((ServerLevel) level, new GatewayAddress(chevrons));
				IGateway otherTE;
				if(loc != null){
					MinecraftServer server = level.getServer();
					if((otherTE = GatewayAddress.evalTE(loc, server)) != null){
						Vec3 centerPos = new Vec3(worldPosition.getX() + 0.5D, worldPosition.getY() - (size-2D) / 2D, worldPosition.getZ() + 0.5D);
						float scalingRadius = (size - 2) / 2F;
						for(Entity e : entities){
							float relPosH = Mth.clamp(-facing.getStepZ() * ((float) (e.getX() - centerPos.x) / scalingRadius) + facing.getStepX() * ((float) (e.getZ() - centerPos.z) / scalingRadius), -1, 1);
							float relPosV = Mth.clamp((float) (e.getY() - centerPos.y) / scalingRadius, -1, 1);
							playTPEffect(level, e.getX(), e.getY(), e.getZ());//Play effects at the start position
							otherTE.teleportEntity(e, relPosH, relPosV, facing);
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
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		//Active only
		address = nbt.contains("address") ? GatewayAddress.deserialize(nbt.getInt("address")) : null;
		EnumBeamAlignments[] lastDialedChev = new EnumBeamAlignments[4];
		for(int i = 0; i < 4; i++){
			chevrons[i] = nbt.contains("chev_" + i) ? EnumBeamAlignments.values()[nbt.getInt("chev_" + i)] : null;
			lastDialedChev[i] = nbt.contains("dialed_" + i) ? EnumBeamAlignments.values()[nbt.getInt("dialed_" + i)] : null;
		}
		lastDialed = new GatewayAddress(lastDialedChev);
		origin = nbt.getBoolean("origin");
		//Generic
		size = nbt.getInt("size");
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
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
		nbt.putBoolean("origin", origin);

		//Generic
		nbt.putInt("size", size);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries){
		CompoundTag nbt = super.getUpdateTag(pRegistries);
		for(int i = 0; i < 4; i++){
			if(chevrons[i] != null){
				nbt.putInt("chev_" + i, chevrons[i].ordinal());
			}
		}

		nbt.putInt("size", size);
		return nbt;
	}

	@Override
	public void setBlockState(BlockState newState){
		super.setBlockState(newState);
		BlockState oldState = getBlockState();
		if(oldState != null && oldState.getBlock() == newState.getBlock() && oldState.hasProperty(CRProperties.ACTIVE) && oldState.getValue(CRProperties.ACTIVE) && oldState.getValue(CRProperties.HORIZ_FACING) == newState.getValue(CRProperties.HORIZ_FACING).getOpposite()){
			//Special case:
			//If we're wrenching an assembled gateway to swap the orientation between two valid orientations, don't reset the tile-entity
			return;
		}

		//This is not, strictly speaking, optimized
		//Pre MC1.21, default behavior for all TEs was that changing blockstate invalidated capability caches
		//Post MC1.21, this is no longer the case, which opens up some opportunities for optimization
		//But everything was written with the assumption of invalidation on state change,
		//So anything other than re-implementing the old default is going to introduce a lot of new bugs
		level.invalidateCapabilities(worldPosition);
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
				size = (int) message;
				break;
		}
	}
}
