package com.Da_Technomancer.crossroads.fluids;

import com.Da_Technomancer.crossroads.Main;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockMoltenCopper extends BlockFluidClassic{

	protected static final FluidMoltenCopper MOLTEN_COPPER = new FluidMoltenCopper();

	public BlockMoltenCopper(){
		super(MOLTEN_COPPER, Material.LAVA);
		MOLTEN_COPPER.setBlock(this);
		setUnlocalizedName("blockMoltenCopper");
		setRegistryName("blockMoltenCopper");
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this).setRegistryName("blockMoltenCopper"));
	}

	/**
	 * For normal use.
	 */
	public static Fluid getMoltenCopper(){
		return FluidRegistry.getFluid("copper");
	}

	private static class FluidMoltenCopper extends Fluid{

		private FluidMoltenCopper(){
			super("copper", new ResourceLocation(Main.MODID + ":blocks/moltencopper_still"), new ResourceLocation(Main.MODID + ":blocks/moltencopper_flow"));
			setDensity(3000);
			setTemperature(6000);
			setViscosity(1300);
		}

	}
}
