package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.ObjectHolder;

import java.util.ArrayList;

@ObjectHolder(Crossroads.MODID)
public class SequenceBoxContainer extends Container{

	@ObjectHolder("sequence_box")
	private static ContainerType<SequenceBoxContainer> type = null;

	public final BlockPos pos;
	public final ArrayList<String> inputs;
	public int index;

	public SequenceBoxContainer(int id, PlayerInventory playerInventory, PacketBuffer data){
		super(type, id);
		if(data == null){
			pos = null;
			index = 0;
			inputs = new ArrayList<>(0);
			inputs.add("0");
		}else{
			pos = data.readBlockPos();
			index = data.readVarInt();
			int size = data.readVarInt();
			inputs = new ArrayList<>(size);
			for(int i = 0; i < size; i++){
				inputs.add(data.readString(Short.MAX_VALUE));
			}
		}
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn){
		return pos != null && pos.distanceSq(playerIn.getPositionVec(), true) <= 64;
	}
}
