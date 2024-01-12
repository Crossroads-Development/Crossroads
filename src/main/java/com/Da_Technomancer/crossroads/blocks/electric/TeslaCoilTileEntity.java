package com.Da_Technomancer.crossroads.blocks.electric;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.packets.CRPackets;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.LeydenJar;
import com.Da_Technomancer.essentials.api.IItemStorage;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import com.Da_Technomancer.essentials.api.packets.ILongReceiver;
import com.Da_Technomancer.essentials.api.packets.SendLongToClient;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class TeslaCoilTileEntity extends BlockEntity implements ITickableTileEntity, ILongReceiver, IItemStorage{

	public static final BlockEntityType<TeslaCoilTileEntity> TYPE = CRTileEntity.createType(TeslaCoilTileEntity::new, CRBlocks.teslaCoil);

	public static final int CAPACITY = 2000;

	private int storedSelf = 0;
	public boolean redstone = false;
	private ItemStack battery = ItemStack.EMPTY;

	public TeslaCoilTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	private LazyOptional<IEnergyStorage> stackOpt;
	@Nullable
	private IEnergyStorage getBatteryHandler(){
		if(battery.isEmpty()){
			return null;
		}
		if(stackOpt == null || !stackOpt.isPresent()){
			stackOpt = battery.getCapability(ForgeCapabilities.ENERGY, Direction.UP);
		}
		if(stackOpt.isPresent()){
			return stackOpt.orElseThrow(NullPointerException::new);
		}
		return null;
	}

	private boolean hasJar(){
		return battery.getItem() instanceof LeydenJar;
	}

	public int getTotalFE(){
		if(level.isClientSide){
			return storedSelf;
		}
		if(hasJar()){
			return storedSelf + getBatteryHandler().getEnergyStored();
		}
		return storedSelf;
	}

	protected ItemStack removeBattery(){
		if(!battery.isEmpty()){
			ItemStack result = battery;
			battery = ItemStack.EMPTY;
			stackOpt = null;
			setChanged();
			level.setBlock(worldPosition, getBlockState().setValue(CRProperties.ACTIVE, false), MiscUtil.BLOCK_FLAGS_NORMAL);
			return result;
		}
		return ItemStack.EMPTY;
	}

	protected ItemStack addBattery(ItemStack newBattery){
		if(battery.isEmpty() && newBattery.getCapability(ForgeCapabilities.ENERGY).isPresent()){
			battery = newBattery.split(1);
			stackOpt = null;
			setTotalFE(getTotalFE());
			setChanged();
			level.setBlock(worldPosition, getBlockState().setValue(CRProperties.ACTIVE, true), MiscUtil.BLOCK_FLAGS_NORMAL);
			return newBattery;
		}
		return newBattery;
	}

	public void setTotalFE(int totalFE){
		//Client-side: storedSelf = FE storage
		//No battery: storedSelf = FE storage
		//Jar-type battery: Jar is extension of FE storage. Stored preferentially in battery
		//Non-jar battery: Jar is separate FE storage- can add but not withdraw. Deposit as much in battery as possible
		if(level.isClientSide){
			storedSelf = totalFE;
			setChanged();
			return;
		}
		IEnergyStorage handler = getBatteryHandler();
		if(hasJar() && handler != null){
			int handlerFE = handler.getEnergyStored();
			if(handlerFE > totalFE){
				handler.extractEnergy(handlerFE - totalFE, false);
				storedSelf = 0;
			}else{
				totalFE -= handlerFE;
				totalFE -= handler.receiveEnergy(totalFE, false);
				storedSelf = totalFE;
			}
		}else if(handler != null && handler.canReceive()){
			totalFE -= handler.receiveEnergy(totalFE - handler.getEnergyStored(), false);
			storedSelf = totalFE;
		}else{
			storedSelf = totalFE;
		}
		setChanged();
	}

	public int getCapacity(){
		if(hasJar()){
			return CAPACITY + getBatteryHandler().getMaxEnergyStored();
		}
		return CAPACITY;
	}

	public float getRedstone(){
		return getTotalFE();
	}

	public void syncState(){
		int message = 0;
		if(redstone){
			message |= 1;
		}
		message |= getTotalFE() << 1;
		CRPackets.sendPacketAround(level, worldPosition, new SendLongToClient((byte) 0, message, worldPosition));
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayer sendingPlayer){
		if(identifier == 0){
			//Client side only
			redstone = (message & 1) == 1;
			storedSelf = (int) (message >>> 1);
		}
	}

	@Override
	public void tick(){
		//This is a common tick method
		//Due to the use of RNG to generate the bolt on both sides, depending on the implementation of jolt(),
		//the timing of jolts may differ on the client vs server sides
		if(!redstone && level.random.nextInt(10) == 0 && getTotalFE() > 0){
			BlockEntity topTE = level.getBlockEntity(worldPosition.above());
			if(topTE instanceof TeslaCoilTopTileEntity){
				((TeslaCoilTopTileEntity) topTE).jolt(this);
			}
		}
	}

	@Override
	public void serverTick(){
		ITickableTileEntity.super.serverTick();
		int totalFE;
		if(!redstone && (totalFE = getTotalFE()) > 0){
			Direction facing = getBlockState().getValue(CRProperties.HORIZ_FACING);
			BlockEntity te = level.getBlockEntity(worldPosition.relative(facing));
			LazyOptional<IEnergyStorage> energyOpt;
			if(te != null && (energyOpt = te.getCapability(ForgeCapabilities.ENERGY, facing.getOpposite())).isPresent()){
				IEnergyStorage storage = energyOpt.orElseThrow(NullPointerException::new);
				int moved = storage.receiveEnergy(totalFE, false);
				if(moved > 0){
					setTotalFE(totalFE - moved);
				}
			}
		}
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		nbt.putInt("stored", storedSelf);
		if(!battery.isEmpty()){
			nbt.put("battery", battery.save(new CompoundTag()));
		}
		nbt.putBoolean("reds", redstone);
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		storedSelf = nbt.getInt("stored");
		redstone = nbt.getBoolean("reds");
		if(nbt.contains("battery")){
			battery = ItemStack.of(nbt.getCompound("battery"));
		}else{
			//TODO remove: backwards compatibility
			if(!nbt.getBoolean("from_client") && getBlockState().getValue(CRProperties.ACTIVE)){
				battery = new ItemStack(CRItems.leydenJar);
				LeydenJar.setCharge(battery, Math.min(LeydenJar.MAX_CHARGE, storedSelf));
				storedSelf -= Math.min(LeydenJar.MAX_CHARGE, storedSelf);
			}
		}
	}

	@Override
	public CompoundTag getUpdateTag(){
		CompoundTag nbt = super.getUpdateTag();
		nbt.putInt("stored", getTotalFE());
		nbt.putBoolean("reds", redstone);
		nbt.putBoolean("from_client", true);//TODO remove: backwards compatibility
		return nbt;
	}

	public void rotate(){
		optIn.invalidate();
		optIn = LazyOptional.of(EnergyHandlerIn::new);
		optOut.invalidate();
		optOut = LazyOptional.of(EnergyHandlerOut::new);
	}

	@Override
	public void dropItems(Level world, BlockPos pos){
		Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), battery);
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		optIn.invalidate();
		optOut.invalidate();
		itemOpt.invalidate();
	}

	private LazyOptional<IEnergyStorage> optIn = LazyOptional.of(EnergyHandlerIn::new);
	private LazyOptional<IEnergyStorage> optOut = LazyOptional.of(EnergyHandlerOut::new);
	private LazyOptional<IItemHandler> itemOpt = LazyOptional.of(ItemHandler::new);

	@SuppressWarnings("unchecked")
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == ForgeCapabilities.ENERGY){
			return (LazyOptional<T>) (side == getBlockState().getValue(CRProperties.HORIZ_FACING) ? optOut : optIn);
		}
		if(cap == ForgeCapabilities.ITEM_HANDLER){
			return (LazyOptional<T>) itemOpt;
		}
		return super.getCapability(cap, side);
	}

	protected class ItemHandler implements IItemHandler{

		@Override
		public int getSlots(){
			return 1;
		}

		@Override
		public @NotNull ItemStack getStackInSlot(int slot){
			return slot == 0 ? battery : ItemStack.EMPTY;
		}

		@Override
		public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate){
			if(battery.isEmpty() && isItemValid(slot, stack)){
				ItemStack copy = stack.copy();
				if(!simulate){
					return addBattery(copy);
				}
				copy.shrink(1);
				return copy;
			}
			return stack;
		}

		@Override
		public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate){
			if(slot == 0 && amount > 0 && !battery.isEmpty()){
				if(simulate){
					return battery.copy();
				}
				return removeBattery();
			}
			return ItemStack.EMPTY;
		}

		@Override
		public int getSlotLimit(int slot){
			return slot == 0 ? 1 : 0;
		}

		@Override
		public boolean isItemValid(int slot, @NotNull ItemStack stack){
			return slot == 0 && stack.getCapability(ForgeCapabilities.ENERGY, Direction.UP).isPresent();
		}
	}

	protected class EnergyHandlerIn implements IEnergyStorage{

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate){
			int totalFE = getTotalFE();
			int toInsert = Math.min(maxReceive, getMaxEnergyStored() - totalFE);

			if(!simulate){
				setTotalFE(totalFE + toInsert);
			}
			return toInsert;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate){
			return 0;
		}

		@Override
		public int getEnergyStored(){
			return getTotalFE();
		}

		@Override
		public int getMaxEnergyStored(){
			return getCapacity();
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

	private class EnergyHandlerOut implements IEnergyStorage{

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate){
			return 0;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate){
			int totalFE = getTotalFE();
			int toExtract = Math.min(totalFE, maxExtract);
			if(!simulate){
				setTotalFE(totalFE - toExtract);
			}
			return toExtract;
		}

		@Override
		public int getEnergyStored(){
			return getTotalFE();
		}

		@Override
		public int getMaxEnergyStored(){
			return getCapacity();
		}

		@Override
		public boolean canExtract(){
			return true;
		}

		@Override
		public boolean canReceive(){
			return false;
		}
	}
}
