package com.Da_Technomancer.crossroads.fluids;

import com.Da_Technomancer.crossroads.Main;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockMoltenCopper extends BlockFluidClassic{

	private static FluidMoltenCopper moltenCopper = new FluidMoltenCopper();

	public BlockMoltenCopper(){
		super(moltenCopper, Material.LAVA);
		moltenCopper.setBlock(this);
		setUnlocalizedName("blockMoltenCopper");
		this.setRegistryName("blockMoltenCopper");
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this).setRegistryName("blockMoltenCopper"));
	}

	public static FluidMoltenCopper getMoltenCopper(){
		return moltenCopper;
	}

	private static class FluidMoltenCopper extends Fluid{

		private FluidMoltenCopper(){
			super("moltencopper", new ResourceLocation(Main.MODID + ":blocks/moltencopper_still"), new ResourceLocation(Main.MODID + ":blocks/moltencopper_flow"));
			setDensity(3000);
			setTemperature(6000);
			setViscosity(1300);
		}

	}
}
