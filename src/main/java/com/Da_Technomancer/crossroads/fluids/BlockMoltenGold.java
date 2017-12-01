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

public class BlockMoltenGold extends BlockFluidClassic{

	protected static final FluidMoltenGold MOLTEN_GOLD = new FluidMoltenGold();

	public BlockMoltenGold(){
		super(MOLTEN_GOLD, Material.LAVA);
		MOLTEN_GOLD.setBlock(this);
		String name = "molten_gold";
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
	public static Fluid getMoltenGold(){
		return FluidRegistry.getFluid("gold");
	}

	private static class FluidMoltenGold extends Fluid{

		private FluidMoltenGold(){
			super("gold", new ResourceLocation(Main.MODID + ":blocks/moltengold_still"), new ResourceLocation(Main.MODID + ":blocks/moltengold_flow"));
			setDensity(3000);
			setTemperature(1500);
			setViscosity(1300);
		}

	}
}
