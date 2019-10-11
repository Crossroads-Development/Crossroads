package com.Da_Technomancer.crossroads.API.alchemy;

import com.Da_Technomancer.crossroads.API.effects.alchemy.IAlchEffect;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.entity.EntityFlameCore;
import com.Da_Technomancer.crossroads.particles.ModParticles;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;

public class AlchemyUtil{

	public static final int MB_PER_REAG = 100;
	/**
	 * Conversion factor between degrees kelvin * amount alchemy system, and degrees kelvin normal heat. Based on game balance.
	 */
	public static final double ALCHEMY_TEMP_CONVERSION = 100D;
	public static final int ALCHEMY_TIME = 2;

	public static int getGasRange(int gasQty){
		return Math.min((int) Math.sqrt(gasQty), 8);
	}

	public static int getSplashRange(int liquidQty){
		return Math.min(3, Math.max(1, liquidQty / 8));
	}

	/**
	 * Performs the effects of releasing chemicals into the world. Call on the virtual server side only
	 * @param world The world
	 * @param pos The position to release the chemicals at
	 * @param reags The reagents to release. Temperature does matter. The passed map will not be modified
	 */
	public static void releaseChemical(World world, BlockPos pos, ReagentMap reags){
		boolean hasFire = false;
		int flameRange = 0;

		//find if there is a contained flame phase reagent, and if so, find the range of the flame burst
		//The flame reagent with the largest radius dominates
		for(Map.Entry<IReagent, Function<Integer, Integer>> flameReag : AlchemyCore.FLAME_RANGES.entrySet()){
			int qty;
			if((qty = reags.getQty(flameReag.getKey())) != 0){
				hasFire = true;
				flameRange = Math.max(flameRange, flameReag.getValue().apply(qty));
			}
		}

		//The flame effect takes priority over normal effects
		if(hasFire){
			if(CRConfig.phelEffect.get()){
				EntityFlameCore coreFlame = new EntityFlameCore(world);
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
			ArrayList<QueuedEffect> effects = new ArrayList<>(reags.size());

			int maxRange = 0;

			for(Map.Entry<IReagent, Integer> reag : reags.entrySet()){
				int qty = reag.getValue();
				if(qty > 0){
					IReagent type = reag.getKey();
					EnumMatterPhase phase = type.getPhase(reags.getTempC());
					IAlchEffect eff = type.getEffect(phase);
					switch(phase){
						case GAS:
							int range = getGasRange(qty);
							maxRange = Math.max(range, maxRange);
							effects.add(new QueuedEffect(type.getColor(phase), eff, phase, range, qty));
							break;
						case LIQUID:
							int lRange = getSplashRange(qty);
							maxRange = Math.max(lRange, maxRange);
							effects.add(new QueuedEffect(type.getColor(phase), eff, phase, lRange, qty));
							Color lcol = type.getColor(phase);
							for(int i = 0; i < 16; i++){
								float horizSpeed = lRange / 20F;
								float offset = i < 8 ? 0 : 1;
								((ServerWorld) world).spawnParticle(ModParticles.COLOR_SPLASH, false, pos.getX() + 0.5D, pos.getY() + 1.3F, pos.getZ() + 0.5D, 0, horizSpeed * Math.cos((2 * i + offset) * Math.PI / 8), (3 * offset + 1) * horizSpeed / 8D, horizSpeed * Math.sin((2 * i + offset) * Math.PI / 8), 1F, lcol.getRed(), lcol.getGreen(), lcol.getBlue(), lcol.getAlpha());
							}
							break;
						case SOLID:
							if(eff != null){
								eff.doEffect(world, pos, qty, phase, reags);
							}
							Color col = type.getColor(phase);
							for(int i = 0; i < 5; i++){
								((ServerWorld) world).spawnParticle(ModParticles.COLOR_SOLID, false, pos.getX() + 0.25D + world.rand.nextFloat() / 2F, pos.getY() + 1.3F, pos.getZ() + 0.25D + world.rand.nextFloat() / 2F, 0, 0, 0, 0, 1F, col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha());
							}
							break;
						case FLAME:
						default:
							//If there was a flame, we shouldn't be in this branch of the if...else, and if we hit the default, then someone has invented a new form of matter
							Crossroads.logger.error("Attempted to perform effect with invalid reagent phase %1$s and reagent %2$s.", phase, reag.getKey().getName());
							break;
					}
				}
			}

			for(int i = -maxRange; i <= maxRange; i++){
				for(int j = -maxRange; j <= maxRange; j++){
					for(int k = -maxRange; k <= maxRange; k++){
						for(QueuedEffect eff : effects){
							if(eff.range * eff.range >= i * i + j * j + k * k){
								eff.perform(world, pos.add(i, j, k), reags);
							}
						}
					}
				}
			}
		}
	}

	private static class QueuedEffect{

		private final Color col;
		private final IAlchEffect effect;
		private final EnumMatterPhase phase;
		private final int range;
		private final int qty;

		public QueuedEffect(Color col, @Nullable IAlchEffect effect, EnumMatterPhase phase, int range, int qty){
			this.col = col;
			this.effect = effect;
			this.phase = phase;
			this.range = range;
			this.qty = qty;
		}

		private void perform(World world, BlockPos pos, ReagentMap reags){
			if(effect != null){
				effect.doEffect(world, pos, qty, phase, reags);
			}

			if(phase == EnumMatterPhase.GAS){
				((ServerWorld) world).spawnParticle(ModParticles.COLOR_GAS, false, (float) pos.getX() + Math.random(), (float) pos.getY() + Math.random(), (float) pos.getZ() + Math.random(), 0, (Math.random() * 2D - 1D) * 0.015D, Math.random() * 0.015D, (Math.random() * 2D - 1D) * 0.015D, 1F, col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha());
			}
		}
	}
}
