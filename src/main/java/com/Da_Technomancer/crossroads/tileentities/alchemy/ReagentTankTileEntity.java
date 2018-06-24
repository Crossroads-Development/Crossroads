package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.alchemy.*;
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

public class ReagentTankTileEntity extends AlchemyCarrierTE{

	public ReagentTankTileEntity(){
		super();
	}

	public ReagentTankTileEntity(boolean glass){
		super(glass);
	}

	@Override
	public double transferCapacity(){
		return 10_000D;
	}

	public double getAmount(){
		return amount;
	}

	public NBTTagCompound getContentNBT(){
		NBTTagCompound nbt = new NBTTagCompound();
		for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
			if(contents[i] != null){
				nbt.setDouble(i + "_am", contents[i].getAmount());
			}
		}
		nbt.setDouble("heat", heat);
		return nbt;
	}

	public void writeContentNBT(NBTTagCompound nbt){
		heat = nbt.getDouble("heat");
		for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
			contents[i] = nbt.hasKey(i + "_am") ? new ReagentStack(AlchemyCore.REAGENTS[i], nbt.getDouble(i + "_am")) : null;
		}
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
		if(cap == Capabilities.CHEMICAL_HANDLER_CAPABILITY){
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
		if(cap == Capabilities.CHEMICAL_HANDLER_CAPABILITY){
			return (T) handler;
		}
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (T) itemHandler;
		}
		return super.getCapability(cap, side);
	}


	private final ItemHandler itemHandler = new ItemHandler();

	@Override
	public boolean correctReag(){
		boolean out = super.correctReag();
		boolean destroy = false;

		if(glass){
			for(int i = 0; i < contents.length; i++){
				ReagentStack reag = contents[i];
				if(reag == null){
					continue;
				}
				if(!reag.getType().canGlassContain()){
					heat -= (heat / amount) * reag.getAmount();
					amount -= reag.getAmount();
					destroy |= reag.getType().destroysBadContainer();
					contents[i] = null;
				}
			}
			if(destroy){
				destroyChamber();
				return false;
			}
		}

		return out;
	}

	private class ItemHandler implements IItemHandler{

		private ItemStack[] fakeInventory = new ItemStack[AlchemyCore.ITEM_TO_REAGENT.size()];

		private void updateFakeInv(){
			fakeInventory = new ItemStack[AlchemyCore.ITEM_TO_REAGENT.size()];
			int index = 0;
			double endTemp = handler.getTemp();
			for(IReagent reag : AlchemyCore.ITEM_TO_REAGENT.values()){
				fakeInventory[index] = contents[reag.getIndex()] != null && contents[reag.getIndex()].getPhase(endTemp) == EnumMatterPhase.SOLID ? reag.getStackFromReagent(contents[reag.getIndex()]) : ItemStack.EMPTY;
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
					double perAmount = reag.getReagentFromStack(testStack).getAmount();
					int trans = Math.min(stack.getCount(), (int) ((transferCapacity() - amount) / perAmount));
					if(!simulate){
						if(contents[reag.getIndex()] == null){
							contents[reag.getIndex()] = new ReagentStack(reag, perAmount * (double) trans);
						}else{
							contents[reag.getIndex()].increaseAmount(perAmount * (double) trans);
						}
						heat += Math.min(reag.getMeltingPoint() + 263D, 290D) * perAmount * (double) trans;
						dirtyReag = true;
						markDirty();
					}
					testStack.setCount(stack.getCount() - trans);
					return testStack.isEmpty() ? ItemStack.EMPTY : testStack;
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
						int reagIndex = AlchemyCore.ITEM_TO_REAGENT.get(fakeInventory[slot]).getIndex();
						double endTemp = handler.getTemp();
						double reagAmount = AlchemyCore.REAGENTS[reagIndex].getReagentFromStack(outStack).getAmount();
						if(contents[reagIndex].increaseAmount(-reagAmount) <= 0){
							contents[reagIndex] = null;
						}
						heat -= (endTemp + 273D) * reagAmount;
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
			double temp = heat / amount - 273D;
			IBlockState state = world.getBlockState(pos);
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
			SoundType sound = state.getBlock().getSoundType(state, world, pos, null);
			world.playSound(null, pos, sound.getBreakSound(), SoundCategory.BLOCKS, sound.getVolume(), sound.getPitch());
			for(ReagentStack r : contents){
				if(r != null){
					r.getType().onRelease(world, pos, r.getAmount(), temp, r.getPhase(temp), contents);
				}
			}
		}
	}
}
