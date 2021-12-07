package com.Da_Technomancer.crossroads.tileentities.beams;

import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.beams.BeamUnitStorage;
import com.Da_Technomancer.crossroads.API.templates.BeamRenderTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.ObjectHolder;

import java.util.ArrayList;

@ObjectHolder(Crossroads.MODID)
public class QuartzStabilizerTileEntity extends BeamRenderTE implements IInfoTE{

	@ObjectHolder("quartz_stabilizer")
	public static BlockEntityType<QuartzStabilizerTileEntity> TYPE = null;

	private static final int CAPACITY = 1024;
	private static final int[] RATES = new int[] {1, 2, 4, 8, 16, 32, 64};

	private int setting = 0;
	private BeamUnitStorage storage = new BeamUnitStorage();

	public QuartzStabilizerTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	private Direction getDir(){
		BlockState state = getBlockState();
		if(state.getBlock() != CRBlocks.quartzStabilizer){
			return Direction.NORTH;
		}
		return state.getValue(ESProperties.FACING);
	}

	public int adjustSetting(){
		setting += 1;
		setting %= RATES.length;
		setChanged();
		return RATES[setting];
	}

	public int getRedstone(){
		return storage.getPower();
	}

	@Override
	public CompoundTag m_6945_(CompoundTag nbt){
		super.m_6945_(nbt);
		nbt.putInt("setting", setting);
		storage.writeToNBT("stab_mag", nbt);
		return nbt;
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		setting = nbt.getInt("setting");
		storage = BeamUnitStorage.readFromNBT("stab_mag", nbt);
	}

	@Override
	protected void doEmit(BeamUnit toEmit){
		if(toEmit != null){
			storage.addBeam(toEmit);

			//Prevent overfilling by removing excess beams
			if(storage.getPower() > CAPACITY){
				storage.subtractBeam(storage.getOutput().mult(1D - (double) CAPACITY / (double) storage.getPower(), false));
			}
		}

		Direction dir = getDir();

		if(!storage.isEmpty()){
			int toFill = RATES[setting];
			BeamUnit toDraw = new BeamUnit(MiscUtil.withdrawExact(storage.getOutput().getValues(), toFill));
			storage.subtractBeam(toDraw);

			if(beamer[dir.get3DDataValue()].emit(toDraw, level)){
				refreshBeam(dir.get3DDataValue());
			}
		}else if(beamer[dir.get3DDataValue()].emit(BeamUnit.EMPTY, level)){
			refreshBeam(dir.get3DDataValue());
		}
	}

	@Override
	protected boolean[] inputSides(){
		boolean[] out = {true, true, true, true, true, true};
		out[getDir().get3DDataValue()] = false;
		return out;
	}

	@Override
	protected boolean[] outputSides(){
		boolean[] out = new boolean[6];
		out[getDir().get3DDataValue()] = true;
		return out;
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		super.addInfo(chat, player, hit);
		chat.add(new TranslatableComponent("tt.crossroads.quartz_stabilizer.output", RATES[setting]));
		chat.add(new TranslatableComponent("tt.crossroads.quartz_stabilizer.storage", storage.getOutput().toString()));
	}
}
