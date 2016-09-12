package com.Da_Technomancer.crossroads.API.enums;

import java.awt.Color;
import java.util.Random;

import com.Da_Technomancer.crossroads.API.effects.BlockEffect;
import com.Da_Technomancer.crossroads.API.effects.ChargeEffect;
import com.Da_Technomancer.crossroads.API.effects.EnchantEffect;
import com.Da_Technomancer.crossroads.API.effects.EnergizeEffect;
import com.Da_Technomancer.crossroads.API.effects.EqualibriumEffect;
import com.Da_Technomancer.crossroads.API.effects.ExplodeEffect;
import com.Da_Technomancer.crossroads.API.effects.FusionEffect;
import com.Da_Technomancer.crossroads.API.effects.GrowEffect;
import com.Da_Technomancer.crossroads.API.effects.IEffect;
import com.Da_Technomancer.crossroads.API.effects.LightEffect;
import com.Da_Technomancer.crossroads.API.effects.PlaceEffect;
import com.Da_Technomancer.crossroads.API.effects.TimeEffect;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;

import net.minecraft.init.Blocks;

public enum MagicElements{
	
	TIME(new TimeEffect(), new TimeEffect.VoidTimeEffect(), new Color(255, 100, 0), 16),
	ENCHANTMENT(new EnchantEffect(), new EnchantEffect.DisenchantEffect(), new Color(251, 255, 184), 16),
	LIGHT(new LightEffect(), new LightEffect.VoidLightEffect(), new Color(255, 255, 255), 68),
	EQUALIBRIUM(new EqualibriumEffect(), new EqualibriumEffect.VoidEqualibriumEffect(), new Color(255, 0, 255), 72),
	CHARGE(new ChargeEffect(), new ChargeEffect.VoidChargeEffect(), new Color(255, 255, 0), 72),
	EXPANSION(new PlaceEffect(), new PlaceEffect.BreakEffect(), new Color(0, 255, 255), 72),
	FUSION(new FusionEffect(), new FusionEffect.VoidFusionEffect(), new Color(132, 255, 255), 60),
	
	
	//These MUST be declared last so they have bottom priority.
	ENERGY(new EnergizeEffect(), new EnergizeEffect.VoidEnergizeEffect(), new Color(255, 0, 0), 250),
	POTENTIAL(new GrowEffect(), new GrowEffect.PlantKillEffect(), new Color(0, 255, 0), 250),
	STABILITY(null, new ExplodeEffect(), new Color(0, 0, 255), 250),
	VOID(new BlockEffect(Blocks.AIR.getDefaultState()), new BlockEffect(Blocks.AIR.getDefaultState()), new Color(0, 0, 0), 0),
	//If there are any combinations that result in NO_MATCH, then another element should be made to fill that spot
	NO_MATCH(null, null, new Color(255, 255, 255), 255);
	
	private final IEffect effect;
	private final IEffect voidEffect;
	private final Color mid;
	private final int range;
	
	MagicElements(IEffect eff, IEffect voidEff, Color cent, int range){
		this.effect = eff;
		this.voidEffect = voidEff;
		this.mid = cent;
		this.range = range;
	}
	
	public IEffect getEffect(){
		return effect;
	}
	
	public IEffect getVoidEffect(){
		return voidEffect;
	}
	
	private static final Random rand = new Random();
	public IEffect getMixEffect(Color col){
		if(col == null){
			return effect;
		}
		int top = Math.max(col.getBlue(), Math.max(col.getRed(), col.getGreen()));
		
		if(top < rand.nextInt(128) + 128){
			return voidEffect;
		}
		return effect;
	}
	
	public boolean contains(Color test){
		if(test == null){
			return false;
		}
		
		if(test.getRed() < (mid.getRed() - range) || test.getRed() > (mid.getRed() + range)){
			return false;
		}
		if(test.getGreen() < (mid.getGreen() - range) || test.getGreen() > (mid.getGreen() + range)){
			return false;
		}
		if(test.getBlue() < (mid.getBlue() - range) || test.getBlue() > (mid.getBlue() + range)){
			return false;
		}
		
		return true;
	}
	
	public static MagicElements getElement(MagicUnit magic){
		return magic == null ? null : getElement(magic.getTrueRGB());
	}
	
	public static MagicElements getElement(Color col){
		for(MagicElements elem : MagicElements.values()){
			if(elem.contains(col)){
				return elem;
			}
		}
		
		return VOID;
	}
}