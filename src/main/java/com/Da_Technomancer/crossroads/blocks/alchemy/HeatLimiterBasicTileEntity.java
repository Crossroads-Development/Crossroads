package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.CRCapabilities;
import com.Da_Technomancer.crossroads.api.heat.HeatUtil;
import com.Da_Technomancer.crossroads.api.heat.IHeatCapable;
import com.Da_Technomancer.crossroads.api.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.api.templates.IInfoTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.gui.container.HeatLimiterContainer;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import com.Da_Technomancer.essentials.api.packets.INBTReceiver;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;


import javax.annotation.Nullable;
import java.util.ArrayList;

public class HeatLimiterBasicTileEntity extends BlockEntity implements ITickableTileEntity, IInfoTE, MenuProvider, INBTReceiver, IHeatCapable{

	public static final BlockEntityType<HeatLimiterBasicTileEntity> TYPE = CRTileEntity.createType(HeatLimiterBasicTileEntity::new, CRBlocks.heatLimiterBasic);

	private double heatIn = 0;
	private double heatOut = 0;
	private boolean init = false;

	public float setting = 0;
	public String expression = "0";

	private final IHeatHandler heatHandlerIn = new HeatHandler(true);
	private final IHeatHandler heatHandlerOut = new HeatHandler(false);

	public HeatLimiterBasicTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	protected HeatLimiterBasicTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state){
		super(type, pos, state);
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
	public void serverTick(){
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
	public void setBlockState(BlockState pBlockState){
		super.setBlockState(pBlockState);
		//This is not, strictly speaking, optimized
		//Pre MC1.21, default behavior for all TEs was that changing blockstate invalidated capability caches
		//Post MC1.21, this is no longer the case, which opens up some opportunities for optimization
		//But everything was written with the assumption of invalidation on state change,
		//So anything other than re-implementing the old default is going to introduce a lot of new bugs
		level.invalidateCapabilities(worldPosition);
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		nbt.putBoolean("init_heat", init);
		nbt.putDouble("heat_in", heatIn);
		nbt.putDouble("heat_out", heatOut);
		nbt.putFloat("setting", setting);
		nbt.putString("expression", expression);
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		init = nbt.getBoolean("init_heat");
		heatIn = nbt.getDouble("heat_in");
		heatOut = nbt.getDouble("heat_out");
		setting = nbt.getFloat("setting");
		expression = nbt.getString("expression");
	}

	@Override
	@Nullable
	public IHeatHandler getHeatHandler(Direction dir){
		Direction facing = getBlockState().getValue(CRProperties.FACING);
		if(dir == null || dir == facing.getOpposite()){
			return heatHandlerIn;
		}else if(dir == facing){
			return heatHandlerOut;
		}
		return null;
	}

	@Override
	public Component getDisplayName(){
		return Component.translatable("container.heat_limiter");
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