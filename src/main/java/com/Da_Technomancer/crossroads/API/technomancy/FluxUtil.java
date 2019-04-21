package com.Da_Technomancer.crossroads.API.technomancy;

import com.Da_Technomancer.crossroads.API.beams.BeamManager;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.dimensions.PrototypeWorldProvider;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import org.apache.logging.log4j.Level;

import java.util.Random;

public class FluxUtil{

	public static final int FLUX_TIME = BeamManager.BEAM_TIME;
	private static final Random RAND = new Random();

	public static int getFePerFlux(boolean client){
		return ModConfig.getConfigInt(ModConfig.fePerFlux, client);
	}

	public static void overloadFlux(World worldIn, BlockPos pos){
		worldIn.destroyBlock(pos, ModConfig.getConfigBool(ModConfig.entropyDropBlock, false));
		fluxEvent(worldIn, pos, RAND.nextInt(64) + 1);
	}

	public static void fluxEvent(World worldIn, BlockPos pos, int intensity){
		if(worldIn.provider instanceof PrototypeWorldProvider){
			return;
		}

		if(intensity >= 50 && ModConfig.voidChunk.getBoolean()){
			ChunkPos chunkPos = new ChunkPos(pos);
			for(int x = 0; x < 16; x++){
				for(int z = 0; z < 16; z++){
					for(int y = 0; y < 256; y++){
						worldIn.setBlockState(chunkPos.getBlock(x, y, z), Blocks.AIR.getDefaultState());
					}
				}
			}
		}else if(intensity >= 30 && ModConfig.resetChunk.getBoolean()){
			try{
				//The chunk at world gen
				ChunkPos chunkPos = new ChunkPos(pos);
				ChunkProviderServer provider = ((ChunkProviderServer) worldIn.getChunkProvider());
				Chunk swapWith = provider.chunkGenerator.generateChunk(chunkPos.x, chunkPos.z);//provider.provideChunk(chunkPos.x, chunkPos.z);//.generateChunk(pos.getX() >> 4, pos.getZ() >> 4);
				//The chunk currently
				Chunk current = worldIn.getChunk(pos);
				//Perform the swap
				setChunk(current, swapWith);
				current.setTerrainPopulated(false);
				current.populate(provider, provider.chunkGenerator);
				current.setTerrainPopulated(true);
				current.populate(provider, provider.chunkGenerator);
			}catch(Exception e){
				Main.logger.log(Level.ERROR, "Something went wrong while resetting a chunk. Disable this in the config if necessary. Please report this as a bug.", e);
			}
		}else if(intensity >= 5 && ModConfig.magicChunk.getBoolean()){
			ChunkPos base = worldIn.getChunk(pos).getPos();
			for(int i = 0; i < 64; i++){
				BlockPos effectPos = base.getBlock(RAND.nextInt(16), RAND.nextInt(256), RAND.nextInt(16));
				EnumBeamAlignments element;
				do{
					element = EnumBeamAlignments.values()[RAND.nextInt(EnumBeamAlignments.values().length)];
				}while(element == EnumBeamAlignments.TIME || element.getEffect() == null);
				element.getEffect().doEffect(worldIn, effectPos, intensity, null);
			}
		}else if(ModConfig.blastChunk.getBoolean()){
			worldIn.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), intensity, true);
		}else{
			Main.logger.info("There would have been a flux event at " + pos.toString() + " in dimension " + worldIn.provider.getDimension() + " of severity " + intensity + ", but the relevant flux event is disabled in the config. Lucky you.");
		}
	}

	private static void setChunk(Chunk copyTo, Chunk copyFrom){
		World tarWorld = copyTo.getWorld();
		ChunkPos tarCPos = copyTo.getPos();
		ChunkPos srcCPos = copyFrom.getPos();
		for(int x = 0; x < 16; x++){
			for(int z = 0; z < 16; z++){
				for(int y = 255; y >= 0; y--){
					BlockPos newPos = tarCPos.getBlock(x, y, z);
					BlockPos oldPos = srcCPos.getBlock(x, y, z);
					tarWorld.setBlockState(newPos, copyFrom.getBlockState(oldPos));
					TileEntity oldTe = copyFrom.getTileEntity(oldPos, Chunk.EnumCreateEntityType.CHECK);
					if(oldTe != null){
						NBTTagCompound nbt = new NBTTagCompound();
						oldTe.writeToNBT(nbt);
						nbt.setInteger("x", newPos.getX());
						nbt.setInteger("y", newPos.getY());
						nbt.setInteger("z", newPos.getZ());
						TileEntity newTe = tarWorld.getTileEntity(newPos);
						if(newTe != null){
							newTe.readFromNBT(nbt);
						}
					}
				}
			}
		}

		copyTo.setModified(true);
		copyTo.checkLight();
	}
}
