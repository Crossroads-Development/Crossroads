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

public class BlockSteam extends BlockFluidClassic{

	protected static final FluidSteam STEAM = new FluidSteam();

	public BlockSteam(){
		super(STEAM, Material.WATER);
		STEAM.setBlock(this);
		setUnlocalizedName("steam");
		setRegistryName("steam");
		ModBlocks.toRegister.add(this);
	}

	@Override
	public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos){
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
			super("steam", new ResourceLocation(Main.MODID + ":blocks/steam_still"), new ResourceLocation(Main.MODID + ":blocks/steam_flow"));

			setDensity(-5);
			setTemperature(473); // 200C
			setViscosity(200);
			setGaseous(true);
		}

	}

}