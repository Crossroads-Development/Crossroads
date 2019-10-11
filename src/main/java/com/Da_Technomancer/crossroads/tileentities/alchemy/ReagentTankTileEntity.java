package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.alchemy.*;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.redstone.IAdvancedRedstoneHandler;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
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

	public CompoundNBT getContentNBT(){
		if(contents.getTotalQty() == 0){
			return null;
		}
		CompoundNBT nbt = new CompoundNBT();
		writeToNBT(nbt);
		return nbt;
	}

	public void writeContentNBT(CompoundNBT nbt){
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
	protected void performTransfer(){
		EnumTransferMode[] modes = getModes();
		for(int i = 0; i < 6; i++){
			if(modes[i].isOutput()){
				Direction side = Direction.byIndex(i);
				TileEntity te = world.getTileEntity(pos.offset(side));
				if(contents.getTotalQty() <= 0 || te == null || !te.hasCapability(Capabilities.CHEMICAL_CAPABILITY, side.getOpposite())){
					continue;
				}

				IChemicalHandler otherHandler = te.getCapability(Capabilities.CHEMICAL_CAPABILITY, side.getOpposite());
				if(otherHandler.getMode(side.getOpposite()) == EnumTransferMode.BOTH && modes[i] == EnumTransferMode.BOTH){
					continue;
				}

				if(contents.getTotalQty() != 0){
					if(otherHandler.insertReagents(contents, side.getOpposite(), handler)){
						correctReag();
						markDirty();
					}
				}
			}
		}
	}

	private final RedstoneHandler redsHandler = new RedstoneHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.CHEMICAL_CAPABILITY){
			return (T) handler;
		}
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (T) itemHandler;
		}
		if(cap == Capabilities.ADVANCED_REDSTONE_CAPABILITY){
			return (T) redsHandler;
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

		if(destroy){
			destroyChamber();
		}else{
			for(IReagent type : toRemove){
				contents.removeReagent(type, contents.get(type));
			}
		}
	}

	public int getRedstone(){
		return (int) Math.ceil(Math.min(15, 15D * contents.getTotalQty() / (double) transferCapacity()));
	}

	private class RedstoneHandler implements IAdvancedRedstoneHandler{

		@Override
		public double getOutput(boolean read){
			return read ? 15D * Math.min(1D, contents.getTotalQty() / (double) transferCapacity()) : 0;
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
						double itemTemp = HeatUtil.convertBiomeTemp(world, pos);
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
						contents.removeReagent(reag, canExtract);
						dirtyReag = true;
						markDirty();
					}
					return outStack;
				}catch(NullPointerException e){
					Crossroads.logger.log(Level.FATAL, "Alchemy Item/Reagent map error. Slot: " + slot + ", Stack: " + fakeInventory[slot], e);
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
			BlockState state = world.getBlockState(pos);
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
			SoundType sound = state.getBlock().getSoundType(state, world, pos, null);
			world.playSound(null, pos, sound.getBreakSound(), SoundCategory.BLOCKS, sound.getVolume(), sound.getPitch());
			AlchemyUtil.releaseChemical(world, pos, contents);
		}
	}
}
