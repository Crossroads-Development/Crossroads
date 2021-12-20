package com.Da_Technomancer.crossroads.blocks.heat;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.heat.HeatReservoirTileEntity;
import com.Da_Technomancer.essentials.blocks.redstone.IReadable;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import com.Da_Technomancer.essentials.tileentities.ITickableTileEntity;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.List;

public class HeatReservoir extends BaseEntityBlock implements IReadable{

	public HeatReservoir(){
		super(CRBlocks.getMetalProperty());
		String name = "heat_reservoir";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
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
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag advanced){
		tooltip.add(new TranslatableComponent("tt.crossroads.heat_battery.info"));
		tooltip.add(new TranslatableComponent("tt.crossroads.heat_battery.reds"));
		CompoundTag nbt = stack.getTag();
		if(nbt != null && nbt.contains("temp")){
			tooltip.add(new TranslatableComponent("tt.crossroads.boilerplate.degrees_c", CRConfig.formatVal(nbt.getDouble("temp"))));
		}
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder){
		BlockEntity te = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if(te instanceof HeatReservoirTileEntity){
			ItemStack drop = new ItemStack(this.asItem(), 1);
			drop.setTag(((HeatReservoirTileEntity) te).getDropNBT());
			return Lists.newArrayList(drop);
		}
		return super.getDrops(state, builder);
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		BlockEntity te;
		CompoundTag nbt;
		if((nbt = stack.getTag()) != null && (te = world.getBlockEntity(pos)) instanceof HeatReservoirTileEntity){
			LazyOptional<IHeatHandler> heatOpt = te.getCapability(Capabilities.HEAT_CAPABILITY, null);
			if(heatOpt.isPresent()){
				heatOpt.orElseThrow(NullPointerException::new).setTemp(nbt.getDouble("temp"));
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
		LazyOptional<IHeatHandler> heatOpt;
		if(te != null && (heatOpt = te.getCapability(Capabilities.HEAT_CAPABILITY, null)).isPresent()){
			return (float) heatOpt.orElseThrow(NullPointerException::new).getTemp();
		}
		return 0;
	}
}
