package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.CRCapabilities;
import com.Da_Technomancer.crossroads.api.heat.HeatUtil;
import com.Da_Technomancer.crossroads.api.heat.IHeatCapable;
import com.Da_Technomancer.crossroads.api.heat.IHeatHandler;
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

//We can't use ModuleTE because this has 2 internal temperatures
public class MaxwellDemonTileEntity extends BlockEntity implements ITickableTileEntity, IInfoTE, IHeatCapable, IFluidCapable, FatFeederTileEntity.IFeedableTE{

	public static final BlockEntityType<MaxwellDemonTileEntity> TYPE = CRTileEntity.createType(MaxwellDemonTileEntity::new, CRBlocks.maxwellDemon);

	public static final double MAX_TEMP = 2500;
	public static final double MIN_TEMP = -200;
	private static final int MAX_FEEDER_FAT = 200;//Fat-feeder limit, ignored for manual feeding
	public static final int FAT_CONSUMPTION = 1;

	private double tempUp = 0;
	private double tempDown = 0;
	private boolean init = false;
	private double rate = -1;//Not saved/loaded to NBT, as we want this to regenerate on reload with the config
	private int fat = 0;

	public MaxwellDemonTileEntity(BlockPos blockPos, BlockState blockState){
		super(TYPE, blockPos, blockState);
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		chat.add(Component.translatable("tt.crossroads.maxwell_demon.read_top", CRConfig.formatVal(tempUp, player)));
		chat.add(Component.translatable("tt.crossroads.maxwell_demon.read_bottom", CRConfig.formatVal(tempDown, player)));
		chat.add(Component.translatable("tt.crossroads.maxwell_demon.read_biome", CRConfig.formatVal(HeatUtil.convertBiomeTemp(level, worldPosition), player)));
		chat.add(Component.translatable("tt.crossroads.maxwell_demon.fat", fat, FAT_CONSUMPTION, fat / FAT_CONSUMPTION / 20));
	}

	private void init(){
		if(!init){
			tempUp = HeatUtil.convertBiomeTemp(level, worldPosition);
			tempDown = tempUp;
			init = true;
		}
		if(rate < 0){
			rate = CRConfig.demonPower.get();
		}
	}

	@Override
	public void serverTick(){
		init();

		//Make heat / cold
		if(fat > 0){
			fat = Math.max(fat - FAT_CONSUMPTION, 0);
			if(tempUp < MAX_TEMP){
				tempUp = Math.min(MAX_TEMP, tempUp + rate);
				setChanged();
			}
			if(tempDown > MIN_TEMP){
				tempDown = Math.max(MIN_TEMP, tempDown - rate);
				setChanged();
			}
		}

		//Heat transfer
		for(int i = 0; i < 2; i++){
			Direction dir = Direction.from3DDataValue(i);

			BlockPos relPos = worldPosition.relative(dir);
			IHeatHandler heatHandler;
			if((heatHandler = level.getCapability(CRCapabilities.HEAT_CAPABILITY, relPos, dir.getOpposite())) != null){
				double reservePool = i == 0 ? tempDown : tempUp;
				if(i == 0){
					tempDown -= reservePool;
				}else{
					tempUp -= reservePool;
				}

				reservePool += heatHandler.getTemp();
				heatHandler.addHeat(-heatHandler.getTemp());
				reservePool /= 2;
				if(i == 0){
					tempDown += reservePool;
				}else{
					tempUp += reservePool;
				}
				heatHandler.addHeat(reservePool);
			}
		}
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		nbt.putBoolean("init_heat", init);
		nbt.putDouble("temp_u", tempUp);
		nbt.putDouble("temp_d", tempDown);
		nbt.putInt("fat", fat);
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		init = nbt.getBoolean("init_heat");
		tempUp = nbt.getDouble("temp_u");
		tempDown = nbt.getDouble("temp_d");
		fat = nbt.getInt("fat");
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

	private final IHeatHandler heatHandlerUp = new HeatHandler(true);
	private final IHeatHandler heatHandlerDown = new HeatHandler(false);
	private final IFluidHandler fluidHandler = new FluidHandler();

	@Override
	@Nullable
	public IHeatHandler getHeatHandler(Direction dir){
		if(dir == null || dir == Direction.UP){
			return heatHandlerUp;
		}else if(dir == Direction.DOWN){
			return heatHandlerDown;
		}
		return null;
	}

	@Nullable
	@Override
	public IFluidHandler getFluidHandler(Direction direction){
		return direction == null || direction.getAxis() != Direction.Axis.Y ? fluidHandler : null;
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

	private class HeatHandler implements IHeatHandler{

		private final boolean up;

		private HeatHandler(boolean up){
			this.up = up;
		}

		@Override
		public double getTemp(){
			init();
			return up ? tempUp : tempDown;
		}

		@Override
		public void setTemp(double tempIn){
			init = true;
			if(up){
				tempUp = tempIn;
			}else{
				tempDown = tempIn;
			}
			setChanged();
		}

		@Override
		public void addHeat(double heat){
			init();
			if(up){
				tempUp += heat;
			}else{
				tempDown += heat;
			}
			setChanged();
		}
	}
}
