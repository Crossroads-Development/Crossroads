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

public class BlockMoltenGold extends BlockFluidClassic{

	protected static final FluidMoltenGold MOLTEN_GOLD = new FluidMoltenGold();

	public BlockMoltenGold(){
		super(MOLTEN_GOLD, Material.LAVA);
		MOLTEN_GOLD.setBlock(this);
		String name = "molten_gold";
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
	public static Fluid getMoltenGold(){
		return FluidRegistry.getFluid("gold");
	}

	private static class FluidMoltenGold extends Fluid{

		private FluidMoltenGold(){
			super("gold", new ResourceLocation(Crossroads.MODID + ":blocks/moltengold_still"), new ResourceLocation(Crossroads.MODID + ":blocks/moltengold_flow"));
			setDensity(3000);
			setTemperature(1500);
			setViscosity(1300);
		}

	}
}
