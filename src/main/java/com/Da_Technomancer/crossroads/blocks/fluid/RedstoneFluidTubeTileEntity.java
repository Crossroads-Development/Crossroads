package com.Da_Technomancer.crossroads.blocks.fluid;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.templates.IInfoTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;

public class RedstoneFluidTubeTileEntity extends FluidTubeTileEntity implements IInfoTE{

	public static final BlockEntityType<RedstoneFluidTubeTileEntity> TYPE = CRTileEntity.createType(RedstoneFluidTubeTileEntity::new, CRBlocks.redstoneFluidTube);

	private boolean isInverted;

	public RedstoneFluidTubeTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	@Override
	public void serverTick(){
		if(getBlockState().getValue(CRProperties.REDSTONE_BOOL)){
			super.serverTick();
		}
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		if(isInverted){
			chat.add(Component.translatable("tt.crossroads.redstone_fluid_tube.redstone.inverted"));
		}else{
			chat.add(Component.translatable("tt.crossroads.redstone_fluid_tube.redstone.normal"));
		}
	}

	@Override
	protected boolean canConnect(Direction side){
		return super.canConnect(side) && getBlockState().getValue(CRProperties.REDSTONE_BOOL);
	}

	public boolean isInverted(){
		return isInverted;
	}

	public void setInverted(boolean inverted){
		isInverted = inverted;
		setChanged();
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		nbt.putBoolean("inverted", isInverted);
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		isInverted = nbt.getBoolean("inverted");
	}
}
