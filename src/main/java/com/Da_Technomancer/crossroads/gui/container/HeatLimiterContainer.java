package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class HeatLimiterContainer extends AbstractContainerMenu{

	public float output;
	public String conf;
	public BlockPos pos;

	@ObjectHolder("heat_limiter")
	private static MenuType<HeatLimiterContainer> TYPE = null;

	public HeatLimiterContainer(int id, Inventory playerInventory, FriendlyByteBuf data){
		this(id, playerInventory, data == null ? 0 : data.readFloat(), data == null ? null : data.readUtf(), data == null ? null : data.readBlockPos());
	}

	public HeatLimiterContainer(int id, Inventory playerInventory, float output, String settingStr, BlockPos pos){
		super(TYPE, id);
		this.output = output;
		this.conf = settingStr;
		this.pos = pos;
	}

	@Override
	public boolean stillValid(Player playerIn){
		return true;
	}
}
