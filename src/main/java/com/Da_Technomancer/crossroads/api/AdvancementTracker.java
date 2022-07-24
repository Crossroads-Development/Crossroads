package com.Da_Technomancer.crossroads.api;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.HashMap;

public class AdvancementTracker{

	private static final HashMap<String, Boolean> progressMap = new HashMap<>(16);

	/**
	 * Tracks Crossroads advancements.
	 * Required for hasAdvancement() to work on the client side
	 * Usually called in init() in relevant screens
	 */
	@OnlyIn(Dist.CLIENT)
	public static void listen(){
		Minecraft.getInstance().player.connection.getAdvancements().setListener(Listener.INSTANCE);
	}

	/**
	 * Gets whether a player has completed a crossroads advancement
	 * On the client side, this will only work if listen() has been called (usually done in init() in Screen)
	 * @param ent The player to get the advancements of
	 * @param advancement The path of the advancement to check. Assumes this is a crossroads advancement
	 * @return Whether the passed advancement has been completed
	 */
	public static boolean hasAdvancement(Player ent, String advancement){
		if(ent instanceof ServerPlayer){
			return ((ServerPlayer) ent).getAdvancements().getOrStartProgress(ent.level.getServer().getAdvancements().getAdvancement(new ResourceLocation(Crossroads.MODID, advancement))).isDone();
		}else if(ent instanceof LocalPlayer){
			return progressMap.getOrDefault(advancement, false);
		}else{
			Crossroads.logger.error("Advancement fetch on illegal entity type: " + (ent == null ? "NULL" : ent.toString()) + "; with advancement: " + advancement + "; Report to mod author");
			return false;
		}
	}

	/**
	 * Forcibly locks or unlocks a Crossroads advancement for a player
	 * @param ent The player to adjust the advancement for
	 * @param advancement The path of the advancement to (un)lock- assumes this is a Crossroads advancement
	 * @param enabled If true, enable the advancement- otherwise lock it
	 */
	public static void unlockAdvancement(ServerPlayer ent, String advancement, boolean enabled){
		PlayerAdvancements playAdv = ent.getAdvancements();
		Advancement adv = ent.level.getServer().getAdvancements().getAdvancement(new ResourceLocation(Crossroads.MODID, advancement));
		if(adv == null){
			return;//No advancement with this name exists
		}

		AdvancementProgress prog = playAdv.getOrStartProgress(adv);

		if(enabled){
			for(String s : prog.getRemainingCriteria()){
				playAdv.award(adv, s);
			}
		}else{
			for(String s : prog.getCompletedCriteria()){
				playAdv.revoke(adv, s);
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	private static class Listener implements ClientAdvancements.Listener{

		private static final Listener INSTANCE = new Listener();

		@Override
		public void onUpdateAdvancementProgress(Advancement advancementIn, AdvancementProgress progress){
			ResourceLocation id = advancementIn.getId();
			if(id.getNamespace().equals(Crossroads.MODID)){
				progressMap.put(id.getPath(), progress.isDone());
			}
		}

		@Override
		public void onSelectedTabChanged(@Nullable Advancement advancementIn){

		}

		@Override
		public void onAddAdvancementRoot(Advancement advancementIn){

		}

		@Override
		public void onRemoveAdvancementRoot(Advancement advancementIn){

		}

		@Override
		public void onAddAdvancementTask(Advancement advancementIn){

		}

		@Override
		public void onRemoveAdvancementTask(Advancement advancementIn){

		}

		@Override
		public void onAdvancementsCleared(){
			progressMap.clear();
		}
	}
}
