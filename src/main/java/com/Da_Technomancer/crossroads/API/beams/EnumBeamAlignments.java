package com.Da_Technomancer.crossroads.API.beams;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.effects.*;
import com.Da_Technomancer.crossroads.API.packets.StoreNBTToClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Locale;

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
	public static EnumBeamAlignments getAlignment(@Nonnull BeamUnit magic){
		return magic.isEmpty() ? NO_MATCH : getAlignment(magic.getTrueRGB());
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
		if(voi){
			return MiscUtil.localize("alignment." + toString().toLowerCase() + ".void");
		}else{
			return MiscUtil.localize("alignment." + toString().toLowerCase());
		}
	}

	@Override
	public String toString(){
		return name().toLowerCase(Locale.US);
	}

	/**
	 * Gets whether a player has discovered this alignment.
	 * If this is the client side, make sure the nbt cache is up to date (via StoreNBTToClient)
	 * @param player The player to check
	 * @return Whether the given player has unlocked this alignment
	 */
	public boolean isDiscovered(PlayerEntity player){
		return StoreNBTToClient.getPlayerTag(player).getCompound("alignments").getBoolean(toString());
	}

	/**
	 * Sets whether the player has discovered this alignment
	 * Only meaningful if called on the server side
	 * @param player The player to discover this element
	 * @param discover If true, discover the element. If false, "undiscover" the element
	 */
	public void discover(PlayerEntity player, boolean discover){
		CompoundNBT nbt = StoreNBTToClient.getPlayerTag(player);
		if(!nbt.contains("alignments")){
			nbt.put("alignments", new CompoundNBT());
		}
		if(isDiscovered(player) ^ discover){
			nbt.getCompound("alignments").putBoolean(toString(), discover);
			StoreNBTToClient.syncNBTToClient((ServerPlayerEntity) player);
			//Doesn't use deletion-chat as the element discovery notification shouldn't be wiped away in 1 tick.
			if(discover){
				player.sendMessage(new TranslationTextComponent("tt.crossroads.element_discover", getLocalName(false)).applyTextStyle(TextFormatting.BOLD));
			}else{
				player.sendMessage(new TranslationTextComponent("tt.crossroads.element_discover.undo", getLocalName(false)).applyTextStyle(TextFormatting.BOLD));
			}
		}
	}
}