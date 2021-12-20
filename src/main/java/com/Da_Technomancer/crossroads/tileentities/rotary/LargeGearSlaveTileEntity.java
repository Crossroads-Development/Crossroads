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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;

@ObjectHolder(Crossroads.MODID)
public class LargeGearSlaveTileEntity extends BlockEntity implements IInfoTE{

	@ObjectHolder("large_gear_slave")
	public static BlockEntityType<LargeGearSlaveTileEntity> TYPE = null;

	public LargeGearSlaveTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	public BlockPos masterPos;//Defined relative to this block's position
	private Direction facing = null;

	protected Direction getFacing(){
		if(facing == null){
			BlockState state = getBlockState();
			if(state.getBlock() != CRBlocks.largeGearSlave){
				setRemoved();
				return Direction.NORTH;
			}
			facing = state.getValue(ESProperties.FACING);
		}

		return facing;
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
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
			BlockEntity te = level.getBlockEntity(worldPosition.offset(masterPos));
			if(te instanceof LargeGearMasterTileEntity){
				((LargeGearMasterTileEntity) te).breakGroup(side, drop);
			}
		}
	}

	private boolean isEdge(){
		return masterPos != null && masterPos.distManhattan(BlockPos.ZERO) == 1;
	}

	@Override
	public CompoundTag getUpdateTag(){
		CompoundTag nbt = super.getUpdateTag();
		if(masterPos != null){
			nbt.putLong("mast", masterPos.asLong());
		}
		return nbt;
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		this.masterPos = BlockPos.of(nbt.getLong("mast"));
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		if(masterPos != null){
			nbt.putLong("mast", masterPos.asLong());
		}
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
			BlockEntity te = level.getBlockEntity(worldPosition.offset(masterPos));
			if(te instanceof LargeGearMasterTileEntity){
				LazyOptional<IAxleHandler> axleOpt = te.getCapability(Capabilities.AXLE_CAPABILITY, getFacing());
				return axleOpt.isPresent() ? axleOpt.orElseThrow(NullPointerException::new) : null;
			}
			return null;
		}
	}
}
