package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.tileentities.electric.TeslaCoilTopTileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class ChargingStandTileEntity extends GlasswareHolderTileEntity{

	@ObjectHolder("charging_stand")
	private static TileEntityType<ChargingStandTileEntity> type = null;

	private static final int ENERGY_CAPACITY = 100;
	public static final int DRAIN = 10;

	private int fe = 0;

	public ChargingStandTileEntity(){
		super(type);
	}

	@Override
	public void tick(){
		if(world.isRemote){
			return;
		}
		if(fe > 0){
			fe = Math.max(0, fe - DRAIN);
			if(world.getGameTime() % 10 == 0){
				CRRenderUtil.addArc(world, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, pos.getX() + world.rand.nextFloat(), pos.getY() + world.rand.nextFloat(), pos.getZ() + world.rand.nextFloat(), 1, 0F, TeslaCoilTopTileEntity.COLOR_CODES[(int) (world.getGameTime() % 3)]);
			}
		}
		super.tick();
	}

	@Override
	public boolean isCharged(){
		return fe > 0 || super.isCharged();
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		fe = nbt.getInt("fe");
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putInt("fe", fe);
		return nbt;
	}

	@Override
	protected EnumTransferMode[] getModes(){
		//Does not connect to conduits, unlike the glassware holder
		return new EnumTransferMode[] {EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE};
	}

	@Override
	public void remove(){
		super.remove();
		elecOpt.invalidate();
	}

	private final LazyOptional<IEnergyStorage> elecOpt = LazyOptional.of(ElecHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == CapabilityEnergy.ENERGY){
			return (LazyOptional<T>) elecOpt;
		}
		if(side == Direction.UP && cap == Capabilities.CHEMICAL_CAPABILITY){
			//The super class (glassware holder) would return the handler for a conduit connection on the top
			//The charging stand does not allow that connection
			return LazyOptional.empty();
		}
		return super.getCapability(cap, side);
	}

	private class ElecHandler implements IEnergyStorage{

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate){
			int toMove = Math.min(ENERGY_CAPACITY - fe, maxReceive);

			if(!simulate && toMove > 0){
				fe += toMove;
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
			return fe;
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
}
