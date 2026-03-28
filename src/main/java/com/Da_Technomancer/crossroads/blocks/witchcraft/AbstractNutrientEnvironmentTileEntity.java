package com.Da_Technomancer.crossroads.blocks.witchcraft;

import com.Da_Technomancer.crossroads.api.templates.InventoryTE;
import com.Da_Technomancer.crossroads.api.witchcraft.ICultivatable;
import com.Da_Technomancer.crossroads.api.witchcraft.IPerishable;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractNutrientEnvironmentTileEntity extends InventoryTE{

	private final int[] cultivatedSlots;
	private final int nutrientTankIndex;
	protected long lastTick;

	public AbstractNutrientEnvironmentTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int invSize, int[] cultivatedSlots, int nutrientTankIndex){
		super(type, pos, state, invSize);
		this.cultivatedSlots = cultivatedSlots;
		this.nutrientTankIndex = nutrientTankIndex;
	}

	public static float getAverageLifetime(Level world, ItemStack... perishableStacks){
		int totalLifetime = 0;//Ticks
		int itemCount = 0;
		long currentTime = world.getGameTime();
		for(ItemStack stack : perishableStacks){
			if(stack.getItem() instanceof IPerishable){
				long spoilTime = IPerishable.getAndInitSpoilTime(stack, world);
				itemCount++;
				if(spoilTime > currentTime){
					totalLifetime += spoilTime - currentTime;
				}
			}
		}
		if(itemCount == 0){
			return 0;
		}
		return (float) totalLifetime / itemCount;
	}

	public float getRedstone(){
		//Return average lifetime remaining for the contents, in seconds
		ItemStack[] toMeasure = new ItemStack[cultivatedSlots.length];

		for(int i = 0; i < toMeasure.length; i++){
			toMeasure[i] = inventory[cultivatedSlots[i]];
		}
		return getAverageLifetime(level, toMeasure) / 20F;
	}

	protected boolean canCultivate(){
		return fluids[nutrientTankIndex].getFluid() == CRFluids.nutrientSolution.getStill() && fluids[nutrientTankIndex].getAmount() > 0;
	}

	@Override
	public void serverTick(){
		super.serverTick();
		long gameTime = level.getGameTime();
		long timeSinceLastTick = gameTime - lastTick;

		if(timeSinceLastTick > 0 && canCultivate()){
			for(int cultivated : cultivatedSlots){
				ItemStack stack = inventory[cultivated];
				if(stack.getItem() instanceof ICultivatable item){
					//Drain liquid
					if(!fluids[nutrientTankIndex].isEmpty() && gameTime % getPassiveNutrientDrainInterval() == 0){
						//Liquid drain is 1mB every (nutrient drain interval) ticks
						fluids[nutrientTankIndex].shrink(1);
					}
					//Update the item
					item.cultivate(stack, level, timeSinceLastTick);
				}
			}
		}
		lastTick = gameTime;
		setChanged();
	}

	protected abstract int getPassiveNutrientDrainInterval();

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		lastTick = nbt.getLong("last_tick");
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		nbt.putLong("last_tick", lastTick);
	}
}
