package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.beams.BeamUnit;
import com.Da_Technomancer.crossroads.api.beams.BeamUnitStorage;
import com.Da_Technomancer.crossroads.api.templates.BeamRenderTE;
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

public class QuartzStabilizerTileEntity extends BeamRenderTE implements IInfoTE{

	public static final BlockEntityType<QuartzStabilizerTileEntity> TYPE = CRTileEntity.createType(QuartzStabilizerTileEntity::new, CRBlocks.quartzStabilizer);

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
		return state.getValue(CRProperties.FACING);
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
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		nbt.putInt("setting", setting);
		storage.writeToNBT("stab_mag", nbt);
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

			if(getBeamHelpers()[dir.get3DDataValue()].emit(toDraw, level)){
				refreshBeam(dir.get3DDataValue());
			}
		}else if(getBeamHelpers()[dir.get3DDataValue()].emit(BeamUnit.EMPTY, level)){
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
		chat.add(Component.translatable("tt.crossroads.quartz_stabilizer.output", RATES[setting]));
		chat.add(Component.translatable("tt.crossroads.quartz_stabilizer.storage", storage.getOutput().toString()));
	}
}
