package com.Da_Technomancer.crossroads.fluids;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.function.Supplier;

public class GenericFluid extends FlowingFluidBlock{

	private static final Block.Properties BLOCK_PROP = Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops();
	private static final Block.Properties BLOCK_PROP_HOT = Block.Properties.create(Material.LAVA).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops().setLightLevel(state -> 15);
	private static final Item.Properties BUCKET_PROP = new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(ItemGroup.MISC);

	public static FluidData create(String name, boolean lavaLike, boolean gaseous){
		FluidData data = new FluidData();
		Supplier<FlowingFluid> stillS = () -> data.still;
		Supplier<FlowingFluid> flowS = () -> data.flowing;
		Supplier<FlowingFluidBlock> blockS = () -> data.block;
		Supplier<Item> itemS = () -> data.bucket;
		data.still = new Still(name, stillS, flowS, blockS, itemS, lavaLike, gaseous);
		data.flowing = new Flowing(name, stillS, flowS, blockS, itemS, lavaLike, gaseous);
		data.bucket = new BucketItem(stillS, BUCKET_PROP).setRegistryName(name + "_bucket");
		data.block = new GenericFluid(name, stillS, lavaLike ? BLOCK_PROP_HOT : BLOCK_PROP);

		CRFluids.toRegister.add(data.still);
		CRFluids.toRegister.add(data.flowing);
		CRBlocks.toRegister.add(data.block);
		CRItems.toRegister.add(data.bucket);
		return data;
	}

	protected GenericFluid(String name, Supplier<FlowingFluid> still, Block.Properties prop){
		super(still, prop);
		setRegistryName(name);
	}

	public static class FluidData{

		public FlowingFluid still;
		public FlowingFluid flowing;
		public FlowingFluidBlock block;
		public Item bucket;

		private FluidData(){

		}
	}

	private static class Flowing extends ForgeFlowingFluid.Flowing{

		private Flowing(String name, Supplier<? extends Fluid> stillSupplier, Supplier<? extends Fluid> flowSupplier, Supplier<FlowingFluidBlock> blockSupplier, Supplier<Item> bucketSupplier, boolean hot, boolean gaseous){
			super(new Properties(stillSupplier, flowSupplier, FluidAttributes.builder(new ResourceLocation(Crossroads.MODID, "block/" + name + "_still"), new ResourceLocation(Crossroads.MODID, "block/" + name + "_flow")).luminosity(hot ? 15 : 0).density(gaseous ? -100 : 1000).temperature(hot ? 3000 : 300).viscosity(gaseous ? 500 : hot ? 6000 : 1000)).block(blockSupplier).bucket(bucketSupplier));
			setRegistryName("flowing_" + name);
		}
	}

	private static class Still extends ForgeFlowingFluid.Source{

		private Still(String name, Supplier<? extends Fluid> stillSupplier, Supplier<? extends Fluid> flowSupplier, Supplier<FlowingFluidBlock> blockSupplier, Supplier<Item> bucketSupplier, boolean hot, boolean gaseous){
			super(new Properties(stillSupplier, flowSupplier, FluidAttributes.builder(new ResourceLocation(Crossroads.MODID, "block/" + name + "_still"), new ResourceLocation(Crossroads.MODID, "block/" + name + "_flow")).luminosity(hot ? 15 : 0).density(gaseous ? -100 : 1000).temperature(hot ? 3000 : 300).viscosity(gaseous ? 500 : hot ? 6000 : 1000)).block(blockSupplier).bucket(bucketSupplier));
			setRegistryName(name);
		}
	}
}
