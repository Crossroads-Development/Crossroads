package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.technomancy.GatewayAddress;
import com.Da_Technomancer.crossroads.API.technomancy.GatewaySavedData;
import com.Da_Technomancer.crossroads.API.technomancy.IFluxLink;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import net.minecraft.block.BlockState;
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
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@ObjectHolder(Crossroads.MODID)
public class GatewayFrameTileEntity extends TileEntity implements ITickableTileEntity, IInfoTE, IFluxLink{

	@ObjectHolder("gateway_frame")
	private static TileEntityType<GatewayFrameTileEntity> type = null;
	private static final int INERTIA = 100;//Moment of inertia

	//These fields are only correct for the top center block of the multiblock (isActive() returns true)
	//They will not necessarily be null/empty/0 if this inactive- always check isActive()
	private int flux = 0;
	private HashSet<BlockPos> links = new HashSet<>(1);
	private GatewayAddress address = null;//The address of THIS gateway
	private double[] rotary = new double[4];//Rotary spin data (0: speed, 1: energy, 2: power, 3: last energy)
	private float angle = 0;//Used for rendering and dialing chevrons. Because it's used for logic, we don't use the master axis angle syncing, which is render-based
	private float clientW = 0;//Speed on the client. On the server, acts as a record of value sent to client
	private EnumBeamAlignments[] chevrons = new EnumBeamAlignments[4];//Current values locked into chevrons. Null for unset chevrons
	private boolean origin = false;//Whether this gateway started the connection in dialed (determines which side has flux)
	private LazyOptional<IAxleHandler> axleOpt = null;
	private LazyOptional<IBeamHandler> beamOpt = null;

	//These fields will be correct for any portion of a formed multiblock
	private BlockPos key = null;//The relative position of the top center of the multiblock. Null if this is not formed
	private int size = 0;//Diameter of the multiblock, from top center to bottom center
	private Direction.Axis plane = null;//Legal values are null (unformed), x (for structure in x-y plane), and z (for structure in y-z plane). This should never by y

	public GatewayFrameTileEntity(){
		super(type);
	}

	/**
	 * Determines whether this TE should do anything
	 * @return Whether this block is formed into a multiblock and is the top center block (which handles all the logic)
	 */
	public boolean isActive(){
		return getBlockState().get(CRProperties.ACTIVE) && getBlockState().get(CRProperties.UP);
	}

	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		//TODO
	}

	/**
	 * Called when this block is broken. Disassembles the rest of the multiblock if formed
	 */
	public void dismantle(){
		if(!world.isRemote && isActive()){
			//The head dismantles the entire multiblock, restoring inactive states

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
						world.setBlockState(mutPos, otherState.with(CRProperties.ACTIVE, false).with(CRProperties.UP, false));
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

		Direction.Axis axis = null;
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
					world.setBlockState(mutPos, otherState.with(CRProperties.ACTIVE, true).with(CRProperties.UP, i == 0 && j == size / 2));
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

		return true;
	}

	private static boolean legalForGateway(BlockState state){
		return state.getBlock() == CRBlocks.gatewayFrame && !state.get(CRProperties.ACTIVE);
	}

	@Override
	public void tick(){
		if(isActive()){
			//This TE only ticks if it is active
			//TODO
		}
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		//Active only
		flux = nbt.getInt("flux");
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
		if(identifier == LINK_PACKET_ID){
			links.add(BlockPos.fromLong(message));
			markDirty();
		}else if(identifier == CLEAR_PACKET_ID){
			links.clear();
			markDirty();
		}

		//TODO client-server value synchronization
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
			return angle + partialTicks * clientW / 20F;
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
			//TODO implementation that handles locking in chevrons
		}
	}
}
