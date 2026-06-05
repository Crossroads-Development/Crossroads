package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.api.CRCapabilities;
import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.rotary.*;
import com.Da_Technomancer.crossroads.api.templates.IInfoTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;


import javax.annotation.Nullable;
import java.util.ArrayList;

public class LargeGearSlaveTileEntity extends BlockEntity implements IInfoTE, ICogCapable{

	public static final BlockEntityType<LargeGearSlaveTileEntity> TYPE = CRTileEntity.createType(LargeGearSlaveTileEntity::new, CRBlocks.largeGearSlave);

	public LargeGearSlaveTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	public BlockPos masterPos;//Defined relative to this block's position
	private Direction facing = null;

	private final ICogHandler cogHandler = new CogHandler();

	protected Direction getFacing(){
		if(facing == null){
			BlockState state = getBlockState();
			if(state.getBlock() != CRBlocks.largeGearSlave){
				setRemoved();
				return Direction.NORTH;
			}
			facing = state.getValue(CRProperties.FACING);
		}

		return facing;
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		IAxleHandler axle = cogHandler.getAxle();
		if(axle == null){
			return;
		}

		RotaryUtil.addRotaryInfo(chat, axle, false, player);
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
	public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries){
		CompoundTag nbt = super.getUpdateTag(pRegistries);
		if(masterPos != null){
			nbt.putLong("mast", masterPos.asLong());
		}
		return nbt;
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		this.masterPos = BlockPos.of(nbt.getLong("mast"));
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		if(masterPos != null){
			nbt.putLong("mast", masterPos.asLong());
		}
	}

	@Override
	@Nullable
	public ICogHandler getCogHandler(Direction dir){
		if(isEdge() && getFacing() == dir){
			return cogHandler;
		}
		return null;
	}

	@Override
	public void setBlockState(BlockState pBlockState){
		super.setBlockState(pBlockState);
		//This is not, strictly speaking, optimized
		//Pre MC1.21, default behavior for all TEs was that changing blockstate invalidated capability caches
		//Post MC1.21, this is no longer the case, which opens up some opportunities for optimization
		//But everything was written with the assumption of invalidation on state change,
		//So anything other than re-implementing the old default is going to introduce a lot of new bugs
		level.invalidateCapabilities(worldPosition);
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
			if(masterPos == null || level == null){
				return null;
			}
			BlockEntity te = level.getBlockEntity(worldPosition.offset(masterPos));
			if(te instanceof LargeGearMasterTileEntity){
				return level.getCapability(CRCapabilities.AXLE_CAPABILITY, te.getBlockPos(), te.getBlockState(), te, getFacing());

			}
			return null;
		}
	}
}
