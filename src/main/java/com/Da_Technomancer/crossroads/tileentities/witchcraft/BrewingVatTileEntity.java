package com.Da_Technomancer.crossroads.tileentities.witchcraft;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.BrewingVatContainer;
import com.Da_Technomancer.essentials.blocks.BlockUtil;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;

import com.Da_Technomancer.crossroads.API.templates.InventoryTE.ItemHandler;

@ObjectHolder(Crossroads.MODID)
public class BrewingVatTileEntity extends InventoryTE{

	@ObjectHolder("brewing_vat")
	public static BlockEntityType<BrewingVatTileEntity> type = null;

	public static final int[] TEMP_TIERS = {75, 90, 100};
	public static final int[] SPEED_MULT = {1, 2, 0};
	public static final int[] HEAT_DRAIN = {1, 2, 2};
	public static final int REQUIRED = 400;
	private int progress = 0;

	public BrewingVatTileEntity(BlockPos pos, BlockState state){
		super(type, 7);//Index 0: Ingredient; 1-3: Input potions; 4-6: Output potions
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		chat.add(new TranslatableComponent("tt.crossroads.boilerplate.progress", progress, REQUIRED));
		super.addInfo(chat, player, hit);
	}

	@Override
	protected boolean useHeat(){
		return true;
	}

	public int getProgess(){
		return progress;
	}

	@Override
	public void tick(){
		super.tick();

		if(level.isClientSide){
			return;
		}

		ItemStack created = ItemStack.EMPTY;

		//Only allow crafting if all inputs are present, all input potions are the same item, and all outputs are empty
		if(!inventory[0].isEmpty() && !inventory[1].isEmpty() && BlockUtil.sameItem(inventory[1], inventory[2]) && BlockUtil.sameItem(inventory[1], inventory[3]) && inventory[4].isEmpty() && inventory[5].isEmpty() && inventory[6].isEmpty()){
			created = BrewingRecipeRegistry.getOutput(inventory[1], inventory[0]);
		}

		if(created.isEmpty()){
			progress = 0;
		}

		//Actually advance the progress and consume heat
		int tier = HeatUtil.getHeatTier(temp, TEMP_TIERS);
		if(tier >= 0){
			temp -= HEAT_DRAIN[tier];

			if(!created.isEmpty()){
				progress += SPEED_MULT[tier];
				if(progress >= REQUIRED){
					progress = 0;

					//Consume the ingredients and create the output
					inventory[0].shrink(1);
					inventory[1].shrink(1);
					inventory[2].shrink(1);
					inventory[3].shrink(1);
					inventory[4] = created.copy();
					inventory[5] = created.copy();
					inventory[6] = created.copy();
				}
			}

			setChanged();
		}
	}

	@Override
	public int getMaxStackSize(int slot){
		//Any slot (other than ingredient) can only hold 1 item
		//The potion brewing helper tends to misbehave unless all the ingredients are in stacks of 1
		return slot == 0 ? super.getMaxStackSize(slot) : 1;
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		progress = nbt.getInt("prog");
	}

	@Override
	public CompoundTag save(CompoundTag nbt){
		super.save(nbt);
		nbt.putInt("prog", progress);
		return nbt;
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		itemOpt.invalidate();
	}

	private final LazyOptional<IItemHandler> itemOpt = LazyOptional.of(ItemHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == Capabilities.HEAT_CAPABILITY){
			return (LazyOptional<T>) heatOpt;
		}

		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (LazyOptional<T>) itemOpt;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return index >= 4 && index < 7;//Output slots
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		if(!super.canPlaceItem(index, stack)){
			return false;
		}
		if(index == 0){
			return BrewingRecipeRegistry.isValidIngredient(stack);
		}
		if(index > 0 && index < 4){
			if(stack.getCount() > 1){
				//BrewingRecipeRegistry.isValidInput only passes if the stacksize is 1
				stack = stack.copy();
				stack.setCount(1);
			}
			return BrewingRecipeRegistry.isValidInput(stack);
		}
		return false;
	}

	@Override
	public Component getDisplayName(){
		return new TranslatableComponent("container.crossroads.brewing_vat");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player playerEntity){
		return new BrewingVatContainer(id, playerInventory, createContainerBuf());
	}
}
