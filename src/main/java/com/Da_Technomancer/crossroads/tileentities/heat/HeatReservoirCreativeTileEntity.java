package com.Da_Technomancer.crossroads.tileentities.heat;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.HeatReservoirCreativeContainer;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;

@ObjectHolder(Crossroads.MODID)
public class HeatReservoirCreativeTileEntity extends ModuleTE implements INBTReceiver, MenuProvider{

	@ObjectHolder("heat_reservoir_creative")
	public static BlockEntityType<HeatReservoirCreativeTileEntity> TYPE = null;

	public float setting = 0;
	public String expression = "0";

	public HeatReservoirCreativeTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	@Override
	protected boolean useHeat(){
		return true;
	}

	@Override
	protected HeatHandler createHeatHandler(){
		return new FixedTempHeatHandler();
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		nbt.putFloat("setting", setting);
		nbt.putString("expression", expression);
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		setting = nbt.getFloat("setting");
		expression = nbt.getString("expression");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player){
		return new HeatReservoirCreativeContainer(id, playerInv, setting, expression, worldPosition);
	}

	@Override
	public Component getDisplayName(){
		return new TranslatableComponent("container.crossroads.heat_reservoir_creative");
	}

	@Override
	public void receiveNBT(CompoundTag nbt, @Nullable ServerPlayer player){
		if(nbt.contains("value")){
			setting = nbt.getFloat("value");
			expression = nbt.getString("config");
			temp = setting;
			setChanged();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing){
		if(capability == Capabilities.HEAT_CAPABILITY){
			return (LazyOptional<T>) heatOpt;
		}
		return super.getCapability(capability, facing);
	}

	private class FixedTempHeatHandler extends HeatHandler{

		public void init(){
			if(!initHeat){
				temp = setting;
				initHeat = true;
				setChanged();
			}
		}

		@Override
		public void setTemp(double tempIn){
			init();
			//No-op
		}

		@Override
		public void addHeat(double heat){
			//No-op
		}
	}
}
