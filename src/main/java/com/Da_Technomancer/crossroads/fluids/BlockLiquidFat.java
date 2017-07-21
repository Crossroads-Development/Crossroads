package com.Da_Technomancer.crossroads.fluids;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;

import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class BlockLiquidFat extends BlockFluidClassic{

	protected static final FluidLiquidFat LIQUID_FAT = new FluidLiquidFat();

	public BlockLiquidFat(){
		super(LIQUID_FAT, Material.WATER);
		LIQUID_FAT.setBlock(this);
		setUnlocalizedName("blockLiquidFat");
		setRegistryName("blockLiquidFat");
		ModBlocks.toRegister.add(this);
	}

	/**
	 * For normal use.
	 */
	public static Fluid getLiquidFat(){
		return FluidRegistry.getFluid("liquidfat");
	}

	private static class FluidLiquidFat extends Fluid{

		private FluidLiquidFat(){
			super("liquidfat", new ResourceLocation(Main.MODID + ":blocks/liquidfat_still"), new ResourceLocation(Main.MODID + ":blocks/liquidfat_flow"));
			setDensity(2000);
			setViscosity(2000);
		}
	}
}
