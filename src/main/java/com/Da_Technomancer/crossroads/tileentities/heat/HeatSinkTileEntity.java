package com.Da_Technomancer.crossroads.tileentities.heat;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import com.Da_Technomancer.crossroads.Crossroads;
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

import java.util.ArrayList;

@ObjectHolder(Crossroads.MODID)
public class HeatSinkTileEntity extends ModuleTE{

	@ObjectHolder("heat_sink")
	private static TileEntityType<HeatSinkTileEntity> type = null;

	public static final int[] MODES = {5, 10, 15, 20, 25};
	private int mode = 0;

	public HeatSinkTileEntity(){
		super(type);
	}

	public int cycleMode(){
		mode = (mode + 1) % MODES.length;
		setChanged();
		return mode;
	}

	@Override
	protected boolean useHeat(){
		return true;
	}

	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		chat.add(new TranslationTextComponent("tt.crossroads.heat_sink.loss",  MODES[mode]));
		super.addInfo(chat, player, hit);
	}

	@Override
	public void tick(){
		super.tick();

		if(level.isClientSide){
			return;
		}

		double prevTemp = temp;
		double biomeTemp = HeatUtil.convertBiomeTemp(level, worldPosition);
		temp += Math.min(MODES[mode], Math.abs(temp - biomeTemp)) * Math.signum(biomeTemp - temp);
		if(temp != prevTemp){
			setChanged();
		}
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt){
		super.load(state, nbt);
		mode = nbt.getInt("mode");
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt){
		super.save(nbt);
		nbt.putInt("mode", mode);
		return nbt;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing){
		if(capability == Capabilities.HEAT_CAPABILITY){
			return (LazyOptional<T>) heatOpt;
		}

		return super.getCapability(capability, facing);
	}
}
