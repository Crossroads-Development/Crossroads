package com.Da_Technomancer.crossroads.fluids;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class BlockSteam extends BlockFluidClassic{

	protected static final FluidSteam STEAM = new FluidSteam();

	public BlockSteam(){
		super(STEAM, Material.WATER);
		STEAM.setBlock(this);
		setTranslationKey("steam");
		setRegistryName("steam");
		CrossroadsBlocks.toRegister.add(this);
	}

	@Override
	public int getLightOpacity(BlockState state, IBlockAccess world, BlockPos pos){
		return 2;
	}

	/**
	 * For normal use.
	 */
	public static Fluid getSteam(){
		return FluidRegistry.getFluid("steam");
	}

	private static class FluidSteam extends Fluid{

		private FluidSteam(){
			super("steam", new ResourceLocation(Crossroads.MODID + ":blocks/steam_still"), new ResourceLocation(Crossroads.MODID + ":blocks/steam_flow"));

			setDensity(-5);
			setTemperature(473); // 200C
			setViscosity(200);
			setGaseous(true);
		}

	}

}