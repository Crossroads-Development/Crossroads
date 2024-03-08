package com.Da_Technomancer.crossroads.effects.beam_effects;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.CRReflection;
import com.Da_Technomancer.crossroads.api.beams.BeamHit;
import com.Da_Technomancer.crossroads.api.beams.BeamUnit;
import com.Da_Technomancer.crossroads.api.beams.BeamUtil;
import com.Da_Technomancer.crossroads.api.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.api.technomancy.FluxUtil;
import com.Da_Technomancer.essentials.api.ReflectionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
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
				//Note that we only apply this effect once every BeamUtil.BEAM_TIME ticks; need to adjust applied affect to compensate
				//Increase the odds that we apply, and once that caps out, increase number of extra ticks applied at a time
				int cappedPower = Math.min(64, power);
				int extraTicks = cappedPower * BeamUtil.BEAM_TIME / Math.min(64, cappedPower * BeamUtil.BEAM_TIME);
				if(beamHit.getWorld().random.nextInt(64) < cappedPower * BeamUtil.BEAM_TIME){
					TickingBlockEntity ticker = getTicker(beamHit.getWorld(), beamHit.getPos());
					if(ticker != null){
						for(int i = 0; i < extraTicks; i++){
							ticker.tick();
						}
					}

					BlockState state = beamHit.getEndState();
					if(shouldApplyExtraRandomTick(beamHit.getWorld(), state, extraTicks)){
						state.randomTick(beamHit.getWorld(), beamHit.getPos(), beamHit.getWorld().random);
					}
				}
			}
		}
	}

	public static boolean shouldApplyExtraRandomTick(Level level, BlockState state, int extraTicks){
		int randomTickRule = level.getGameRules().getInt(GameRules.RULE_RANDOMTICKING);
		//Blocks have a 16^3/randomTickSpeed chance of a random tick each game tick in vanilla
		return state.isRandomlyTicking() && randomTickRule > 0 && level.random.nextInt(16 * 16 * 16 / level.getGameRules().getInt(GameRules.RULE_RANDOMTICKING)) < extraTicks;
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
