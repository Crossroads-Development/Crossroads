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
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.EdibleBlob;
import com.Da_Technomancer.essentials.api.IFluidCapable;
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
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class HamsterWheelTileEntity extends BlockEntity implements ITickableTileEntity, FatFeederTileEntity.IFeedableTE, IInfoTE, IFluidCapable{

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
	protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		fat = nbt.getInt("fat");
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.saveAdditional(nbt, registries);
		nbt.putInt("fat", fat);
	}

	private final IFluidHandler fluidHandler = new FluidHandler();

	@Nullable
	@Override
	public IFluidHandler getFluidHandler(Direction direction){
		return direction != getBlockState().getValue(CRProperties.HORIZ_FACING) ? fluidHandler : null;
	}

	private class FluidHandler implements IFluidHandler{

		@Override
		public int getTanks(){
			return 1;
		}

		@Override
		public FluidStack getFluidInTank(int tank){
			return fat > 0 ? new FluidStack(CRFluids.liquidFat.getStill(), fat) : FluidStack.EMPTY;
		}

		@Override
		public int getTankCapacity(int tank){
			return MAX_FEEDER_FAT;
		}

		@Override
		public boolean isFluidValid(int tank, FluidStack stack){
			return stack.getFluid().isSame(CRFluids.liquidFat.getStill()) && tank == 0;
		}

		@Override
		public int fill(FluidStack resource, FluidAction action){
			if(isFluidValid(0, resource) && fat < getTankCapacity(0)){
				int added = Math.min(getTankCapacity(0) - fat, resource.getAmount());
				if(action.execute()){
					fat += added;
				}
				return added;
			}
			return 0;
		}

		@Override
		public FluidStack drain(FluidStack resource, FluidAction action){
			return FluidStack.EMPTY;
		}

		@Override
		public FluidStack drain(int maxDrain, FluidAction action){
			return FluidStack.EMPTY;
		}
	}
}
