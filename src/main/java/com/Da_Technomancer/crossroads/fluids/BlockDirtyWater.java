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

public class BlockDirtyWater extends BlockFluidClassic{

	protected static final FluidDirtyWater DIRTY_WATER = new FluidDirtyWater();

	public BlockDirtyWater(){
		super(DIRTY_WATER, Material.WATER);
		DIRTY_WATER.setBlock(this);
		setTranslationKey("dirty_water");
		setRegistryName("dirty_water");
		CrossroadsBlocks.toRegister.add(this);
	}

	@Override
	public int getLightOpacity(BlockState state, IBlockAccess world, BlockPos pos){
		return 12;
	}

	/**
	 * For normal use.
	 */
	public static Fluid getDirtyWater(){
		return FluidRegistry.getFluid("dirty_water");
	}

	private static class FluidDirtyWater extends Fluid{

		private FluidDirtyWater(){
			super("dirty_water", new ResourceLocation(Crossroads.MODID, "blocks/dirty_water_still"), new ResourceLocation(Crossroads.MODID, "blocks/dirty_water_flow"));
		}
	}
}
