package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.HeatLimiterContainer;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.Da_Technomancer.essentials.packets.INBTReceiver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;

@ObjectHolder(Crossroads.MODID)
public class HeatLimiterBasicTileEntity extends BlockEntity implements TickableBlockEntity, IInfoTE, MenuProvider, INBTReceiver{

	@ObjectHolder("heat_limiter_basic")
	private static BlockEntityType<HeatLimiterBasicTileEntity> type = null;

	private double heatIn = 0;
	private double heatOut = 0;
	private boolean init = false;

	public float setting = 0;
	public String expression = "0";

	public HeatLimiterBasicTileEntity(){
		super(type);
	}

	protected HeatLimiterBasicTileEntity(BlockEntityType<? extends HeatLimiterBasicTileEntity> type){
		super(type);
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		init();
		HeatUtil.addHeatInfo(chat, heatIn, Short.MIN_VALUE);//Add the first temp without biome temp to prevent double printing
		HeatUtil.addHeatInfo(chat, heatOut, HeatUtil.convertBiomeTemp(level, worldPosition));
	}

	protected double getSetting(){
		return setting;
	}

	@Override
	public void tick(){
		if(level.isClientSide){
			return;
		}

		if(!init){
			init();
		}

		double goalTemp = getSetting();
		boolean blueMode = getBlockState().getValue(CRProperties.ACTIVE);

		if(blueMode){
			//Trick to re-use the same logic; reverted at the end
			heatIn = -heatIn;
			heatOut = -heatOut;
			goalTemp = -goalTemp;
		}

		if(heatOut < goalTemp){
			double toTrans;
			if(heatIn > goalTemp){
				toTrans = goalTemp - heatOut;
				toTrans = Math.min(toTrans, heatIn - goalTemp);
			}else{
				toTrans = heatIn - heatOut;
				toTrans /= 2D;
				toTrans = Math.max(0, toTrans);
			}
			heatOut += toTrans;
			heatIn -= toTrans;
			setChanged();
		}

		if(blueMode){
			//Trick to re-use the same logic. Started above
			heatIn = -heatIn;
			heatOut = -heatOut;
		}
	}

	private void init(){
		if(!init){
			init = true;
			heatIn = HeatUtil.convertBiomeTemp(level, worldPosition);
			heatOut = HeatUtil.convertBiomeTemp(level, worldPosition);
			setChanged();
		}
	}

	@Override
	public CompoundTag save(CompoundTag nbt){
		super.save(nbt);
		nbt.putBoolean("init_heat", init);
		nbt.putDouble("heat_in", heatIn);
		nbt.putDouble("heat_out", heatOut);
		nbt.putFloat("setting", setting);
		nbt.putString("expression", expression);
		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundTag nbt){
		super.load(state, nbt);
		init = nbt.getBoolean("init_heat");
		heatIn = nbt.getDouble("heat_in");
		heatOut = nbt.getDouble("heat_out");
		setting = nbt.getFloat("setting");
		expression = nbt.getString("expression");
	}

	@Override
	public void clearCache(){
		super.clearCache();
		heatInOpt.invalidate();
		heatOutOpt.invalidate();
		heatInOpt = LazyOptional.of(() -> new HeatHandler(true));
		heatOutOpt = LazyOptional.of(() -> new HeatHandler(false));
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		heatInOpt.invalidate();
		heatOutOpt.invalidate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		Direction facing = getBlockState().getValue(ESProperties.FACING);
		if(cap == Capabilities.HEAT_CAPABILITY){
			if(side == null || side == facing.getOpposite()){
				return (LazyOptional<T>) heatInOpt;
			}else if(side == facing){
				return (LazyOptional<T>) heatOutOpt;
			}
		}
		return super.getCapability(cap, side);
	}

	private LazyOptional<IHeatHandler> heatInOpt = LazyOptional.of(() -> new HeatHandler(true));
	private LazyOptional<IHeatHandler> heatOutOpt = LazyOptional.of(() -> new HeatHandler(false));

	@Override
	public Component getDisplayName(){
		return new TranslatableComponent("container.heat_limiter");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player){
		return new HeatLimiterContainer(id, playerInv, setting, expression, worldPosition);
	}

	@Override
	public void receiveNBT(CompoundTag nbt, @Nullable ServerPlayer player){
		if(nbt.contains("value")){
			setting = nbt.getFloat("value");
			expression = nbt.getString("config");
			setChanged();
		}
	}

	private class HeatHandler implements IHeatHandler{

		private final boolean in;

		private HeatHandler(boolean in){
			this.in = in;
		}

		@Override
		public double getTemp(){
			init();
			return in ? heatIn : heatOut;
		}

		@Override
		public void setTemp(double tempIn){
			init = true;
			if(in){
				heatIn = Math.max(HeatUtil.ABSOLUTE_ZERO, tempIn);
			}else{
				heatOut = Math.max(HeatUtil.ABSOLUTE_ZERO, tempIn);
			}
			setChanged();
		}

		@Override
		public void addHeat(double heatChange){
			init();
			if(in){
				heatIn = Math.max(HeatUtil.ABSOLUTE_ZERO, heatIn + heatChange);
			}else{
				heatOut = Math.max(HeatUtil.ABSOLUTE_ZERO, heatOut + heatChange);
			}
			setChanged();
		}
	}
}