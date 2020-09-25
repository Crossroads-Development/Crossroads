package com.Da_Technomancer.crossroads.API.effects.alchemy;

import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class SpawnItemAlchemyEffect implements IAlchEffect{

	private final Item spawnedItem;

	public SpawnItemAlchemyEffect(Item spawnedItem){
		this.spawnedItem = spawnedItem;
	}

	@Override
	public void doEffect(World world, BlockPos pos, int amount, EnumMatterPhase phase, ReagentMap reags){
		if(phase == EnumMatterPhase.SOLID){//Very important requirement- otherwise we spawn a massive number of these, creating a dupe bug
			InventoryHelper.spawnItemStack(world, pos.getX() + Math.random(), pos.getY() + Math.random(), pos.getZ() + Math.random(), new ItemStack(spawnedItem, amount));
		}
	}

	@Override
	public ITextComponent getName(){
		return new TranslationTextComponent("effect.spawn_item", spawnedItem.getName().getString());
	}
}
