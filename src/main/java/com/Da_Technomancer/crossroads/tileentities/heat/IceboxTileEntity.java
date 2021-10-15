package com.Da_Technomancer.crossroads.tileentities.heat;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.recipes.IceboxRec;
import com.Da_Technomancer.crossroads.gui.container.IceboxContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.Optional;

@ObjectHolder(Crossroads.MODID)
public class IceboxTileEntity extends InventoryTE{

	@ObjectHolder("icebox")
	public static BlockEntityType<IceboxTileEntity> TYPE = null;

	public static final int RATE = 10;
	public static final int MIN_TEMP = -20;

	private int burnTime;
	private int maxBurnTime = 0;

	public IceboxTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 1);
	}

	public int getCoolProg(){
		return maxBurnTime == 0 ? 0 : 100 * burnTime / maxBurnTime;
	}

	@Override
	protected boolean useHeat(){
		return true;
	}

	@Override
	public void serverTick(){
		super.serverTick();

		if(burnTime != 0){
			if(temp > MIN_TEMP){
				temp = Math.max(MIN_TEMP, temp - RATE);
			}
			if(--burnTime == 0){
				level.setBlock(worldPosition, CRBlocks.icebox.defaultBlockState().setValue(CRProperties.ACTIVE, false), 18);
			}
			setChanged();
		}

		Optional<IceboxRec> rec;
		if(burnTime == 0 && (rec = level.getRecipeManager().getRecipeFor(CRRecipes.COOLING_TYPE, this, level)).isPresent()){
			burnTime = Math.round(rec.get().getCooling());
			maxBurnTime = burnTime;
			Item item = inventory[0].getItem();
			inventory[0].shrink(1);
			if(inventory[0].isEmpty() && item.hasContainerItem(inventory[0])){
				inventory[0] = item.getContainerItem(inventory[0]);
			}
			level.setBlock(worldPosition, CRBlocks.icebox.defaultBlockState().setValue(CRProperties.ACTIVE, true), 18);
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
	public CompoundTag save(CompoundTag nbt){
		super.save(nbt);
		nbt.putInt("burn", burnTime);
		nbt.putInt("max_burn", maxBurnTime);
		return nbt;
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		itemOpt.invalidate();
	}

	private LazyOptional<IItemHandler> itemOpt = LazyOptional.of(ItemHandler::new);

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
		return index == 0 && level.getRecipeManager().getRecipeFor(CRRecipes.COOLING_TYPE, new SimpleContainer(stack), level).isPresent();
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return false;
	}

	@Override
	public Component getDisplayName(){
		return new TranslatableComponent("container.icebox");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player){
		return new IceboxContainer(id, playerInv, createContainerBuf());
	}
}
