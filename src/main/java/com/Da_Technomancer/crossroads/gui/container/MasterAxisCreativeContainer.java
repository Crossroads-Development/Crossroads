package com.Da_Technomancer.crossroads.gui.container;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class MasterAxisCreativeContainer extends AbstractContainerMenu{

	public float output;
	public String conf;
	public BlockPos pos;

	protected static final MenuType<MasterAxisCreativeContainer> TYPE = CRContainers.createConType(MasterAxisCreativeContainer::new);

	public MasterAxisCreativeContainer(int id, Inventory playerInventory, FriendlyByteBuf data){
		this(id, playerInventory, data == null ? 0 : data.readFloat(), data == null ? null : data.readUtf(), data == null ? null : data.readBlockPos());
	}

	public MasterAxisCreativeContainer(int id, Inventory playerInventory, float output, String settingStr, BlockPos pos){
		super(TYPE, id);
		this.output = output;
		this.conf = settingStr;
		this.pos = pos;
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int fromSlot){
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(Player playerIn){
		return true;
	}
}
