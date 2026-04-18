package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.CRCapabilities;
import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.api.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.api.templates.IInfoTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.blocks.fluid.FatFeederTileEntity;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.EdibleBlob;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;

public class HamsterWheelTileEntity extends BlockEntity implements ITickableTileEntity, FatFeederTileEntity.IFeedableTE, IInfoTE{

	public static final BlockEntityType<HamsterWheelTileEntity> TYPE = CRTileEntity.createType(HamsterWheelTileEntity::new, CRBlocks.hamsterWheel);

	private static final int MAX_FEEDER_FAT = 200;//Fat-feeder limit, ignored for manual feeding
	public static final int FAT_CONSUMPTION = 1;

	public float angle = 0;
	public float nextAngle = 0;
	private int fat;
	private double power = -1;

	public HamsterWheelTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	private double getPower(){
		if(power < 0){
			power = CRConfig.hamsterPower.getAsDouble();
		}
		return power;
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		chat.add(Component.translatable("tt.crossroads.hamster_wheel.fat", fat, FAT_CONSUMPTION, fat / FAT_CONSUMPTION / 20));
	}

	@Override
	public void tick(){
		Direction facing = getBlockState().getValue(CRProperties.HORIZ_FACING);
		IAxleHandler axle = level.getCapability(CRCapabilities.AXLE_CAPABILITY, worldPosition.relative(facing), facing.getOpposite());
		if(axle != null){
			if(level.isClientSide){
				angle = axle.getAngle(0);
				nextAngle = axle.getAngle(1F);
				return;
			}
			if(fat > 0){
				fat = Math.max(0, fat - FAT_CONSUMPTION);
				axle.addEnergy(getPower() * RotaryUtil.getCCWSign(facing), true);
				setChanged();
			}
		}else if(level.isClientSide){
			nextAngle = angle;
		}
	}

	@Override
	public int attemptFeed(int availableFat){
		if(fat < MAX_FEEDER_FAT){
			int fed = Math.min(MAX_FEEDER_FAT - fat, availableFat);
			fat += fed;
			setChanged();
			return fed;
		}
		return 0;
	}

	public ItemStack feedItem(ItemStack toFeed){
		if(toFeed.is(CRItems.edibleBlob) && fat == 0){
			int fatEq = (EdibleBlob.getHealAmount(toFeed) + EdibleBlob.getTrueSat(toFeed)) * CRConfig.fatPerValue.getAsInt();
			fat += fatEq;
			setChanged();
			toFeed = toFeed.copy();
			toFeed.shrink(1);
			return toFeed;
		}
		return toFeed;
	}

	@Override
	protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		fat = nbt.getInt("fat");
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.saveAdditional(nbt, registries);
		nbt.putInt("fat", fat);
	}
}
