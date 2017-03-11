package com.Da_Technomancer.crossroads.fluids;

import com.Da_Technomancer.crossroads.Main;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockMoltenCopshowium extends BlockFluidClassic{

	protected static final FluidMoltenCopshowium MOLTEN_COPSHOWIUM = new FluidMoltenCopshowium();

	public BlockMoltenCopshowium(){
		super(MOLTEN_COPSHOWIUM, Material.LAVA);
		MOLTEN_COPSHOWIUM.setBlock(this);
		String name = "molten_copshowium";
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this).setRegistryName(name));
	}

	/**
	 * For normal use.
	 */
	public static Fluid getMoltenCopshowium(){
		return FluidRegistry.getFluid("copshowium");
	}

	private static class FluidMoltenCopshowium extends Fluid{

		private FluidMoltenCopshowium(){
			super("copshowium", new ResourceLocation(Main.MODID, "blocks/moltencopshowium_still"), new ResourceLocation(Main.MODID, "blocks/moltencopshowium_flow"));
			setDensity(3000);
			setTemperature(6000);
			setViscosity(1300);
		}
	}
}
