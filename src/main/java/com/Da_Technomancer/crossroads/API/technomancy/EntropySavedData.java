package com.Da_Technomancer.crossroads.API.technomancy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.Main;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

/**
 * Temporal Entropy is a Technomancy mechanic where a global value is increased by several machines as a byproduct.
 * Assorted negative effects occur if the global temporal entropy gets too high, and several machines can decrease it. It also slowly decays on its own.
 * The player will only ever see Temporal Entropy measured as a percentage (capped at 100%), but internally Temporal Entropy is on a linear scale from 0 to MAX_VALUE
 */
public class EntropySavedData extends WorldSavedData{

	public static final String ID = Main.MODID + "_flux";
	public static final int MAX_VALUE = 100_000;
	//Temporal Entropy is able to slightly go above the "official" maximum value for playability. The ability of entropy to go over the "maximum" is never displayed
	private static final int OVERFLOW_MAX = (int) (MAX_VALUE * 1.05D);

	public EntropySavedData(){
		super(ID);
	}

	public EntropySavedData(String name){
		super(name);
	}

	/**
	 *
	 * @param w A digital-server side world
	 * @return The temporal entropy as a percentage
	 */
	public static double getEntropy(World w){
		return MiscUtil.betterRound(100D * Math.min(get(w).temporalEntropy / (double) MAX_VALUE, 1D), 3);
	}

	/**
	 * Gets a severity level to control when negative effects should trigger
	 * @param w A digital-server side world
	 * @return The severity level
	 */
	public static Severity getSeverity(World w){
		return Severity.getSeverity(getEntropy(w));
	}

	/**
	 * Converts an entropy point value to a percentage
	 * Client side safe
	 * @param pts The number of points
	 * @return The equivalent percentage, for display
	 */
	public static double getPercentage(int pts){
		return 100D * Math.min(pts / (double) MAX_VALUE, 1D);
	}

	/**
	 * Converts an entropy percentage value to a number of pts
	 * Client side safe
	 * @param perc The entropy percentage
	 * @return The equivalent pts
	 */
	public static int getPts(double perc){
		return (int) (Math.min(perc / 100D, 1D) * (double) MAX_VALUE);
	}

	/**
	 * Adjusts the global temporal entropy
	 * @param w A digital-server side world
	 * @param change The amount to adjust the temporal entropy level. Value interpreted as a number of points, not a %
	 */
	public static void addEntropy(World w, int change){
		EntropySavedData data = get(w);
		int newEntropy = Math.min(OVERFLOW_MAX, data.temporalEntropy + change);
		newEntropy = Math.max(0, newEntropy);
		if(newEntropy != data.temporalEntropy){
			data.temporalEntropy = newEntropy;
			data.markDirty();
		}
	}

	private static EntropySavedData get(World world){
		MapStorage storage = world.getMapStorage();
		EntropySavedData data;
		try{
			 data = (EntropySavedData) storage.getOrLoadData(EntropySavedData.class, ID);
		}catch(NullPointerException e){
			Main.logger.error("Failed EntropicSavedData get due to null MapStorage", e);
			return new EntropySavedData();//Blank storage that prevents actual read/write, but avoids a crash
		}

		if (data == null) {
			data = new EntropySavedData();
			storage.setData(ID, data);
		}
		return data;
	}

	private int temporalEntropy;

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		temporalEntropy = nbt.getInteger("flux");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		nbt.setInteger("flux", temporalEntropy);
		return nbt;
	}

	public enum Severity{

		NEGLIGABLE(0, 0),
		UNSTABLE(1, 25),
		HARMFUL(2, 50),
		DESTRUCTIVE(3, 100);

		private final int rank;

		public int getLowerBound(){
			return lowerBound;
		}

		private final int lowerBound;

		/*
		 * @param rank The level of severity. Effectively the same as Severity::ordinal
		 * @param lowerBound The minimum temporal entropy as a percentage to reach this severity
		 */
		Severity(int rank, int lowerBound){
			this.rank = rank;
			this.lowerBound = lowerBound;
		}

		public int getRank(){
			return rank;
		}

		public static Severity getSeverity(double entropy){
			Severity level = NEGLIGABLE;
			for(Severity lev : values()){
				if(entropy < lev.lowerBound){
					return level;
				}
				level = lev;
			}
			return level;
		}
	}
}
