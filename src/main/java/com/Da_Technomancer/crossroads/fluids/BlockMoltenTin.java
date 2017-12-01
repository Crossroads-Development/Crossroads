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

public class BlockMoltenTin extends BlockFluidClassic{

	protected static final FluidMoltenTin MOLTEN_TIN = new FluidMoltenTin();

	public BlockMoltenTin(){
		super(MOLTEN_TIN, Material.LAVA);
		MOLTEN_TIN.setBlock(this);
		String name = "molten_tin";
		setUnlocalizedName(name);
		setRegistryName(name);
		ModBlocks.toRegister.add(this);
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos){
		return 15;
	}

	/**
	 * For normal use.
	 */
	public static Fluid getMoltenTin(){
		return FluidRegistry.getFluid("tin");
	}

	private static class FluidMoltenTin extends Fluid{

		private FluidMoltenTin(){
			super("tin", new ResourceLocation(Main.MODID + ":blocks/moltentin_still"), new ResourceLocation(Main.MODID + ":blocks/moltentin_flow"));
			setDensity(3000);
			setTemperature(1500);
			setViscosity(1300);
		}
	}
}
