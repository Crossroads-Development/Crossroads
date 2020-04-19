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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.registries.ObjectHolder;

import java.util.ArrayList;

@ObjectHolder(Crossroads.MODID)
public class AtmosChargerTileEntity extends TileEntity implements ITickableTileEntity, IInfoTE{

	@ObjectHolder("atmos_charger")
	private static TileEntityType<AtmosChargerTileEntity> type = null;

	private static final Tag<Block> ANTENNA_TAG = new BlockTags.Wrapper(new ResourceLocation(Crossroads.MODID, "atmos_antenna"));

	private static final int FE_CAPACITY = 20_000;

	private int fe = 0;
	private int renderTimer = 0;
	private Boolean mode = null;

	public AtmosChargerTileEntity(){
		super(type);
	}

	public void resetCache(){
		mode = null;
	}

	private boolean isExtractMode(){
		if(mode != null){
			return mode;
		}
		BlockState state = world.getBlockState(pos);
		if(state.getBlock() != CRBlocks.atmosCharger){
			return false;
		}
		mode = state.get(CRProperties.ACTIVE);
		return mode;
	}

	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		if(player.world instanceof ServerWorld){
			int charge = AtmosChargeSavedData.getCharge((ServerWorld) player.world);
			chat.add(new TranslationTextComponent("tt.crossroads.atmos_charger.reading", charge, AtmosChargeSavedData.getCapacity(), MiscUtil.preciseRound(100D * charge / AtmosChargeSavedData.getCapacity(), 1)));
		}
	}

	private boolean isValidStructure(){
		//Requires 4 iron bars (block type controlled via tag) placed in a pillar on top
		BlockPos checkPos = pos;
		for(int i = 0; i < 4; i++){
			checkPos = checkPos.up();
			if(!world.getBlockState(checkPos).isIn(ANTENNA_TAG)){
				return false;
			}
		}
		return true;
	}

	@Override
	public void tick(){
		BlockState state = world.getBlockState(pos);
		if(world.isRemote || !(state.getBlock() instanceof AtmosCharger)){
			return;
		}
		renderTimer--;

		int atmosCharge = AtmosChargeSavedData.getCharge((ServerWorld) world);

		if(isExtractMode()){
			int op = Math.min((FE_CAPACITY - fe) / 1000, atmosCharge / 1000);
			if(op != 0 && isValidStructure()){
				fe += op * 1000;
				atmosCharge -= op * 1000;
				AtmosChargeSavedData.setCharge((ServerWorld) world, atmosCharge);
				markDirty();
				renderArc(false);
			}

			//Transfer fe out
			if(fe > 0){
				for(int i = 0; i < 4; i++){
					Direction side = Direction.byHorizontalIndex(i);
					TileEntity te = world.getTileEntity(pos.offset(side));
					LazyOptional<IEnergyStorage> otherCap;
					if(te != null && (otherCap = te.getCapability(CapabilityEnergy.ENERGY, side.getOpposite())).isPresent()){
						int moved = otherCap.orElseThrow(NullPointerException::new).receiveEnergy(fe, false);
						if(moved > 0){
							fe -= moved;
							markDirty();
						}
					}
				}
			}
		}else{
			int op = Math.min(fe / 1000, (AtmosChargeSavedData.getCapacity() - atmosCharge) / 1000);
			if(op != 0 && isValidStructure()){
				fe -= op * 1000;
				atmosCharge += op * 1000;
				AtmosChargeSavedData.setCharge((ServerWorld) world, atmosCharge);
				markDirty();
				renderArc(true);
			}
		}
	}

	private void renderArc(boolean up){
		if(renderTimer <= 0){
			renderTimer = 10;
			int arcs = world.rand.nextInt(4) + 2;
			float angle = (float) Math.PI * 2F / arcs;
			float[] start = new float[] {pos.getX() + 0.5F, pos.getY() + 1.1F, pos.getZ() + 0.5F};
			float[] startEn = new float[] {start[0], pos.getY() + 4.75F, start[2]};
			if(!up){
				//Flip travel direction to go down
				float swap = start[1];
				start[1] = startEn[1];
				startEn[1] = swap;
			}

			Vec3d arcVec = CRRenderUtil.VEC_K.scale(1.4D);
			int color = TeslaCoilTopTileEntity.COLOR_CODES[(int) (world.getGameTime() % 3)];
			for(int i = 0; i < arcs; i++){
				arcVec = arcVec.rotateYaw(angle);
				CRRenderUtil.addArc(world, start[0], start[1], start[2], start[0] + (float) arcVec.x, start[1] + (float) arcVec.y, start[2] + (float) arcVec.z, startEn[0], startEn[1], startEn[2], 1, 0F, (byte) 20, color);
			}
		}
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
	public void remove(){
		super.remove();
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
				markDirty();
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
				markDirty();
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
