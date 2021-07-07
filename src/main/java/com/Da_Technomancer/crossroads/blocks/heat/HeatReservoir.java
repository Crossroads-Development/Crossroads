package com.Da_Technomancer.crossroads.blocks.heat;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.heat.HeatReservoirTileEntity;
import com.Da_Technomancer.essentials.blocks.redstone.IReadable;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import com.google.common.collect.Lists;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.List;

public class HeatReservoir extends ContainerBlock implements IReadable{

	public HeatReservoir(){
		super(CRBlocks.getMetalProperty());
		String name = "heat_reservoir";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn){
		return new HeatReservoirTileEntity();
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add(new TranslationTextComponent("tt.crossroads.heat_battery.info"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.heat_battery.reds"));
		CompoundNBT nbt = stack.getTag();
		if(nbt != null && nbt.contains("temp")){
			tooltip.add(new TranslationTextComponent("tt.crossroads.boilerplate.degrees_c", CRConfig.formatVal(nbt.getDouble("temp"))));
		}
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder){
		TileEntity te = builder.getOptionalParameter(LootParameters.BLOCK_ENTITY);
		if(te instanceof HeatReservoirTileEntity){
			ItemStack drop = new ItemStack(this.asItem(), 1);
			drop.setTag(((HeatReservoirTileEntity) te).getDropNBT());
			return Lists.newArrayList(drop);
		}
		return super.getDrops(state, builder);
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		TileEntity te;
		CompoundNBT nbt;
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
	public int getAnalogOutputSignal(BlockState blockState, World worldIn, BlockPos pos){
		return RedstoneUtil.clampToVanilla(read(worldIn, pos, blockState));
	}

	@Override
	public float read(World world, BlockPos pos, BlockState state){
		TileEntity te = world.getBlockEntity(pos);
		LazyOptional<IHeatHandler> heatOpt;
		if(te != null && (heatOpt = te.getCapability(Capabilities.HEAT_CAPABILITY, null)).isPresent()){
			return (float) heatOpt.orElseThrow(NullPointerException::new).getTemp();
		}
		return 0;
	}
}
