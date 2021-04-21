package com.Da_Technomancer.crossroads.tileentities.heat;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.SmelterContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Optional;

@ObjectHolder(Crossroads.MODID)
public class SmelterTileEntity extends InventoryTE{

	@ObjectHolder("smelter")
	private static TileEntityType<SmelterTileEntity> type = null;

	public static final int REQUIRED = 500;
	public static final int[] TEMP_TIERS = {200, 300};
	public static final int USAGE = 5;

	private int progress = 0;

	public SmelterTileEntity(){
		super(type, 2);// 0 = Input, 1 = Output
	}

	public int getProgress(){
		return progress;
	}

	@Override
	protected boolean useHeat(){
		return true;
	}

	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		chat.add(new TranslationTextComponent("tt.crossroads.boilerplate.progress", progress, REQUIRED));
		super.addInfo(chat, player, hit);
	}

	@Override
	public void tick(){
		super.tick();
		if(level.isClientSide){
			return;
		}

		int tier = HeatUtil.getHeatTier(temp, TEMP_TIERS);
		if(tier != -1){
			temp -= USAGE * (tier + 1);

			ItemStack output = getOutput();
			if(!inventory[0].isEmpty() && !output.isEmpty()){
				progress += USAGE * (tier + 1);
				if(progress >= REQUIRED){
					progress = 0;

					if(inventory[1].isEmpty()){
						inventory[1] = output;
					}else{
						inventory[1].grow(output.getCount());
					}
					inventory[0].shrink(1);
				}
			}else{
				progress = 0;
			}
			setChanged();
		}
	}

	private ItemStack getOutput(){
		Optional<FurnaceRecipe> recOpt = level.getRecipeManager().getRecipeFor(IRecipeType.SMELTING, this, level);
		ItemStack stack = recOpt.isPresent() ? recOpt.get().getResultItem() : ItemStack.EMPTY;

		if(stack.isEmpty()){
			return ItemStack.EMPTY;
		}

		if(!inventory[1].isEmpty() && !ItemStack.isSame(stack, inventory[1])){
			return ItemStack.EMPTY;
		}

		if(!inventory[1].isEmpty() && getMaxStackSize() - inventory[1].getCount() < stack.getCount()){
			return ItemStack.EMPTY;
		}

		return stack.copy();
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt){
		super.load(state, nbt);
		progress = nbt.getInt("prog");
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt){
		super.save(nbt);
		nbt.putInt("prog", progress);
		return nbt;
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		itemOpt.invalidate();
	}

	private LazyOptional<IItemHandler> itemOpt = LazyOptional.of(ItemHandler::new);

	@SuppressWarnings("unchecked")
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.HEAT_CAPABILITY && (side == Direction.UP || side == null)){
			return (LazyOptional<T>) heatOpt;
		}
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && side != Direction.UP){
			return (LazyOptional<T>) itemOpt;
		}

		return super.getCapability(cap, side);
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		return index == 0 && level.getRecipeManager().getRecipeFor(IRecipeType.SMELTING, new Inventory(stack), level).isPresent();
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return index == 1;
	}

	@Override
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.smelter");
	}

	@Nullable
	@Override
	public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity playerEntity){
		return new SmelterContainer(id, playerInventory, createContainerBuf());
	}
}
