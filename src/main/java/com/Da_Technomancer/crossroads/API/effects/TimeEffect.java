package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.CRReflection;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.ReflectionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
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

	//TODO test
	private static final Field chunkTickerField = ReflectionUtil.reflectField(CRReflection.CHUNK_TICKER_MAP);

	@Override
	public void doBeamEffect(EnumBeamAlignments align, boolean voi, int power, Level worldIn, BlockPos pos, @Nullable Direction dir){
		if(!performTransmute(align, voi, power, worldIn, pos)){
			if(voi){
				FluxUtil.fluxEvent(worldIn, pos);
			}else{
				if(worldIn.random.nextInt(64) < power){
					TickingBlockEntity ticker = getTicker(worldIn, pos);
					if(ticker != null){
						ticker.tick();
					}

					BlockState state = worldIn.getBlockState(pos);
					if(state.isRandomlyTicking()){
						state.randomTick((ServerLevel) worldIn, pos, worldIn.random);
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
