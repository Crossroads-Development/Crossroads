package com.Da_Technomancer.crossroads.tileentities.heat;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.gui.container.IceboxContainer;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import com.Da_Technomancer.crossroads.items.crafting.recipes.IceboxRec;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
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
	private static TileEntityType<IceboxTileEntity> type = null;

	public static int RATE = 10;
	public static int MIN_TEMP = -20;

	private int burnTime;
	private int maxBurnTime = 0;
	public IntReferenceHolder coolProg = IntReferenceHolder.single();

	public IceboxTileEntity(){
		super(type, 1);
	}

	@Override
	protected boolean useHeat(){
		return true;
	}

	@Override
	public void tick(){
		super.tick();
		if(world.isRemote){
			return;
		}

		if(burnTime != 0 && temp > MIN_TEMP){
			temp = Math.max(MIN_TEMP, temp - RATE);
			if(--burnTime == 0){
				world.setBlockState(pos, CRBlocks.icebox.getDefaultState().with(CRProperties.ACTIVE, false), 18);
			}
			coolProg.set(maxBurnTime == 0 ? 0 : 100 * burnTime / maxBurnTime);
			markDirty();
		}

		Optional<IceboxRec> rec;
		if(burnTime == 0 && (rec = world.getRecipeManager().getRecipe(RecipeHolder.COOLING_TYPE, this, world)).isPresent()){
			burnTime = Math.round(rec.get().getCooling());
			maxBurnTime = burnTime;
			coolProg.set(100 * burnTime / maxBurnTime);
			Item item = inventory[0].getItem();
			inventory[0].shrink(1);
			if(inventory[0].isEmpty() && item.hasContainerItem(inventory[0])){
				inventory[0] = item.getContainerItem(inventory[0]);
			}
			world.setBlockState(pos, CRBlocks.icebox.getDefaultState().with(CRProperties.ACTIVE, true), 18);
			markDirty();
		}
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		burnTime = nbt.getInt("burn");
		maxBurnTime = nbt.getInt("max_burn");
		coolProg.set(maxBurnTime == 0 ? 0 : 100 * burnTime / maxBurnTime);
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putInt("burn", burnTime);
		nbt.putInt("max_burn", maxBurnTime);
		return nbt;
	}

	@Override
	public void remove(){
		super.remove();
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
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return index == 0 && world.getRecipeManager().getRecipe(RecipeHolder.COOLING_TYPE, this, world).isPresent();
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction){
		return false;
	}

	@Override
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.icebox");
	}

	@Nullable
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player){
		return new IceboxContainer(id, playerInv, createContainerBuf());
	}
}
