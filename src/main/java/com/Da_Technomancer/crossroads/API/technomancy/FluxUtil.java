package com.Da_Technomancer.crossroads.API.technomancy;

import com.Da_Technomancer.crossroads.API.beams.BeamManager;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.dimensions.PrototypeWorldProvider;
import com.Da_Technomancer.crossroads.dimensions.WorkspaceWorldProvider;
import com.Da_Technomancer.crossroads.render.RenderUtil;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import org.apache.logging.log4j.Level;

import java.awt.*;
import java.util.Random;

public class FluxUtil{

	public static final int FLUX_TIME = BeamManager.BEAM_TIME;
	private static final Random RAND = new Random();
	private static final int[] fluxCol = new int[] {new Color(255, 0, 0, 255).getRGB(), new Color(255, 240, 240, 255).getRGB(), new Color(255, 150, 0, 255).getRGB()};

	public static void renderFlux(World world, BlockPos startPos, BlockPos endPos, int flux){
		RenderUtil.addArc(world.provider.getDimension(), startPos.getX() + 0.5F, startPos.getY() + 0.5F, startPos.getZ() + 0.5F, endPos.getX() + 0.5F, endPos.getY() + 0.5F, endPos.getZ() + 0.5F, startPos.getX() + 0.5F, startPos.getY() + 0.5F, startPos.getZ() + 0.5F, (int) Math.ceil(flux / 8D), 0.25F, (byte) FLUX_TIME, fluxCol[RAND.nextInt(3)]);
	}

	public static void fluxEvent(World worldIn, BlockPos pos, int intensity){
		if(worldIn.provider instanceof WorkspaceWorldProvider || worldIn.provider instanceof PrototypeWorldProvider){
			return;
		}

		int severity = RAND.nextInt(intensity) + 1;

		if(severity >= 50 && ModConfig.voidChunk.getBoolean()){
			ChunkPos chunkPos = new ChunkPos(pos);
			for(int x = 0; x < 16; x++){
				for(int z = 0; z < 16; z++){
					for(int y = 0; y < 256; y++){
						worldIn.setBlockState(chunkPos.getBlock(x, y, z), Blocks.AIR.getDefaultState());
					}
				}
			}
		}else if(severity >= 30 && ModConfig.resetChunk.getBoolean()){
			try{
				Chunk swapWith = ((ChunkProviderServer) worldIn.getChunkProvider()).chunkGenerator.generateChunk(pos.getX() >> 4, pos.getZ() >> 4);
				swapWith.populate(worldIn.getChunkProvider(), ((ChunkProviderServer) worldIn.getChunkProvider()).chunkGenerator);
				Chunk current = worldIn.getChunk(pos);
				setChunk(current, swapWith);
			}catch(Exception e){
				Main.logger.log(Level.ERROR, "Something went wrong while reseting a chunk. Disable this in the config if necessary. Please report this as a bug.", e);
			}
		}else if(severity >= 5 && ModConfig.magicChunk.getBoolean()){
			ChunkPos base = worldIn.getChunk(pos).getPos();
			for(int i = 0; i < severity; i++){
				BlockPos effectPos = base.getBlock(RAND.nextInt(16), RAND.nextInt(256), RAND.nextInt(16));
				EnumBeamAlignments element;
				do{
					element = EnumBeamAlignments.values()[RAND.nextInt(EnumBeamAlignments.values().length)];
				}while(element == EnumBeamAlignments.TIME || element.getEffect() == null);
				element.getEffect().doEffect(worldIn, effectPos, severity);
			}
		}else if(ModConfig.blastChunk.getBoolean()){
			worldIn.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), severity, true);
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
