package com.Da_Technomancer.crossroads.fluids;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;;

public class BlockDistilledWater extends BlockFluidClassic{

	protected static final FluidDistilledWater DISTILLED_WATER = new FluidDistilledWater();

	public BlockDistilledWater(){
		super(DISTILLED_WATER, Material.WATER);
		DISTILLED_WATER.setBlock(this);
		setTranslationKey("distilled_water");
		setRegistryName("distilled_water");
		CrossroadsBlocks.toRegister.add(this);
	}

	@Override
	public int getLightOpacity(BlockState state, IBlockAccess world, BlockPos pos){
		return 2;
	}

	/**
	 * For normal use.
	 */
	public static Fluid getDistilledWater(){
		return FluidRegistry.getFluid("distilled_water");
	}

	private static class FluidDistilledWater extends Fluid{

		private FluidDistilledWater(){
			super("distilled_water", new ResourceLocation(Crossroads.MODID, "blocks/distilledwater_still"), new ResourceLocation(Crossroads.MODID, "blocks/distilledwater_flow"));
		}
	}
}
