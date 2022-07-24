package com.Da_Technomancer.crossroads.blocks.fluid;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.templates.InventoryTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import com.Da_Technomancer.crossroads.gui.container.RadiatorContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;

public class RadiatorTileEntity extends InventoryTE{

	public static final BlockEntityType<RadiatorTileEntity> TYPE = CRTileEntity.createType(RadiatorTileEntity::new, CRBlocks.radiator);

	public static final int[] TIERS = new int[] {100, 200, 300, 400, 500};//Steam use per tick
	private int mode = 0;

	public RadiatorTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 0);
		fluidProps[0] = new TankProperty(10_000, true, false, f -> CraftingUtil.tagContains(CRFluids.STEAM, f));
		fluidProps[1] = new TankProperty(10_000, false, true);
		initFluidManagers();
	}

	@Override
	public int fluidTanks(){
		return 2;
	}

	@Override
	public boolean useHeat(){
		return true;
	}

	public int getMode(){
		return mode;
	}

	public int cycleMode(){
		mode = (mode + 1) % TIERS.length;
		setChanged();
		return mode;
	}

	@Override
	public void serverTick(){
		super.serverTick();

		if(fluids[0].getAmount() >= TIERS[mode] && fluidProps[1].capacity - fluids[1].getAmount() >= TIERS[mode]){
			temp += TIERS[mode] * (double) CRConfig.steamWorth.get() / 1000;
			if(fluids[1].isEmpty()){
				fluids[1] = new FluidStack(CRFluids.distilledWater.still, TIERS[mode]);
			}else{
				fluids[1].grow(TIERS[mode]);
			}

			fluids[0].shrink(TIERS[mode]);
			setChanged();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){

		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			if(side == null || side.getAxis() == Direction.Axis.Y){
				return (LazyOptional<T>) globalFluidOpt;
			}
		}

		if(cap == Capabilities.HEAT_CAPABILITY && side != Direction.UP && side != Direction.DOWN){
			return (LazyOptional<T>) heatOpt;
		}

		return super.getCapability(cap, side);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return false;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		return false;
	}

	@Override
	public Component getDisplayName(){
		return Component.translatable("container.radiator");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player playerEntity){
		return new RadiatorContainer(id, playerInventory, createContainerBuf());
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		mode = nbt.getInt("mode");
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		nbt.putInt("mode", mode);
	}
}
