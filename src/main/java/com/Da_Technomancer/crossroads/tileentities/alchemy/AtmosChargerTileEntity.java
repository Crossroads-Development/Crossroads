package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.alchemy.AtmosChargeSavedData;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.alchemy.AtmosCharger;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.tileentities.electric.TeslaCoilTopTileEntity;
import com.Da_Technomancer.essentials.tileentities.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.registries.ObjectHolder;

import java.util.ArrayList;

@ObjectHolder(Crossroads.MODID)
public class AtmosChargerTileEntity extends BlockEntity implements ITickableTileEntity, IInfoTE{

	@ObjectHolder("atmos_charger")
	public static BlockEntityType<AtmosChargerTileEntity> TYPE = null;

	private static final Tag<Block> ANTENNA_TAG = BlockTags.createOptional(new ResourceLocation(Crossroads.MODID, "atmos_antenna"));

	private static final int FE_CAPACITY = 20_000;

	private int fe = 0;
	private int renderTimer = 0;
	private Boolean mode = null;

	public AtmosChargerTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	@Override
	public void setBlockState(BlockState state){
		super.setBlockState(state);
		mode = null;
	}

	private boolean isExtractMode(){
		if(mode != null){
			return mode;
		}
		BlockState state = getBlockState();
		if(state.getBlock() != CRBlocks.atmosCharger){
			return false;
		}
		mode = state.getValue(CRProperties.ACTIVE);
		return mode;
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		if(player.level instanceof ServerLevel){
			int charge = AtmosChargeSavedData.getCharge((ServerLevel) player.level);
			chat.add(new TranslatableComponent("tt.crossroads.atmos_charger.reading", charge, AtmosChargeSavedData.getCapacity(), MiscUtil.preciseRound(100D * charge / AtmosChargeSavedData.getCapacity(), 1)));
		}
	}

	private boolean isValidStructure(){
		//Requires 4 iron bars (block type controlled via tag) placed in a pillar on top
		BlockPos checkPos = worldPosition;
		for(int i = 0; i < 4; i++){
			checkPos = checkPos.above();
			if(!level.getBlockState(checkPos).is(ANTENNA_TAG)){
				return false;
			}
		}
		return true;
	}

	@Override
	public void serverTick(){
		ITickableTileEntity.super.serverTick();
		BlockState state = getBlockState();
		if(!(state.getBlock() instanceof AtmosCharger)){
			return;
		}
		renderTimer--;

		int atmosCharge = AtmosChargeSavedData.getCharge((ServerLevel) level);

		if(isExtractMode()){
			int op = Math.min((FE_CAPACITY - fe) / 1000, atmosCharge / 1000);
			if(op != 0 && isValidStructure()){
				fe += op * 1000;
				atmosCharge -= op * 1000;
				AtmosChargeSavedData.setCharge((ServerLevel) level, atmosCharge);
				setChanged();
				renderArc(false);
			}

			//Transfer fe out
			if(fe > 0){
				for(int i = 0; i < 4; i++){
					Direction side = Direction.from2DDataValue(i);
					BlockEntity te = level.getBlockEntity(worldPosition.relative(side));
					LazyOptional<IEnergyStorage> otherCap;
					if(te != null && (otherCap = te.getCapability(CapabilityEnergy.ENERGY, side.getOpposite())).isPresent()){
						int moved = otherCap.orElseThrow(NullPointerException::new).receiveEnergy(fe, false);
						if(moved > 0){
							fe -= moved;
							setChanged();
						}
					}
				}
			}
		}else{
			int op = Math.min(fe / 1000, (AtmosChargeSavedData.getCapacity() - atmosCharge) / 1000);
			if(op != 0 && isValidStructure()){
				fe -= op * 1000;
				atmosCharge += op * 1000;
				AtmosChargeSavedData.setCharge((ServerLevel) level, atmosCharge);
				setChanged();
				renderArc(true);
			}
		}
	}

	private void renderArc(boolean charging){
		if(renderTimer <= 0){
			renderTimer = 10;

			int color = TeslaCoilTopTileEntity.COLOR_CODES[(int) (level.getGameTime() % 3)];
			if(charging){
				//Render electric arcs coming from the tip of the rod
				int arcs = level.random.nextInt(3) + 1;
				float[] start = new float[] {worldPosition.getX() + 0.5F, worldPosition.getY() + 5, worldPosition.getZ() + 0.5F};
				for(int i = 0; i < arcs; i++){
					float[] end = new float[] {start[0] + (level.random.nextFloat() - 0.5F) * 6F, start[1] + 6F * level.random.nextFloat(), start[2] + (level.random.nextFloat() - 0.5F) * 6F};
					CRRenderUtil.addArc(level, start[0], start[1], start[2], end[0], end[1], end[2], 1, 0F, (byte) 10, color, true);
				}
			}else{
				//Render arcs striking from various points along the rod
				int arcs = level.random.nextInt(3) + 2;
				float[] start = new float[] {worldPosition.getX() + 0.5F, 0, worldPosition.getZ() + 0.5F};
				for(int i = 0; i < arcs; i++){
					start[1] = worldPosition.getY() + 1F + level.random.nextFloat() * 4F;//Randomize start height along the rod
					float[] end = new float[] {start[0] + (level.random.nextFloat() - 0.5F) * 6F, start[1] + level.random.nextFloat() * 1.5F, start[2] + (level.random.nextFloat() - 0.5F) * 6F};
					CRRenderUtil.addArc(level, start[0], start[1], start[2], end[0], end[1], end[2], level.random.nextInt(3) / 2 + 1, 0.2F, (byte) 10, color, true);
				}
			}
		}
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		fe = nbt.getInt("fe");
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		nbt.putInt("fe", fe);
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		feOpt.invalidate();
	}

	private LazyOptional<IEnergyStorage> feOpt = LazyOptional.of(ElecHandler::new);

	@Override
	@SuppressWarnings("unchecked")
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == CapabilityEnergy.ENERGY && side != Direction.UP){
			return (LazyOptional<T>) feOpt;
		}
		return super.getCapability(cap, side);
	}

	private class ElecHandler implements IEnergyStorage{

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate){
			if(isExtractMode()){
				return 0;
			}
			int toMove = Math.min(FE_CAPACITY - fe, maxReceive);

			if(!simulate && toMove > 0){
				fe += toMove;
				setChanged();
			}

			return toMove;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate){
			if(!isExtractMode()){
				return 0;
			}

			int toMove = Math.min(maxExtract, fe);
			if(!simulate){
				fe -= toMove;
				setChanged();
			}
			return toMove;
		}

		@Override
		public int getEnergyStored(){
			return fe;
		}

		@Override
		public int getMaxEnergyStored(){
			return FE_CAPACITY;
		}

		@Override
		public boolean canExtract(){
			return isExtractMode();
		}

		@Override
		public boolean canReceive(){
			return !isExtractMode();
		}
	}
}
