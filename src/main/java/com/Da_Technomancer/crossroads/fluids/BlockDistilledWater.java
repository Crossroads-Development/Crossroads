package com.Da_Technomancer.crossroads.fluids;

import com.Da_Technomancer.crossroads.Main;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockDistilledWater extends BlockFluidClassic{

	private static FluidDistilledWater distilledWater = new FluidDistilledWater();
	 
	public BlockDistilledWater() {
		super(distilledWater, Material.WATER);
		distilledWater.setBlock(this);
		setUnlocalizedName("blockDistilledWater");
		this.setRegistryName("blockDistilledWater");
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this).setRegistryName("blockDistilledWater"));
	
	}
	
	public static FluidDistilledWater getDistilledWater(){
		return distilledWater;
	}
	
	
	public static class FluidDistilledWater extends Fluid{

		public FluidDistilledWater() {
			super("distilledwater", new ResourceLocation(Main.MODID + ":blocks/distilledwater_still"), new ResourceLocation(Main.MODID + ":blocks/distilledwater_flow"));
		}
		
	
	}
}
