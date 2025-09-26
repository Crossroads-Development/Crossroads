package com.Da_Technomancer.crossroads.fluids;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.Supplier;

public class GenericFluid extends LiquidBlock{

	private static final Item.Properties BUCKET_PROP = new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1);

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
			properties.pathType(PathType.LAVA);
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
				properties.pathType(PathType.OPEN);
				properties.adjacentPathType(null);
				properties.viscosity(500);
				properties.density(-100);
			}else{
				properties.supportsBoating(true);
				properties.fallDistanceModifier(0F);
			}
			properties.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY).sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH);
		}

		final ResourceLocation stillTexture = ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "block/" + name + "_still");
		final ResourceLocation flowTexture = ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "block/" + name + "_flow");

		data.type = new FluidType(properties);
		data.still = new Still(data::getType, data::getStill, data::getFlowing, data::getBlock, data::getBucket);
		data.flowing = new Flowing(data::getType, data::getStill, data::getFlowing, data::getBlock, data::getBucket);
		data.bucket = new BucketItem(data.getStill(), BUCKET_PROP);

		data.block = new GenericFluid(data.still, (lavaLike ? BlockBehaviour.Properties.of().mapColor(MapColor.FIRE) : BlockBehaviour.Properties.of().mapColor(MapColor.WATER)).liquid().sound(SoundType.EMPTY).pushReaction(PushReaction.DESTROY).noCollission().strength(100.0F).noLootTable().lightLevel(state -> light).replaceable());

		CRFluids.toRegisterType.put(name, data.type);
		CRFluids.toRegisterFluid.put(name, data.still);
		CRFluids.toRegisterClient.add(Pair.of(new ClientFluidExtension(stillTexture, flowTexture), data.type));
		CRFluids.toRegisterFluid.put("flowing_" + name, data.flowing);
		CRBlocks.queueForRegister(name, data.block, false, null);
		CRItems.queueForRegister(name + "_bucket", data.bucket);

		return data;
	}

	protected GenericFluid(FlowingFluid still, BlockBehaviour.Properties prop){
		super(still, prop);
	}

	public static class FluidData{

		private FluidType type;
		private FlowingFluid still;
		private FlowingFluid flowing;
		private LiquidBlock block;
		private Item bucket;

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

	private static class Flowing extends BaseFlowingFluid.Flowing{

		private Flowing(Supplier<? extends FluidType> typeSupplier, Supplier<? extends Fluid> stillSupplier, Supplier<? extends Fluid> flowSupplier, Supplier<LiquidBlock> blockSupplier, Supplier<Item> bucketSupplier){
			super(new Properties(typeSupplier, stillSupplier, flowSupplier).block(blockSupplier).bucket(bucketSupplier));
		}
	}

	private static class Still extends BaseFlowingFluid.Source{

		private Still(Supplier<? extends FluidType> typeSupplier, Supplier<? extends Fluid> stillSupplier, Supplier<? extends Fluid> flowSupplier, Supplier<LiquidBlock> blockSupplier, Supplier<Item> bucketSupplier){
			super(new Properties(typeSupplier, stillSupplier, flowSupplier).block(blockSupplier).bucket(bucketSupplier));
		}
	}

	private static class ClientFluidExtension implements IClientFluidTypeExtensions{

		private final ResourceLocation stillTexture;
		private final ResourceLocation flowTexture;

		public ClientFluidExtension(ResourceLocation stillTexture, ResourceLocation flowTexture){
			this.stillTexture = stillTexture;
			this.flowTexture = flowTexture;
		}

		@Override
		public ResourceLocation getStillTexture(){
			return stillTexture;
		}

		@Override
		public ResourceLocation getFlowingTexture(){
			return flowTexture;
		}

	}
}
