package com.Da_Technomancer.crossroads.API.alchemy;

import com.Da_Technomancer.crossroads.API.effects.alchemy.IAlchEffect;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.entity.EntityFlameCore;
import com.Da_Technomancer.crossroads.particles.CRParticles;
import com.Da_Technomancer.crossroads.particles.ColorParticleData;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

public class AlchemyUtil{

	/**
	 * Conversion factor between degrees kelvin * amount alchemy system, and degrees kelvin normal heat. Based on game balance.
	 */
	public static final double ALCHEMY_TEMP_CONVERSION = 100D;
	//Alchemy calculations are performed once every ALCHEMY_TIME ticks instead of every tick to reduce lag
	public static final int ALCHEMY_TIME = 2;

	private static int getGasRange(int gasQty){
		return Math.min((int) Math.sqrt(gasQty), 8);
	}

	private static int getSplashRange(int liquidQty){
		return Math.min(3, Math.max(1, liquidQty / 8));
	}

	/**
	 * Performs the effects of releasing chemicals into the world. Call on the virtual server side only
	 * @param world The world
	 * @param pos The position to release the chemicals at
	 * @param reags The reagents to release. Temperature does matter. The passed map will not be modified
	 */
	public static void releaseChemical(World world, BlockPos pos, ReagentMap reags){
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
		for(Map.Entry<IReagent, Integer> reagEnt : reags.entrySet()){
			IReagent reag = reagEnt.getKey();
			if(reag != null){
				EnumMatterPhase p = reag.getPhase(tempC);
				int qty = reagEnt.getValue();
				Color c = reag.getColor(p);
				switch(p){
					case FLAME:
						//The flame reagent with the largest radius dominates
						flameRange = Math.max(flameRange, AlchemyCore.FLAME_RANGES.get(reag).apply(reagEnt.getValue()));
						break;
					case GAS:
						gasQty += qty;
						gasCol[0] += c.getRed() * qty;
						gasCol[1] += c.getGreen() * qty;
						gasCol[2] += c.getBlue() * qty;
						gasCol[3] += c.getAlpha() * qty;
						effectsGas.add(new QueuedEffect(reag.getEffect(p), p, qty));
						break;
					case LIQUID:
						liqQty += qty;
						liqCol[0] += c.getRed() * qty;
						liqCol[1] += c.getGreen() * qty;
						liqCol[2] += c.getBlue() * qty;
						liqCol[3] += c.getAlpha() * qty;
						effectsLiq.add(new QueuedEffect(reag.getEffect(p), p, qty));
						break;
					case SOLID:
						solQty += qty;
						solCol[0] += c.getRed() * qty;
						solCol[1] += c.getGreen() * qty;
						solCol[2] += c.getBlue() * qty;
						solCol[3] += c.getAlpha() * qty;
						effectsSol.add(new QueuedEffect(reag.getEffect(p), p, qty));
					default:
						//If we hit the default, then someone has invented a new form of matter
						Crossroads.logger.error("Attempted to perform effect with invalid reagent phase %1$s and reagent %2$s.", p.name(), reag.getName());
						break;
				}
			}
		}

		//The flame effect takes priority over normal effects
		if(flameRange > 0){
			if(CRConfig.phelEffect.get()){
				EntityFlameCore coreFlame = EntityFlameCore.type.create(world);
				coreFlame.setPosition(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
				coreFlame.setInitialValues(reags, flameRange);
				world.addEntity(coreFlame);
			}else{
				//If flame effect is disabled in the config, just spawn a single small fire
				BlockState prev = world.getBlockState(pos);
				if(prev.getBlock() == Blocks.FIRE || CRConfig.isProtected(world, pos, prev)){
					return;
				}
				world.setBlockState(pos, Blocks.FIRE.getDefaultState());
			}
		}else{
			if(liqQty > 0){
				effectsLiq.addAll(effectsSol);//Perform solid effects alongside liquid
				int maxRange = getSplashRange(liqQty);
				liqCol[0] = (liqCol[0] + solCol[0]) / (liqQty + solQty);
				liqCol[1] = (liqCol[1] + solCol[1]) / (liqQty + solQty);
				liqCol[2] = (liqCol[2] + solCol[2]) / (liqQty + solQty);
				liqCol[3] = (liqCol[3] + solCol[3]) / (liqQty + solQty);
				for(int i = 0; i < 16; i++){
					float horizSpeed = maxRange / 20F;
					float offset = i < 8 ? 0 : 1;
					((ServerWorld) world).spawnParticle(new ColorParticleData(CRParticles.COLOR_SPLASH, new Color(liqCol[0], liqCol[1], liqCol[2], liqCol[3])), pos.getX() + 0.5D, pos.getY() + 1.3F, pos.getZ() + 0.5D, 1, horizSpeed * Math.cos((2 * i + offset) * Math.PI / 8), (3 * offset + 1) * horizSpeed / 8D, horizSpeed * Math.sin((2 * i + offset) * Math.PI / 8), 1F);
				}
				for(int i = -maxRange; i <= maxRange; i++){
					for(int j = -maxRange; j <= maxRange; j++){
						for(int k = -maxRange; k <= maxRange; k++){
							for(QueuedEffect eff : effectsLiq){
								//Pythagorean distance- not taxicab
								if(maxRange * maxRange >= i * i + j * j + k * k){
									eff.perform(world, pos.add(i, j, k), reags);
								}
							}
						}
					}
				}
			}else{
				//Perform solid independently
				solCol[0] /= solQty;
				solCol[1] /= solQty;
				solCol[2] /= solQty;
				solCol[3] /= solQty;

				for(QueuedEffect e : effectsSol){
					e.perform(world, pos, reags);
				}
				for(int i = 0; i < 5; i++){
					((ServerWorld) world).spawnParticle(new ColorParticleData(CRParticles.COLOR_SOLID, new Color(solCol[0], solCol[1], solCol[2], solCol[3])), pos.getX() + 0.25D + world.rand.nextFloat() / 2F, pos.getY() + 1.3F, pos.getZ() + 0.25D + world.rand.nextFloat() / 2F, 1, 0, 0, 0, 1F);
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
								//Pythagorean distance- not taxicab
								if(maxRange * maxRange >= i * i + j * j + k * k){
									eff.perform(world, pos.add(i, j, k), reags);
									((ServerWorld) world).spawnParticle(new ColorParticleData(CRParticles.COLOR_GAS, new Color(gasCol[0], gasCol[1], gasCol[2], gasCol[3])), (float) pos.getX() + Math.random(), (float) pos.getY() + Math.random(), (float) pos.getZ() + Math.random(), 1, (Math.random() * 2D - 1D) * 0.015D, Math.random() * 0.015D, (Math.random() * 2D - 1D) * 0.015D, 1F);
								}
							}
						}
					}
				}
			}
		}
	}

	private static class QueuedEffect{

		private final IAlchEffect effect;
		private final EnumMatterPhase phase;
		private final int qty;

		private QueuedEffect(@Nullable IAlchEffect effect, EnumMatterPhase phase, int qty){
			this.effect = effect;
			this.phase = phase;
			this.qty = qty;
		}

		private void perform(World world, BlockPos pos, ReagentMap reags){
			if(effect != null){
				effect.doEffect(world, pos, qty, phase, reags);
			}
		}
	}
}
