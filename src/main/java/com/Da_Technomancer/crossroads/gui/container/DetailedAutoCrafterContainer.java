package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.items.PathSigil;
import com.Da_Technomancer.essentials.gui.container.AutoCrafterContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class DetailedAutoCrafterContainer extends AutoCrafterContainer{

	protected static final MenuType<DetailedAutoCrafterContainer> TYPE = CRContainers.createConType(DetailedAutoCrafterContainer::new);

	public DetailedAutoCrafterContainer(int id, Inventory playerInventory, FriendlyByteBuf data){
		this(id, playerInventory, new SimpleContainer(20), data.readBlockPos());
	}

	public DetailedAutoCrafterContainer(int id, Inventory playerInventory, Container inv, BlockPos pos){
		super((MenuType<? extends AutoCrafterContainer>) TYPE, id, playerInventory, inv, pos);
		//Sigil slot, ID 55
		addSlot(new Slot(inv, 19, 106, 51){
			@Override
			public boolean mayPlace(ItemStack stack){
				return stack.getItem() instanceof PathSigil;
			}
		});
	}
}
