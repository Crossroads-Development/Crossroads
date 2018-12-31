package com.Da_Technomancer.crossroads.fluids;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.render.bakedModel.BakedModelLoader;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelFluid;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

public final class ModFluids{

	public static BlockDistilledWater distilledWater;
	public static BlockSteam steam;
	public static BlockLiquidFat liquidFat;
	public static BlockMoltenCopshowium moltenCopshowium;
	public static BlockDirtyWater dirtyWater;

	public static void init(){
		FluidRegistry.registerFluid(BlockSteam.STEAM);
		steam = new BlockSteam();
		FluidRegistry.addBucketForFluid(BlockSteam.STEAM);

		FluidRegistry.registerFluid(BlockDistilledWater.DISTILLED_WATER);
		distilledWater = new BlockDistilledWater();
		FluidRegistry.addBucketForFluid(BlockDistilledWater.DISTILLED_WATER);

		FluidRegistry.registerFluid(BlockLiquidFat.LIQUID_FAT);
		liquidFat = new BlockLiquidFat();
		FluidRegistry.addBucketForFluid(BlockLiquidFat.LIQUID_FAT);

		FluidRegistry.registerFluid(BlockMoltenCopshowium.MOLTEN_COPSHOWIUM);
		moltenCopshowium = new BlockMoltenCopshowium();
		FluidRegistry.addBucketForFluid(BlockMoltenCopshowium.MOLTEN_COPSHOWIUM);

		FluidRegistry.registerFluid(BlockDirtyWater.DIRTY_WATER);
		dirtyWater = new BlockDirtyWater();
		FluidRegistry.addBucketForFluid(BlockDirtyWater.DIRTY_WATER);
	}

	@SideOnly(Side.CLIENT)
	public static void registerRenderers(){
		registerFluidBlockRendering(BlockSteam.STEAM, "steam");
		registerFluidBlockRendering(BlockDistilledWater.DISTILLED_WATER, "distilled_water");
		registerFluidBlockRendering(BlockLiquidFat.LIQUID_FAT, "liquid_fat");
		registerFluidBlockRendering(BlockMoltenCopshowium.MOLTEN_COPSHOWIUM, "copshowium");
		registerFluidBlockRendering(BlockDirtyWater.DIRTY_WATER, "dirty_water");
		for(Map.Entry<String, OreSetup.OreProfile> molten : OreSetup.metalStages.entrySet()){
			BakedModelLoader.MODEL_MAP.put(registerFluidBlockRendering(molten.getValue().molten, "molten_metal_" + molten.getValue().molten.getName()), new ModelFluid(molten.getValue().molten));
		}
	}

	/**
	 * The code below is based on EnderIO code, though is not an exact copy.
	 * This is permitted by the EnderIO license at the time of writing this.
	 */
	private static class FluidStateMapper extends StateMapperBase implements ItemMeshDefinition{

		private final ModelResourceLocation location;

		public FluidStateMapper(String variant){
			location = new ModelResourceLocation(Main.MODID + ":fluids", variant);
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
	private static ModelResourceLocation registerFluidBlockRendering(Fluid fluid, String variant){
		FluidStateMapper mapper = new FluidStateMapper(variant);
		Block block = fluid.getBlock();
		Item item = Item.getItemFromBlock(block);

		// item-model
		ModelLoader.registerItemVariants(item);
		ModelLoader.setCustomMeshDefinition(item, mapper);

		// block-model
		ModelLoader.setCustomStateMapper(block, mapper);
		return mapper.location;
	}
}
