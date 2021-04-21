package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.tileentities.electric.TeslaCoilTopTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
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
		if(level.isClientSide){
			return;
		}
		if(fe > 0){
			fe = Math.max(0, fe - DRAIN);
			if(level.random.nextInt(10) == 0){
				//Create an arc from one of the vertical metal pieces on the model to another, chosen at random
				Vector3d startOffset = new Vector3d(-6.5D / 16D, level.random.nextFloat() * 12D / 16D + 2D / 16D, level.random.nextFloat() * 6D / 16D - 3D / 16D);
				Vector3d endOffset = new Vector3d(6.5D / 16D, level.random.nextFloat() * 12D / 16D + 2D / 16D, level.random.nextFloat() * 6D / 16D - 3D / 16D);
				Vector3d centeredPos = Vector3d.atBottomCenterOf(worldPosition);
				Vector3d arcStart = level.random.nextBoolean() ? startOffset.add(centeredPos) : startOffset.yRot((float) Math.PI / 2F).add(centeredPos);
				Vector3d arcEnd = level.random.nextBoolean() ? endOffset.add(centeredPos) : endOffset.yRot((float) Math.PI / 2F).add(centeredPos);
				CRRenderUtil.addArc(level, arcStart, arcEnd, 1, 0F, TeslaCoilTopTileEntity.COLOR_CODES[(int) (level.getGameTime() % 3)]);
			}
		}
		super.tick();
	}

	@Override
	public boolean isCharged(){
		return fe > 0 || super.isCharged();
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt){
		super.load(state, nbt);
		fe = nbt.getInt("fe");
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt){
		super.save(nbt);
		nbt.putInt("fe", fe);
		return nbt;
	}

	@Override
	protected EnumTransferMode[] getModes(){
		//Does not connect to conduits, unlike the glassware holder
		return new EnumTransferMode[] {EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE};
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
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
				setChanged();
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
