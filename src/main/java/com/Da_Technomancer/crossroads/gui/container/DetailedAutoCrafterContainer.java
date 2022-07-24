package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.api.MiscUtil;
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
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class DetailedAutoCrafterContainer extends AutoCrafterContainer{

	private static final Supplier<MenuType<?>> TYPE_SPL = MiscUtil.getCRRegistryObject("detailed_auto_crafter", ForgeRegistries.Keys.MENU_TYPES);

	public DetailedAutoCrafterContainer(int id, Inventory playerInventory, FriendlyByteBuf data){
		this(id, playerInventory, new SimpleContainer(20), data.readBlockPos());
	}

	public DetailedAutoCrafterContainer(int id, Inventory playerInventory, Container inv, BlockPos pos){
		super((MenuType<? extends AutoCrafterContainer>) TYPE_SPL.get(), id, playerInventory, inv, pos);
		//Sigil slot, ID 55
		addSlot(new Slot(inv, 19, 106, 51){
			@Override
			public boolean mayPlace(ItemStack stack){
				return stack.getItem() instanceof PathSigil;
			}
		});
	}
}
