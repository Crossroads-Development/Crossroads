package com.Da_Technomancer.crossroads.API.effects;

import java.util.Random;

import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.enums.MagicElements;

import net.minecraft.crash.CrashReport;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;

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

		for(int i = RAND.nextInt((int) mult); i < mult; i++){
			worldIn.getBlockState(pos).getBlock().randomTick(worldIn, pos, worldIn.getBlockState(pos), RAND);
		}
	}

	public static class VoidTimeEffect implements IEffect{

		@Override
		public void doEffect(World worldIn, BlockPos pos, double mult){
			//TODO temporary for testing
			System.out.println("------------------------FLUX_EVENT-----------------------------");
			/*
			
			int severity = RAND.nextInt((int) Math.min(mult, 128)) + 1;

			if(severity >= 100 && ModConfig.voidChunk.getBoolean()){
				try{
					Chunk current = worldIn.getChunkFromBlockCoords(pos);
					for(int x = 0; x < 16; x++){
						for(int z = 0; z < 16; z++){
							for(int y = 0; y < 256; y++){
								BlockPos posAir = new BlockPos(x, y, z);
								current.setBlockState(posAir, Blocks.AIR.getDefaultState());
							}
						}
					}
				}catch(Exception e){
					CrashReport report = new CrashReport("Something went wrong while voiding a chunk. Disable this in the config if necessary, and hope nothing got corrupted.", e);
					new ReportedException(report).printStackTrace();
				}
			}else if(severity > 60 && ModConfig.resetChunk.getBoolean()){
				try{
					Chunk swapWith = ((ChunkProviderServer) worldIn.getChunkProvider()).chunkGenerator.provideChunk(pos.getX() >> 4, pos.getZ() >> 4);
					swapWith.populateChunk(worldIn.getChunkProvider(), ((ChunkProviderServer) worldIn.getChunkProvider()).chunkGenerator);
					Chunk current = worldIn.getChunkFromBlockCoords(pos);
					setChunk(current, swapWith);
				}catch(Exception e){
					CrashReport report = new CrashReport("Something went wrong while reseting a chunk. Disable this in the config if necessary, and hope nothing got corrupted.", e);
					new ReportedException(report).printStackTrace();
				}
			}else if(severity > 10 && ModConfig.magicChunk.getBoolean()){
				try{
					ChunkPos base = worldIn.getChunkFromBlockCoords(pos).getChunkCoordIntPair();
					for(int i = 0; i < severity; i++){
						BlockPos effectPos = base.getBlock(RAND.nextInt(16), RAND.nextInt(256), RAND.nextInt(16));
						MagicElements element = null;
						do{
							element = MagicElements.values()[RAND.nextInt(MagicElements.values().length)];
						}while(element == MagicElements.TIME || element.getEffect() == null);
						element.getEffect().doEffect(worldIn, effectPos, severity);
					}
				}catch(Exception e){
					CrashReport report = new CrashReport("Something went wrong while magic-ifying a chunk. Disable this in the config if necessary, and hope nothing got corrupted.", e);
					new ReportedException(report).printStackTrace();
				}
			}else{
				worldIn.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), severity, false);
			}*/
		}
		
		private static void setChunk(Chunk current, Chunk swapWith){
			for(int x = 0; x < 16; x++){
				for(int z = 0; z < 16; z++){
					for(int y = 0; y < 256; y++){
						BlockPos pos = new BlockPos(x, y, z);
						current.setBlockState(pos, swapWith.getBlockState(pos));
						if(swapWith.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK) != null){
							NBTTagCompound nbt = new NBTTagCompound();
							swapWith.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK).writeToNBT(nbt);
							nbt.setInteger("x", current.getChunkCoordIntPair().getXStart() + pos.getX());
							nbt.setInteger("y", pos.getY());
							nbt.setInteger("z", current.getChunkCoordIntPair().getZStart() + pos.getZ());
							current.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK).readFromNBT(nbt);
						}
					}
				}
			}
			
			
			current.setBiomeArray(swapWith.getBiomeArray());
			current.setModified(true);
			current.checkLight();
		}
	}
}
