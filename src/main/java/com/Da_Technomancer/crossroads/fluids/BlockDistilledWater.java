package com.Da_Technomancer.crossroads.fluids;

import com.Da_Technomancer.crossroads.Main;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockDistilledWater extends BlockFluidClassic{

	protected static final FluidDistilledWater DISTILLED_WATER = new FluidDistilledWater();

	public BlockDistilledWater(){
		super(DISTILLED_WATER, Material.WATER);
		DISTILLED_WATER.setBlock(this);
		setUnlocalizedName("blockDistilledWater");
		setRegistryName("blockDistilledWater");
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this).setRegistryName("blockDistilledWater"));

	}

	/**
	 * For normal use.
	 */
	public static Fluid getDistilledWater(){
		return FluidRegistry.getFluid("distilledwater");
	}
	
	private static class FluidDistilledWater extends Fluid{

		private FluidDistilledWater(){
			super("distilledwater", new ResourceLocation(Main.MODID + ":blocks/distilledwater_still"), new ResourceLocation(Main.MODID + ":blocks/distilledwater_flow"));
		}
	}
}
