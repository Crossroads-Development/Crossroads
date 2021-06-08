package com.Da_Technomancer.crossroads.tileentities.witchcraft;

import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.API.witchcraft.ICultivatable;
import com.Da_Technomancer.crossroads.API.witchcraft.IPerishable;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.World;

public abstract class AbstractNutrientEnvironmentTileEntity extends InventoryTE{

	private final int[] cultivatedSlots;
	private final int nutrientTankIndex;
	protected long lastTick;

	public AbstractNutrientEnvironmentTileEntity(TileEntityType<? extends InventoryTE> type, int invSize, int[] cultivatedSlots, int nutrientTankIndex){
		super(type, invSize);
		this.cultivatedSlots = cultivatedSlots;
		this.nutrientTankIndex = nutrientTankIndex;
	}

	public static float getAverageLifetime(World world, ItemStack... perishableStacks){
		int totalLifetime = 0;//Ticks
		int itemCount = 0;
		long currentTime = world.getGameTime();
		for(ItemStack stack : perishableStacks){
			if(stack.getItem() instanceof IPerishable){
				long spoilTime = ((IPerishable) stack.getItem()).getSpoilTime(stack, world);
				itemCount++;
				if(spoilTime > currentTime){
					totalLifetime += spoilTime - currentTime;
				};
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
		return fluids[nutrientTankIndex].getFluid() == CRFluids.nutrientSolution.still && fluids[nutrientTankIndex].getAmount() > 0;
	}

	@Override
	public void tick(){
		super.tick();
		long gameTime = level.getGameTime();
		if(gameTime > lastTick && canCultivate()){
			for(int cultivated : cultivatedSlots){
				ItemStack stack = inventory[cultivated];
				if(stack.getItem() instanceof ICultivatable){
					ICultivatable item = (ICultivatable) stack.getItem();
					//Drain liquid
					if(!fluids[nutrientTankIndex].isEmpty()){
						fluids[nutrientTankIndex].shrink(item.getPassiveNutrientDrain());
					}
					//Update the item
					item.cultivate(stack, level, 1);
				}
			}
		}
		lastTick = gameTime;
		setChanged();
	}

	@Override
	public void onLoad(){
		super.onLoad();
		//While this block is unloaded, the gametime has still been advancing,
		//so the stored items have decayed without this block countering that
		//When we reload, we do a single large freeze operation to account for time spent unloaded, plus a small extra as a buffer
		long gameTime = level.getGameTime();
		if(gameTime > lastTick){
			for(int cultivated : cultivatedSlots){
				ItemStack stack = inventory[cultivated];
				if(stack.getItem() instanceof ICultivatable){
					((ICultivatable) stack.getItem()).cultivate(stack, level, gameTime - lastTick + 1);
				}
			}
		}
		lastTick = gameTime;
//		setChanged(); Note to self: Calling setChanged() in onLoad() freezes the loading process; bad
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt){
		super.load(state, nbt);
		lastTick = nbt.getLong("last_tick");
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt){
		nbt = super.save(nbt);
		nbt.putLong("last_tick", lastTick);
		return nbt;
	}
}
