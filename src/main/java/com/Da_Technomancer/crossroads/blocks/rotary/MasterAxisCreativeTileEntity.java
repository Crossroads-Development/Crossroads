package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.api.rotary.AxisTypes;
import com.Da_Technomancer.crossroads.api.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.api.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.gui.container.MasterAxisCreativeContainer;
import com.Da_Technomancer.essentials.api.packets.INBTReceiver;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class MasterAxisCreativeTileEntity extends MasterAxisTileEntity implements INBTReceiver, MenuProvider{
	
	public static final BlockEntityType<MasterAxisCreativeTileEntity> TYPE = CRTileEntity.createType(MasterAxisCreativeTileEntity::new, CRBlocks.masterAxisCreative);

	public MasterAxisCreativeTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	public float setting = 0;
	public String expression = "0";

	@Override
	protected void runCalc(){
		//Based on a simplified version of the implementation in RedstoneMasterAxis

		baseSpeed = setting;
		double[] energyCalcResults = RotaryUtil.getTotalEnergy(rotaryMembers, true);
		double sumIRot = energyCalcResults[3];//Sum of every gear's moment of inertia time rotation ratio squared

		double cost = sumIRot * Math.pow(baseSpeed, 2) / 2D;//Total energy required to hold the output at the requested base speed

		if(Double.isNaN(cost)){
			cost = 0;//There's a NaN bug somewhere, and I can't find it. This should work
		}

		energyChange = cost - sumEnergy;
		energyLossChange = energyCalcResults[1];
		sumEnergy = cost;

		for(IAxleHandler gear : rotaryMembers){
			double gearSpeed = baseSpeed * gear.getRotationRatio();
			gear.setEnergy(Math.signum(gearSpeed) * Math.pow(gearSpeed, 2) * gear.getMoInertia() / 2D);
		}

		runAngleCalc();
	}

	@Override
	protected AxisTypes getAxisType(){
		return AxisTypes.FIXED;
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
		return new MasterAxisCreativeContainer(id, playerInv, setting, expression, worldPosition);
	}

	@Override
	public Component getDisplayName(){
		return Component.translatable("container.crossroads.master_axis_creative");
	}

	@Override
	public void receiveNBT(CompoundTag nbt, @Nullable ServerPlayer player){
		if(nbt.contains("value")){
			setting = nbt.getFloat("value");
			expression = nbt.getString("config");
			setChanged();
		}
	}
}
