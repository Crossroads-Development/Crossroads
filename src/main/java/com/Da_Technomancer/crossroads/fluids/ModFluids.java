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

public final class ModFluids{

	public static BlockDistilledWater distilledWater;
	public static BlockSteam steam;
	public static BlockMoltenCopper moltenCopper;
	public static BlockLiquidFat liquidFat;
	public static BlockMoltenCopshowium moltenCopshowium;

	public static void init(){
		FluidRegistry.registerFluid(BlockSteam.STEAM);
		steam = new BlockSteam();
		FluidRegistry.addBucketForFluid(BlockSteam.STEAM);

		FluidRegistry.registerFluid(BlockMoltenCopper.MOLTEN_COPPER);
		moltenCopper = new BlockMoltenCopper();
		FluidRegistry.addBucketForFluid(BlockMoltenCopper.MOLTEN_COPPER);

		FluidRegistry.registerFluid(BlockDistilledWater.DISTILLED_WATER);
		distilledWater = new BlockDistilledWater();
		FluidRegistry.addBucketForFluid(BlockDistilledWater.DISTILLED_WATER);

		FluidRegistry.registerFluid(BlockLiquidFat.LIQUID_FAT);
		liquidFat = new BlockLiquidFat();
		FluidRegistry.addBucketForFluid(BlockLiquidFat.LIQUID_FAT);

		FluidRegistry.registerFluid(BlockMoltenCopshowium.MOLTEN_COPSHOWIUM);
		moltenCopshowium = new BlockMoltenCopshowium();
		FluidRegistry.addBucketForFluid(BlockMoltenCopshowium.MOLTEN_COPSHOWIUM);
	}
	
	@SideOnly(Side.CLIENT)
	public static void registerRenderers(){
		registerFluidBlockRendering(BlockSteam.STEAM);
		registerFluidBlockRendering(BlockMoltenCopper.MOLTEN_COPPER);
		registerFluidBlockRendering(BlockDistilledWater.DISTILLED_WATER);
		registerFluidBlockRendering(BlockLiquidFat.LIQUID_FAT);
		registerFluidBlockRendering(BlockMoltenCopshowium.MOLTEN_COPSHOWIUM);
	}

	/*
	 * The code below is based on EnderIO code, though is not an exact
	 * copy. This is permitted by the EnderIO license.
	 */
	private static class FluidStateMapper extends StateMapperBase implements ItemMeshDefinition{

		public final ModelResourceLocation location;

		public FluidStateMapper(Fluid fluid){
			location = new ModelResourceLocation(Main.MODID + ":fluids", fluid.getName());
		}

		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state){
			return location;
		}

		@Override
		public ModelResourceLocation getModelLocation(ItemStack stack){
			return location;
		}
	}

	@SideOnly(Side.CLIENT)
	private static void registerFluidBlockRendering(Fluid fluid){

		FluidStateMapper mapper = new FluidStateMapper(fluid);
		Block block = fluid.getBlock();
		Item item = Item.getItemFromBlock(block);

		// item-model
		if(item != null){
			ModelLoader.registerItemVariants(item);
			ModelLoader.setCustomMeshDefinition(item, mapper);
		}
		// block-model
		if(block != null){
			ModelLoader.setCustomStateMapper(block, mapper);
		}
	}
}
