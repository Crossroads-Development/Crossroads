package com.Da_Technomancer.crossroads.tileentities.witchcraft;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.API.witchcraft.IPerishable;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.crafting.CRItemTags;
import com.Da_Technomancer.crossroads.gui.container.IncubatorContainer;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.essentials.blocks.BlockUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;

@ObjectHolder(Crossroads.MODID)
public class IncubatorTileEntity extends InventoryTE{

	public static final int REQUIRED = 2 * 60 * 20;//Total required progress for one operation, 2min
	public static final double SLOW_MULT = 0.1D;//Speed compared to full speed when in the operating temp range, but not near the target temp
	public static final int MIN_TEMP = 25;//Minimum operating temp
	public static final int MAX_TEMP = 150;//Maximum operating temp
	public static final int MARGIN = 10;//Considered near the target temp if within MARGIN of targetTemp

	private double progress = 0;
	private int targetTemp = 0;//Target temperature will be between (MIN_TEMP + MARGIN) and (MAX_TEMP - MARGIN), inclusive, once initialized

	@ObjectHolder("incubator")
	public static TileEntityType<IncubatorTileEntity> type = null;

	public IncubatorTileEntity(){
		super(type, 3);
		//Index 0: mutator, index 1: eggs, index 2: output
	}

	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		double target = getTargetTemp();
		chat.add(new TranslationTextComponent("tt.crossroads.boilerplate.progress", progress, REQUIRED));
		chat.add(new TranslationTextComponent("tt.crossroads.incubator.target", target, target - MARGIN, target + MARGIN));
		super.addInfo(chat, player, hit);
	}

	@Override
	protected boolean useHeat(){
		return true;
	}

	public int getProgress(){
		return (int) progress;
	}

	public int getTargetTemp(){
		if(targetTemp < MIN_TEMP){
			//Assume the target temperature has been reset, select a new value
			targetTemp = level.random.nextInt((int) (MAX_TEMP - MIN_TEMP - MARGIN * 2D) + 1) + MIN_TEMP + MARGIN;
			setChanged();
		}
		return targetTemp;
	}

	public static boolean withinTarget(double temp, double target){
		return temp <= target + MARGIN && temp >= target - MARGIN;
	}

	@Override
	public void tick(){
		super.tick();

		if(!level.isClientSide){
			boolean validRecipe = false;
			if(isValidMutator(inventory[0], level)){
				ItemStack toCreate = getCreatedItem(inventory[0], level);
				//Check that we have the other ingredient, and that there is space for the output
				if(!inventory[1].isEmpty() && (inventory[2].isEmpty() || BlockUtil.sameItem(inventory[2], toCreate) && toCreate.getCount() + inventory[2].getCount() <= toCreate.getMaxStackSize())){
					validRecipe = true;

					//Increase progress
					if(temp <= MAX_TEMP && temp >= MIN_TEMP){
						if(withinTarget(temp, getTargetTemp())){
							progress += 1D;
						}else{
							progress += SLOW_MULT;
						}
						if(progress >= REQUIRED){
							progress = 0;
							targetTemp = 0;//Reset the target temp, to be re-randomized
							if(inventory[2].isEmpty()){
								inventory[2] = toCreate;
							}else{
								inventory[2].grow(toCreate.getCount());
							}
							inventory[0].shrink(1);
							inventory[1].shrink(1);
						}
						setChanged();
					}
				}
			}else if(inventory[2].isEmpty()){
				//Eject the invalid input item, including spoiled embryos
				inventory[2] = inventory[0];
				inventory[0] = ItemStack.EMPTY;
				setChanged();
			}

			if(!validRecipe){
				//Reset any accumulated progress
				progress = 0;
			}
		}
	}

	private static ItemStack getCreatedItem(ItemStack mutator, World world){
		Item item = mutator.getItem();
		if(item == CRItems.embryo){
			ItemStack out = new ItemStack(CRItems.geneticSpawnEgg, 1);
			CRItems.geneticSpawnEgg.withEntityTypeData(out, CRItems.embryo.getEntityTypeData(mutator));
			return out;
		}
		if(item == CRItems.mutagen){
			ItemStack out = new ItemStack(CRItems.potionExtension);
			CRItems.potionExtension.getSpoilTime(out, world);
			return out;
		}
		return ItemStack.EMPTY;
	}

	private static boolean isValidMutator(ItemStack stack, World world){
		Item item = stack.getItem();
		if(item instanceof IPerishable && ((IPerishable) item).isSpoiled(stack, world)){
			return false;
		}
		return stack.getItem() == CRItems.embryo || stack.getItem() == CRItems.mutagen;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt){
		super.load(state, nbt);
		targetTemp = nbt.getInt("target");
		progress = nbt.getDouble("progress");
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt){
		super.save(nbt);
		nbt.putInt("target", targetTemp);
		nbt.putDouble("progress", progress);
		return nbt;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		return index == 0 && isValidMutator(stack, level) || index == 1 && CRItemTags.INCUBATOR_EGG.contains(stack.getItem());
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction side){
		return index == 2;
	}

	@Override
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.crossroads.incubator");
	}

	@Nullable
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player){
		return new IncubatorContainer(id, playerInv, createContainerBuf());
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
		if(capability == Capabilities.HEAT_CAPABILITY){
			return (LazyOptional<T>) heatOpt;
		}
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (LazyOptional<T>) itemOpt;
		}
		return super.getCapability(capability, facing);
	}
}
