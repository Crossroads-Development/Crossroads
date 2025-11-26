package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.blocks.technomancy.DetailedAutoCrafter;
import com.Da_Technomancer.crossroads.blocks.technomancy.DetailedAutoCrafterTileEntity;
import com.Da_Technomancer.crossroads.crafting.DetailedCrafterRec;
import com.Da_Technomancer.essentials.api.BlockMenuContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeHolder;

public class DetailedAutoCrafterContainer extends BlockMenuContainer<DetailedAutoCrafterTileEntity> implements ContainerListener{

	private final ContainerData containerData;
	private final ResultContainer resultContainer = new ResultContainer();


	public DetailedAutoCrafterContainer(int id, Inventory playerInventory, FriendlyByteBuf data){
		super(CRContainers.DETAILED_AUTO_CRAFTER_CONTAINER.get(), id, playerInventory, data);
		//Called on client side
		containerData = new SimpleContainerData(10);
	}

	public DetailedAutoCrafterContainer(int id, Inventory playerInventory, FriendlyByteBuf data, ContainerData containerData){
		super(CRContainers.DETAILED_AUTO_CRAFTER_CONTAINER.get(), id, playerInventory, data);
		this.containerData = containerData;
		addSlotListener(this);
	}

	@Override
	protected void addSlots(){
		// Crafting input slots, ID 0-8
		for(int i = 0; i < 9; i++){
			addSlot(new Slot(te, i, 26 + (i % 3) * 18, 17 + (i / 3) * 18));
		}
		addSlot(new StrictSlot(te.sigilSlotContainer, 0, 134, 57));
		addSlot(new NonInteractiveResultSlot(resultContainer, 0, 134, 35));

		addDataSlots(containerData);
		refreshRecipeResult();
	}

	public DetailedAutoCrafterTileEntity getTE(){
		return te;
	}

	@Override
	protected int slotCount(){
		return 11;
	}

	@Override
	protected int[] getInvStart(){
		return new int[] {8, 83};
	}

	private void refreshRecipeResult(){
		if(te.getLevel() instanceof ServerLevel level){
			CraftingInput craftinginput = te.asCraftInput();
			RecipeHolder<DetailedCrafterRec> rec = DetailedAutoCrafter.getRecipe(level, te, false);
			ItemStack itemstack;
			if(rec == null){
				itemstack = ItemStack.EMPTY;
			}else{
				itemstack = rec.value().assemble(craftinginput, level.registryAccess());
			}
			this.resultContainer.setItem(0, itemstack);
		}
	}

	@Override
	public void slotChanged(AbstractContainerMenu pContainerToSend, int pDataSlotIndex, ItemStack pStack){
		refreshRecipeResult();
	}

	@Override
	public void dataChanged(AbstractContainerMenu pContainerMenu, int pDataSlotIndex, int pValue){

	}

	public boolean isSlotDisabled(int pSlot){
		return pSlot > -1 && pSlot < 9 && this.containerData.get(pSlot) == 1;
	}

	public void setSlotState(int pSlot, boolean pEnabled){
		CrafterSlot crafterslot = (CrafterSlot) this.getSlot(pSlot);
		this.containerData.set(crafterslot.index, pEnabled ? 0 : 1);
		this.broadcastChanges();
	}

	public boolean isPowered(){
		return this.containerData.get(9) == 1;
	}
}
