package com.Da_Technomancer.crossroads.fluids;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;

import java.util.ArrayList;

public final class CrossroadsFluids{

	protected static final Block.Properties BLOCK_PROP = Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops();
	protected static final Item.Properties BUCKET_PROP = new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(ItemGroup.MISC);

	public static CrossroadsFluid distilledWater;
	public static CrossroadsFluid dirtyWater;
	public static CrossroadsFluid steam;
	public static CrossroadsFluid liquidFat;
	//TODO molten metals

	public static ArrayList<Fluid> toRegister = new ArrayList<>();

	public static void init(){
		distilledWater = new CrossroadsFluid("distilled_water");
		dirtyWater = new CrossroadsFluid("dirty_water");
		steam = new CrossroadsFluid("steam");
		liquidFat = new CrossroadsFluid("liquid_fat");
	}
}
