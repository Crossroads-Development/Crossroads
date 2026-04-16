package com.Da_Technomancer.crossroads.api.alchemy;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.ambient.particles.CRParticles;
import com.Da_Technomancer.crossroads.ambient.particles.ColorParticleData;
import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.heat.HeatUtil;
import com.Da_Technomancer.crossroads.entity.EntityFlameCore;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class AlchemyUtil{

	/**
	 * Conversion factor of (degrees kelvin normal heat) / (degrees kelvin * amount alchemy system). Based on game balance.
	 */
	public static final double ALCHEMY_TEMP_CONVERSION = 6D;
	//Alchemy calculations are performed once every ALCHEMY_TIME ticks instead of every tick to reduce lag, slow things down
	public static final int ALCHEMY_TIME = 4;

	private static int getGasRange(int gasQty){
		return Math.min(8, Math.max(2, gasQty / 3));
	}

	private static int getSplashRange(int liquidQty){
		return Math.min(4, Math.max(2, liquidQty / 5));
	}

	/**
	 * Performs the effects of releasing chemicals into the world. Call on the virtual server side only
	 * @param world The world
	 * @param pos The position to release the chemicals at
	 * @param reags The reagents to release. Temperature does matter. The passed map will not be modified
	 */
	public static void releaseChemical(Level world, BlockPos pos, ReagentMap reags){
//		boolean hasFire = false;
		int solQty = 0;
		int liqQty = 0;
		int gasQty = 0;
		int flameRange = 0;
		int[] solCol = new int[4];
		int[] liqCol = new int[4];
		int[] gasCol = new int[4];

		ArrayList<QueuedEffect> effectsSol = new ArrayList<>(reags.keySetSize());
		ArrayList<QueuedEffect> effectsLiq = new ArrayList<>(reags.keySetSize());
		ArrayList<QueuedEffect> effectsGas = new ArrayList<>(reags.keySetSize());

		double tempC = reags.getTempC();
		for(IReagent reag : reags.keySetReag()){
			if(reag != null){
				EnumMatterPhase p = reag.getPhase(tempC);
				int qty = reags.getQty(reag);
				Color c = reag.getColor(p);
				switch(p){
					case FLAME:
						//The flame reagent with the largest radius dominates
						flameRange = Math.max(flameRange, reag.getFlameRadius(qty));
						break;
					case GAS:
						gasQty += qty;
						gasCol[0] += c.getRed() * qty;
						gasCol[1] += c.getGreen() * qty;
						gasCol[2] += c.getBlue() * qty;
						gasCol[3] += c.getAlpha() * qty;
						effectsGas.add(new QueuedEffect(reag.getEffect(), qty));
						break;
					case LIQUID:
						liqQty += qty;
						liqCol[0] += c.getRed() * qty;
						liqCol[1] += c.getGreen() * qty;
						liqCol[2] += c.getBlue() * qty;
						liqCol[3] += c.getAlpha() * qty;
						effectsLiq.add(new QueuedEffect(reag.getEffect(), qty));
						break;
					case SOLID:
						solQty += qty;
						solCol[0] += c.getRed() * qty;
						solCol[1] += c.getGreen() * qty;
						solCol[2] += c.getBlue() * qty;
						solCol[3] += c.getAlpha() * qty;
						effectsSol.add(new QueuedEffect(reag.getEffect(), qty));
						break;
					default:
						//If we hit the default, then someone has invented a new form of matter
						Crossroads.logger.error(String.format("Attempted to perform effect with invalid reagent phase %1$s and reagent %2$s.", p.name(), reag.getName()));
						break;
				}
			}
		}

		//The flame effect takes priority over normal effects
		if(flameRange > 0){
			if(CRConfig.phelEffect.get()){
				EntityFlameCore coreFlame = EntityFlameCore.type.create(world);
				coreFlame.setPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
				coreFlame.setInitialValues(reags, flameRange);
				world.addFreshEntity(coreFlame);
			}else{
				//If flame effect is disabled in the config, just spawn a single small fire
				BlockState prev = world.getBlockState(pos);
				if(prev.getBlock() == Blocks.FIRE || CRConfig.isProtected(world, pos, prev)){
					return;
				}
				world.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
			}
		}else{
			if(liqQty > 0){
				effectsLiq.addAll(effectsSol);//Perform solid effects alongside liquid
				int maxRange = getSplashRange(liqQty);
				liqCol[0] = (liqCol[0] + solCol[0]) / (liqQty + solQty);
				liqCol[1] = (liqCol[1] + solCol[1]) / (liqQty + solQty);
				liqCol[2] = (liqCol[2] + solCol[2]) / (liqQty + solQty);
				liqCol[3] = (liqCol[3] + solCol[3]) / (liqQty + solQty);
				for(int i = 0; i < 32; i++){
					float horizSpeed = maxRange / 20F * 0.5F;
					float offset = i < 16 ? 0 : 1;
					((ServerLevel) world).sendParticles(new ColorParticleData(CRParticles.COLOR_SPLASH, new Color(liqCol[0], liqCol[1], liqCol[2], liqCol[3])), pos.getX() + 0.5D, pos.getY() + 1.3F, pos.getZ() + 0.5D, 1, 0.5 * Math.cos((i + offset) * Math.PI / 8), (3 * offset + 1) / 8D, 0.5 * Math.sin((i + offset) * Math.PI / 8), horizSpeed);
				}
				for(int i = -maxRange; i <= maxRange; i++){
					for(int j = -maxRange; j <= maxRange; j++){
						for(int k = -maxRange; k <= maxRange; k++){
							for(QueuedEffect eff : effectsLiq){
								//Taxicab distance
								if(maxRange >= Math.abs(i) + Math.abs(j) + Math.abs(k)){
									eff.perform(world, pos.offset(i, j, k), reags, EnumMatterPhase.LIQUID);
								}
							}
						}
					}
				}
			}else if(solQty > 0){
				//Perform solid independently
				solCol[0] /= solQty;
				solCol[1] /= solQty;
				solCol[2] /= solQty;
				solCol[3] /= solQty;

				for(QueuedEffect e : effectsSol){
					e.perform(world, pos, reags, EnumMatterPhase.SOLID);
				}
				for(int i = 0; i < 5; i++){
					((ServerLevel) world).sendParticles(new ColorParticleData(CRParticles.COLOR_SOLID, new Color(solCol[0], solCol[1], solCol[2], solCol[3])), pos.getX() + 0.25D + world.random.nextFloat() / 2F, pos.getY() + 1.3F, pos.getZ() + 0.25D + world.random.nextFloat() / 2F, 1, 0, 0, 0, 0F);
				}
			}
			if(gasQty > 0){
				gasCol[0] /= gasQty;
				gasCol[1] /= gasQty;
				gasCol[2] /= gasQty;
				gasCol[3] /= gasQty;
				int maxRange = getGasRange(gasQty);
				for(int i = -maxRange; i <= maxRange; i++){
					for(int j = -maxRange; j <= maxRange; j++){
						for(int k = -maxRange; k <= maxRange; k++){
							for(QueuedEffect eff : effectsGas){
								//Taxicab distance
								if(maxRange >= Math.abs(i) + Math.abs(j) + Math.abs(k)){
									eff.perform(world, pos.offset(i, j, k), reags, EnumMatterPhase.GAS);
									((ServerLevel) world).sendParticles(new ColorParticleData(CRParticles.COLOR_GAS, new Color(gasCol[0], gasCol[1], gasCol[2], gasCol[3])), (float) pos.getX() + Math.random(), (float) pos.getY() + Math.random(), (float) pos.getZ() + Math.random(), 1, (Math.random() * 2D - 1D), Math.random(), (Math.random() * 2D - 1D), 0.015D);
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * For an item being converted into a reagent, calculates the temperature the newly created reagent should be considered to be
	 * @param reagent The reagent being added
	 * @param biomeTemp Ambient biome temperature, in Celcius
	 * @return Reagent temperature, in Celcius
	 */
	public static double getInputItemTemp(IReagent reagent, double biomeTemp){
		double melting = reagent.getMeltingPoint();
		if(biomeTemp < melting){
			return biomeTemp;
		}else{
			return Math.max(melting - 100D, HeatUtil.ABSOLUTE_ZERO);
		}
	}

	/**
	 * For a (forge-style) fluid converted into a reagent, calculates the temperature the newly created reagent should be considered to be
	 * @param biomeTemp Ambient biome temperature, in Celcius
	 * @return Reagent temperature, in Celcius
	 */
	public static double getInputFluidTemp(IReagent reagent, double biomeTemp){
		Predicate<Double> legal = (temp) -> temp >= reagent.getMeltingPoint() && temp < reagent.getBoilingPoint();
		//Try the fluid's modder-defined temperature
		Fluid reagentFluid = CraftingUtil.getPreferredEntry(reagent.getFluid().getMatchedFluids(), ForgeRegistries.Keys.FLUIDS);
		if(reagentFluid == null){
			//Why are we checking fluid temperature for a reagent with no fluid?
			Crossroads.logger.warn("Reagent fluid temperature queried for invalid reagent: " + reagent.getID());
			return biomeTemp;
		}

		//Fluid temperature properties are kind of meaningless, but sometimes roughly correspond to a number in Kelvin
		double temp = HeatUtil.toCelcius(reagentFluid.getFluidType().getTemperature());
		if(legal.test(temp)){
			return temp;
		}
		//Check biome temperature
		temp = biomeTemp;
		if(legal.test(temp)){
			return temp;
		}
		//20*C above the melting point
		temp = Math.max(HeatUtil.ABSOLUTE_ZERO, reagent.getMeltingPoint()) + 20;
		if(legal.test(temp)){
			return temp;
		}
		//The exact melting point
		return Math.max(HeatUtil.ABSOLUTE_ZERO, reagent.getMeltingPoint());
	}

	private record QueuedEffect(@Nullable IAlchEffect effect, int qty){

		private void perform(Level world, BlockPos pos, ReagentMap reags, EnumMatterPhase phase){
			if(effect != null){
				effect.doEffect(world, pos, qty, phase, reags);
			}
		}
	}

	private static final Random RANDOM = new Random();

	/**
	 * Transfers some of the stored reagents from one map to another. Meant for conduit transfers.
	 * For more precise control of the choice of reagents to be transferred, see MiscUtil::withdrawExact
	 * @param source Source reagent map. Will be modified.
	 * @param destination Destination reagent map. Will be modified.
	 * @param toTransferMax Maximum total amount of reagent to be transferred
	 * @param maxAllowedTransferOfType Function providing maximum quantity of a given reagent type we're allowed to transfer, on a per-type basis. Separate limit from toTransferMax (which applies to the total)- the more limiting of the two will be enforced. Use Integer.MAX_VALUE for no limit. Negative values treated equivalent to 0.
	 * @return Total quantity that was actually transferred
	 */
	public static int transferSomeReagents(ReagentMap source, ReagentMap destination, int toTransferMax, Function<IReagent, Integer> maxAllowedTransferOfType){
		Set<IReagent> sourceKeySet = source.keySetReag();
		if(sourceKeySet.isEmpty() || toTransferMax <= 0){
			return 0;
		}
		// Optimization for a very common case: Source is size 1
		if(sourceKeySet.size() == 1){
			for(IReagent reag : sourceKeySet){
				//Only one reag type
				int toTransfer = Math.min(source.getQty(reag), Math.min(toTransferMax, maxAllowedTransferOfType.apply(reag)));
				destination.transferReagent(reag, toTransfer, source);
				return toTransfer;
			}
		}

		//Optimization for a very common case: toTransferMax is 1
		if(toTransferMax == 1){
			//Build a map of reagents we can transfer
			ArrayList<Pair<IReagent, Integer>> transferLimits = new ArrayList<>(sourceKeySet.size());//Per-reagent limits on transfer
			int totalWeight = 0;//For weighted-random selection from transferLimits map. Weights are the transfer limit value
			for(IReagent reag : sourceKeySet){
				int perReagLimit = Math.min(source.getQty(reag), maxAllowedTransferOfType.apply(reag));
				if(perReagLimit > 0){
					transferLimits.add(Pair.of(reag, perReagLimit));
					totalWeight += perReagLimit;
				}
			}
			//Can't transfer more than the total transfer limits of the available reagents
			toTransferMax = Math.min(toTransferMax, totalWeight);
			assert toTransferMax == 0 || toTransferMax == 1;
			//Randomly select a unit to transfer
			if(toTransferMax == 1){
				//Pick a reagent type to transfer at random, selection weighted by allowed transfer quantity
				int selection = RANDOM.nextInt(totalWeight);
				for(Pair<IReagent, Integer> transferEntry : transferLimits){
					selection -= transferEntry.getRight();
					if(selection < 0){
						//Transfer 1 unit of this reagent and re-roll
						destination.transferReagent(transferEntry.getLeft(), 1, source);
//						//Update the transfer limits table, as we've depleted amount available by 1
//						int prevLimit = transferLimits.get(transferEntry);
//						int newLimit = Math.min(prevLimit, source.getQty(transferEntry));
//						if(newLimit < prevLimit){
//							totalWeight -= prevLimit - newLimit;
//							transferLimits.replace(transferEntry, newLimit);
//						}
						break;
					}
				}
				return 1;
			}
			return 0;
		}

		//Full algorithm
		//Build a map of reagents we can transfer
		LinkedHashMap<IReagent, Integer> transferLimits = new LinkedHashMap<>(sourceKeySet.size());//Per-reagent limits on transfer, actively updated.
		int totalWeight = 0;//For weighted-random selection from transferLimits map. Weights are the transfer limit value
		for(IReagent reag : sourceKeySet){
			int perReagLimit = Math.min(source.getQty(reag), maxAllowedTransferOfType.apply(reag));
			if(perReagLimit > 0){
				transferLimits.put(reag, perReagLimit);
				totalWeight += perReagLimit;
			}
		}
		//Can't transfer more than the total transfer limits of the available reagents
		toTransferMax = Math.min(toTransferMax, totalWeight);
		//Transfer is done 1 randomly-selected unit at a time (not very efficient, but as of writing, there are literally 0 use-cases that transfer more than 1 unit total anyway, so doesn't matter)
		for(int i = 0; i < toTransferMax; i++){
			//Pick a reagent type to transfer at random, selection weighted by allowed transfer quantity
			int selection = RANDOM.nextInt(totalWeight);
			for(IReagent transferEntry : transferLimits.keySet()){
				selection -= transferLimits.get(transferEntry);
				if(selection < 0){
					//Transfer 1 unit of this reagent and re-roll
					destination.transferReagent(transferEntry, 1, source);
					//Update the transfer limits table, as we've depleted amount available by 1
					int prevLimit = transferLimits.get(transferEntry);
					int newLimit = Math.min(prevLimit, source.getQty(transferEntry));
					if(newLimit < prevLimit){
						totalWeight -= prevLimit - newLimit;
						transferLimits.replace(transferEntry, newLimit);
					}
					break;
				}
			}
		}
		return toTransferMax;
	}
}
