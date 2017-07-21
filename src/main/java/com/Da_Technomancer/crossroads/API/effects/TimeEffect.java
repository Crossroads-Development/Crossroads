package com.Da_Technomancer.crossroads.API.effects;

import java.util.Random;

import org.apache.logging.log4j.Level;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.enums.MagicElements;
import com.Da_Technomancer.crossroads.API.magic.BeamManager;
import com.Da_Technomancer.crossroads.dimensions.WorkspaceWorldProvider;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;

public class TimeEffect implements IEffect{

	private static final Random RAND = new Random();

	@Override
	public void doEffect(World worldIn, BlockPos pos, double mult){
		TileEntity te = worldIn.getTileEntity(pos);
		if(te instanceof ITickable){
			for(EnumFacing dir : EnumFacing.values()){
				if(te.hasCapability(Capabilities.MAGIC_HANDLER_CAPABILITY, dir)){
					return;
				}
			}

			for(int i = 0; i < mult * BeamManager.BEAM_TIME; i++){
				//Each tick the TileEntity is queried again because some TileEntities destroy themselves on tick. 
				((ITickable) worldIn.getTileEntity(pos)).update();
				if(!(worldIn.getTileEntity(pos) instanceof ITickable)){
					break;
				}
			}
		}

		if(worldIn.getBlockState(pos).getBlock().getTickRandomly()){
			for(int i = 0; i < mult * BeamManager.BEAM_TIME; i++){
				worldIn.getBlockState(pos).getBlock().randomTick(worldIn, pos, worldIn.getBlockState(pos), RAND);
			}
		}
	}

	public static class VoidTimeEffect implements IEffect{

		@Override
		public void doEffect(World worldIn, BlockPos pos, double mult){
			if(worldIn.provider instanceof WorkspaceWorldProvider){
				return;
			}
			
			int severity = RAND.nextInt((int) mult) + 1;

			if(severity >= 50 && ModConfig.voidChunk.getBoolean()){
				ChunkPos chunkPos = new ChunkPos(pos);
				for(int x = 0; x < 16; x++){
					for(int z = 0; z < 16; z++){
						for(int y = 0; y < 256; y++){
							worldIn.setBlockState(chunkPos.getBlock(x, y, z), Blocks.AIR.getDefaultState());
						}
					}
				}
			}else if(severity > 30 && ModConfig.resetChunk.getBoolean()){
				try{
					Chunk swapWith = ((ChunkProviderServer) worldIn.getChunkProvider()).chunkGenerator.generateChunk(pos.getX() >> 4, pos.getZ() >> 4);
					swapWith.populate(worldIn.getChunkProvider(), ((ChunkProviderServer) worldIn.getChunkProvider()).chunkGenerator);
					Chunk current = worldIn.getChunkFromBlockCoords(pos);
					setChunk(current, swapWith);
				}catch(Exception e){
					Main.logger.log(Level.ERROR, "Something went wrong while reseting a chunk. Disable this in the config if necessary. Please report this as a bug.", e);
				}
			}else if(severity > 5 && ModConfig.magicChunk.getBoolean()){
				ChunkPos base = worldIn.getChunkFromBlockCoords(pos).getPos();
				for(int i = 0; i < severity; i++){
					BlockPos effectPos = base.getBlock(RAND.nextInt(16), RAND.nextInt(256), RAND.nextInt(16));
					MagicElements element = null;
					do{
						element = MagicElements.values()[RAND.nextInt(MagicElements.values().length)];
					}while(element == MagicElements.TIME || element.getEffect() == null);
					element.getEffect().doEffect(worldIn, effectPos, severity);
				}
			}else if(ModConfig.blastChunk.getBoolean()){
				worldIn.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), severity, false);
			}else{
				Main.logger.info("There would have been a flux event at " + pos.toString() + " in dimension " + worldIn.provider.getDimension() + " of severity " + severity + ", but the relevant flux event is disabled in the config. Lucky you.");
			}
		}

		private static void setChunk(Chunk copyTo, Chunk copyFrom){
			for(int x = 0; x < 16; x++){
				for(int z = 0; z < 16; z++){
					for(int y = 0; y < 256; y++){
						BlockPos newPos = copyTo.getPos().getBlock(x, y, z);
						BlockPos oldPos = copyFrom.getPos().getBlock(x, y, z);
						copyTo.getWorld().setBlockState(newPos, copyTo.getWorld().getBlockState(oldPos));
						TileEntity oldTe = copyTo.getWorld().getTileEntity(oldPos);
						if(oldTe != null){
							NBTTagCompound nbt = new NBTTagCompound();
							oldTe.writeToNBT(nbt);
							nbt.setInteger("x", newPos.getX());
							nbt.setInteger("y", newPos.getY());
							nbt.setInteger("z", newPos.getZ());
							TileEntity newTe = copyTo.getWorld().getTileEntity(newPos);
							newTe.readFromNBT(nbt);
						}
					}
				}
			}

			copyTo.setModified(true);
			copyTo.checkLight();
		}
	}
}
