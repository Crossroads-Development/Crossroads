package com.Da_Technomancer.crossroads.integration.JEI;

import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.crafting.recipes.DetailedCrafterRec;
import com.Da_Technomancer.crossroads.gui.container.DetailedAutoCrafterContainer;
import com.Da_Technomancer.essentials.packets.SendNBTToServer;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class DetailedAutoCrafterTransfers{

	public static class DetailedRecipeTransfer implements IRecipeTransferHandler<DetailedAutoCrafterContainer, DetailedCrafterRec>{

		private final IRecipeTransferHandlerHelper helper;

		public DetailedRecipeTransfer(IRecipeTransferHandlerHelper helper) {
			this.helper = helper;
		}

		public Class<DetailedAutoCrafterContainer> getContainerClass() {
			return DetailedAutoCrafterContainer.class;
		}

		public Class<DetailedCrafterRec> getRecipeClass() {
			return DetailedCrafterRec.class;
		}

		@Nullable
		@Override
		public IRecipeTransferError transferRecipe(DetailedAutoCrafterContainer c, DetailedCrafterRec recipe, IRecipeLayout iRecipeLayout, Player playerEntity, boolean maxTransfer, boolean doTransfer){
			try{
				if(doTransfer && recipe != null && c.te != null){
					c.te.recipe = recipe.getId();
					CompoundTag nbt = new CompoundTag();
					nbt.putString("recipe", c.te.recipe.toString());
					CRPackets.sendPacketToServer(new SendNBTToServer(nbt, c.te.getBlockPos()));
				}
			}catch(Exception e){
				return helper.createUserErrorWithTooltip(new TranslatableComponent("tt.essentials.jei.recipe_transfer.fail"));
			}
			return null;
		}
	}
}
