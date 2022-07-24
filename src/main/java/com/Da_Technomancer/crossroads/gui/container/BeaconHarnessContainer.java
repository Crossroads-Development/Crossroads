package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.technomancy.BeaconHarnessTileEntity;
import com.Da_Technomancer.essentials.api.IntDeferredRef;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class BeaconHarnessContainer extends AbstractContainerMenu{

	private static final Supplier<MenuType<?>> TYPE_SPL = MiscUtil.getCRRegistryObject("beacon_harness", ForgeRegistries.Keys.MENU_TYPES);

	public final IntDeferredRef cycleRef;
	public final BeaconHarnessTileEntity te;

	public BeaconHarnessContainer(int id, Inventory playerInv, FriendlyByteBuf data){
		super(TYPE_SPL.get(), id);

		BlockPos pos = data.readBlockPos();
		BlockEntity rawTE = playerInv.player.level.getBlockEntity(pos);
		if(rawTE instanceof BeaconHarnessTileEntity){
			te = (BeaconHarnessTileEntity) rawTE;
		}else{
			//Should never happen
			te = null;
			Crossroads.logger.error("Null/Incorrect TileEntity passed to BeaconHarnessContainer!");
		}

		if(te == null){
			cycleRef = new IntDeferredRef(() -> 0, false);
		}else{
			cycleRef = new IntDeferredRef(te::getCycles, te.getLevel().isClientSide);
		}
		addDataSlot(cycleRef);
	}

	@Override
	public boolean stillValid(Player playerIn){
		return te.stillValid(playerIn);
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int fromSlot){
		return ItemStack.EMPTY;
	}
}
