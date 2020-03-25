package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.API.technomancy.GatewayAddress;
import com.Da_Technomancer.crossroads.API.technomancy.GatewaySavedData;
import com.Da_Technomancer.crossroads.API.technomancy.IFluxLink;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.packets.SendLongToClient;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ObjectHolder(Crossroads.MODID)
public class GatewayFrameTileEntity extends TileEntity implements ITickableTileEntity, IInfoTE, IFluxLink{

	@ObjectHolder("gateway_frame")
	private static TileEntityType<GatewayFrameTileEntity> type = null;
	private static final int INERTIA = 100;//Moment of inertia
	public static final int FLUX_PER_CYCLE = 4;

	//These fields are only correct for the top center block of the multiblock (isActive() returns true)
	//They will not necessarily be null/empty/0 if this inactive- always check isActive()
	private int flux = 0;
	private int fluxToTrans = 0;
	private HashSet<BlockPos> links = new HashSet<>(1);
	private GatewayAddress address = null;//The address of THIS gateway
	private double[] rotary = new double[4];//Rotary spin data (0: speed, 1: energy, 2: power, 3: last energy)
	private float angle = 0;//Used for rendering and dialing chevrons. Because it's used for logic, we don't use the master axis angle syncing, which is render-based
	private float clientAngle = 0;//Angle on the client. On the server, acts as a record of value sent to client
	private float clientW = 0;//Speed on the client. On the server, acts as a record of value sent to client
	//Visible for rendering
	public EnumBeamAlignments[] chevrons = new EnumBeamAlignments[4];//Current values locked into chevrons. Null for unset chevrons
	private boolean origin = false;//Whether this gateway started the connection in dialed (determines which side has flux)

	private LazyOptional<IAxleHandler> axleOpt = null;
	private LazyOptional<IBeamHandler> beamOpt = null;

	//These fields will be correct for any portion of a formed multiblock
	private BlockPos key = null;//The relative position of the top center of the multiblock. Null if this is not formed
	private int size = 0;//Diameter of the multiblock, from top center to bottom center
	private Direction.Axis plane = null;//Legal values are null (unformed), x (for structure in x-y plane), and z (for structure in y-z plane). This should never by y

	private static void teleportEntity(Entity e, World target, double posX, double posY, double posZ){
		//Moves an entity to any position in any dimension
		if(e.world.dimension.getType() == target.dimension.getType()){
			//Same dimension TP
			e.setPosition(posX, posY, posZ);
		}else{
			//Different dimension TP
			//TODO This is not practical to implement in MC1.14, but is in MC1.15;
			//see https://github.com/MinecraftForge/MinecraftForge/pull/6404
			//Log an info and put a warning in chat
			Crossroads.logger.info("Cross-dimensional teleportation not implemented for the Gateway in MC1.14, due to forge issues. See forge pull request #6404 for more information");
			if(e instanceof PlayerEntity){
				e.sendMessage(new StringTextComponent("Cross-dimensional teleportation NYI! Sorry. Same dimensional portals work"));//I know I should be using localization- but this is temporary due to forge
			}
		}
	}

	public GatewayFrameTileEntity(){
		super(type);
	}

	/**
	 * Determines whether this TE should do anything
	 * @return Whether this block is formed into a multiblock and is the top center block (which handles all the logic)
	 */
	public boolean isActive(){
		return getBlockState().get(CRProperties.ACTIVE) && getBlockState().get(CRProperties.TOP);
	}

	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		if(isActive()){
			//Address of this gateway
			String[] names = new String[4];
			for(int i = 0; i < 4; i++){
				names[i] = address.getEntry(i).getLocalName(false);
			}
			chat.add(new TranslationTextComponent("tt.crossroads.gateway.chevron.address", names[0], names[1], names[2], names[3]));

			//Chevrons
			for(int i = 0; i < 4; i++){
				if(chevrons[i] == null){
					names[i] = MiscUtil.localize("tt.crossroads.gateway.chevron.none");
				}else{
					names[i] = chevrons[i].getLocalName(false);
				}
			}
			chat.add(new TranslationTextComponent("tt.crossroads.gateway.chevron.dialed", names[0], names[1], names[2], names[3]));

			RotaryUtil.addRotaryInfo(chat, rotary, INERTIA, axleOpt.orElseGet(AxleHandler::new).getRotationRatio(), true);
			FluxUtil.addFluxInfo(chat, this, chevrons[3] != null ? FLUX_PER_CYCLE : 0);
			FluxUtil.addLinkInfo(chat, this);
		}
	}

	//Gateway connection management

	/**
	 * Cancel any connection. Also tells any connected gateway to disconnect
	 * Safe to use even when not dialed/connected
	 * Virtual-server side only
	 */
	private void undial(){
		GatewayAddress dialed = new GatewayAddress(chevrons);
		//Wipe the chevrons before undialing the connected gateway to prevent an infinite recursion loop
		for(int i = 0; i < 4; i++){
			chevrons[i] = null;
		}
		origin = false;
		if(dialed.fullAddress()){
			playEffects(false);

			GatewayAddress.Location loc = GatewaySavedData.lookupAddress((ServerWorld) world, dialed);
			GatewayFrameTileEntity te = loc == null ? null : loc.evalTE(world.getServer());
			if(te != null){
				te.undial();//Undial the connected gateway
			}
		}
		markDirty();
		CRPackets.sendPacketAround(world, pos, new SendLongToClient(3, 0L, pos));
	}

	/**
	 * Dials this gateway to another gateway
	 * Disconnects from any previous connection
	 * Virtual server side only
	 * @param toLinkTo The address to link to
	 * @param source Whether this gateway is the source of the connection
	 * @return Whether this dialing succeeded
	 */
	private boolean dial(GatewayAddress toLinkTo, boolean source){
		undial();
		GatewayAddress.Location loc = GatewaySavedData.lookupAddress((ServerWorld) world, toLinkTo);
		GatewayFrameTileEntity te = loc == null ? null : loc.evalTE(world.getServer());
		if(te != null){
			if(source){
				//Dial the other gateway to this
				if(!te.dial(address, false)){
					//For some reason the other gateway has refused to dial. This should never happen, but we handle it just in case
					playEffects(false);
					return false;
				}
			}
			//Successful linking after confirming the partner exists
			for(int i = 0; i < 4; i++){
				chevrons[i] = toLinkTo.getEntry(i);
			}
			origin = false;
			playEffects(true);
			return true;
		}else{
			//This has failed
			playEffects(false);
			return false;
		}
	}

	/**
	 * Creates purely aesthetic sounds/particles
	 * Virtual-server side only
	 * @param success Whether this is for a successful action (like connecting) or an unsucessful action (like dialing a fake address)
	 */
	private void playEffects(boolean success){
		world.playSound(pos.getX() + 0.5F, pos.getY() - 1.5F, pos.getZ() + 0.5F, success ? SoundEvents.BLOCK_END_PORTAL_FRAME_FILL : SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 1F, world.rand.nextFloat(), true);
	}

	//Multiblock management

	/**
	 * Called when this block is broken. Disassembles the rest of the multiblock if formed
	 */
	public void dismantle(){
		if(!world.isRemote && isActive()){
			//The head dismantles the entire multiblock, restoring inactive states

			undial();//Cancel our connection

			//Release our address back into the pool
			GatewaySavedData.releaseAddress((ServerWorld) world, address);

			BlockPos.MutableBlockPos mutPos = new BlockPos.MutableBlockPos(pos);
			Direction horiz = plane == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;//horizontal direction
			mutPos.move(horiz, -size / 2);
			for(int i = 0; i < size; i++){
				for(int j = 0; j < size; j++){
					//Iterate over a size-by-size square (technically excessive as the multiblock is hollow) and disable each individually (including this)
					BlockState otherState = world.getBlockState(mutPos);
					if(otherState.getBlock() == CRBlocks.gatewayFrame){
						world.setBlockState(mutPos, otherState.with(CRProperties.ACTIVE, false).with(CRProperties.TOP, false));
					}
					TileEntity te = world.getTileEntity(mutPos);
					if(te instanceof GatewayFrameTileEntity){
						GatewayFrameTileEntity otherTE = (GatewayFrameTileEntity) te;
						otherTE.key = null;
						otherTE.size = 0;
						otherTE.plane = null;
						otherTE.axleOpt = null;
						otherTE.beamOpt = null;
						otherTE.address = null;
						otherTE.origin = false;
						otherTE.markDirty();
						otherTE.updateContainingBlockInfo();
					}
					mutPos.move(horiz, 1).move(Direction.DOWN, 1);
				}
			}
		}else if(key != null){
			//The rest of the multiblock asks the head to dismantle
			TileEntity te = world.getTileEntity(pos.add(key));
			if(te instanceof GatewayFrameTileEntity){
				((GatewayFrameTileEntity) te).dismantle();
			}
		}
	}


	/**
	 * Attempts to assemble this into a multiblock
	 * This will only work if this is the top-center block
	 * @return Whether this suceeded at forming the multiblock
	 */
	public boolean assemble(){
		if(world.isRemote){
			return false;//Server side only
		}
		if(getBlockState().get(CRProperties.ACTIVE)){
			return false;//This is already part of a multiblock
		}

		//First step is to determine the size
		int newSize = 0;
		BlockPos.MutableBlockPos mutPos = new BlockPos.MutableBlockPos(pos);
		//Maximum size is a 9x9, odd sized squares only
		for(int i = 1; i < 9; i++){
			mutPos.move(Direction.DOWN);
			BlockState state = world.getBlockState(mutPos);
			if(legalForGateway(state)){
				newSize = i + 1;
				break;
			}else if(!state.isAir(world, mutPos)){
				return false;//There is an obstruction
			}
		}
		if(newSize % 2 == 0){
			return false;//Even sizes are not allowed! Also catches newSize == 0
		}

		Direction.Axis axis;
		if(legalForGateway(world.getBlockState(pos.east())) && legalForGateway(world.getBlockState(pos.west()))){
			axis = Direction.Axis.X;
		}else if(legalForGateway(world.getBlockState(pos.south())) && legalForGateway(world.getBlockState(pos.north()))){
			axis = Direction.Axis.Z;
		}else{
			return false;//There wasn't even enough of the structure to determine what orientation it's supposed to have
		}

		//First pass over the area is to confirm this is a legal structure
		Direction horiz = Direction.getFacingFromAxis(Direction.AxisDirection.POSITIVE, axis);
		mutPos.setPos(pos).move(horiz, -size / 2);
		for(int i = 0; i < size; i++){
			for(int j = 0; j < size; j++){
				//Iterate over a size-by-size square and check each pos
				BlockState otherState = world.getBlockState(mutPos);

				if(i == 0 || i == size - 1 || j == 0 || j == size - 1){
					//We are on the edges
					if(!legalForGateway(otherState)){
						return false;
					}
				}else if(!otherState.isAir(world, mutPos)){
					return false;//We are on the inside, and expect air
				}

				mutPos.move(horiz, 1);
			}
			mutPos.move(Direction.DOWN, 1);
		}

		//Configure this TE
		//Request an address- fail if we can't get one
		address = GatewaySavedData.requestAddress((ServerWorld) world, pos);
		if(address == null){
			return false;
		}
		//Resetting the optionals to null forces the optional cache to regenerate
		axleOpt = null;
		beamOpt = null;

		//Second pass is to actually assemble the structure
		mutPos.setPos(pos).move(horiz, -size / 2);
		for(int i = 0; i < size; i++){
			for(int j = 0; j < size; j++){
				//Iterate over a size-by-size square and modify each edge
				BlockState otherState = world.getBlockState(mutPos);
				if(i == 0 || i == size - 1 || j == 0 || j == size - 1){
					//We are on the edges
					world.setBlockState(mutPos, otherState.with(CRProperties.ACTIVE, true).with(CRProperties.TOP, i == 0 && j == size / 2));
					TileEntity te = world.getTileEntity(mutPos);
					if(te instanceof GatewayFrameTileEntity){
						//Despite the name otherTE, for exactly one position otherTE == this
						GatewayFrameTileEntity otherTE = (GatewayFrameTileEntity) te;
						otherTE.key = pos.subtract(mutPos);
						otherTE.plane = axis;
						otherTE.size = size;
						otherTE.updateContainingBlockInfo();
					}
				}

				mutPos.move(horiz, 1);
			}
			mutPos.move(Direction.DOWN, 1);
		}

		//Send a packet to the client with the size and orientation info
		CRPackets.sendPacketAround(world, pos, new SendLongToClient(5, plane.ordinal() | (size << 2), pos));

		return true;
	}

	private static boolean legalForGateway(BlockState state){
		return state.getBlock() == CRBlocks.gatewayFrame && !state.get(CRProperties.ACTIVE);
	}

	@Override
	public int getReadingFlux(){
		return FluxUtil.findReadingFlux(this, flux, fluxToTrans);
	}

	@Override
	public void tick(){
		if(isActive()){
			//This TE only ticks if it is active
			clientAngle += clientW / 20F;
			if(!world.isRemote){
				angle += rotary[0] / 20F;
				if(Math.abs(clientAngle - angle) >= Math.PI / 8D || Math.abs(clientW - rotary[0]) >= Math.PI / 16D){
					//Resync the speed and angle to the client
					clientAngle = angle;
					clientW = (float) rotary[0];
					long packet = (long) Float.floatToIntBits(clientAngle) << 32L | (long) Float.floatToIntBits(clientW);
					CRPackets.sendPacketAround(world, pos, new SendLongToClient(4, packet, pos));
					markDirty();
				}

				//Teleportation
				if(chevrons[3] != null && plane != null){
					Direction horiz = Direction.getFacingFromAxis(Direction.AxisDirection.POSITIVE, plane);
					AxisAlignedBB area = new AxisAlignedBB(pos.down(size).offset(horiz, -size / 2), pos.offset(horiz, size / 2 + 1));
					List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, area, EntityPredicates.IS_ALIVE);
					if(!entities.isEmpty()){
						GatewayAddress.Location loc = GatewaySavedData.lookupAddress((ServerWorld) world, new GatewayAddress(chevrons));
						if(loc != null){
							GatewayFrameTileEntity otherTE = loc.evalTE(world.getServer());
							BlockPos endPos = loc.pos.down(2);
							if(otherTE != null && otherTE.plane != null){
								endPos = endPos.offset(Direction.getFacingFromAxisDirection(otherTE.plane, Direction.AxisDirection.NEGATIVE).rotateY());
							}
							World targetWorld = loc.evalDim(world.getServer());
							if(targetWorld == null){
								return;
							}
							for(Entity e : entities){
								playTPEffect(world, e.posX, e.posY, e.posZ);//Play effects at the start position
								teleportEntity(e, targetWorld, endPos.getX() + 0.5D, endPos.getY(), endPos.getZ() + 0.5D);
							}
							playTPEffect(targetWorld, endPos.getX() + 0.5D, endPos.getY(), endPos.getZ() + 0.5D);
						}
					}
				}

				//Handle flux
				long stage = world.getGameTime() % FluxUtil.FLUX_TIME;
				if(stage == 0){
					if(origin){
						flux += FLUX_PER_CYCLE;
					}
					if(flux != 0){
						fluxToTrans += flux;
						flux = 0;
						markDirty();
					}
				}else if(stage == 1){
					flux += FluxUtil.performTransfer(this, links, fluxToTrans);
					fluxToTrans = 0;
					FluxUtil.checkFluxOverload(this);
				}
			}
		}
	}

	private static void playTPEffect(World world, double xPos, double yPos, double zPos){
		//Spawn smoke particles
		for(int i = 0; i < 10; i++){
			world.addOptionalParticle(ParticleTypes.SMOKE, xPos + Math.random() - 0.5D, yPos + Math.random() - 0.5D, zPos + Math.random() - 0.5D, Math.random() - 0.5F, Math.random() - 0.5F, Math.random() - 0.5F);
		}
		//play a sound
		world.playSound(xPos, yPos, zPos, SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.BLOCKS, 1, (float) Math.random(), true);
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		//Active only
		flux = nbt.getInt("flux");
		fluxToTrans = nbt.getInt("flux_trans");
		if(nbt.contains("link")){
			links.add(BlockPos.fromLong(nbt.getLong("link")));
		}else{
			links.clear();
		}
		address = nbt.contains("address") ? GatewayAddress.deserialize(nbt.getInt("address")) : null;
		for(int i = 0; i < 4; i++){
			rotary[i] = nbt.getDouble("rot_" + i);
			chevrons[i] = nbt.contains("chev_" + i) ? EnumBeamAlignments.values()[nbt.getInt("chev_" + i)] : null;
		}
		clientW = (float) rotary[0];
		angle = nbt.getFloat("angle");
		clientAngle = angle;
		origin = nbt.getBoolean("origin");

		//Generic
		key = nbt.contains("key") ? BlockPos.fromLong(nbt.getLong("key")) : null;
		size = nbt.getInt("size");
		plane = nbt.contains("plane") ? Direction.Axis.values()[nbt.getInt("plane")] : null;
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		//Active only
		nbt.putInt("flux", flux);
		nbt.putInt("flux_trans", fluxToTrans);
		if(links.size() == 1){
			nbt.putLong("link", links.iterator().next().toLong());
		}
		if(address != null){
			nbt.putInt("address", address.serialize());
		}
		for(int i = 0; i < 4; i++){
			nbt.putDouble("rot_" + i, rotary[i]);
			if(chevrons[i] != null){
				nbt.putInt("chev_" + i, chevrons[i].ordinal());
			}
		}
		nbt.putFloat("angle", angle);
		nbt.putBoolean("origin", origin);

		//Generic
		if(key != null){
			nbt.putLong("key", key.toLong());
		}
		nbt.putInt("size", size);
		if(plane != null){
			nbt.putInt("plane", plane.ordinal());
		}

		return nbt;
	}

	@Override
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
		if(links.size() == 1){
			nbt.putLong("link", links.iterator().next().toLong());
		}
		for(int i = 0; i < 4; i++){
			nbt.putDouble("rot_" + i, rotary[i]);//Strictly speaking, we only need rotary[0] on the client
			if(chevrons[i] != null){
				nbt.putInt("chev_" + i, chevrons[i].ordinal());
			}
		}
		nbt.putFloat("angle", angle);

		nbt.putInt("size", size);
		if(plane != null){
			nbt.putInt("plane", plane.ordinal());
		}
		return nbt;
	}

	//Flux boilerplate

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
		return links;
	}

	//Capabilities

	private void genOptionals(){
		if(axleOpt == null){
			if(isActive()){
				axleOpt = LazyOptional.of(AxleHandler::new);
				beamOpt = LazyOptional.of(BeamHandler::new);
			}else{
				axleOpt = LazyOptional.empty();
				beamOpt = LazyOptional.empty();
			}
		}
	}

	@Override
	public void remove(){
		super.remove();
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
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayerEntity player){
		switch(identifier){
			case LINK_PACKET_ID:
				links.add(BlockPos.fromLong(message));
				markDirty();
				break;
			case CLEAR_PACKET_ID:
				links.clear();
				markDirty();
				break;
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
		public void propogate(IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius, boolean renderOffset){
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
		public void markChanged(){
			markDirty();
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
		public double[] getMotionData(){
			return rotary;
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
				undial();
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
				undial();
				return;
			}

			chevrons[index] = alignment;//Dial in a new chevron
			if(index == 3){
				//If this is the final chevron, make the connection
				boolean success = dial(new GatewayAddress(chevrons), true);
				playEffects(success);
			}
			CRPackets.sendPacketAround(world, pos, new SendLongToClient(3, new GatewayAddress(chevrons).serialize(), pos));
			markDirty();
		}
	}
}
