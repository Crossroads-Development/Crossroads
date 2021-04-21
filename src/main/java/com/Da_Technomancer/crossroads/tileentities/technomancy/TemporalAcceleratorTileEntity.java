package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.packets.SendPlayerTickCountToClient;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.API.technomancy.IFluxLink;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.technomancy.TemporalAccelerator;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import java.util.ArrayList;

@ObjectHolder(Crossroads.MODID)
public class TemporalAcceleratorTileEntity extends IFluxLink.FluxHelper{

	@ObjectHolder("temporal_accelerator")
	public static TileEntityType<TemporalAcceleratorTileEntity> type = null;

	public static final int FLUX_MULT = 2;
	public static final int SIZE = 5;

	private int intensity = 0;//Power of the incoming beam
	private int infoIntensity = 0;
	private long lastRunTick;//Used to prevent accelerators affecting each other
	//BlockState cache
	private Direction facing;
	private TemporalAccelerator.Mode mode;

	public TemporalAcceleratorTileEntity(){
		super(type, null, Behaviour.SOURCE);
	}

	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		chat.add(new TranslationTextComponent("tt.crossroads.time_accel.boost", 100 * extraTicks(infoIntensity)));
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
	public AxisAlignedBB getRenderBoundingBox(){
		//Increase render BB to include links
		return new AxisAlignedBB(worldPosition).inflate(getRange());
	}

	private Direction getFacing(){
		if(facing == null){
			BlockState state = getBlockState();
			if(!(state.getBlock() instanceof TemporalAccelerator)){
				setRemoved();
				return Direction.DOWN;
			}
			facing = state.getValue(ESProperties.FACING);
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
			facing = state.getValue(ESProperties.FACING);
			mode = state.getValue(CRProperties.ACCELERATOR_TARGET);
		}

		return mode;
	}

	@Override
	public void tick(){
		//Handle flux
		super.tick();

		if(!level.isClientSide && level.getGameTime() != lastRunTick){
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
					//I'm sure there's a clever formula for this, but I don't see it
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
					AxisAlignedBB bb = new AxisAlignedBB(startPos, endPos);
					//Perform entity effect
					ArrayList<Entity> ents = (ArrayList<Entity>) level.getEntitiesOfClass(Entity.class, bb);

					for(Entity ent : ents){
						if(ent instanceof ServerPlayerEntity){
							//Players have to tick on both the client and server side or things act very strange
							CRPackets.sendPacketToPlayer((ServerPlayerEntity) ent, new SendPlayerTickCountToClient(extraTicks + 1));
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
									TileEntity te = level.getBlockEntity(effectPos);
									if(te instanceof ITickableTileEntity){
										for(int run = 0; run < extraTicks; run++){
											((ITickableTileEntity) te).tick();
										}
									}
								}

								//Perform block tick effect
								if(mode.accelerateBlockTicks){
									BlockState state = level.getBlockState(effectPos);
									//Blocks have a 16^3/randomTickSpeed chance of a random tick each game tick in vanilla
									if(state.isRandomlyTicking() && level.random.nextInt(16 * 16 * 16 / level.getGameRules().getInt(GameRules.RULE_RANDOMTICKING)) < extraTicks){
										state.randomTick((ServerWorld) level, effectPos, level.random);
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
	public void clearCache(){
		super.clearCache();
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
	public CompoundNBT save(CompoundNBT nbt){
		super.save(nbt);
		nbt.putInt("intensity", intensity);
		nbt.putLong("last_run", lastRunTick);
		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt){
		super.load(state, nbt);
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
