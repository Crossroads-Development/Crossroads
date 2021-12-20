package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.*;
import com.Da_Technomancer.crossroads.API.beams.*;
import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.ambient.sounds.CRSounds;
import com.Da_Technomancer.crossroads.items.technomancy.StaffTechnomancy;
import com.Da_Technomancer.essentials.packets.ILongReceiver;
import com.Da_Technomancer.essentials.packets.SendLongToClient;
import com.Da_Technomancer.essentials.tileentities.ITickableTileEntity;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;

@ObjectHolder(Crossroads.MODID)
public class BeamCannonTileEntity extends BlockEntity implements ITickableTileEntity, IInfoTE, ILongReceiver{

	@ObjectHolder("beam_cannon")
	public static BlockEntityType<BeamCannonTileEntity> TYPE = null;

	public static final double INERTIA = 0;
	private static final float ROTATION_SPEED = (float) Math.PI / 40F;//Rate of convergence between angle and axle 'speed' in radians/tick. Yes, this terminology is confusing
	private static final int RANGE = 256;

	//Rotary data and networking
	//Index 0: bottom axle (overall rotation control); Index 1: Side inputs (angle of incidence control)
	private final double[] energy = new double[2];
	private final float[] angle = new float[2];//Current angle, used for output. Because it's used for logic, we don't use the master axis angle syncing, which is render-based
	public final float[] clientAngle = new float[2];//Angle on the client. On the server, acts as a record of value sent to client
	private final float[] clientW = new float[2];//Speed on the client (post adjustment). On the server, acts as a record of value sent to client

	//Beam stuff
	private long activeCycle;//To prevent tick acceleration and deal with some chunk loading weirdness
	private final BeamUnitStorage[] queued = {new BeamUnitStorage(), new BeamUnitStorage()};
	private BeamUnit readingBeam = BeamUnit.EMPTY;
	//Rendering data, records what was sent to the client on the server
	public Color beamCol = Color.WHITE;
	public int beamSize = 0;//This is the beam radius, not power
	public int beamLength = 0;

	//Whether the angle of this beam cannon is locked with a wrench
	private boolean locked = false;

	public BeamCannonTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		if(locked){
			chat.add(new TranslatableComponent("tt.crossroads.beam_cannon.base.angle.lock", MiscUtil.preciseRound(angle[0], 3)));
			chat.add(new TranslatableComponent("tt.crossroads.beam_cannon.side.angle.lock", MiscUtil.preciseRound(angle[1], 3)));
		}else{
			chat.add(new TranslatableComponent("tt.crossroads.beam_cannon.base.angle", MiscUtil.preciseRound(angle[0], 3), MiscUtil.preciseRound(MiscUtil.clockModulus((float) baseAxleHandler.getSpeed(), (float) Math.PI * 2F), 3)));
			chat.add(new TranslatableComponent("tt.crossroads.beam_cannon.side.angle", MiscUtil.preciseRound(angle[1], 3), MiscUtil.preciseRound(Mth.clamp(sideAxleHandler.getSpeed(), -Math.PI / 2F, Math.PI / 2F), 3)));
		}
		if(!readingBeam.isEmpty()){
			EnumBeamAlignments.getAlignment(readingBeam).discover(player, true);
			chat.add(new TranslatableComponent("tt.crossroads.beam_cannon.beam", readingBeam.toString()));
		}
		RotaryUtil.addRotaryInfo(chat, baseAxleHandler, true);
		RotaryUtil.addRotaryInfo(chat, sideAxleHandler, true);
	}

	@Override
	public void serverTick(){
		ITickableTileEntity.super.serverTick();

		//Perform angle movement on the server
		if(!locked){
			float angleTarget0 = (float) baseAxleHandler.getSpeed();
			float angleTarget1 = (float) sideAxleHandler.getSpeed();
			if(angleTarget0 != angle[0] || angleTarget1 != angle[1]){
				angle[0] += calcAngleChange(angleTarget0, angle[0], true);
				angle[1] += calcAngleChange(angleTarget1, angle[1], false);
				//Check for resyncing angle data to client
				final double errorMargin = Math.PI / 32D;
				if(Math.abs(clientAngle[0] - angle[0]) >= errorMargin || Math.abs(clientW[0] - angleTarget0) >= errorMargin / 2D || Math.abs(clientAngle[1] - angle[1]) >= errorMargin || Math.abs(clientW[1] - angleTarget1) >= errorMargin / 2D){
					//Resync the speed and angle to the client
					updateMotionToClient();
				}
			}
		}

		//Output beam
		if(level.getGameTime() % BeamUtil.BEAM_TIME == 0){
			BeamUnit out = queued[0].getOutput();
			queued[0].clear();
			queued[0].addBeam(queued[1]);
			queued[1].clear();
			activeCycle = level.getGameTime();
			readingBeam = out;
			setChanged();
			if(out.getPower() > BeamUtil.POWER_LIMIT){
				out = out.mult((float) BeamUtil.POWER_LIMIT / (float) out.getPower(), true);
			}

			Color outCol = out.getRGB();
			int outPower = out.getPower();
			float outLength = 0;

			if(!out.isEmpty()){
				Vec3 rayTraceSt = Vec3.atCenterOf(worldPosition);
				Direction facing = getBlockState().getValue(CRProperties.FACING);

				//ray is a unit vector pointing in the aimed direction
				//Done via several multiplied rotation matrices simplified into a single multiplied quaternion for facing and a single vector
				float sinPhi = (float) Math.sin(angle[1]);
				Vector3f ray = new Vector3f(-(float) Math.sin(angle[0]) * sinPhi, (float) Math.cos(angle[1]), (float) Math.cos(angle[0]) * sinPhi);
				Quaternion directionRotation = getRotationFromDirection(facing).toQuaternion();
				ray.transform(directionRotation);

				//rayTraceSt is offset 'up' by 3.5/16 blocks to match the render, which has to be rotated by the facing
				Vector3f upShift = new Vector3f(0, 3.5F / 16F, 0);
				upShift.transform(directionRotation);
				rayTraceSt = rayTraceSt.add(upShift.x(), upShift.y(), upShift.z());

				Triple<BlockPos, Vec3, Direction> beamHitResult = StaffTechnomancy.rayTraceBeams(out, level, rayTraceSt, rayTraceSt, new Vec3(ray), null, worldPosition, RANGE);
				BlockPos endPos = beamHitResult.getLeft();
				if(endPos != null){//Should always be true
					outLength = (float) beamHitResult.getMiddle().distanceTo(rayTraceSt);
					Direction effectDir = beamHitResult.getRight();
					BlockEntity te = level.getBlockEntity(endPos);
					LazyOptional<IBeamHandler> opt;
					if(te != null && (opt = te.getCapability(Capabilities.BEAM_CAPABILITY, effectDir)).isPresent()){
						opt.orElseThrow(NullPointerException::new).setBeam(out);
					}else{
						EnumBeamAlignments align = EnumBeamAlignments.getAlignment(out);
						if(!level.isOutsideBuildHeight(endPos)){
							align.getEffect().doBeamEffect(align, out.getVoid() != 0, Math.min(64, outPower), level, endPos, effectDir);
						}
					}
				}
			}

			if(!outCol.equals(beamCol) || BeamUtil.getBeamRadius(outPower) != beamSize || Math.abs(beamLength - outLength) >= 0.5F){
				beamCol = outCol;
				beamSize = BeamUtil.getBeamRadius(outPower);
				beamLength = Math.round(outLength);
				long packet = 0;
				if(outPower != 0){
					packet |= beamCol.getRGB() & 0xFFFFFF;//Encode color, Remove the alpha bits
					packet |= ((beamSize - 1) & 0xF) << 24;//Encode beam radius
					packet |= ((beamLength - 1) & 0xFFL) << 28L;
				}
				CRPackets.sendPacketAround(level, worldPosition, new SendLongToClient(3, packet, worldPosition));
			}

			//Play sounds
			//Can be called on both the virtual server and client side, but only actually does anything on the server side as the passed player is null
			if(CRConfig.beamSounds.get() && level.getGameTime() % 60 == 0){
				//Play a sound if ANY side is outputting a beam
				if(beamSize > 0){
					//The attenuation distance defined for this sound in sounds.json is significant, and makes the sound have a very short range
					CRSounds.playSoundServer(level, worldPosition, CRSounds.BEAM_PASSIVE, SoundSource.BLOCKS, 0.7F, 0.3F);
				}
			}
		}
	}

	@Override
	public void tick(){
		//Perform angle movement on the client, and track what the client is probably doing on the server
		clientAngle[0] += calcAngleChange(clientW[0], clientAngle[0], true);
		clientAngle[1] += calcAngleChange(clientW[1], clientAngle[1], false);
	}

	private ServerQuaternion getRotationFromDirection(Direction dir){
		//Reimplementation of Direction.getRotation(), as that method is client side only
		ServerQuaternion quaternion = new ServerQuaternion(Vector3f.XP, 90, true);//Quaternion for XP axis with 90 degree rotation
		switch(dir) {
			case DOWN:
				return new ServerQuaternion(Vector3f.XP, 180, true);//XP axis, rotated 180 degrees
			case UP:
				return ServerQuaternion.getOne();
			case NORTH:
				quaternion.multiply(new ServerQuaternion(Vector3f.ZP, 180, true));//ZP, rotated 180 deg
				return quaternion;
			case SOUTH:
				return quaternion;
			case WEST:
				quaternion.multiply(new ServerQuaternion(Vector3f.ZP, 90, true));//ZP, rotated 90 deg
				return quaternion;
			case EAST:
			default:
				quaternion.multiply(new ServerQuaternion(Vector3f.ZP, -90.0F, true));//ZP, rotated -90 deg
				return quaternion;
		}
	}

	private void updateMotionToClient(){
		clientAngle[0] = angle[0];
		clientAngle[1] = angle[1];
		if(locked){
			clientW[0] = angle[0];
			clientW[1] = angle[1];
		}else{
			clientW[0] = (float) baseAxleHandler.getSpeed();
			clientW[1] = (float) sideAxleHandler.getSpeed();
		}
		long packet0 = (Integer.toUnsignedLong(Float.floatToRawIntBits(clientAngle[0])) << 32L) | Integer.toUnsignedLong(Float.floatToRawIntBits(clientW[0]));
		CRPackets.sendPacketAround(level, worldPosition, new SendLongToClient(0, packet0, worldPosition));
		long packet1 = (Integer.toUnsignedLong(Float.floatToRawIntBits(clientAngle[1])) << 32L) | Integer.toUnsignedLong(Float.floatToRawIntBits(clientW[1]));
		CRPackets.sendPacketAround(level, worldPosition, new SendLongToClient(1, packet1, worldPosition));
	}

	private static float calcAngleChange(float target, float current, boolean allowLooping){
		final float pi2 = (float) Math.PI * 2F;
		float angleChange;
		if(allowLooping){
			angleChange = MiscUtil.clockModulus(target, pi2) - MiscUtil.clockModulus(current, pi2);
			//Due to circular path, the two routes to the target need to be compared, and the shortest taken
			if(angleChange > Math.PI || angleChange < -Math.PI){
				if(angleChange > 0){
					angleChange -= pi2;
				}else{
					angleChange += pi2;
				}
			}
		}else{
			final float piHalf = (float) Math.PI / 2F;
			target = Math.min(Math.max(target, -piHalf), piHalf);
			angleChange = (target % pi2) - (current % pi2);
		}
		angleChange = Mth.clamp(angleChange, -ROTATION_SPEED, ROTATION_SPEED);
		return angleChange;
	}

	public void updateLock(Player player){
		locked = !locked;
		setChanged();
		if(level.isClientSide){
			if(locked){
				MiscUtil.chatMessage(player, new TranslatableComponent("tt.crossroads.beam_cannon.lock"));
			}else{
				MiscUtil.chatMessage(player, new TranslatableComponent("tt.crossroads.beam_cannon.unlock"));
			}
		}else{
			//Send update packet to ensure this reaches all client
			CRPackets.sendPacketAround(level, worldPosition, new SendLongToClient(4, locked ? 1 : 0, worldPosition));
		}
	}

	@Override
	public AABB getRenderBoundingBox(){
		//Expand the render box to include all possible beams from this block
		return new AABB(worldPosition.offset(-RANGE, -RANGE, -RANGE), worldPosition.offset(1 + RANGE, 1 + RANGE, 1 + RANGE));
	}

	@Override
	public void receiveLong(byte id, long value, @Nullable ServerPlayer sender){
		if(id == 0 || id == 1){
			clientAngle[id] = Float.intBitsToFloat((int) (value >>> 32L));
			clientW[id] = Float.intBitsToFloat((int) (value & 0xFFFFFFFFL));
		}else if(id == 3){
			//Beam output packet
			if(value == 0){
				beamCol = Color.WHITE;
				beamSize = 0;
				beamLength = 0;
			}else{
				beamCol = Color.decode(Integer.toString((int) (value & 0xFFFFFFL)));
				beamSize = (int) ((value >>> 24) & 0xF) + 1;
				beamLength = (int) (value >>> 28) + 1;
			}
		}else if(id == 4){
			//Locking/unlocking
			locked = value > 0;
		}
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		for(int i = 0; i < 2; i++){
			energy[i] = nbt.getDouble("energy_" + i);
			angle[i] = nbt.getFloat("angle_" + i);
			clientAngle[i] = angle[i];
			clientW[i] = nbt.getFloat("clientw_" + i);
		}
		activeCycle = nbt.getLong("cycle");
		queued[0] = BeamUnitStorage.readFromNBT("storage_0", nbt);
		queued[1] = BeamUnitStorage.readFromNBT("storage_1", nbt);
		beamCol = new Color(nbt.getInt("color"));
		beamSize = nbt.getInt("beam_size");
		beamLength = nbt.getInt("beam_length");
		locked = nbt.getBoolean("locked");
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		for(int i = 0; i < 2; i++){
			nbt.putDouble("energy_" + i, energy[i]);
			nbt.putFloat("angle_" + i, angle[i]);
			nbt.putFloat("clientw_" + i, clientW[i]);
		}
		nbt.putLong("cycle", activeCycle);
		queued[0].writeToNBT("storage_0", nbt);
		queued[1].writeToNBT("storage_1", nbt);
		nbt.putInt("color", beamCol.getRGB());
		nbt.putInt("beam_size", beamSize);
		nbt.putInt("beam_length", beamLength);
		nbt.putBoolean("locked", locked);
	}

	@Override
	public CompoundTag getUpdateTag(){
		CompoundTag nbt = super.getUpdateTag();
		saveAdditional(nbt);
		return nbt;
	}

	@Override
	public void setBlockState(BlockState stateIn){
		super.setBlockState(stateIn);
		baseAxleOpt.invalidate();
		sideAxleOpt.invalidate();
		sideAxleAltOpt.invalidate();
		beamOpt.invalidate();
		baseAxleOpt = LazyOptional.of(() -> baseAxleHandler);
		sideAxleOpt = LazyOptional.of(() -> sideAxleHandler);
		sideAxleAltOpt = LazyOptional.of(() -> sideAxleHandlerAlt);
		beamOpt = LazyOptional.of(() -> beamHandler);
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		baseAxleOpt.invalidate();
		sideAxleOpt.invalidate();
		sideAxleAltOpt.invalidate();
		beamOpt.invalidate();
	}

	private final AxleHandler baseAxleHandler = new AxleHandler(0, false);
	private final AxleHandler sideAxleHandler = new AxleHandler(1, false);
	private final AxleHandler sideAxleHandlerAlt = new AxleHandler(1, true);
	private final BeamHandler beamHandler = new BeamHandler();
	private LazyOptional<IAxleHandler> baseAxleOpt = LazyOptional.of(() -> baseAxleHandler);
	private LazyOptional<IAxleHandler> sideAxleOpt = LazyOptional.of(() -> sideAxleHandler);
	private LazyOptional<IAxleHandler> sideAxleAltOpt = LazyOptional.of(() -> sideAxleHandlerAlt);
	private LazyOptional<IBeamHandler> beamOpt = LazyOptional.of(() -> beamHandler);

	@Nonnull
	@Override
	@SuppressWarnings("unchecked")
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side){
		if(cap == Capabilities.BEAM_CAPABILITY){
			return (LazyOptional<T>) beamOpt;
		}
		Direction blockFacing = getBlockState().getValue(CRProperties.FACING);
		if(cap == Capabilities.AXLE_CAPABILITY && blockFacing != side){
			if(side == null || side == blockFacing.getOpposite()){
				return (LazyOptional<T>) baseAxleOpt;
			}else if((blockFacing.getAxis() == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.Y) == side.getAxis()){
				return (LazyOptional<T>) sideAxleAltOpt;
			}else{
				return (LazyOptional<T>) sideAxleOpt;
			}
		}
		return super.getCapability(cap, side);
	}

	private class BeamHandler implements IBeamHandler{

		@Override
		public void setBeam(@Nonnull BeamUnit mag){
			if(!mag.isEmpty()){
				queued[level.getGameTime() == activeCycle ? 0 : 1].addBeam(mag);
				setChanged();
			}
		}
	}

	private class AxleHandler implements IAxleHandler{

		//Fairly generic implementation that leaves angle management to tick()

		private final int index;
		private final boolean altAxis;//For the alt axis handler, it refers to the main side axis handler
		private double rotRatio;
		private byte updateKey;
		private IAxisHandler masterAxis;

		private AxleHandler(int index, boolean altAxis){
			this.index = index;
			this.altAxis = altAxis;
		}

		@Override
		public void propagate(IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius, boolean renderOffset){
			//If true, this has already been checked.
			if(key == (altAxis ? sideAxleHandler.updateKey : updateKey) || masterIn.addToList(this)){
				return;
			}

			if(altAxis){
				sideAxleHandler.rotRatio = rotRatioIn == 0 ? 1 : rotRatioIn;
				sideAxleHandler.updateKey = key;
				sideAxleHandler.masterAxis = masterIn;
			}else{
				rotRatio = rotRatioIn == 0 ? 1 : rotRatioIn;
				updateKey = key;
				masterAxis = masterIn;
			}

			if(index == 1){
				Direction.Axis blockAxis = getBlockState().getValue(CRProperties.FACING).getAxis();
				Direction.Axis handlerAxis = (altAxis == (blockAxis == Direction.Axis.X)) ? Direction.Axis.Z : Direction.Axis.Y;
				for(Direction dir : Direction.values()){
					Direction.Axis dirAxis = dir.getAxis();
					if(dirAxis != blockAxis){
						//Invert renderOffset if switching axis from the handler axis
						RotaryUtil.propagateAxially(level.getBlockEntity(worldPosition.relative(dir)), dir.getOpposite(), this, masterIn, key, (dirAxis == handlerAxis) == renderOffset);
					}
				}
			}
		}

		@Override
		public double getRotationRatio(){
			return altAxis ? sideAxleHandler.rotRatio : rotRatio;
		}

		@Override
		public float getAngle(float partialTicks){
			return clientAngle[index] + partialTicks * clientW[index] / 20F;
		}

		@Override
		public void disconnect(){
			if(altAxis){
				sideAxleHandler.masterAxis = null;
			}else{
				masterAxis = null;
			}
		}

		@Override
		public double getSpeed(){
			if(altAxis){
				return sideAxleHandler.masterAxis == null ? 0 : sideAxleHandler.rotRatio * sideAxleHandler.masterAxis.getBaseSpeed();
			}else{
				return masterAxis == null ? 0 : rotRatio * masterAxis.getBaseSpeed();
			}
		}

		@Override
		public double getEnergy(){
			return energy[index];
		}

		@Override
		public void setEnergy(double newEnergy){
			energy[index] = newEnergy;
			setChanged();
		}

		@Override
		public double getMoInertia(){
			return INERTIA;
		}
	}
}
