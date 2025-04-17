package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.api.CRCapabilities;
import com.Da_Technomancer.crossroads.api.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.api.alchemy.IChemicalHandler;
import com.Da_Technomancer.crossroads.api.electric.IEnergyCapable;
import com.Da_Technomancer.crossroads.api.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.blocks.electric.TeslaCoilTopTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import net.neoforged.neoforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public class ChargingStandTileEntity extends GlasswareHolderTileEntity implements IEnergyCapable{

	public static final BlockEntityType<ChargingStandTileEntity> TYPE = CRTileEntity.createType(ChargingStandTileEntity::new, CRBlocks.chargingStand);

	private static final int ENERGY_CAPACITY = 100;
	public static final int DRAIN = 10;

	private int fe = 0;

	private final IEnergyStorage energyHandler = new ElecHandler();

	public ChargingStandTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
		reactionChamber = new ReactionChamberImpl(() -> fe > 0);
	}

	@Override
	public void serverTick(){
		if(fe > 0){
			fe = Math.max(0, fe - DRAIN);
			if(level.random.nextInt(10) == 0){
				//Create an arc from one of the vertical metal pieces on the model to another, chosen at random
				Vec3 startOffset = new Vec3(-6.5D / 16D, level.random.nextFloat() * 12D / 16D + 2D / 16D, level.random.nextFloat() * 6D / 16D - 3D / 16D);
				Vec3 endOffset = new Vec3(6.5D / 16D, level.random.nextFloat() * 12D / 16D + 2D / 16D, level.random.nextFloat() * 6D / 16D - 3D / 16D);
				Vec3 centeredPos = Vec3.atBottomCenterOf(worldPosition);
				Vec3 arcStart = level.random.nextBoolean() ? startOffset.add(centeredPos) : startOffset.yRot((float) Math.PI / 2F).add(centeredPos);
				Vec3 arcEnd = level.random.nextBoolean() ? endOffset.add(centeredPos) : endOffset.yRot((float) Math.PI / 2F).add(centeredPos);
				CRRenderUtil.addArc(level, arcStart, arcEnd, 1, 0F, TeslaCoilTopTileEntity.COLOR_CODES[(int) (level.getGameTime() % 3)]);
			}
		}
		super.serverTick();
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		fe = nbt.getInt("fe");
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		nbt.putInt("fe", fe);
	}

	@Override
	protected EnumTransferMode[] getModes(){
		//Does not connect to conduits, unlike the glassware holder
		return new EnumTransferMode[] {EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE};
	}

	@Nullable
	@Override
	public IEnergyStorage getEnergyHandler(Direction dir){
		return energyHandler;
	}

	@Override
	public IChemicalHandler getChemicalHandler(Direction dir){
		if(dir == Direction.UP){
			return null;
		}
		return super.getChemicalHandler(dir);
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
