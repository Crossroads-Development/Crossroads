package com.Da_Technomancer.crossroads.API;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.multiplayer.ClientAdvancementManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
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
		Minecraft.getInstance().player.connection.getAdvancementManager().setListener(Listener.INSTANCE);
	}

	/**
	 * Gets whether a player has completed a crossroads advancement
	 * On the client side, this will only work if listen() has been called (usually done in init() in Screen)
	 * @param ent The player to get the advancements of
	 * @param advancement The path of the advancement to check. Assumes this is a crossroads advancement
	 * @return Whether the passed advancement has been completed
	 */
	public static boolean hasAdvancement(PlayerEntity ent, String advancement){
		if(ent instanceof ServerPlayerEntity){
			return ((ServerPlayerEntity) ent).getAdvancements().getProgress(ent.world.getServer().getAdvancementManager().getAdvancement(new ResourceLocation(Crossroads.MODID, advancement))).isDone();
		}else if(ent instanceof ClientPlayerEntity){
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
	public static void unlockAdvancement(ServerPlayerEntity ent, String advancement, boolean enabled){
		PlayerAdvancements playAdv = ent.getAdvancements();
		Advancement adv = ent.world.getServer().getAdvancementManager().getAdvancement(new ResourceLocation(Crossroads.MODID, advancement));
		if(adv == null){
			return;//No advancement with this name exists
		}

		AdvancementProgress prog = playAdv.getProgress(adv);

		if(enabled){
			for(String s : prog.getRemaningCriteria()){
				playAdv.grantCriterion(adv, s);
			}
		}else{
			for(String s : prog.getCompletedCriteria()){
				playAdv.revokeCriterion(adv, s);
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	private static class Listener implements  ClientAdvancementManager.IListener{

		private static final Listener INSTANCE = new Listener();

		@Override
		public void onUpdateAdvancementProgress(Advancement advancementIn, AdvancementProgress progress){
			ResourceLocation id = advancementIn.getId();
			if(id.getNamespace().equals(Crossroads.MODID)){
				progressMap.put(id.getPath(), progress.isDone());
			}
		}

		@Override
		public void setSelectedTab(@Nullable Advancement advancementIn){

		}

		@Override
		public void rootAdvancementAdded(Advancement advancementIn){

		}

		@Override
		public void rootAdvancementRemoved(Advancement advancementIn){

		}

		@Override
		public void nonRootAdvancementAdded(Advancement advancementIn){

		}

		@Override
		public void nonRootAdvancementRemoved(Advancement advancementIn){

		}

		@Override
		public void advancementsCleared(){
			progressMap.clear();
		}
	}
}
