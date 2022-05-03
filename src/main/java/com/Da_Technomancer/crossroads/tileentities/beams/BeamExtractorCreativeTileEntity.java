package com.Da_Technomancer.crossroads.tileentities.beams;

import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.templates.BeamRenderTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.BeamExtractorCreativeContainer;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.Da_Technomancer.essentials.packets.INBTReceiver;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;

@ObjectHolder(Crossroads.MODID)
public class BeamExtractorCreativeTileEntity extends BeamRenderTE implements MenuProvider, INBTReceiver{

	@ObjectHolder("beam_extractor_creative")
	public static BlockEntityType<BeamExtractorCreativeTileEntity> TYPE = null;

	private Direction facing = null;

	public String[] expression = new String[] {"0", "0", "0", "0"};
	public BeamUnit output = BeamUnit.EMPTY;

	public BeamExtractorCreativeTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	private Direction getFacing(){
		if(facing == null){
			BlockState s = getBlockState();
			if(s.hasProperty(ESProperties.FACING)){
				facing = s.getValue(ESProperties.FACING);
			}else{
				return Direction.DOWN;
			}
		}

		return facing;
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		output.writeToNBT("output", nbt);
		for(int i = 0; i < 4; i++){
			nbt.putString("expression_" + i, expression[i]);
		}
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		output = BeamUnit.readFromNBT("output", nbt);
		for(int i = 0; i < 4; i++){
			expression[i] = nbt.getString("expression_" + i);
		}
	}

	@Override
	public void setBlockState(BlockState stateIn){
		super.setBlockState(stateIn);
		facing = null;
	}

	@Override
	public Component getDisplayName(){
		return new TranslatableComponent("container.crossroads.beam_extractor_creative");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player){
		return new BeamExtractorCreativeContainer(id, playerInv, output, expression, worldPosition);
	}

	@Override
	public void receiveNBT(CompoundTag nbt, @Nullable ServerPlayer player){
		if(nbt.contains("value")){
			output = BeamUnit.readFromNBT("value", nbt);
			for(int i = 0; i < 4; i++){
				expression[i] = nbt.getString("config_" + i);
			}
			setChanged();
		}
	}

	@Override
	protected void doEmit(BeamUnit toEmit){
		Direction dir = getFacing();
		if(beamer[dir.get3DDataValue()].emit(level.hasNeighborSignal(worldPosition) ? BeamUnit.EMPTY : output, level)){
			refreshBeam(dir.get3DDataValue());
		}
	}

	@Override
	protected boolean[] inputSides(){
		return new boolean[6];//All false
	}

	@Override
	protected boolean[] outputSides(){
		boolean[] out = new boolean[6];
		out[getFacing().get3DDataValue()] = true;
		return out;
	}
}
