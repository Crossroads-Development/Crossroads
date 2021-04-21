package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.PathSigil;
import com.Da_Technomancer.essentials.gui.container.AutoCrafterContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class DetailedAutoCrafterContainer extends AutoCrafterContainer{

	@ObjectHolder("detailed_auto_crafter")
	private static ContainerType<AutoCrafterContainer> TYPE = null;

	public DetailedAutoCrafterContainer(int id, PlayerInventory playerInventory, PacketBuffer data){
		this(id, playerInventory, new Inventory(20), data.readBlockPos());
	}

	public DetailedAutoCrafterContainer(int id, PlayerInventory playerInventory, IInventory inv, BlockPos pos){
		super(TYPE, id, playerInventory, inv, pos);
		//Sigil slot, ID 55
		addSlot(new Slot(inv, 19, 106, 51){
			@Override
			public boolean mayPlace(ItemStack stack){
				return stack.getItem() instanceof PathSigil;
			}
		});
	}
}
