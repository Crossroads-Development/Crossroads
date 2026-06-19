package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.essentials.api.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public class SequenceBoxContainer extends AbstractContainerMenu{


	public final BlockPos pos;
	public final ArrayList<String> inputs;
	public int outputIndex;

	public SequenceBoxContainer(int id, Inventory playerInventory, FriendlyByteBuf data){
		super(CRContainers.SEQUENCE_BOX_CONTAINER.get(), id);
		if(data == null){
			pos = null;
			outputIndex = 0;
			inputs = new ArrayList<>(0);
			inputs.add("0");
		}else{
			pos = data.readBlockPos();
			outputIndex = data.readVarInt();
			int size = data.readVarInt();
			inputs = new ArrayList<>(size);
			for(int i = 0; i < size; i++){
				inputs.add(data.readUtf(Short.MAX_VALUE));
			}
		}
	}

	@Override
	public boolean stillValid(Player playerIn){
		return pos != null && BlockUtil.playerInRangeOfUI(playerIn, pos);
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int fromSlot){
		return ItemStack.EMPTY;
	}
}
