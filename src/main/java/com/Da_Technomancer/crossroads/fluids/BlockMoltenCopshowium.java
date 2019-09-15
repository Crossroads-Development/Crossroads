package com.Da_Technomancer.crossroads.fluids;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class BlockMoltenCopshowium extends BlockFluidClassic{

	protected static final FluidMoltenCopshowium MOLTEN_COPSHOWIUM = new FluidMoltenCopshowium();

	public BlockMoltenCopshowium(){
		super(MOLTEN_COPSHOWIUM, Material.LAVA);
		MOLTEN_COPSHOWIUM.setBlock(this);
		String name = "molten_copshowium";
		setTranslationKey(name);
		setRegistryName(name);
		CrossroadsBlocks.toRegister.add(this);
	}

	@Override
	public int getLightValue(BlockState state, IBlockAccess world, BlockPos pos){
		return 15;
	}

	/**
	 * For normal use.
	 */
	public static Fluid getMoltenCopshowium(){
		return FluidRegistry.getFluid("copshowium");
	}

	private static class FluidMoltenCopshowium extends Fluid{

		private FluidMoltenCopshowium(){
			super("copshowium", new ResourceLocation(Crossroads.MODID, "blocks/moltencopshowium_still"), new ResourceLocation(Crossroads.MODID, "blocks/moltencopshowium_flow"));
			setDensity(3000);
			setTemperature(1500);
			setViscosity(1300);
		}
	}
}
