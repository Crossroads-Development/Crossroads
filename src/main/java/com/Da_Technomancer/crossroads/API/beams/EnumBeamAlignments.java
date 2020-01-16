package com.Da_Technomancer.crossroads.API.beams;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.effects.*;
import net.minecraft.block.Blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

public enum EnumBeamAlignments{
	
	TIME(new TimeEffect(), new Color(255, 100, 0), 16),
	ENCHANTMENT(new EnchantEffect(), new Color(251, 255, 184), 16),
	EQUILIBRIUM(new EqualibriumEffect(), new Color(255, 132, 255), 40),
	RIFT(new RiftEffect(), new Color(255, 0, 255), 96),
	CHARGE(new ChargeEffect(), new Color(255, 255, 0), 128),
	EXPANSION(new PlaceEffect(), new Color(0, 255, 255), 72),
	FUSION(BeamEffect.INSTANCE, new Color(132, 255, 255), 64),
	LIGHT(BeamEffect.INSTANCE, new Color(255, 255, 255), 128),
	
	//These MUST be declared last so they have bottom priority.
	STABILITY(new ExplodeEffect(), new Color(0, 0, 255), 254),
	POTENTIAL(new GrowEffect(), new Color(0, 255, 0), 254),
	ENERGY(new EnergizeEffect(), new Color(255, 0, 0), 254),
	VOID(new VoidEffect(), new Color(0, 0, 0), 0),
	//If there are any combinations that result in NO_MATCH, then another element should be made to fill that spot- Exists solely to prevent NullPointerExceptions
	NO_MATCH(BeamEffect.INSTANCE, new Color(255, 255, 255), 255);
	
	private final BeamEffect effect;
	private final Color mid;
	private final int range;

	EnumBeamAlignments(BeamEffect eff, Color cent, int range){
		this.effect = eff;
		this.mid = cent;
		this.range = range;
	}

	@Nonnull
	public BeamEffect getEffect(){
		return effect;
	}

	public boolean contains(Color test){
		if(test == null){
			return false;
		}

		return Math.abs(test.getRed() - mid.getRed()) < range && Math.abs(test.getGreen() - mid.getGreen()) < range && Math.abs(test.getBlue() - mid.getBlue()) < range;
	}

	@Nonnull
	public static EnumBeamAlignments getAlignment(@Nullable BeamUnit magic){
		return magic == null ? EnumBeamAlignments.NO_MATCH : getAlignment(magic.getTrueRGB());
	}

	@Nonnull
	public static EnumBeamAlignments getAlignment(Color col){
		for(EnumBeamAlignments elem : EnumBeamAlignments.values()){
			if(elem.contains(col)){
				return elem;
			}
		}
		
		return VOID;
	}

	public String getLocalName(boolean voi){
		if(voi){
			return MiscUtil.localize("alignment." + toString().toLowerCase() + ".void");
		}else{
			return MiscUtil.localize("alignment." + toString().toLowerCase());
		}
	}
}