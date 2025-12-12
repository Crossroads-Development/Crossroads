package com.Da_Technomancer.crossroads.blocks.fluid;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.heat.HeatUtil;
import com.Da_Technomancer.crossroads.api.heat.IHeatCapable;
import com.Da_Technomancer.crossroads.api.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.api.templates.InventoryTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import com.Da_Technomancer.crossroads.gui.container.SteamBoilerContainer;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.essentials.api.IFluidCapable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class SteamBoilerTileEntity extends InventoryTE implements IHeatCapable, IFluidCapable{

	public static final BlockEntityType<SteamBoilerTileEntity> TYPE = CRTileEntity.createType(SteamBoilerTileEntity::new, CRBlocks.steamBoiler);

	public static final int BATCH_SIZE = 100;
	public static final int[] TIERS = {100, 200, 300, 400, 500};

	private final IFluidHandler waterHandler = new FluidHandler(0);
	private final IFluidHandler steamHandler = new FluidHandler(1);

	public SteamBoilerTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 1);//Salt
		fluidProps[0] = new TankProperty(8_000, true, false, f -> CraftingUtil.tagContains(FluidTags.WATER, f) || CraftingUtil.tagContains(CRFluids.DISTILLED_WATER, f));
		fluidProps[1] = new TankProperty(8_000, false, true, fluid -> true);
		initFluidManagers();
	}

	@Override
	public int fluidTanks(){
		return 2;//0: Water; 1: Steam
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		super.addInfo(chat, player, hit);
		chat.add(Component.translatable("tt.crossroads.steam_boiler.salt", inventory[0].getCount()));
	}

	@Override
	public void serverTick(){
		super.serverTick();
		int tier = HeatUtil.getHeatTier(temp, TIERS);

		if(tier != -1){
			temp -= (double) CRConfig.steamWorth.get() * (tier + 1) * (double) BATCH_SIZE / 1000D;

			int fluidCap = fluidProps[0].capacity;

			if(fluids[0].getAmount() >= BATCH_SIZE && fluidCap - fluids[1].getAmount() >= BATCH_SIZE && inventory[0].getCount() < 64){
				boolean salty = !CraftingUtil.tagContains(CRFluids.DISTILLED_WATER, fluids[0].getFluid());

				int batches = Math.min(tier + 1, fluids[0].getAmount() / BATCH_SIZE);
				batches = Math.min(batches, (fluidCap - fluids[1].getAmount()) / BATCH_SIZE);
				if(salty){
					batches = Math.min(batches, 64 - inventory[0].getCount());
				}
				fluids[0].shrink(batches * BATCH_SIZE);
				if(fluids[1].isEmpty()){
					fluids[1] = new FluidStack(CRFluids.steam.getStill(), BATCH_SIZE * batches);
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
	@Nullable
	public IFluidHandler getFluidHandler(Direction dir){
		if(dir == null){
			return globalFluidHandler;
		}
		if(dir == Direction.UP){
			return steamHandler;
		}
		return waterHandler;
	}

	@Override
	@Nullable
	public IHeatHandler getHeatHandler(Direction dir){
		if(dir == null || dir == Direction.DOWN){
			return heatHandler;
		}
		return null;
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
		return Component.translatable("container.steam_boiler");
	}


	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity){
		return new SteamBoilerContainer(i, playerInventory, createContainerBuf());
	}
}
