package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.beams.*;
import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.ambient.sounds.CRSounds;
import com.Da_Technomancer.crossroads.items.technomancy.StaffTechnomancy;
import com.Da_Technomancer.essentials.packets.SendLongToClient;
import com.mojang.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
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
public class BeamCannonTileEntity extends AbstractCannonTileEntity{

	@ObjectHolder("beam_cannon")
	public static BlockEntityType<BeamCannonTileEntity> TYPE = null;

	private static final int RANGE = 256;

	//Once again, I find myself wishing java allowed multiple super classes
	//Because we need the cannon stuff, so we have to re-implement all the beam logic

	//Beam stuff
	private long activeCycle;//To prevent tick acceleration and deal with some chunk loading weirdness
	private final BeamUnitStorage[] queued = {new BeamUnitStorage(), new BeamUnitStorage()};
	private BeamUnit readingBeam = BeamUnit.EMPTY;
	//Rendering data, records what was sent to the client on the server
	public Color beamCol = Color.WHITE;
	public int beamSize = 0;//This is the beam radius, not power
	public int beamLength = 0;

	public BeamCannonTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		super.addInfo(chat, player, hit);
		if(!readingBeam.isEmpty()){
			EnumBeamAlignments.getAlignment(readingBeam).discover(player, true);
			chat.add(new TranslatableComponent("tt.crossroads.beam_cannon.beam", readingBeam.toString()));
		}
	}

	@Override
	public void serverTick(){
		super.serverTick();

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
				Direction facing = getBlockState().getValue(CRProperties.FACING);
				//Unit vector pointing in the aimed direction
				Vec3 rayVec3 = getAimedVec();
				//rayTraceSt is offset 'up' by 3.5/16 blocks to match the render
				Vector3f upShift = facing.step();
				upShift.mul(3.5F / 16F);
				Vec3 rayTraceSt = Vec3.atCenterOf(worldPosition).add(upShift.x(), upShift.y(), upShift.z());

				Triple<BlockPos, Vec3, Direction> beamHitResult = StaffTechnomancy.rayTraceBeams(out, level, rayTraceSt, rayTraceSt, rayVec3, null, worldPosition, RANGE);
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
							align.getEffect().doBeamEffect(align, out.getVoid() != 0, Math.min(64, outPower), level, endPos, effectDir, rayVec3);
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
	public AABB getRenderBoundingBox(){
		//Expand the render box to include all possible beams from this block
		return new AABB(worldPosition.offset(-RANGE, -RANGE, -RANGE), worldPosition.offset(1 + RANGE, 1 + RANGE, 1 + RANGE));
	}

	@Override
	public void receiveLong(byte id, long value, @Nullable ServerPlayer sender){
		super.receiveLong(id, value, sender);
		if(id == 3){
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
		}
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		activeCycle = nbt.getLong("cycle");
		queued[0] = BeamUnitStorage.readFromNBT("storage_0", nbt);
		queued[1] = BeamUnitStorage.readFromNBT("storage_1", nbt);
		beamCol = new Color(nbt.getInt("color"));
		beamSize = nbt.getInt("beam_size");
		beamLength = nbt.getInt("beam_length");
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		nbt.putLong("cycle", activeCycle);
		queued[0].writeToNBT("storage_0", nbt);
		queued[1].writeToNBT("storage_1", nbt);
		nbt.putInt("color", beamCol.getRGB());
		nbt.putInt("beam_size", beamSize);
		nbt.putInt("beam_length", beamLength);
	}

	@Override
	public void setBlockState(BlockState stateIn){
		super.setBlockState(stateIn);
		beamOpt.invalidate();
		beamOpt = LazyOptional.of(() -> beamHandler);
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		beamOpt.invalidate();
	}

	private final BeamHandler beamHandler = new BeamHandler();
	private LazyOptional<IBeamHandler> beamOpt = LazyOptional.of(() -> beamHandler);

	@Nonnull
	@Override
	@SuppressWarnings("unchecked")
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side){
		if(cap == Capabilities.BEAM_CAPABILITY){
			return (LazyOptional<T>) beamOpt;
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
}
