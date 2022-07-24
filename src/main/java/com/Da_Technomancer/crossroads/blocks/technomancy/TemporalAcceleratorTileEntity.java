package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.beams.BeamUnit;
import com.Da_Technomancer.crossroads.api.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.api.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.api.packets.CRPackets;
import com.Da_Technomancer.crossroads.api.packets.SendPlayerTickCountToClient;
import com.Da_Technomancer.crossroads.api.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.api.technomancy.IFluxLink;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.effects.beam_effects.TimeEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import java.util.ArrayList;

public class TemporalAcceleratorTileEntity extends IFluxLink.FluxHelper{

	public static final BlockEntityType<TemporalAcceleratorTileEntity> TYPE = CRTileEntity.createType(TemporalAcceleratorTileEntity::new, CRBlocks.temporalAccelerator);

	public static final int FLUX_MULT = 2;
	public static final int SIZE = 5;

	private int intensity = 0;//Power of the incoming beam
	private int infoIntensity = 0;
	private long lastRunTick;//Used to prevent accelerators affecting each other
	//BlockState cache
	private Direction facing;
	private TemporalAccelerator.Mode mode;

	public TemporalAcceleratorTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, null, Behaviour.SOURCE);
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		chat.add(Component.translatable("tt.crossroads.time_accel.boost", 100 * extraTicks(infoIntensity)));
		FluxUtil.addFluxInfo(chat, this, producedFlux(infoIntensity));
		super.addInfo(chat, player, hit);
	}

	/**
	 * Calculates how many extra ticks to apply based on intensity
	 * @param power Intensity to use
	 * @return The number of extra ticks to apply
	 */
	private int extraTicks(int power){
		return power / 4;
	}

	/**
	 * Calculates the current flux production based on intensity
	 * @param power Intensity to use
	 * @return The flux to be produced this cycle
	 */
	private int producedFlux(int power){
		int boost = extraTicks(power);
		if(boost > 0){
			return (int) Math.pow(2, boost) * FLUX_MULT;
		}
		return 0;
	}

	@Override
	public AABB getRenderBoundingBox(){
		//Increase render BB to include links
		return new AABB(worldPosition).inflate(getRange());
	}

	private Direction getFacing(){
		if(facing == null){
			BlockState state = getBlockState();
			if(!(state.getBlock() instanceof TemporalAccelerator)){
				setRemoved();
				return Direction.DOWN;
			}
			facing = state.getValue(CRProperties.FACING);
			mode = state.getValue(CRProperties.ACCELERATOR_TARGET);
		}

		return facing;
	}

	private TemporalAccelerator.Mode getMode(){
		if(facing == null){
			BlockState state = getBlockState();
			if(!(state.getBlock() instanceof TemporalAccelerator)){
				setRemoved();
				return TemporalAccelerator.Mode.ENTITIES;
			}
			facing = state.getValue(CRProperties.FACING);
			mode = state.getValue(CRProperties.ACCELERATOR_TARGET);
		}

		return mode;
	}

	@Override
	public void serverTick(){
		//Handle flux
		super.serverTick();

		if(level.getGameTime() != lastRunTick){
			//Prevent time acceleration of this block
			lastRunTick = level.getGameTime();
			int extraTicks = extraTicks(intensity);

			if(level.getGameTime() % FluxUtil.FLUX_TIME == 0){
				addFlux(producedFlux(intensity));
				infoIntensity = intensity;
				intensity = 0;//Reset stored beam power
			}

			TemporalAccelerator.Mode mode = getMode();

			if(extraTicks > 0 && !isShutDown()){
				BlockPos startPos;//Inclusive
				BlockPos endPos;//Exclusive
				//Assumes SIZE is odd
				switch(getFacing()){
					//This should probably be done as a simple formula if it ever gets rewritten. See AutoInjectorTileEntity for an example
					case DOWN:
						startPos = worldPosition.offset(-SIZE / 2, -SIZE, -SIZE / 2);
						endPos = worldPosition.offset(SIZE / 2 + 1, 0, SIZE / 2 + 1);
						break;
					case UP:
						startPos = worldPosition.offset(-SIZE / 2, 1, -SIZE / 2);
						endPos = worldPosition.offset(SIZE / 2 + 1, 1 + SIZE, SIZE / 2 + 1);
						break;
					case NORTH:
						startPos = worldPosition.offset(-SIZE / 2, -SIZE / 2, -SIZE);
						endPos = worldPosition.offset(SIZE / 2 + 1, SIZE / 2 + 1, 0);
						break;
					case SOUTH:
						startPos = worldPosition.offset(-SIZE / 2, -SIZE / 2, 1);
						endPos = worldPosition.offset(SIZE / 2 + 1, SIZE / 2 + 1, 1 + SIZE);
						break;
					case WEST:
						startPos = worldPosition.offset(-SIZE, -SIZE / 2, -SIZE / 2);
						endPos = worldPosition.offset(0, SIZE / 2 + 1, SIZE / 2 + 1);
						break;
					case EAST:
					default://Should not occur
						startPos = worldPosition.offset(1, -SIZE / 2, -SIZE / 2);
						endPos = worldPosition.offset(1 + SIZE, SIZE / 2 + 1, SIZE / 2 + 1);
						break;
				}

				if(mode.accelerateEntities){
					AABB bb = new AABB(startPos, endPos);
					//Perform entity effect
					ArrayList<Entity> ents = (ArrayList<Entity>) level.getEntitiesOfClass(Entity.class, bb);

					for(Entity ent : ents){
						if(ent instanceof ServerPlayer){
							//Players have to tick on both the client and server side or things act very strange
							CRPackets.sendPacketToPlayer((ServerPlayer) ent, new SendPlayerTickCountToClient(extraTicks + 1));
						}
						for(int i = 0; i < extraTicks; i++){
							ent.tick();
						}
					}
				}

				boolean actOnTe = mode.accelerateTileEntities && CRConfig.teTimeAccel.get();
				if(actOnTe || mode.accelerateBlockTicks){
					//Iterate over the entire affected region
					for(int x = startPos.getX(); x < endPos.getX(); x++){
						for(int y = startPos.getY(); y < endPos.getY(); y++){
							for(int z = startPos.getZ(); z < endPos.getZ(); z++){
								BlockPos effectPos = new BlockPos(x, y, z);

								//Perform tile entity effect
								if(actOnTe){
									TickingBlockEntity te = TimeEffect.getTicker(level, effectPos);
									if(te != null){
										for(int run = 0; run < extraTicks; run++){
											te.tick();
										}
									}
								}

								//Perform block tick effect
								if(mode.accelerateBlockTicks){
									BlockState state = level.getBlockState(effectPos);
									//Blocks have a 16^3/randomTickSpeed chance of a random tick each game tick in vanilla
									if(state.isRandomlyTicking() && level.random.nextInt(16 * 16 * 16 / level.getGameRules().getInt(GameRules.RULE_RANDOMTICKING)) < extraTicks){
										state.randomTick((ServerLevel) level, effectPos, level.random);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void setBlockState(BlockState stateIn){
		super.setBlockState(stateIn);
		facing = null;
		mode = null;
		beamOpt.invalidate();
		beamOpt = LazyOptional.of(BeamHandler::new);
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		beamOpt.invalidate();
	}

	private LazyOptional<IBeamHandler> beamOpt = LazyOptional.of(BeamHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.BEAM_CAPABILITY && (side == null || side == getFacing().getOpposite())){
			return (LazyOptional<T>) beamOpt;
		}

		return super.getCapability(cap, side);
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		nbt.putInt("intensity", intensity);
		nbt.putLong("last_run", lastRunTick);
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		intensity = nbt.getInt("intensity");
		lastRunTick = nbt.getLong("last_run");
	}

	private class BeamHandler implements IBeamHandler{

		@Override
		public void setBeam(BeamUnit mag){
			if(mag != null && EnumBeamAlignments.getAlignment(mag) == EnumBeamAlignments.TIME){
				if(mag.getVoid() == 0){
					intensity += mag.getPower();//Speed up time
				}
				setChanged();
			}
		}
	}
}
