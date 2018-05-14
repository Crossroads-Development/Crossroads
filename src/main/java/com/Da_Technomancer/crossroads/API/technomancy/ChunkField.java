package com.Da_Technomancer.crossroads.API.technomancy;

import com.Da_Technomancer.crossroads.API.magic.EnumMagicElements;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class ChunkField{

	private static final Random RAND = new Random();

	public short fluxForce = 0;
	public byte flux;
	public byte[][] nodes = new byte[16][16];
	public short[][] nodeForce = new short[16][16];
	private byte cooldown;
	public boolean isActive;
	public boolean refreshed;

	public ChunkField(boolean empty){
		if(!empty){
			isActive = true;
			flux = 7;
			refreshed = true;
			for(int i = 0; i < 16; i++){
				for(int j = 0; j < 16; j++){
					nodes[i][j] = 7;
				}
			}
		}
	}

	private void wipe(){
		if(isActive){
			isActive = false;
			cooldown = 10;
		}
	}

	/**
	 * @return Whether this ChunkField should be removed
	 */
	public boolean tick(World world, ChunkPos pos){
		if(!refreshed){
			wipe();
		}
		refreshed = false;

		if(isActive){
			int rateChange = 0;
			int topRate = 0;
			for(int x = 0; x < 16; x++){
				for(int z = 0; z < 16; z++){
					short change = nodeForce[x][z];
					nodeForce[x][z] = 0;
					int rate = (int) Math.max(1, Math.min(8 + change, 128));
					rateChange += Math.abs(rate - 1 - nodes[x][z]);
					nodes[x][z] = (byte) (rate - 1);
					topRate = Math.max(rate, topRate);
				}
			}


			int randGrowth = RAND.nextInt(1 + (int) Math.ceil(Math.abs(flux - 7) / 8F));
			flux = (byte) Math.min(127, (int) flux + rateChange / 2);

			if((int) flux + (int) fluxForce + randGrowth + 1 < topRate){
				//If time is stopped, flux manipulators and random growth stop applying
				flux = (byte) Math.min(127, (int) flux + 2);
			}else{
				flux = (byte) Math.max(0, Math.min(127, (int) flux + (int) fluxForce + randGrowth));
			}

			fluxForce = 0;

			if(flux == 127){
				wipe();
				for(int i = 0; i < 3; i++){
					EnumMagicElements.TIME.getVoidEffect().doEffect(world, pos.getBlock(RAND.nextInt(16), RAND.nextInt(128), RAND.nextInt(16)), 128);
				}
			}
		}else{
			return --cooldown <= 0;
		}

		return false;
	}

	public void writeToNBT(NBTTagCompound nbt, long key){
		NBTTagCompound inner = new NBTTagCompound();
		if(isActive){
			inner.setByte("fl", flux);
			inner.setShort("fl_f", fluxForce);
			inner.setBoolean("ref", refreshed);
			inner.setBoolean("ac", true);
			for(int i = 0; i < 16; i++){
				for(int j = 0; j < 16; j++){
					inner.setByte("r_" + i + "_" + j, nodes[i][j]);
				}
			}
		}else{
			inner.setBoolean("ac", false);
			inner.setByte("co", cooldown);
		}
		nbt.setTag("c_" + key, inner);
	}

	@Nullable
	public static ChunkField readFromNBT(NBTTagCompound nbt, long key){
		if(!nbt.hasKey("c_" + key)){
			return null;
		}
		NBTTagCompound inner = nbt.getCompoundTag("c_" + key);
		ChunkField out = new ChunkField(true);

		if(inner.getBoolean("ac")){
			out.isActive = true;
			out.flux = inner.getByte("fl");
			out.fluxForce = inner.getShort("fl_f");
			out.refreshed = inner.getBoolean("ref");
			for(int i = 0; i < 16; i++){
				for(int j = 0; j < 16; j++){
					out.nodes[i][j] = inner.getByte("r_" + i + "_" + j);
				}
			}
		}else{
			out.isActive = false;
			out.cooldown = inner.getByte("co");
		}

		return out;
	}
}
