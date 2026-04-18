package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;

public class RedsAlchemicalTubeTileEntity extends AlchemicalTubeTileEntity{

	public static final BlockEntityType<RedsAlchemicalTubeTileEntity> TYPE = CRTileEntity.createType(RedsAlchemicalTubeTileEntity::new, CRBlocks.redsAlchemicalTubeGlass, CRBlocks.redsAlchemicalTubeCrystal);

	private boolean isInverted;

	public RedsAlchemicalTubeTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	public RedsAlchemicalTubeTileEntity(BlockPos pos, BlockState state, boolean glass){
		super(TYPE, pos, state, glass);
	}

	private boolean isUnlocked(){
		return getBlockState().getValue(CRProperties.REDSTONE_BOOL);
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		super.addInfo(chat, player, hit);
		if(isInverted){
			chat.add(Component.translatable("tt.crossroads.reds_alch_tube.redstone.inverted"));
		}else{
			chat.add(Component.translatable("tt.crossroads.reds_alch_tube.redstone.normal"));
		}
	}

	public boolean isInverted(){
		return isInverted;
	}

	public void setInverted(boolean inverted){
		isInverted = inverted;
		setChanged();
	}

	@Override
	protected void performTransfer(boolean ignorePhase){
		if(isUnlocked()){
			super.performTransfer(ignorePhase);
		}
	}

	@Override
	protected boolean allowConnect(Direction side){
		return isUnlocked() && super.allowConnect(side);
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		nbt.putBoolean("inverted", isInverted);
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		isInverted = nbt.getBoolean("inverted");
	}
}
