package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.crafting.CRItemTags;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import com.Da_Technomancer.crossroads.gui.container.SteamBoilerContainer;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;

@ObjectHolder(Crossroads.MODID)
public class SteamBoilerTileEntity extends InventoryTE{

	@ObjectHolder("steam_boiler")
	public static BlockEntityType<SteamBoilerTileEntity> TYPE = null;

	public static final int BATCH_SIZE = 100;
	public static final int[] TIERS = {100, 200, 300, 400, 500};

	public SteamBoilerTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 1);//Salt
		fluidProps[0] = new TankProperty(8_000, true, false, f -> CRItemTags.tagContains(FluidTags.WATER, f) || CRItemTags.tagContains(CRFluids.DISTILLED_WATER, f));
		fluidProps[1] = new TankProperty(8_000, false, true, fluid -> true);
		initFluidManagers();
	}
	
	@Override
	public int fluidTanks(){
		return 2;//0: Water; 1: Steam
	}
	
	@Override
	public boolean useHeat(){
		return true;
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		super.addInfo(chat, player, hit);
		chat.add(new TranslatableComponent("tt.crossroads.steam_boiler.salt", inventory[0].getCount()));
	}

	@Override
	public void serverTick(){
		super.serverTick();
		int tier = HeatUtil.getHeatTier(temp, TIERS);
		
		if(tier != -1){
			temp -= (double) CRConfig.steamWorth.get() * (tier + 1) * (double) BATCH_SIZE / 1000D;

			int fluidCap = fluidProps[0].capacity;
			
			if(fluids[0].getAmount() >= BATCH_SIZE && fluidCap - fluids[1].getAmount() >= BATCH_SIZE && inventory[0].getCount() < 64){
				boolean salty = CRItemTags.tagContains(FluidTags.WATER, fluids[0].getFluid());

				int batches = Math.min(tier + 1, fluids[0].getAmount() / BATCH_SIZE);
				batches = Math.min(batches, (fluidCap - fluids[1].getAmount()) / BATCH_SIZE);
				if(salty){
					batches = Math.min(batches, 64 - inventory[0].getCount());
				}
				fluids[0].shrink(batches * BATCH_SIZE);
				if(fluids[1].isEmpty()){
					fluids[1] = new FluidStack(CRFluids.steam.still, BATCH_SIZE * batches);
				}else{
					fluids[1].grow(BATCH_SIZE * batches);
				}

				if(salty){
					if(inventory[0].isEmpty()){
						inventory[0] = new ItemStack(CRItems.dustSalt, batches);
					}else{
						inventory[0].grow(batches);
					}
				}
			}
			setChanged();
		}
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		waterOpt.invalidate();
		steamOpt.invalidate();
	}

	private final LazyOptional<IFluidHandler> waterOpt = LazyOptional.of(() -> new FluidHandler(0));
	private final LazyOptional<IFluidHandler> steamOpt = LazyOptional.of(() -> new FluidHandler(1));

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing){

		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			if(facing == null){
				return (LazyOptional<T>) globalFluidOpt;
			}
			if(facing == Direction.UP){
				return (LazyOptional<T>) steamOpt;
			}
			return (LazyOptional<T>) waterOpt;
		}

		if(capability == Capabilities.HEAT_CAPABILITY && (facing == null || facing == Direction.DOWN)){
			return (LazyOptional<T>) heatOpt;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return false;//Automation is not allowed to interact with the salt slot
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		return false;//Automation is not allowed to interact with the salt slot
	}

	@Override
	public Component getDisplayName(){
		return new TranslatableComponent("container.steam_boiler");
	}


	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity){
		return new SteamBoilerContainer(i, playerInventory, createContainerBuf());
	}
}
