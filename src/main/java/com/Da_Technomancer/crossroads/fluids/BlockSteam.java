package com.Da_Technomancer.crossroads.fluids;

import com.Da_Technomancer.crossroads.Main;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockSteam extends BlockFluidClassic {

	private static FluidSteam steam = new FluidSteam();
	 
	public BlockSteam() {
		super(steam, Material.WATER);
		steam.setBlock(this);
		setUnlocalizedName("blockSteam");
		this.setRegistryName("blockSteam");
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this).setRegistryName("blockSteam"));
	
	}
	
	public static FluidSteam getSteam(){
		return steam;
	}
	
	
	public static class FluidSteam extends Fluid{

		public FluidSteam() {
			super("steam", new ResourceLocation(Main.MODID + ":blocks/steam_still"), new ResourceLocation(Main.MODID + ":blocks/steam_flow"));

			setDensity(-5);
			setTemperature(473); //200C
			setViscosity(200);
			setGaseous(true);
		}
		
	
	}

}