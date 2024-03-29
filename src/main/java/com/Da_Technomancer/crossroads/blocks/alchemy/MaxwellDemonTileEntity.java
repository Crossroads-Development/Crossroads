package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.heat.HeatUtil;
import com.Da_Technomancer.crossroads.api.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.api.templates.IInfoTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
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

import java.util.ArrayList;

//We can't use ModuleTE because this has 2 internal temperatures
public class MaxwellDemonTileEntity extends BlockEntity implements ITickableTileEntity, IInfoTE{

	public static final BlockEntityType<MaxwellDemonTileEntity> TYPE = CRTileEntity.createType(MaxwellDemonTileEntity::new, CRBlocks.maxwellDemon);

	public static final double MAX_TEMP = 2500;
	public static final double MIN_TEMP = -200;

	private double tempUp = 0;
	private double tempDown = 0;
	private boolean init = false;
	private double rate = -1;//Not saved/loaded to NBT, as we want this to regenerate on reload with the config

	public MaxwellDemonTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
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

			BlockEntity te = level.getBlockEntity(worldPosition.relative(dir));
			LazyOptional<IHeatHandler> heatOpt;
			if(te != null && (heatOpt = te.getCapability(Capabilities.HEAT_CAPABILITY, dir.getOpposite())).isPresent()){
				double reservePool = i == 0 ? tempDown : tempUp;
				if(i == 0){
					tempDown -= reservePool;
				}else{
					tempUp -= reservePool;
				}

				IHeatHandler handler = heatOpt.orElseThrow(NullPointerException::new);
				reservePool += handler.getTemp();
				handler.addHeat(-handler.getTemp());
				reservePool /= 2;
				if(i == 0){
					tempDown += reservePool;
				}else{
					tempUp += reservePool;
				}
				handler.addHeat(reservePool);
			}
		}
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		nbt.putBoolean("init_heat", init);
		nbt.putDouble("temp_u", tempUp);
		nbt.putDouble("temp_d", tempDown);
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		init = nbt.getBoolean("init_heat");
		tempUp = nbt.getDouble("temp_u");
		tempDown = nbt.getDouble("temp_d");
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		heatOptUp.invalidate();
		heatOptDown.invalidate();
	}

	private final LazyOptional<IHeatHandler> heatOptUp = LazyOptional.of(() -> new HeatHandler(true));
	private final LazyOptional<IHeatHandler> heatOptDown = LazyOptional.of(() -> new HeatHandler(false));

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.HEAT_CAPABILITY){
			if(side == null || side == Direction.UP){
				return (LazyOptional<T>) heatOptUp;
			}else if(side == Direction.DOWN){
				return (LazyOptional<T>) heatOptDown;
			}
		}
		return super.getCapability(cap, side);
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
