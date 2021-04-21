package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.ICogHandler;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;

@ObjectHolder(Crossroads.MODID)
public class LargeGearSlaveTileEntity extends TileEntity implements IInfoTE{

	@ObjectHolder("large_gear_slave")
	private static TileEntityType<LargeGearSlaveTileEntity> type = null;

	public LargeGearSlaveTileEntity(){
		super(type);
	}

	public BlockPos masterPos;//Defined relative to this block's position
	private Direction facing = null;

	protected Direction getFacing(){
		if(facing == null){
			BlockState state = level.getBlockState(worldPosition);
			if(state.getBlock() != CRBlocks.largeGearSlave){
				setRemoved();
				return Direction.NORTH;
			}
			facing = state.getValue(ESProperties.FACING);
		}

		return facing;
	}

	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		IAxleHandler axle = handler.getAxle();
		if(axle == null){
			return;
		}

		RotaryUtil.addRotaryInfo(chat, axle, false);
	}

	public void setInitial(BlockPos masPos){
		masterPos = masPos;
	}

	public void passBreak(Direction side, boolean drop){
		if(masterPos != null){
			TileEntity te = level.getBlockEntity(worldPosition.offset(masterPos));
			if(te instanceof LargeGearMasterTileEntity){
				((LargeGearMasterTileEntity) te).breakGroup(side, drop);
			}
		}
	}

	private boolean isEdge(){
		return masterPos != null && masterPos.distManhattan(BlockPos.ZERO) == 1;
	}

	@Override
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
		if(masterPos != null){
			nbt.putLong("mast", masterPos.asLong());
		}
		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt){
		super.load(state, nbt);
		this.masterPos = BlockPos.of(nbt.getLong("mast"));
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt){
		super.save(nbt);
		if(masterPos != null){
			nbt.putLong("mast", masterPos.asLong());
		}
		return nbt;
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		cogOpt.invalidate();
	}

	private final ICogHandler handler = new CogHandler();
	private final LazyOptional<ICogHandler> cogOpt = LazyOptional.of(() -> handler);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == Capabilities.COG_CAPABILITY && isEdge() && getFacing() == facing){
			return (LazyOptional<T>) cogOpt;
		}else{
			return super.getCapability(capability, facing);
		}
	}

	private class CogHandler implements ICogHandler{

		@Override
		public void connect(IAxisHandler masterIn, byte key, double rotationRatioIn, double lastRadius, Direction cogOrient, boolean renderOffset){
			if(cogOrient == Direction.getNearest(-masterPos.getX(), -masterPos.getY(), -masterPos.getZ())){
				IAxleHandler axle = getAxle();
				if(axle != null){
					axle.propagate(masterIn, key, rotationRatioIn, lastRadius, !renderOffset);
				}
			}
		}

		@Override
		public IAxleHandler getAxle(){
			TileEntity te = level.getBlockEntity(worldPosition.offset(masterPos));
			if(te instanceof LargeGearMasterTileEntity){
				LazyOptional<IAxleHandler> axleOpt = te.getCapability(Capabilities.AXLE_CAPABILITY, getFacing());
				return axleOpt.isPresent() ? axleOpt.orElseThrow(NullPointerException::new) : null;
			}
			return null;
		}
	}
}
