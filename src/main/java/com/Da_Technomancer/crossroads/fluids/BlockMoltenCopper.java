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
		//TODO at some point the fluid name should be changed to just "copper" to provide compatability with Tinker's Construct.
		//However, as doing that change would delete any pre-existing molten copper, this change is not to occur until a point where people expect incompatibility
		//with prior worlds, such as a Minecraft version change. 
		return FluidRegistry.getFluid("moltencopper");
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
