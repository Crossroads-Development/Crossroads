package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.rotary.StirlingEngine;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;

@ObjectHolder(Crossroads.MODID)
public class StirlingEngineTileEntity extends ModuleTE{

	@ObjectHolder("stirling_engine")
	private static TileEntityType<StirlingEngineTileEntity> type = null;

	public static final double INERTIA = 200;
	public static final double RATE = 5;

	private double tempSide;
	private double tempBottom;

	public StirlingEngineTileEntity(){
		super(type);
	}

	@Override
	protected boolean useRotary(){
		return true;
	}

	@Override
	protected boolean useHeat(){
		//We intentionally do NOT use the ModuleTE heat template due to having two separate internal heat devices
		//This method is overriden to return the default as a reminder of that fact
		return false;
	}

	@Override
	protected double getMoInertia(){
		return INERTIA;
	}

	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		chat.add(new TranslationTextComponent("tt.crossroads.stirling_engine.side_temp", CRConfig.formatVal(tempSide)));
		chat.add(new TranslationTextComponent("tt.crossroads.stirling_engine.bottom_temp", CRConfig.formatVal(tempBottom)));
		//We have to add the biome temp manually because we don't use the ModuleTE heat template
		chat.add(new TranslationTextComponent("tt.crossroads.boilerplate.temp.biome", CRConfig.formatVal(temp)));
		super.addInfo(chat, player, hit);
	}

	private void updateWorldState(){
		//Updates the (purely for rending) blockstate in the world to match the gear speed
		BlockState worldState = getBlockState();
		final double slowMin = 0.25D;
		final double fastMin = 0.75D;
		double speedMagnitude = Math.abs(motData[0]);
		int target = 0;
		if(speedMagnitude > slowMin){
			if(speedMagnitude > fastMin){
				target = 2;
			}else{
				target = 1;
			}
			if(motData[0] < 0){
				target = -target;
			}
		}
		target += 2;//Change from [-2, 2] to [0, 4]
		if(worldState.getBlock() instanceof StirlingEngine && worldState.get(CRProperties.RATE_SIGNED) != target){
			world.setBlockState(pos, worldState.with(CRProperties.RATE_SIGNED, target), 2);//Doesn't create block updates
		}
	}

	@Override
	public void tick(){
		super.tick();

		if(world.isRemote){
			return;
		}
		init();

		int level = (int) ((tempSide - tempBottom) / 100D);

		if(level != 0){
			tempSide -= RATE * level;
			tempBottom += RATE * level;

			if(axleHandler.axis != null && Math.signum(level) * motData[0] < CRConfig.stirlingSpeedLimit.get()){
				motData[1] += CRConfig.stirlingMultiplier.get() * RATE * level * Math.abs(level);//5*stirlingMult*level^2 with sign of level
			}

			markDirty();
		}
		updateWorldState();
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt){
		super.read(state, nbt);

		tempSide = nbt.getDouble("temp_side");
		tempBottom = nbt.getDouble("temp_bottom");
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);

		nbt.putDouble("temp_side", tempSide);
		nbt.putDouble("temp_bottom", tempBottom);

		return nbt;
	}

	@Override
	public void remove(){
		super.remove();
		sideHeatOpt.invalidate();
		bottomHeatOpt.invalidate();
	}

	private final LazyOptional<IHeatHandler> sideHeatOpt = LazyOptional.of(SideHeatHandler::new);
	private final LazyOptional<IHeatHandler> bottomHeatOpt = LazyOptional.of(BottomHeatHandler::new);
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == Capabilities.AXLE_CAPABILITY && (facing == null || facing == Direction.UP)){
			return (LazyOptional<T>) axleOpt;
		}
		if(capability == Capabilities.HEAT_CAPABILITY && facing != Direction.UP){
			return facing == Direction.DOWN ? (LazyOptional<T>) bottomHeatOpt : (LazyOptional<T>) sideHeatOpt;
		}

		return super.getCapability(capability, facing);
	}

	private void init(){
		if(!initHeat){
			tempSide = HeatUtil.convertBiomeTemp(world, pos);
			tempBottom = tempSide;
			initHeat = true;
		}
	}

	private class SideHeatHandler implements IHeatHandler{

		@Override
		public double getTemp(){
			init();
			return tempSide;
		}

		@Override
		public void setTemp(double tempIn){
			init();
			tempSide = tempIn;
			markDirty();
		}

		@Override
		public void addHeat(double heat){
			init();
			tempSide += heat;
			markDirty();
		}
	}

	private class BottomHeatHandler implements IHeatHandler{

		@Override
		public double getTemp(){
			init();
			return tempBottom;
		}

		@Override
		public void setTemp(double tempIn){
			init();
			tempBottom = tempIn;
			markDirty();
		}

		@Override
		public void addHeat(double heat){
			init();
			tempBottom += heat;
			markDirty();
		}
	}
}
