package com.Da_Technomancer.crossroads.blocks.heat;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.items.item_sets.HeatCableFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class RedstoneHeatCableTileEntity extends HeatCableTileEntity{

	public static final BlockEntityType<RedstoneHeatCableTileEntity> TYPE = CRTileEntity.createType(RedstoneHeatCableTileEntity::new, HeatCableFactory.REDSTONE_HEAT_CABLES.values().toArray(new HeatCable[0]));

	private boolean isInverted;

	public RedstoneHeatCableTileEntity(BlockPos pos, BlockState state){
		this(pos, state, state.getBlock() instanceof HeatCable hc ? hc.insulator : HeatInsulators.WOOL);
	}

	public RedstoneHeatCableTileEntity(BlockPos pos, BlockState state, HeatInsulators insulator){
		super(TYPE, pos, state, insulator);
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		if(isInverted){
			chat.add(Component.translatable("tt.crossroads.redstone_heat_cable.redstone.inverted"));
		}else{
			chat.add(Component.translatable("tt.crossroads.redstone_heat_cable.redstone.normal"));
		}
		super.addInfo(chat, player, hit);
	}

	private boolean isUnlocked(){
		return getBlockState().getValue(CRProperties.REDSTONE_BOOL);
	}

	@Override
	public void serverTick(){
		if(isUnlocked()){
			super.serverTick();
		}else{
			//Energy loss
			double prevTemp = temp;
			temp = runLoss();

			if(temp != prevTemp){
				setChanged();
			}

			if(temp > insulator.getLimit()){
				if(CRConfig.heatEffects.get()){
					insulator.getEffect().doEffect(level, worldPosition);
				}else{
					level.setBlock(worldPosition, Blocks.FIRE.defaultBlockState(), 3);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == Capabilities.HEAT_CAPABILITY){
			if((facing == null || !locked(facing.get3DDataValue())) && isUnlocked()){
				return (LazyOptional<T>) heatOpt;
			}else{
				return LazyOptional.empty();
			}
		}
		return super.getCapability(capability, facing);
	}

	public float getTemp(){
		return (float) temp;
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
