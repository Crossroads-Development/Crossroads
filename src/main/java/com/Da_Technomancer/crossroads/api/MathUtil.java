package com.Da_Technomancer.crossroads.api;

public final class MathUtil{

	//TODO move to Essentials

	public static int min(int... vals){
		int least = Integer.MAX_VALUE;
		for(int val : vals){
			least = Math.min(least, val);
		}
		return least;
	}

	public static long min(long... vals){
		long least = Long.MAX_VALUE;
		for(long val : vals){
			least = Math.min(least, val);
		}
		return least;
	}

	public static float min(float... vals){
		float least = Float.MAX_VALUE;
		for(float val : vals){
			least = Math.min(least, val);
		}
		return least;
	}

	public static double min(double... vals){
		double least = Double.MAX_VALUE;
		for(double val : vals){
			least = Math.min(least, val);
		}
		return least;
	}

	public static int max(int... vals){
		int least = Integer.MIN_VALUE;
		for(int val : vals){
			least = Math.max(least, val);
		}
		return least;
	}

	public static long max(long... vals){
		long least = Long.MIN_VALUE;
		for(long val : vals){
			least = Math.max(least, val);
		}
		return least;
	}

	public static float max(float... vals){
		float least = Float.MIN_VALUE;
		for(float val : vals){
			least = Math.max(least, val);
		}
		return least;
	}

	public static double max(double... vals){
		double least = Double.MIN_VALUE;
		for(double val : vals){
			least = Math.max(least, val);
		}
		return least;
	}

	public static int clamp(int val, int min, int max){
		return Math.max(min, Math.min(val, max));
	}

	public static long clamp(long val, long min, long max){
		return Math.max(min, Math.min(val, max));
	}

	public static float clamp(float val, float min, float max){
		return Math.max(min, Math.min(val, max));
	}

	public static double clamp(double val, double min, double max){
		return Math.max(min, Math.min(val, max));
	}

	/**
	 * Rounds to a set number of decimal places
	 * @param numIn The value to round
	 * @param decPlac The number of decimal places to round to
	 * @return The rounded value
	 */
	public static double preciseRound(double numIn, int decPlac){
		return Math.round(numIn * Math.pow(10, decPlac)) / Math.pow(10D, decPlac);
	}

	/**
	 * The same as Math.round except if the decimal
	 * is exactly .5 then it rounds down.
	 *
	 * This is for systems that require rounding and
	 * NEED the distribution of output to not be higher than
	 * the input to prevent dupe bugs.
	 * @param in The value to round
	 * @return The rounded value
	 */
	public static int safeRound(double in){
		if(in % 1 <= .5D){
			return (int) Math.floor(in);
		}else{
			return (int) Math.ceil(in);
		}
	}

	public static float clockModulus(float a, float b){
		return ((a % b) + b) % b;
	}
}
