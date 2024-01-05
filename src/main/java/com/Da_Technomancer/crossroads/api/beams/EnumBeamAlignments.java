package com.Da_Technomancer.crossroads.api.beams;

import com.Da_Technomancer.crossroads.api.AdvancementTracker;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.effects.beam_effects.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Locale;

public enum EnumBeamAlignments{

	//Alignments that overlap defer to the one with the lower ordinal
	//i.e. the highest priority alignments are defined first

	TIME(new TimeEffect(), new Color(255, 100, 0), 16),
	ENCHANTMENT(new EnchantEffect(), new Color(251, 255, 184), 16),
	EQUILIBRIUM(new EquilibriumEffect(), new Color(255, 132, 255), 40),
	RIFT(new RiftEffect(), new Color(255, 0, 255), 96),
	CHARGE(new ChargeEffect(), new Color(255, 255, 0), 128),
	EXPANSION(new PlaceEffect(), new Color(0, 255, 255), 72),
	FUSION(BeamEffect.INSTANCE, new Color(132, 255, 255), 64),
	LIGHT(new LightEffect(), new Color(255, 255, 255), 128),
	
	//These MUST be declared last so they have bottom priority.
	STABILITY(new ExplosionEffect(), new Color(0, 0, 255), 254),
	POTENTIAL(new GrowEffect(), new Color(0, 255, 0), 254),
	ENERGY(new EnergizeEffect(), new Color(255, 0, 0), 254),
	VOID(new VoidEffect(), new Color(0, 0, 0), 0),
	//If there are any combinations that result in NO_MATCH, then another element should be made to fill that spot
	//Exists solely to prevent NullPointerExceptions and should never appear to the player
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
	public static EnumBeamAlignments getAlignment(@Nonnull BeamUnit magic){
		return magic.getAlignment();
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

	/**
	 * Gets the localized name of this alignment
	 * @param voi Whether this is the void version
	 * @return The localized name of this alignment
	 */
	public String getLocalName(boolean voi){
		String result;
		if(voi){
			result = MiscUtil.localize("alignment." + toString().toLowerCase() + ".void");
		}else{
			result = MiscUtil.localize("alignment." + toString().toLowerCase());
		}
		return result;
	}

	@Override
	public String toString(){
		return name().toLowerCase(Locale.US);
	}

	/**
	 * Gets whether a player has unlocked this alignment.
	 * If this is the client side, requires AdvancementTracker.listen() having been called first
	 * @param player The player to check
	 * @return Whether the given player has unlocked this alignment
	 */
	public boolean isDiscovered(Player player){
		return AdvancementTracker.hasAdvancement(player, "progress/alignment/" + this.toString());
	}

	/**
	 * Sets whether a player has unlocked this alignment.
	 * Only works on the server side
	 * @param player The player to (un)lock this path for
	 * @param discover Whether this player should have this alignment unlocked. If false, relocks this path
	 */
	public void discover(Player player, boolean discover){
		if(player.level().isClientSide){
			return;//We can't do this on the client side
		}
		AdvancementTracker.unlockAdvancement((ServerPlayer) player, "progress/alignment/" + toString(), discover);
//
//		CompoundNBT nbt = StoreNBTToClient.getPlayerTag(player);
//		if(!nbt.contains("alignments")){
//			nbt.put("alignments", new CompoundNBT());
//		}
//		if(isDiscovered(player) ^ discover){
//			nbt.getCompound("alignments").putBoolean(toString(), discover);
//			StoreNBTToClient.syncNBTToClient((ServerPlayerEntity) player);
//			//Doesn't use deletion-chat as the element discovery notification shouldn't be wiped away in 1 tick.
//			if(discover){
//				MiscUtil.chatMessage(player, new TranslationTextComponent("tt.crossroads.element_discover", getLocalName(false)).applyTextStyle(TextFormatting.BOLD));
//			}else{
//				MiscUtil.chatMessage(player, new TranslationTextComponent("tt.crossroads.element_discover.undo", getLocalName(false)).applyTextStyle(TextFormatting.BOLD));
//			}
//		}
	}
}