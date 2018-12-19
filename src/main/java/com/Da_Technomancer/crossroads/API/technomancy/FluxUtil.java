package com.Da_Technomancer.crossroads.API.technomancy;

import com.Da_Technomancer.crossroads.API.beams.BeamManager;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.dimensions.PrototypeWorldProvider;
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
import java.util.ArrayList;
import java.util.Random;

public class FluxUtil{

	public static final int FLUX_TIME = BeamManager.BEAM_TIME;
	private static final int[] FLUX_COLOR = new int[] {new Color(255, 0, 0, 255).getRGB(), new Color(255, 90, 0, 255).getRGB(), new Color(255, 70, 0, 255).getRGB()};
	private static final Random RAND = new Random();

	public static void renderFlux(World world, BlockPos startPos, BlockPos endPos, int flux){
		RenderUtil.addArc(world.provider.getDimension(), startPos.getX() + 0.5F, startPos.getY() + 0.5F, startPos.getZ() + 0.5F, endPos.getX() + 0.5F, endPos.getY() + 0.5F, endPos.getZ() + 0.5F, startPos.getX() + 0.5F, startPos.getY() + 0.5F, startPos.getZ() + 0.5F, (int) Math.ceil(flux / 8D), 0.25F, (byte) FLUX_TIME, FLUX_COLOR[RAND.nextInt(3)]);
	}

	public static int transFlux(World world, BlockPos pos, ArrayList<BlockPos> links, int flux){
		IFluxHandler[] handlers = new IFluxHandler[links.size()];
		int handlerCount = 0;
		int slots = 0;

		for(BlockPos offset : links){
			TileEntity te = world.getTileEntity(pos.add(offset));
			if(te instanceof IFluxHandler && ((IFluxHandler) te).isFluxReceiver() && ((IFluxHandler) te).canAccept() > 0){
				handlers[handlerCount] = (IFluxHandler) te;
				slots += handlers[handlerCount].canAccept();
				handlerCount++;
			}
		}

		if(slots <= flux){
			for(int i = 0; i < handlerCount; i++){
				handlers[i].addFlux(handlers[i].canAccept());
			}
			return slots;
		}

		float partial = 0;
		int lastUnsatified = 0;
		//Distribute flux as the weighted average of what each handler can accept, with un-distributed decimal quantities of flux stored in partial
		for(int i = 0; i < handlerCount; i++){
			float idealAdded = (float) handlers[i].canAccept() * (float) flux / (float) slots;
			int added = (int) idealAdded;
			partial += idealAdded - added;
			if(partial >= 1F){
				partial -= 1F;
				added += 1;
			}
			if(added < handlers[i].canAccept()){
				lastUnsatified = i;
			}
			handlers[i].addFlux(added);
			try{
				renderFlux(world, pos, ((TileEntity) handlers[i]).getPos(), added);
			}catch(ClassCastException e){
				Main.logger.log(Level.ERROR, "FluxHandler of type " + handlers[i].getClass() + " not attached to TileEntity! Report to mod author", e);
			}
		}
		//Account for rounding errors
		if(partial > 0.0005F){
			handlers[lastUnsatified].addFlux(1);
		}

		return flux;
	}

	public static void fluxEvent(World worldIn, BlockPos pos, int intensity){
		if(worldIn.provider instanceof PrototypeWorldProvider){
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
			for(int i = 0; i < 64; i++){
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
