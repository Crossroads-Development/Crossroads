package com.Da_Technomancer.crossroads.fluids;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CrossroadsItems;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class CrossroadsFluid{

	public FlowingFluid flowing;
	public FlowingFluid still;
	public FlowingFluidBlock block;
	public Item bucket;

	public CrossroadsFluid(String name){
		CrossroadsFluids.toRegister.add(still = new Still(name));
		CrossroadsFluids.toRegister.add(flowing = new Flowing(name));
		CrossroadsBlocks.toRegister.add(block = (FlowingFluidBlock) new FlowingFluidBlock(() -> still, CrossroadsFluids.BLOCK_PROP).setRegistryName(name + "_block"));
		CrossroadsItems.toRegister.add(bucket = new BucketItem(() -> still, CrossroadsFluids.BUCKET_PROP).setRegistryName(name + "_bucket"));
	}

	private abstract class GenericFluid extends ForgeFlowingFluid{

		private GenericFluid(String name){
			super(new Properties(() -> still, () -> flowing, FluidAttributes.builder(new ResourceLocation(Crossroads.MODID, "blocks/" + name + "_still.png"), new ResourceLocation(Crossroads.MODID, "blocks/" + name + "_flow.png"))).block(() -> block).bucket(() -> bucket));
			setRegistryName(name);
		}
	}

	private class Flowing extends GenericFluid{

		private Flowing(String name){
			super(name);
		}

		@Override
		protected void fillStateContainer(StateContainer.Builder<Fluid, IFluidState> builder) {
			super.fillStateContainer(builder);
			builder.add(LEVEL_1_8);
		}

		@Override
		public boolean isSource(IFluidState state){
			return false;
		}

		@Override
		public int getLevel(IFluidState state){
			return state.get(LEVEL_1_8);
		}
	}

	private class Still extends GenericFluid{

		private Still(String name){
			super(name);
		}

		@Override
		public boolean isSource(IFluidState state){
			return true;
		}

		@Override
		public int getLevel(IFluidState state){
			return 8;
		}
	}
}
