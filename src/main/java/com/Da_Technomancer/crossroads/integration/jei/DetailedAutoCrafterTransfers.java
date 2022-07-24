package com.Da_Technomancer.crossroads.integration.jei;

import com.Da_Technomancer.crossroads.api.packets.CRPackets;
import com.Da_Technomancer.crossroads.crafting.DetailedCrafterRec;
import com.Da_Technomancer.crossroads.gui.container.DetailedAutoCrafterContainer;
import com.Da_Technomancer.essentials.api.packets.SendNBTToServer;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

import java.util.Optional;

public class DetailedAutoCrafterTransfers{

	public static class DetailedRecipeTransfer implements IRecipeTransferHandler<DetailedAutoCrafterContainer, DetailedCrafterRec>{

		private final IRecipeTransferHandlerHelper helper;

		public DetailedRecipeTransfer(IRecipeTransferHandlerHelper helper) {
			this.helper = helper;
		}

		public Class<DetailedAutoCrafterContainer> getContainerClass() {
			return DetailedAutoCrafterContainer.class;
		}

		@Override
		public Optional<MenuType<DetailedAutoCrafterContainer>> getMenuType(){
			return Optional.empty();
		}

		@Override
		public RecipeType<DetailedCrafterRec> getRecipeType(){
			return DetailedCrafterCategory.TYPE;
		}

		@Override
		public IRecipeTransferError transferRecipe(DetailedAutoCrafterContainer container, DetailedCrafterRec recipe, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer){
			try{
				if(doTransfer && recipe != null && container.te != null){
					container.te.recipe = recipe.getId();
					CompoundTag nbt = new CompoundTag();
					nbt.putString("recipe", container.te.recipe.toString());
					CRPackets.sendPacketToServer(new SendNBTToServer(nbt, container.te.getBlockPos()));
				}
			}catch(Exception e){
				return helper.createUserErrorWithTooltip(Component.translatable("tt.essentials.jei.recipe_transfer.fail"));
			}
			return null;
		}
	}
}
