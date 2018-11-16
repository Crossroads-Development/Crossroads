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

public class BlockMoltenIron extends BlockFluidClassic{

	protected static final FluidMoltenIron MOLTEN_IRON = new FluidMoltenIron();

	public BlockMoltenIron(){
		super(MOLTEN_IRON, Material.LAVA);
		MOLTEN_IRON.setBlock(this);
		String name = "molten_iron";
		setTranslationKey(name);
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
	public static Fluid getMoltenIron(){
		return FluidRegistry.getFluid("iron");
	}

	private static class FluidMoltenIron extends Fluid{

		private FluidMoltenIron(){
			super("iron", new ResourceLocation(Main.MODID + ":blocks/molteniron_still"), new ResourceLocation(Main.MODID + ":blocks/molteniron_flow"));
			setDensity(3000);
			setTemperature(2000);
			setViscosity(1300);
		}

	}
}
