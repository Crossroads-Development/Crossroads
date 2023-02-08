package com.Da_Technomancer.crossroads.effects.beam_effects;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.CRReflection;
import com.Da_Technomancer.crossroads.api.beams.BeamHit;
import com.Da_Technomancer.crossroads.api.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.api.technomancy.FluxUtil;
import com.Da_Technomancer.essentials.api.ReflectionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class TimeEffect extends BeamEffect{

	private static final Field chunkTickerField = ReflectionUtil.reflectField(CRReflection.CHUNK_TICKER_MAP);

	@Override
	public void doBeamEffect(EnumBeamAlignments align, boolean voi, int power, BeamHit beamHit){
		if(!performTransmute(align, voi, power, beamHit)){
			if(voi){
				FluxUtil.fluxEvent(beamHit.getWorld(), beamHit.getPos());
			}else{
				if(beamHit.getWorld().random.nextInt(64) < power){
					TickingBlockEntity ticker = getTicker(beamHit.getWorld(), beamHit.getPos());
					if(ticker != null){
						ticker.tick();
					}

					BlockState state = beamHit.getEndState();
					if(state.isRandomlyTicking()){
						state.randomTick(beamHit.getWorld(), beamHit.getPos(), beamHit.getWorld().random);
					}
				}
			}
		}
	}

	@Nullable
	public static TickingBlockEntity getTicker(Level world, BlockPos pos){
		ChunkPos chunk = new ChunkPos(pos);
		return getChunkTickers(world, chunk).get(pos);
	}

	@Nonnull
	public static Map<BlockPos, ? extends TickingBlockEntity> getChunkTickers(Level world, ChunkPos chunkPos){
		if(chunkTickerField != null){
			LevelChunk chunk = world.getChunk(chunkPos.x, chunkPos.z);
			try{
				return (Map<BlockPos, ? extends TickingBlockEntity>) chunkTickerField.get(chunk);
			}catch(IllegalAccessException | ClassCastException e){
				Crossroads.logger.catching(e);
			}
		}
		return new HashMap<>(0);//Empty map to not return null
	}
}
