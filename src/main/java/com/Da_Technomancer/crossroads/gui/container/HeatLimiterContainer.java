package com.Da_Technomancer.crossroads.gui.container;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class HeatLimiterContainer extends AbstractContainerMenu{

	public float output;
	public String conf;
	public BlockPos pos;

	protected static final MenuType<HeatLimiterContainer> TYPE = CRContainers.createConType(HeatLimiterContainer::new);

	public HeatLimiterContainer(int id, Inventory playerInventory, FriendlyByteBuf data){
		this(id, playerInventory, data == null ? 0 : data.readFloat(), data == null ? null : data.readUtf(), data == null ? null : data.readBlockPos());
	}

	public HeatLimiterContainer(int id, Inventory playerInventory, float output, String settingStr, BlockPos pos){
		super(TYPE, id);
		this.output = output;
		this.conf = settingStr;
		this.pos = pos;
	}

	@Override
	public boolean stillValid(Player playerIn){
		return true;
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int fromSlot){
		return ItemStack.EMPTY;
	}
}
