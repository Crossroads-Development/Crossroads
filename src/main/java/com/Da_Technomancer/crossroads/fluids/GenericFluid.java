package com.Da_Technomancer.crossroads.fluids;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class GenericFluid extends LiquidBlock{

	private static final Item.Properties BUCKET_PROP = new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(CreativeModeTab.TAB_MISC);

	public static FluidData create(String name, boolean lavaLike, boolean gaseous){
		return create(name, lavaLike, gaseous, lavaLike ? 15 : 0, false);
	}

	public static FluidData create(String name, boolean lavaLike, boolean gaseous, int light, boolean isWater){
		FluidData data = new FluidData();

		FluidType.Properties properties = FluidType.Properties.create();
		properties.descriptionId("fluid.crossroads." + name);
		properties.lightLevel(light);
		if(lavaLike){
			properties.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA);
			properties.canSwim(false);
			properties.canDrown(false);
			properties.pathType(BlockPathTypes.LAVA);
			properties.adjacentPathType(null);
			properties.temperature(1500);
			properties.viscosity(6000);
		}else{
			if(isWater){
				properties.canExtinguish(true);
				properties.canHydrate(true);
//				properties.canConvertToSource(true);
			}
			if(gaseous){
				properties.canDrown(false);
				properties.canSwim(false);
				properties.pathType(BlockPathTypes.OPEN);
				properties.adjacentPathType(null);
				properties.viscosity(500);
				properties.density(-100);
			}else{
				properties.supportsBoating(true);
				properties.fallDistanceModifier(0F);
			}
			properties.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY).sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH);
		}

		final ResourceLocation stillTexture = new ResourceLocation(Crossroads.MODID, "block/" + name + "_still");
		final ResourceLocation flowTexture = new ResourceLocation(Crossroads.MODID, "block/" + name + "_flow");

		data.type = new FluidType(properties){
			@Override
			public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer){
				consumer.accept(new IClientFluidTypeExtensions(){
					@Override
					public ResourceLocation getStillTexture(){
						return stillTexture;
					}

					@Override
					public ResourceLocation getFlowingTexture(){
						return flowTexture;
					}
				});
			}
		};
		data.still = new Still(data::getType, data::getStill, data::getFlowing, data::getBlock, data::getBucket);
		data.flowing = new Flowing(data::getType, data::getStill, data::getFlowing, data::getBlock, data::getBucket);
		data.bucket = new BucketItem(data::getStill, BUCKET_PROP);

		data.block = new GenericFluid(data::getStill, BlockBehaviour.Properties.of(lavaLike ? Material.LAVA : Material.WATER).noCollission().strength(100.0F).noLootTable().lightLevel(state -> light));

		CRFluids.toRegisterType.put(name, data.type);
		CRFluids.toRegisterFluid.put(name, data.still);
		CRFluids.toRegisterFluid.put("flowing_" + name, data.flowing);
		CRBlocks.toRegister.put(name, data.block);
		CRItems.toRegister.put(name + "_bucket", data.bucket);

		return data;
	}

	protected GenericFluid(Supplier<FlowingFluid> still, BlockBehaviour.Properties prop){
		super(still, prop);
	}

	public static class FluidData{

		public FluidType type;
		public FlowingFluid still;
		public FlowingFluid flowing;
		public LiquidBlock block;
		public Item bucket;

		private FluidData(){

		}

		public FluidType getType(){
			return type;
		}

		public FlowingFluid getStill(){
			return still;
		}

		public FlowingFluid getFlowing(){
			return flowing;
		}

		public LiquidBlock getBlock(){
			return block;
		}

		public Item getBucket(){
			return bucket;
		}
	}

	private static class Flowing extends ForgeFlowingFluid.Flowing{

		private Flowing(Supplier<? extends FluidType> typeSupplier, Supplier<? extends Fluid> stillSupplier, Supplier<? extends Fluid> flowSupplier, Supplier<LiquidBlock> blockSupplier, Supplier<Item> bucketSupplier){
			super(new Properties(typeSupplier, stillSupplier, flowSupplier).block(blockSupplier).bucket(bucketSupplier));
		}
	}

	private static class Still extends ForgeFlowingFluid.Source{

		private Still(Supplier<? extends FluidType> typeSupplier, Supplier<? extends Fluid> stillSupplier, Supplier<? extends Fluid> flowSupplier, Supplier<LiquidBlock> blockSupplier, Supplier<Item> bucketSupplier){
			super(new Properties(typeSupplier, stillSupplier, flowSupplier).block(blockSupplier).bucket(bucketSupplier));
		}
	}
}
