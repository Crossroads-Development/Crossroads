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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;

@ObjectHolder(Crossroads.MODID)
public class HeatLimiterBasicTileEntity extends TileEntity implements ITickableTileEntity, IInfoTE, INamedContainerProvider, INBTReceiver{

	@ObjectHolder("heat_limiter_basic")
	private static TileEntityType<HeatLimiterBasicTileEntity> type = null;

	private double heatIn = 0;
	private double heatOut = 0;
	private boolean init = false;

	public float setting = 0;
	public String expression = "0";

	public HeatLimiterBasicTileEntity(){
		super(type);
	}

	protected HeatLimiterBasicTileEntity(TileEntityType<? extends HeatLimiterBasicTileEntity> type){
		super(type);
	}

	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		init();
		HeatUtil.addHeatInfo(chat, heatIn, Short.MIN_VALUE);//Add the first temp without biome temp to prevent double printing
		HeatUtil.addHeatInfo(chat, heatOut, HeatUtil.convertBiomeTemp(world, pos));
	}

	protected double getSetting(){
		return setting;
	}

	@Override
	public void tick(){
		if(world.isRemote){
			return;
		}

		if(!init){
			init();
		}

		double goalTemp = HeatUtil.toCelcius(getSetting());
		boolean blueMode = world.getBlockState(pos).get(CRProperties.ACTIVE);

		if(blueMode){
			heatIn = -heatIn;
			heatOut = -heatOut;
			goalTemp = -goalTemp;
		}

		if(heatOut > goalTemp){
			if(heatIn < goalTemp){
				double toTrans = goalTemp - heatOut;
				toTrans = Math.max(toTrans, heatIn - goalTemp);
				heatOut += toTrans;
				heatIn -= toTrans;
				markDirty();
			}else{
				double toTrans = heatIn - heatOut;
				toTrans /= 2D;
				toTrans = Math.min(0, toTrans);
				heatOut += toTrans;
				heatIn -= toTrans;
				markDirty();
			}
		}else if(heatIn > goalTemp){
			double toTrans = goalTemp - heatOut;
			toTrans = Math.min(toTrans, heatIn - goalTemp);
			heatOut += toTrans;
			heatIn -= toTrans;
			markDirty();
		}else{
			double toTrans = heatIn - heatOut;
			toTrans /= 2D;
			toTrans = Math.max(0, toTrans);
			heatOut += toTrans;
			heatIn -= toTrans;
			markDirty();
		}

		if(blueMode){
			heatIn = -heatIn;
			heatOut = -heatOut;
		}
	}

	private void init(){
		if(!init){
			init = true;
			heatIn = HeatUtil.convertBiomeTemp(world, pos);
			heatOut = HeatUtil.convertBiomeTemp(world, pos);
			markDirty();
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putBoolean("init_heat", init);
		nbt.putDouble("heat_in", heatIn);
		nbt.putDouble("heat_out", heatOut);
		nbt.putFloat("setting", setting);
		nbt.putString("expression", expression);
		return nbt;
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		init = nbt.getBoolean("init_heat");
		heatIn = nbt.getDouble("heat_in");
		heatOut = nbt.getDouble("heat_out");
		setting = nbt.getFloat("setting");
		expression = nbt.getString("expression");
	}

	@Override
	public void remove(){
		super.remove();
		heatInOpt.invalidate();
		heatOutOpt.invalidate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		Direction facing = world.getBlockState(pos).get(ESProperties.FACING);
		if(cap == Capabilities.HEAT_CAPABILITY){
			if(side == null || side == facing.getOpposite()){
				return (LazyOptional<T>) heatInOpt;
			}else if(side == facing){
				return (LazyOptional<T>) heatOutOpt;
			}
		}
		return super.getCapability(cap, side);
	}

	private final LazyOptional<IHeatHandler> heatInOpt = LazyOptional.of(() -> new HeatHandler(true));
	private final LazyOptional<IHeatHandler> heatOutOpt = LazyOptional.of(() -> new HeatHandler(false));

	@Override
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.heat_limiter");
	}

	@Nullable
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player){
		return new HeatLimiterContainer(id, playerInv, setting, expression, pos);
	}

	@Override
	public void receiveNBT(CompoundNBT nbt, @Nullable ServerPlayerEntity player){
		if(nbt.contains("value")){
			setting = nbt.getFloat("value");
			expression = nbt.getString("config");
			markDirty();
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
				heatIn = tempIn;
			}else{
				heatOut = tempIn;
			}
			markDirty();
		}

		@Override
		public void addHeat(double heatChange){
			init();
			if(in){
				heatIn += heatChange;
			}else{
				heatOut += heatChange;
			}
			markDirty();
		}
	}
}