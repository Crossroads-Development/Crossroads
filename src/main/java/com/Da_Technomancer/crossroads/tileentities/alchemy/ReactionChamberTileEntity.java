package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.alchemy.*;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.render.RenderUtil;
import com.Da_Technomancer.crossroads.tileentities.electric.TeslaCoilTopTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.logging.log4j.Level;

public class ReactionChamberTileEntity extends AlchemyReactorTE{

	private int energy = 0;
	private static final int ENERGY_CAPACITY = 100;

	public ReactionChamberTileEntity(){
		super();
	}

	public ReactionChamberTileEntity(boolean glass){
		super(glass);
	}

	@Override
	protected void initHeat(){
		if(!init){
			init = true;
			cableTemp = HeatUtil.convertBiomeTemp(world, pos);
		}
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
	public void tick(){
		if(world.isRemote){
			return;
		}

		if(energy >= 10){
			energy -= 10;
			if(world.getGameTime() % 10 == 0){
				RenderUtil.addArc(world.provider.getDimension(), pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, pos.getX() + world.rand.nextFloat(), pos.getY() + world.rand.nextFloat(), pos.getZ() + world.rand.nextFloat(), 1, 0F, TeslaCoilTopTileEntity.COLOR_CODES[(int) (world.getGameTime() % 3)]);
			}
		}

		super.tick();
	}

	@Override
	public boolean isCharged(){
		return energy >= 10 || super.isCharged();
	}

	@Override
	public int transferCapacity(){
		return 200;
	}

	@Override
	protected boolean useCableHeat(){
		return true;
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
				EnumContainerType cont = otherHandler.getChannel(side.getOpposite());
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

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		energy = nbt.getInt("ener");
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putInt("ener", energy);
		return nbt;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.CHEMICAL_CAPABILITY){
			return (T) handler;
		}
		if(cap == Capabilities.HEAT_CAPABILITY){
			return (T) heatHandler;
		}
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (T) itemHandler;
		}
		if(cap == CapabilityEnergy.ENERGY && (side == null || side.getAxis() != Axis.Y)){
			return (T) energyHandler;
		}
		return super.getCapability(cap, side);
	}

	private final HeatHandler heatHandler = new HeatHandler();
	private final ItemHandler itemHandler = new ItemHandler();
	private final EnergyHandler energyHandler  = new EnergyHandler();

	private class EnergyHandler implements IEnergyStorage{

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate){
			int toMove = Math.min(ENERGY_CAPACITY - energy, maxReceive);

			if(!simulate && toMove > 0){
				energy += toMove;
				markDirty();
			}

			return toMove;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate){
			return 0;
		}

		@Override
		public int getEnergyStored(){
			return energy;
		}

		@Override
		public int getMaxEnergyStored(){
			return ENERGY_CAPACITY;
		}

		@Override
		public boolean canExtract(){
			return false;
		}

		@Override
		public boolean canReceive(){
			return true;
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

	private class HeatHandler implements IHeatHandler{

		@Override
		public double getTemp(){
			initHeat();
			return cableTemp;
		}

		@Override
		public void setTemp(double tempIn){
			init = true;
			cableTemp = tempIn;
			dirtyReag = true;
			markDirty();
		}

		@Override
		public void addHeat(double tempChange){
			initHeat();
			cableTemp += tempChange;
			dirtyReag = true;
			markDirty();
		}
	}
}
