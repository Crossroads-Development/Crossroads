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
import java.awt.*;
import java.util.ArrayList;
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

		ArrayList<QueuedEffect> effectsSol = new ArrayList<>(reags.size());
		ArrayList<QueuedEffect> effectsLiq = new ArrayList<>(reags.size());
		ArrayList<QueuedEffect> effectsGas = new ArrayList<>(reags.size());

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

		double temp = reagentFluid.getFluidType().getTemperature();
		if(legal.test(temp)){
			return temp;
		}
		//Check biome temperature
		temp = biomeTemp;
		if(legal.test(temp)){
			return temp;
		}
		//100*C above the melting point
		temp = Math.min(HeatUtil.ABSOLUTE_ZERO, reagent.getMeltingPoint()) + 100;
		if(legal.test(temp)){
			return temp;
		}
		//The exact melting point
		return Math.min(HeatUtil.ABSOLUTE_ZERO, reagent.getMeltingPoint());
	}

	private static class QueuedEffect{

		private final IAlchEffect effect;
		private final int qty;

		private QueuedEffect(@Nullable IAlchEffect effect, int qty){
			this.effect = effect;
			this.qty = qty;
		}

		private void perform(Level world, BlockPos pos, ReagentMap reags, EnumMatterPhase phase){
			if(effect != null){
				effect.doEffect(world, pos, qty, phase, reags);
			}
		}
	}
}
