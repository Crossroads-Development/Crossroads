package com.Da_Technomancer.crossroads.tileentities.heat;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.gui.container.FireboxContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;

@ObjectHolder(Crossroads.MODID)
public class FireboxTileEntity extends InventoryTE{

	@ObjectHolder("firebox")
	public static BlockEntityType<FireboxTileEntity> TYPE = null;

	public static final int POWER = 10;
	private static final int MAX_TEMP = 15_000;

	private int burnTime;
	private int maxBurnTime = 0;

	public FireboxTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 1);
	}

	@Override
	protected boolean useHeat(){
		return true;
	}

	public int getBurnProg(){
		return maxBurnTime == 0 ? 0 : 100 * burnTime / maxBurnTime;
	}

	@Override
	public void serverTick(){
		super.serverTick();
		if(burnTime != 0){
			temp = Math.min(MAX_TEMP, temp + POWER);
			if(--burnTime == 0){
				level.setBlock(worldPosition, CRBlocks.firebox.defaultBlockState(), 18);
			}
			setChanged();
		}

		int fuelBurn;
		if(burnTime == 0 && (fuelBurn = ForgeHooks.getBurnTime(inventory[0], null)) != 0){
			fuelBurn *= CRConfig.fireboxFuelMult.get();
			int configLimit = CRConfig.fireboxCap.get();
			if(configLimit >= 0){
				fuelBurn = Math.min(fuelBurn, configLimit);
			}
			burnTime = fuelBurn;
			maxBurnTime = burnTime;
			Item item = inventory[0].getItem();
			inventory[0].shrink(1);
			if(inventory[0].isEmpty() && item.hasContainerItem(inventory[0])){
				inventory[0] = item.getContainerItem(inventory[0]);
			}
			level.setBlock(worldPosition, CRBlocks.firebox.defaultBlockState().setValue(CRProperties.ACTIVE, true), 18);
			setChanged();
		}
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		burnTime = nbt.getInt("burn");
		maxBurnTime = nbt.getInt("max_burn");
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		nbt.putInt("burn", burnTime);
		nbt.putInt("max_burn", maxBurnTime);
	}

	private LazyOptional<ItemHandler> itemOpt = LazyOptional.of(ItemHandler::new);

	@Override
	public void setRemoved(){
		super.setRemoved();
		itemOpt.invalidate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing){
		if(capability == Capabilities.HEAT_CAPABILITY && (facing == Direction.UP || facing == null)){
			return (LazyOptional<T>) heatOpt;
		}
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (LazyOptional<T>) itemOpt;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		return index == 0 && ForgeHooks.getBurnTime(stack, null) != 0;
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return index == 0 && !canPlaceItem(index, stack);//Allow removing empty buckets
	}

	@Override
	public Component getDisplayName(){
		return new TranslatableComponent("container.firebox");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player){
		return new FireboxContainer(id, playerInv, createContainerBuf());
	}
}
