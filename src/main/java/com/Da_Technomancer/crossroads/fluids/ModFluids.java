package com.Da_Technomancer.crossroads.fluids;

import com.Da_Technomancer.crossroads.Main;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ModFluids {
	
	public static void init(){
		
		FluidRegistry.registerFluid(BlockSteam.getSteam());
		new BlockSteam();
		FluidRegistry.addBucketForFluid(BlockSteam.getSteam());
		
		FluidRegistry.registerFluid(BlockMoltenCopper.getMoltenCopper());
		new BlockMoltenCopper();
		FluidRegistry.addBucketForFluid(BlockMoltenCopper.getMoltenCopper());
		
		FluidRegistry.registerFluid(BlockDistilledWater.getDistilledWater());
		new BlockDistilledWater();
		FluidRegistry.addBucketForFluid(BlockDistilledWater.getDistilledWater());
		
		FluidRegistry.registerFluid(BlockLiquidFat.getLiquidFat());
		new BlockLiquidFat();
		FluidRegistry.addBucketForFluid(BlockLiquidFat.getLiquidFat());
	}
	
	
	/*
	 * The code below is heavily based on EnderIO code,
	 * though not an exact copy. 
	 */
	public static class FluidStateMapper extends StateMapperBase implements ItemMeshDefinition {

		public final Fluid fluid;
		public final ModelResourceLocation location;

		public FluidStateMapper(Fluid fluid) {
			this.fluid = fluid;
			location = new ModelResourceLocation(Main.MODID + ":fluids", fluid.getName());
		}

		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
			return location;
		}

		@Override
		public ModelResourceLocation getModelLocation(ItemStack stack) {
			return location;
		}
	}

	@SideOnly(Side.CLIENT)
	public static void registerFluidBlockRendering(Fluid fluid) {

		FluidStateMapper mapper = new FluidStateMapper(fluid);
		Block block = fluid.getBlock();
		Item item = Item.getItemFromBlock(block);

		// item-model
		if (item != null) {
			ModelLoader.registerItemVariants(item);
			ModelLoader.setCustomMeshDefinition(item, mapper);
		}
		// block-model
		if (block != null) {
			ModelLoader.setCustomStateMapper(block, mapper);
		}
	}

	@SideOnly(Side.CLIENT)
	public static void registerRenderers() {
		registerFluidBlockRendering(BlockSteam.getSteam());
		registerFluidBlockRendering(BlockMoltenCopper.getMoltenCopper());
		registerFluidBlockRendering(BlockDistilledWater.getDistilledWater());
		registerFluidBlockRendering(BlockLiquidFat.getLiquidFat());
	}
}
