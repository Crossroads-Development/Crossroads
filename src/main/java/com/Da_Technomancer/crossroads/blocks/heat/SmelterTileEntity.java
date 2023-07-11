package com.Da_Technomancer.crossroads.blocks.heat;

import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.heat.HeatUtil;
import com.Da_Technomancer.crossroads.api.templates.InventoryTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.gui.container.SmelterContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Optional;

public class SmelterTileEntity extends InventoryTE{

	public static final BlockEntityType<SmelterTileEntity> TYPE = CRTileEntity.createType(SmelterTileEntity::new, CRBlocks.smelter);

	public static final int REQUIRED = 500;
	public static final int[] TEMP_TIERS = {200, 300};
	public static final int USAGE = 5;

	private int progress = 0;

	public SmelterTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 2);// 0 = Input, 1 = Output
	}

	public int getProgress(){
		return progress;
	}

	@Override
	protected boolean useHeat(){
		return true;
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		chat.add(Component.translatable("tt.crossroads.boilerplate.progress", progress, REQUIRED));
		super.addInfo(chat, player, hit);
	}

	@Override
	public void serverTick(){
		super.serverTick();

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
		Optional<SmeltingRecipe> recOpt = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, this, level);
		ItemStack stack = recOpt.isPresent() ? recOpt.get().getResultItem(level.registryAccess()) : ItemStack.EMPTY;

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
	public void load(CompoundTag nbt){
		super.load(nbt);
		progress = nbt.getInt("prog");
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		nbt.putInt("prog", progress);
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
		if(cap == ForgeCapabilities.ITEM_HANDLER && side != Direction.UP){
			return (LazyOptional<T>) itemOpt;
		}

		return super.getCapability(cap, side);
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		return index == 0 && level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(stack), level).isPresent();
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return index == 1;
	}

	@Override
	public Component getDisplayName(){
		return Component.translatable("container.smelter");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player playerEntity){
		return new SmelterContainer(id, playerInventory, createContainerBuf());
	}
}
