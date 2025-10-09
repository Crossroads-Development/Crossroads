package com.Da_Technomancer.crossroads.blocks.heat;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.CRCapabilities;
import com.Da_Technomancer.crossroads.api.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import com.Da_Technomancer.essentials.api.redstone.IReadable;
import com.Da_Technomancer.essentials.api.redstone.RedstoneUtil;
import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import javax.annotation.Nullable;
import java.util.List;

public class HeatReservoir extends BaseEntityBlock implements IReadable{

	public HeatReservoir(){
		super(CRBlocks.getMetalProperty());
		String name = "heat_reservoir";
		CRBlocks.queueForRegister(name, this);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new HeatReservoirTileEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, HeatReservoirTileEntity.TYPE);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec(){
		return CRBlocks.HEAT_RESERVOIR_TYPE.value();
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag){
		tooltip.add(Component.translatable("tt.crossroads.heat_battery.info"));
		tooltip.add(Component.translatable("tt.crossroads.heat_battery.reds"));
		if(stack.has(CRItems.TEMPERATURE_DATA)){
			tooltip.add(Component.translatable("tt.crossroads.boilerplate.degrees_c", CRConfig.formatVal(stack.get(CRItems.TEMPERATURE_DATA))));
		}
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder){
		BlockEntity te = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if(te instanceof HeatReservoirTileEntity hTe){
			ItemStack drop = new ItemStack(this.asItem(), 1);
			drop.set(CRItems.TEMPERATURE_DATA, hTe.getDropTemp());
			return Lists.newArrayList(drop);
		}
		return super.getDrops(state, builder);
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		BlockEntity te;
		CompoundTag nbt;
		if(stack.has(CRItems.TEMPERATURE_DATA) && (te = world.getBlockEntity(pos)) instanceof HeatReservoirTileEntity){
			IHeatHandler otherHeatHandler = world.getCapability(CRCapabilities.HEAT_CAPABILITY, te.getBlockPos(), null);
			if(otherHeatHandler != null){
				otherHeatHandler.setTemp(stack.get(CRItems.TEMPERATURE_DATA));
			}
		}
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state){
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos){
		return RedstoneUtil.clampToVanilla(read(worldIn, pos, blockState));
	}

	@Override
	public float read(Level world, BlockPos pos, BlockState state){
		BlockEntity te = world.getBlockEntity(pos);
		IHeatHandler heatOpt;
		if((heatOpt = world.getCapability(CRCapabilities.HEAT_CAPABILITY, pos, null)) != null){
			return (float) heatOpt.getTemp();
		}
		return 0;
	}
}
