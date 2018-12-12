package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.alchemy.*;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.Main;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;

public class ReagentTankTileEntity extends AlchemyCarrierTE{

	public ReagentTankTileEntity(){
		super();
	}

	public ReagentTankTileEntity(boolean glass){
		super(glass);
	}

	@Override
	public int transferCapacity(){
		return 1_000;
	}

	public NBTTagCompound getContentNBT(){
		if(contents.getTotalQty() == 0){
			return null;
		}
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		return nbt;
	}

	public void writeContentNBT(NBTTagCompound nbt){
		contents = ReagentMap.readFromNBT(nbt);
		dirtyReag = true;
	}

	@Override
	public EnumContainerType getChannel(){
		return EnumContainerType.NONE;
	}

	@Override
	protected EnumTransferMode[] getModes(){
		return new EnumTransferMode[] {EnumTransferMode.BOTH, EnumTransferMode.BOTH, EnumTransferMode.BOTH, EnumTransferMode.BOTH, EnumTransferMode.BOTH, EnumTransferMode.BOTH};
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.CHEMICAL_CAPABILITY){
			return true;
		}
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return true;
		}
		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.CHEMICAL_CAPABILITY){
			return (T) handler;
		}
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (T) itemHandler;
		}
		return super.getCapability(cap, side);
	}


	private final ItemHandler itemHandler = new ItemHandler();

	@Override
	public void correctReag(){
		super.correctReag();
		correctTemp();

		boolean destroy = false;

		ArrayList<IReagent> toRemove = new ArrayList<>(1);

		for(IReagent type : contents.keySet()){
			ReagentStack reag = contents.getStack(type);
			if(reag.isEmpty()){
				continue;
			}
			if(glass && !reag.getType().canGlassContain()){
				destroy |= reag.getType().destroysBadContainer();
				toRemove.add(type);
			}
		}

		for(IReagent type : toRemove){
			contents.remove(type);
		}

		if(destroy){
			destroyChamber();
		}
	}

	private class ItemHandler implements IItemHandler{

		private ItemStack[] fakeInventory = new ItemStack[AlchemyCore.ITEM_TO_REAGENT.size()];

		private void updateFakeInv(){
			fakeInventory = new ItemStack[AlchemyCore.ITEM_TO_REAGENT.size()];
			int index = 0;
			double endTemp = handler.getTemp();
			for(IReagent reag : AlchemyCore.ITEM_TO_REAGENT.values()){
				int qty = contents.getQty(reag);
				ReagentStack rStack = contents.getStack(reag);
				fakeInventory[index] = qty != 0 && reag.getPhase(endTemp) == EnumMatterPhase.SOLID ? reag.getStackFromReagent(rStack) : ItemStack.EMPTY;
				index++;
			}
		}

		@Override
		public int getSlots(){
			return AlchemyCore.ITEM_TO_REAGENT.size();
		}

		@Override
		public ItemStack getStackInSlot(int slot){
			updateFakeInv();
			return fakeInventory[slot];
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
			if(!stack.isEmpty()){
				IReagent reag = AlchemyCore.ITEM_TO_REAGENT.get(stack);
				if(reag != null){
					if(dirtyReag){
						correctReag();
					}
					ItemStack testStack = stack.copy();
					testStack.setCount(1);
					int trans = Math.min(stack.getCount(), transferCapacity() - contents.getTotalQty());
					if(!simulate){
						double itemTemp = HeatUtil.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
						if(itemTemp >= reag.getMeltingPoint()){
							itemTemp = Math.min(HeatUtil.ABSOLUTE_ZERO, reag.getMeltingPoint() - 100D);
						}
						contents.addReagent(reag, trans, itemTemp);
						dirtyReag = true;
						markDirty();
					}
					testStack.setCount(stack.getCount() - trans);
					return testStack;
				}
			}
			return stack;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate){
			updateFakeInv();
			int canExtract = Math.min(fakeInventory[slot].getCount(), amount);
			if(canExtract > 0){
				try{
					ItemStack outStack = fakeInventory[slot].copy();
					outStack.setCount(canExtract);
					if(!simulate){
						IReagent reag = AlchemyCore.ITEM_TO_REAGENT.get(fakeInventory[slot]);
						double endTemp = handler.getTemp();
						contents.removeReagent(reag, canExtract);
						dirtyReag = true;
						markDirty();
					}
					return outStack;
				}catch(NullPointerException e){
					Main.logger.log(Level.FATAL, "Alchemy Item/Reagent map error. Slot: " + slot + ", Stack: " + fakeInventory[slot], e);
				}
			}

			return ItemStack.EMPTY;
		}

		@Override
		public int getSlotLimit(int slot){
			return 10;
		}
	}

	private boolean broken = false;

	private void destroyChamber(){
		if(!broken){
			broken = true;
			double temp = contents.getTempC();
			IBlockState state = world.getBlockState(pos);
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
			SoundType sound = state.getBlock().getSoundType(state, world, pos, null);
			world.playSound(null, pos, sound.getBreakSound(), SoundCategory.BLOCKS, sound.getVolume(), sound.getPitch());
			for(IReagent reag : contents.keySet()){
				int qty = contents.getQty(reag);
				if(qty > 0){
					reag.onRelease(world, pos, qty, temp, reag.getPhase(temp), contents);
				}
			}
		}
	}
}
