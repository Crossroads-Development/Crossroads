package com.Da_Technomancer.crossroads.API.effects;

import java.util.Random;

import com.Da_Technomancer.crossroads.API.Capabilities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class TimeEffect implements IEffect{

	private static final Random RAND = new Random();

	@Override
	public void doEffect(World worldIn, BlockPos pos, double mult){
		if(worldIn.getTileEntity(pos) instanceof ITickable){
			for(EnumFacing dir : EnumFacing.values()){
				if(worldIn.getTileEntity(pos).hasCapability(Capabilities.MAGIC_HANDLER_CAPABILITY, dir)){
					return;
				}
			}

			for(int i = RAND.nextInt((int) mult); i < mult; i++){
				((ITickable) worldIn.getTileEntity(pos)).update();
				if(!(worldIn.getTileEntity(pos) instanceof ITickable)){
					break;
				}
			}
		}

		if(worldIn.getBlockState(pos).getBlock().getTickRandomly()){
			for(int i = RAND.nextInt((int) mult); i < mult; i++){
				worldIn.getBlockState(pos).getBlock().randomTick(worldIn, pos, worldIn.getBlockState(pos), RAND);
			}
		}
	}

	public static class VoidTimeEffect implements IEffect{

		@Override
		public void doEffect(World worldIn, BlockPos pos, double mult){
			//TODO temporary for testing
			System.out.println("------------------------FLUX_EVENT-----------------------------");
			/*

			int severity = worldIn.provider instanceof WorkspaceWorldProvider ? 3 : RAND.nextInt((int) Math.min(mult, 128)) + 1;

			if(severity >= 100 && ModConfig.voidChunk.getBoolean()){
				ChunkPos chunkPos = new ChunkPos(pos);
				for(int x = 0; x < 16; x++){
					for(int z = 0; z < 16; z++){
						for(int y = 0; y < 256; y++){
							worldIn.setBlockState(chunkPos.getBlock(x, y, z), Blocks.AIR.getDefaultState());
						}
					}
				}
			}else if(severity > 60 && ModConfig.resetChunk.getBoolean()){
				try{
					Chunk swapWith = ((ChunkProviderServer) worldIn.getChunkProvider()).chunkGenerator.provideChunk(pos.getX() >> 4, pos.getZ() >> 4);
					swapWith.populateChunk(worldIn.getChunkProvider(), ((ChunkProviderServer) worldIn.getChunkProvider()).chunkGenerator);
					Chunk current = worldIn.getChunkFromBlockCoords(pos);
					setChunk(current, swapWith);
				}catch(Exception e){
					Main.logger.log(Level.ERROR, "Something went wrong while reseting a chunk. Disable this in the config if necessary. Please report this as a bug.", e);
				}
			}else if(severity > 10 && ModConfig.magicChunk.getBoolean()){
				ChunkPos base = worldIn.getChunkFromBlockCoords(pos).getPos();
				for(int i = 0; i < severity; i++){
					BlockPos effectPos = base.getBlock(RAND.nextInt(16), RAND.nextInt(256), RAND.nextInt(16));
					MagicElements element = null;
					do{
						element = MagicElements.values()[RAND.nextInt(MagicElements.values().length)];
					}while(element == MagicElements.TIME || element.getEffect() == null);
					element.getEffect().doEffect(worldIn, effectPos, severity);
				}
			}else{
				worldIn.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), severity, false);
			}*/
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
