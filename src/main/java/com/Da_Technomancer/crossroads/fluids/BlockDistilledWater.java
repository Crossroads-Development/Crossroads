package com.Da_Technomancer.crossroads.fluids;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class BlockDistilledWater extends BlockFluidClassic{

	protected static final FluidDistilledWater DISTILLED_WATER = new FluidDistilledWater();

	public BlockDistilledWater(){
		super(DISTILLED_WATER, Material.WATER);
		DISTILLED_WATER.setBlock(this);
		setUnlocalizedName("blockDistilledWater");
		setRegistryName("blockdistilledwater");
		ModBlocks.toRegister.add(this);
	}

	@Override
	public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos){
		return 2;
	}

	/**
	 * For normal use.
	 */
	public static Fluid getDistilledWater(){
		return FluidRegistry.getFluid("distilledwater");
	}

	private static class FluidDistilledWater extends Fluid{

		private FluidDistilledWater(){
			super("distilledwater", new ResourceLocation(Main.MODID, "blocks/distilledwater_still"), new ResourceLocation(Main.MODID, "blocks/distilledwater_flow"));
		}
	}
}
