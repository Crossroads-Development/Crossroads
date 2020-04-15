package com.Da_Technomancer.crossroads.tileentities.beams;

import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.beams.BeamUnitStorage;
import com.Da_Technomancer.crossroads.API.templates.BeamRenderTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ObjectHolder;

import java.util.ArrayList;

@ObjectHolder(Crossroads.MODID)
public class QuartzStabilizerTileEntity extends BeamRenderTE implements IInfoTE{

	@ObjectHolder("quartz_stabilizer")
	private static TileEntityType<QuartzStabilizerTileEntity> type = null;

	private static final int CAPACITY = 1024;
	private static final int[] RATES = new int[] {1, 2, 4, 8, 16, 32, 64};

	private int setting = 0;
	private BeamUnitStorage storage = new BeamUnitStorage();

	public QuartzStabilizerTileEntity(){
		super(type);
	}

	private Direction getDir(){
		BlockState state = getBlockState();
		if(state.getBlock() != CRBlocks.quartzStabilizer){
			return Direction.NORTH;
		}
		return state.get(ESProperties.FACING);
	}

	public int adjustSetting(){
		setting += 1;
		setting %= RATES.length;
		markDirty();
		return RATES[setting];
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putInt("setting", setting);
		storage.writeToNBT("stab_mag", nbt);
		return nbt;
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
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

			if(beamer[dir.getIndex()].emit(toDraw, world)){
				refreshBeam(dir.getIndex());
			}
		}else if(beamer[dir.getIndex()].emit(BeamUnit.EMPTY, world)){
			refreshBeam(dir.getIndex());
		}
	}

	@Override
	protected boolean[] inputSides(){
		boolean[] out = {true, true, true, true, true, true};
		out[getDir().getIndex()] = false;
		return out;
	}

	@Override
	protected boolean[] outputSides(){
		boolean[] out = new boolean[6];
		out[getDir().getIndex()] = true;
		return out;
	}

	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		chat.add(new TranslationTextComponent("tt.crossroads.quartz_stabilizer.output", RATES[setting]));
		chat.add(new TranslationTextComponent("tt.crossroads.quartz_stabilizer.storage", storage.getOutput().toString()));
	}
}
