package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class HeatReservoirCreativeContainer extends AbstractContainerMenu{

	public float output;
	public String conf;
	public BlockPos pos;

	private static final Supplier<MenuType<?>> TYPE_SPL = MiscUtil.getCRRegistryObject("heat_reservoir_creative", ForgeRegistries.Keys.MENU_TYPES);

	public HeatReservoirCreativeContainer(int id, Inventory playerInventory, FriendlyByteBuf data){
		this(id, playerInventory, data == null ? 0 : data.readFloat(), data == null ? null : data.readUtf(), data == null ? null : data.readBlockPos());
	}

	public HeatReservoirCreativeContainer(int id, Inventory playerInventory, float output, String settingStr, BlockPos pos){
		super(TYPE_SPL.get(), id);
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
