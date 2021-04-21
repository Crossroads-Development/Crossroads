package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class HeatLimiterContainer extends Container{

	public float output;
	public String conf;
	public BlockPos pos;

	@ObjectHolder("heat_limiter")
	private static ContainerType<HeatLimiterContainer> TYPE = null;

	public HeatLimiterContainer(int id, PlayerInventory playerInventory, PacketBuffer data){
		this(id, playerInventory, data == null ? 0 : data.readFloat(), data == null ? null : data.readUtf(), data == null ? null : data.readBlockPos());
	}

	public HeatLimiterContainer(int id, PlayerInventory playerInventory, float output, String settingStr, BlockPos pos){
		super(TYPE, id);
		this.output = output;
		this.conf = settingStr;
		this.pos = pos;
	}

	@Override
	public boolean stillValid(PlayerEntity playerIn){
		return true;
	}
}
