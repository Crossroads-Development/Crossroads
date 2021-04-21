package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import java.util.ArrayList;

//We can't use ModuleTE because this has 2 internal temperatures
@ObjectHolder(Crossroads.MODID)
public class MaxwellDemonTileEntity extends TileEntity implements ITickableTileEntity, IInfoTE{

	@ObjectHolder("maxwell_demon")
	private static TileEntityType<MaxwellDemonTileEntity> type = null;

	public static final double MAX_TEMP = 2500;
	public static final double MIN_TEMP = -250;
	public static final double RATE = 5;

	private double tempUp = 0;
	private double tempDown = 0;
	private boolean init = false;

	public MaxwellDemonTileEntity(){
		super(type);
	}

	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		chat.add(new TranslationTextComponent("tt.crossroads.maxwell_demon.read_top", CRConfig.formatVal(tempUp)));
		chat.add(new TranslationTextComponent("tt.crossroads.maxwell_demon.read_bottom", CRConfig.formatVal(tempDown)));
		chat.add(new TranslationTextComponent("tt.crossroads.maxwell_demon.read_biome", CRConfig.formatVal(HeatUtil.convertBiomeTemp(level, worldPosition))));
	}

	private void init(){
		if(!init){
			tempUp = HeatUtil.convertBiomeTemp(level, worldPosition);
			tempDown = tempUp;
			init = true;
		}
	}

	@Override
	public void tick(){
		if(level.isClientSide){
			return;
		}

		init();

		if(tempUp < MAX_TEMP){
			tempUp = Math.min(MAX_TEMP, tempUp + RATE);
			setChanged();
		}
		if(tempDown > MIN_TEMP){
			tempDown = Math.max(MIN_TEMP, tempDown - RATE);
			setChanged();
		}

		for(int i = 0; i < 2; i++){
			Direction dir = Direction.from3DDataValue(i);

			TileEntity te = level.getBlockEntity(worldPosition.relative(dir));
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
	public CompoundNBT save(CompoundNBT nbt){
		super.save(nbt);
		nbt.putBoolean("init_heat", init);
		nbt.putDouble("temp_u", tempUp);
		nbt.putDouble("temp_d", tempDown);
		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt){
		super.load(state, nbt);
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
