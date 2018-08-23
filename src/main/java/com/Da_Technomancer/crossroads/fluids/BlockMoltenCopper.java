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

public class BlockMoltenCopper extends BlockFluidClassic{

	protected static final FluidMoltenCopper MOLTEN_COPPER = new FluidMoltenCopper();

	public BlockMoltenCopper(){
		super(MOLTEN_COPPER, Material.LAVA);
		MOLTEN_COPPER.setBlock(this);
		setUnlocalizedName("molten_copper");
		setRegistryName("molten_copper");
		ModBlocks.toRegister.add(this);
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos){
		return 15;
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
			setTemperature(1500);
			setViscosity(1300);
		}

	}
}
