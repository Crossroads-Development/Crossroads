package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.API.technomancy.IFluxLink;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.Da_Technomancer.crossroads.API.technomancy.IFluxLink.Behaviour;

@ObjectHolder(Crossroads.MODID)
public class ChunkAcceleratorTileEntity extends IFluxLink.FluxHelper{

	@ObjectHolder("chunk_accelerator")
	public static BlockEntityType<ChunkAcceleratorTileEntity> type = null;

	public static final int FLUX_MULT = 8;

	private int intensity = 0;//Power of the incoming beam
	private int infoIntensity = 0;
	private long lastRunTick;//Used to prevent accelerators affecting each other

	public ChunkAcceleratorTileEntity(){
		super(type, null, Behaviour.SOURCE);
	}

	public void resetCache(){
		beamOpt.invalidate();
		beamOpt = LazyOptional.of(BeamHandler::new);
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		chat.add(new TranslatableComponent("tt.crossroads.time_accel.boost", 100 * extraTicks(infoIntensity)));
		FluxUtil.addFluxInfo(chat, this, producedFlux(infoIntensity));
		super.addInfo(chat, player, hit);
	}

	/**
	 * Calculates how many extra ticks to apply based on intensity
	 * @param power Intensity to use
	 * @return The number of extra ticks to apply
	 */
	private int extraTicks(int power){
		return power / 16;
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

			//Apply time acceleration
			if(extraTicks > 0 && CRConfig.teTimeAccel.get() && !isShutDown()){
				ChunkPos chunkPos = new ChunkPos(worldPosition);
				//List of every tile entity in the chunk which is tickable
				List<BlockEntity> tickables = level.tickableBlockEntities.stream().filter(te -> te instanceof TickableBlockEntity && te.getBlockPos().getX() >> 4 == chunkPos.x && te.getBlockPos().getZ() >> 4 == chunkPos.z).collect(Collectors.toList());
				for(BlockEntity te : tickables){
					TickableBlockEntity tte = (TickableBlockEntity) te;
					for(int run = 0; run < extraTicks; run++){
						tte.tick();
					}
				}
			}
		}
	}

	@Override
	public CompoundTag save(CompoundTag nbt){
		super.save(nbt);
		nbt.putInt("intensity", intensity);
		nbt.putLong("last_run", lastRunTick);
		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundTag nbt){
		super.load(state, nbt);
		intensity = nbt.getInt("intensity");
		lastRunTick = nbt.getLong("last_run");
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
		if(cap == Capabilities.BEAM_CAPABILITY){
			return (LazyOptional<T>) beamOpt;
		}

		return super.getCapability(cap, side);
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
