package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.CircuitUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.SequenceBoxContainer;
import com.Da_Technomancer.essentials.blocks.redstone.IRedstoneHandler;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import com.Da_Technomancer.essentials.packets.INBTReceiver;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

@ObjectHolder(Crossroads.MODID)
public class SequenceBoxTileEntity extends BlockEntity implements INBTReceiver, MenuProvider{

	public static final int MAX_VALUES = 99;

	@ObjectHolder("sequence_box")
	public static BlockEntityType<SequenceBoxTileEntity> TYPE = null;

	private final ArrayList<Float> sequenceVal = new ArrayList<>();
	private final ArrayList<String> sequenceStr = new ArrayList<>();
	private int index = 0;
	private boolean hadRedstoneSignal = false;//Whether this block is currently receiving a redstone signal- used for differenting pulses

	public SequenceBoxTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
		sanitizeState();
	}

	private void sanitizeState(){
		//These 2 while loops should never actually trigger (in theory)
		while(sequenceStr.size() > MAX_VALUES || sequenceStr.size() > sequenceVal.size()){
			sequenceStr.remove(sequenceStr.size() - 1);
		}
		while(sequenceStr.size() < sequenceVal.size()){
			sequenceVal.remove(sequenceVal.size() - 1);
		}
		//From this point, sequenceStr.size() == sequenceVal.size()

		if(sequenceVal.size() == 0){
			sequenceVal.add(0F);
			sequenceStr.add("0");
		}

		if(index >= sequenceVal.size()){
			index = 0;
		}
	}

	public void worldUpdate(Block blockIn){
		CircuitUtil.updateFromWorld(circHandler, blockIn);
		boolean hasRedstone = level.hasNeighborSignal(worldPosition);
		if(hasRedstone != hadRedstoneSignal){
			hadRedstoneSignal = hasRedstone;
			setChanged();
			if(hasRedstone){
				//advance the sequence
				index++;
				index %= sequenceVal.size();
				circHandler.notifyOutputChange();
			}
		}
	}

	@Override
	public CompoundTag save(CompoundTag nbt){
		super.save(nbt);
		nbt.putBoolean("redstone", hadRedstoneSignal);
		nbt.putInt("index", index);
		for(int i = 0; i < sequenceVal.size(); i++){
			nbt.putFloat(i + "_val", sequenceVal.get(i));
			nbt.putString(i + "_str", sequenceStr.get(i));
		}
		return nbt;
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		hadRedstoneSignal = nbt.getBoolean("redstone");
		index = nbt.getInt("index");
		sequenceVal.clear();
		sequenceStr.clear();
		int i = 0;
		while(nbt.contains(i + "_val")){
			sequenceVal.add(nbt.getFloat(i + "_val"));
			sequenceStr.add(nbt.getString(i + "_str"));
			i++;
		}
		sanitizeState();
	}

	@Override
	public CompoundTag getUpdateTag(){
		CompoundTag nbt =  super.getUpdateTag();
		nbt.putInt("index", index);
		for(int i = 0; i < sequenceVal.size(); i++){
			nbt.putFloat(i + "_val", sequenceVal.get(i));
			nbt.putString(i + "_str", sequenceStr.get(i));
		}
		return nbt;
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		circOpt.invalidate();
	}

	public final CircuitUtil.OutputCircHandler circHandler = new CircuitUtil.OutputCircHandler();
	private final LazyOptional<IRedstoneHandler> circOpt = CircuitUtil.makeBaseCircuitOptional(this, circHandler, () -> index < sequenceVal.size() ? sequenceVal.get(index) : 0F);

	@Nonnull
	@Override
	@SuppressWarnings("unchecked")
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side){
		if(cap == RedstoneUtil.REDSTONE_CAPABILITY){
			return (LazyOptional<T>) circOpt;
		}
		return super.getCapability(cap, side);
	}

	//UI stuff below

	public void encodeBuf(FriendlyByteBuf buf){
		buf.writeBlockPos(worldPosition);
		buf.writeVarInt(index);
		buf.writeVarInt(sequenceStr.size());
		for(String input : sequenceStr){
			buf.writeUtf(input);
		}
	}

	@Override
	public Component getDisplayName(){
		return new TranslatableComponent("container.sequence_box");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player){
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		encodeBuf(buf);
		return new SequenceBoxContainer(id, playerInv, buf);
	}

	@Override
	public void receiveNBT(CompoundTag nbt, @Nullable ServerPlayer sender){
		sequenceVal.clear();
		sequenceStr.clear();
		int i = 0;
		while(nbt.contains(i + "_val")){
			sequenceVal.add(nbt.getFloat(i + "_val"));
			sequenceStr.add(nbt.getString(i + "_str"));
			i++;
		}
		index = nbt.getInt("output_index");
		sanitizeState();
		circHandler.notifyOutputChange();
	}
}
