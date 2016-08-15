package com.Da_Technomancer.crossroads.fluids;

import com.Da_Technomancer.crossroads.Main;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockLiquidFat extends BlockFluidClassic{

	private static FluidLiquidFat liquidFat = new FluidLiquidFat();

	public BlockLiquidFat(){
		super(liquidFat, Material.WATER);
		liquidFat.setBlock(this);
		setUnlocalizedName("blockLiquidFat");
		this.setRegistryName("blockLiquidFat");
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this).setRegistryName("blockLiquidFat"));

	}

	public static FluidLiquidFat getLiquidFat(){
		return liquidFat;
	}

	private static class FluidLiquidFat extends Fluid{

		private FluidLiquidFat(){
			super("liquidfat", new ResourceLocation(Main.MODID + ":blocks/liquidfat_still"), new ResourceLocation(Main.MODID + ":blocks/liquidfat_flow"));
			setDensity(2000);
			setViscosity(2000);
		}
	}
}
