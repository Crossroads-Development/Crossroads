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

public class BlockLiquidFat extends BlockFluidClassic{

	protected static final FluidLiquidFat LIQUID_FAT = new FluidLiquidFat();

	public BlockLiquidFat(){
		super(LIQUID_FAT, Material.WATER);
		LIQUID_FAT.setBlock(this);
		setUnlocalizedName("liquid_fat");
		setRegistryName("liquid_fat");
		ModBlocks.toRegister.add(this);
	}

	@Override
	public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos){
		return 2;
	}

	/**
	 * For normal use.
	 */
	public static Fluid getLiquidFat(){
		return FluidRegistry.getFluid("liquid_fat");
	}

	private static class FluidLiquidFat extends Fluid{

		private FluidLiquidFat(){
			super("liquid_fat", new ResourceLocation(Main.MODID + ":blocks/liquidfat_still"), new ResourceLocation(Main.MODID + ":blocks/liquidfat_flow"));
			setDensity(2000);
			setViscosity(2000);
		}
	}
}
