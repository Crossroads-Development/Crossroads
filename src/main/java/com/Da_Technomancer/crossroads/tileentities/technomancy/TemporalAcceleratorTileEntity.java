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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@ObjectHolder(Crossroads.MODID)
public class TemporalAcceleratorTileEntity extends TileEntity implements ITickableTileEntity, IFluxLink{

	@ObjectHolder("temporal_accelerator")
	private static TileEntityType<TemporalAcceleratorTileEntity> type = null;

	public static final int FLUX_MULT = 2;
	public static final int SIZE = 5;

	private int flux = 0;
	private final HashSet<BlockPos> linkPos = new HashSet<>(1);
	private int intensity = 0;//Power of the incoming beam
	private long lastRunTick;//Used to prevent accelerators affecting each other
	//BlockState cache
	private Direction facing;
	private TemporalAccelerator.Mode mode;

	public TemporalAcceleratorTileEntity(){
		super(type);
	}

	public void resetCache(){
		facing = null;
		mode = null;
		beamOpt.invalidate();
		beamOpt = LazyOptional.of(BeamHandler::new);
	}

	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		chat.add(new TranslationTextComponent("tt.crossroads.time_accel.boost", 100 * extraTicks()));
		FluxUtil.addFluxInfo(chat, this, producedFlux());
		FluxUtil.addLinkInfo(chat, this);
	}

	/**
	 * Calculates how many extra ticks to apply based on intensity
	 * @return The number of extra ticks to apply
	 */
	private int extraTicks(){
		return intensity / 4;
	}

	/**
	 * Calculates the current flux production based on intensity
	 * @return The flux to be produced this cycle
	 */
	private int producedFlux(){
		return (int) Math.pow(2, extraTicks()) * FLUX_MULT;
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayerEntity sendingPlayer){
		if(identifier == LINK_PACKET_ID){
			linkPos.add(BlockPos.fromLong(message));
			markDirty();
		}else if(identifier == CLEAR_PACKET_ID){
			linkPos.clear();
			markDirty();
		}
	}

	private Direction getFacing(){
		if(facing == null){
			BlockState state = world.getBlockState(pos);
			if(!(state.getBlock() instanceof TemporalAccelerator)){
				remove();
				return Direction.DOWN;
			}
			facing = state.get(ESProperties.FACING);
			mode = state.get(CRProperties.ACCELERATOR_TARGET);
		}

		return facing;
	}

	private TemporalAccelerator.Mode getMode(){
		if(facing == null){
			BlockState state = world.getBlockState(pos);
			if(!(state.getBlock() instanceof TemporalAccelerator)){
				remove();
				return TemporalAccelerator.Mode.ENTITIES;
			}
			facing = state.get(ESProperties.FACING);
			mode = state.get(CRProperties.ACCELERATOR_TARGET);
		}

		return mode;
	}

	@Override
	public void tick(){
		if(!world.isRemote && world.getGameTime() != lastRunTick){
			lastRunTick = world.getGameTime();
			int extraTicks = extraTicks();

			if(world.getGameTime() % FluxUtil.FLUX_TIME == 1){
				FluxUtil.performTransfer(this, linkPos);
			}else if(world.getGameTime() % FluxUtil.FLUX_TIME == 0){
				//Sped up time
				addFlux(producedFlux());
				intensity = 0;//Reset stored beam power
				markDirty();
				FluxUtil.checkFluxOverload(this);
			}

			TemporalAccelerator.Mode mode = getMode();

			if(extraTicks > 0){
				BlockPos startPos;//Inclusive
				BlockPos endPos;//Exclusive
				//Assumes SIZE is odd
				switch(getFacing()){
					//I'm sure there's a clever formula for this, but I don't see it
					case DOWN:
						startPos = pos.add(-SIZE / 2, -SIZE, -SIZE / 2);
						endPos = pos.add(SIZE / 2 + 1, 0, SIZE / 2 + 1);
						break;
					case UP:
						startPos = pos.add(-SIZE / 2, 1, -SIZE / 2);
						endPos = pos.add(SIZE / 2 + 1, 1 + SIZE, SIZE / 2 + 1);
						break;
					case NORTH:
						startPos = pos.add(-SIZE / 2, -SIZE / 2, -SIZE);
						endPos = pos.add(SIZE / 2 + 1, SIZE / 2 + 1, 0);
						break;
					case SOUTH:
						startPos = pos.add(-SIZE / 2, -SIZE / 2, 1);
						endPos = pos.add(SIZE / 2 + 1, SIZE / 2 + 1, 1 + SIZE);
						break;
					case WEST:
						startPos = pos.add(-SIZE, -SIZE / 2, -SIZE / 2);
						endPos = pos.add(0, SIZE / 2 + 1, SIZE / 2 + 1);
						break;
					case EAST:
					default://Should not occur
						startPos = pos.add(1, -SIZE / 2, -SIZE / 2);
						endPos = pos.add(1 + SIZE, SIZE / 2 + 1, SIZE / 2 + 1);
						break;
				}

				if(mode.accelerateEntities){
					AxisAlignedBB bb = new AxisAlignedBB(startPos, endPos);
					//Perform entity effect
					ArrayList<Entity> ents = (ArrayList<Entity>) world.getEntitiesWithinAABB(Entity.class, bb);

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
									TileEntity te = world.getTileEntity(effectPos);
									if(te instanceof ITickableTileEntity){
										for(int run = 0; run < extraTicks; run++){
											((ITickableTileEntity) te).tick();
										}
									}
								}

								//Perform block tick effect
								if(mode.accelerateBlockTicks){
									BlockState state = world.getBlockState(effectPos);
									//Blocks have a 16^3/randomTickSpeed chance of a random tick each game tick in vanilla
									if(state.ticksRandomly() && world.rand.nextInt(16 * 16 * 16 / world.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED)) < extraTicks){
										state.randomTick(world, effectPos, world.rand);
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
	public void remove(){
		super.remove();
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
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putInt("intensity", intensity);
		nbt.putLong("last_run", lastRunTick);
		nbt.putInt("flux", flux);
		for(BlockPos linked : linkPos){//Size 0 or 1
			nbt.putLong("link", linked.toLong());
		}
		return nbt;
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		intensity = nbt.getInt("intensity");
		lastRunTick = nbt.getLong("last_run");
		flux = nbt.getInt("flux");
		if(nbt.contains("link")){
			linkPos.add(BlockPos.fromLong(nbt.getLong("link")));
		}else{
			linkPos.clear();
		}
	}

	@Override
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
		for(BlockPos linked : linkPos){//Size 0 or 1
			nbt.putLong("link", linked.toLong());
		}
		return nbt;
	}

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
		return linkPos;
	}

	private class BeamHandler implements IBeamHandler{

		@Override
		public void setBeam(BeamUnit mag){
			if(mag != null && EnumBeamAlignments.getAlignment(mag) == EnumBeamAlignments.TIME){
				if(mag.getVoid() == 0){
					intensity += mag.getPower();//Speed up time
				}else{
					intensity -= mag.getPower();//Slow down time
				}
				markDirty();
			}
		}
	}
}
