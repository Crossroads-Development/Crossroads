package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.gui.container.DetailedAutoCrafterContainer;
import com.Da_Technomancer.crossroads.items.PathSigil;
import com.Da_Technomancer.essentials.api.BlockUtil;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DetailedAutoCrafterTileEntity extends CrafterBlockEntity{

	public static final BlockEntityType<DetailedAutoCrafterTileEntity> TYPE = CRTileEntity.createType(DetailedAutoCrafterTileEntity::new, CRBlocks.detailedAutoCrafter);

	//Specifically the sigil slot is a separate container instance from everything else (which is handled by the superclass)
	public final SimpleContainer sigilSlotContainer = new SimpleContainer(1){
		@Override
		public boolean canPlaceItem(int pSlot, ItemStack pStack){
			return pStack.getItem() instanceof PathSigil;
		}
	};
	private boolean isLoading = false;

	public DetailedAutoCrafterTileEntity(BlockPos pos, BlockState state){
		super(pos, state);
		sigilSlotContainer.addListener(this::sigilSlotChange);
	}

	private void sigilSlotChange(Container container){
		if(!isLoading){
			setChanged();
		}
	}

	@Override
	public BlockEntityType<?> getType(){
		return TYPE;//Kind of dirty- the final BlockEntity.type field is set wrong by the superclass' constructor
	}

	@Override
	protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries){
		super.saveAdditional(pTag, pRegistries);
		pTag.put("sigil_item", BlockUtil.stackToNBT(sigilSlotContainer.getItem(0), pRegistries));
	}

	@Override
	protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries){
		super.loadAdditional(pTag, pRegistries);
		isLoading = true;
		sigilSlotContainer.setItem(0, BlockUtil.nbtToItemStack(pTag.getCompound("sigil_item"), pRegistries));
		isLoading = false;
	}

	@Override
	public Component getDisplayName(){
		return Component.translatable("container.detailed_auto_crafter");
	}

	@Override
	protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory){
		return new DetailedAutoCrafterContainer(pContainerId, pInventory, (new FriendlyByteBuf(Unpooled.buffer())).writeBlockPos(getBlockPos()), this.containerData);
	}
}
