package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.CRCapabilities;
import com.Da_Technomancer.crossroads.api.heat.HeatUtil;
import com.Da_Technomancer.crossroads.api.heat.IHeatCapable;
import com.Da_Technomancer.crossroads.api.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.api.templates.IInfoTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;


import javax.annotation.Nullable;
import java.util.ArrayList;

//We can't use ModuleTE because this has 2 internal temperatures
public class MaxwellDemonTileEntity extends BlockEntity implements ITickableTileEntity, IInfoTE, IHeatCapable{

	public static final BlockEntityType<MaxwellDemonTileEntity> TYPE = CRTileEntity.createType(MaxwellDemonTileEntity::new, CRBlocks.maxwellDemon);

	public static final double MAX_TEMP = 2500;
	public static final double MIN_TEMP = -200;

	private double tempUp = 0;
	private double tempDown = 0;
	private boolean init = false;
	private double rate = -1;//Not saved/loaded to NBT, as we want this to regenerate on reload with the config

	public MaxwellDemonTileEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState){
		super(blockEntityType, blockPos, blockState);
	}
	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		chat.add(Component.translatable("tt.crossroads.maxwell_demon.read_top", CRConfig.formatVal(tempUp)));
		chat.add(Component.translatable("tt.crossroads.maxwell_demon.read_bottom", CRConfig.formatVal(tempDown)));
		chat.add(Component.translatable("tt.crossroads.maxwell_demon.read_biome", CRConfig.formatVal(HeatUtil.convertBiomeTemp(level, worldPosition))));
	}

	private void init(){
		if(!init){
			tempUp = HeatUtil.convertBiomeTemp(level, worldPosition);
			tempDown = tempUp;
			init = true;
		}
		if(rate < 0){
			rate = CRConfig.demonPower.get();
		}
	}

	@Override
	public void serverTick(){
		init();

		if(tempUp < MAX_TEMP){
			tempUp = Math.min(MAX_TEMP, tempUp + rate);
			setChanged();
		}
		if(tempDown > MIN_TEMP){
			tempDown = Math.max(MIN_TEMP, tempDown - rate);
			setChanged();
		}

		for(int i = 0; i < 2; i++){
			Direction dir = Direction.from3DDataValue(i);

			BlockPos relPos = worldPosition.relative(dir);
			IHeatHandler heatHandler;
			if((heatHandler = level.getCapability(CRCapabilities.HEAT_CAPABILITY, relPos, dir.getOpposite())) != null){
				double reservePool = i == 0 ? tempDown : tempUp;
				if(i == 0){
					tempDown -= reservePool;
				}else{
					tempUp -= reservePool;
				}

				reservePool += heatHandler.getTemp();
				heatHandler.addHeat(-heatHandler.getTemp());
				reservePool /= 2;
				if(i == 0){
					tempDown += reservePool;
				}else{
					tempUp += reservePool;
				}
				heatHandler.addHeat(reservePool);
			}
		}
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		nbt.putBoolean("init_heat", init);
		nbt.putDouble("temp_u", tempUp);
		nbt.putDouble("temp_d", tempDown);
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		init = nbt.getBoolean("init_heat");
		tempUp = nbt.getDouble("temp_u");
		tempDown = nbt.getDouble("temp_d");
	}

	private final IHeatHandler heatHandlerUp = new HeatHandler(true);
	private final IHeatHandler heatHandlerDown = new HeatHandler(false);

	@Override
	@Nullable
	public IHeatHandler getHeatHandler(Direction dir){
		if(dir == null || dir == Direction.UP){
			return heatHandlerUp;
		}else if(dir == Direction.DOWN){
			return heatHandlerDown;
		}
		return null;
	}


	private class HeatHandler implements IHeatHandler{

		private final boolean up;

		private HeatHandler(boolean up){
			this.up = up;
		}

		@Override
		public double getTemp(){
			init();
			return up ? tempUp : tempDown;
		}

		@Override
		public void setTemp(double tempIn){
			init = true;
			if(up){
				tempUp = tempIn;
			}else{
				tempDown = tempIn;
			}
			setChanged();
		}

		@Override
		public void addHeat(double heat){
			init();
			if(up){
				tempUp += heat;
			}else{
				tempDown += heat;
			}
			setChanged();
		}
	}
}
