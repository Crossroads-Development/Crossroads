package com.Da_Technomancer.crossroads.blocks.heat;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.heat.HeatReservoirTileEntity;
import com.Da_Technomancer.essentials.blocks.redstone.IReadable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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
		super(Block.Properties.create(Material.IRON).hardnessAndResistance(3));
		String name = "heat_reservoir";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new HeatReservoirTileEntity();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add(new TranslationTextComponent("tt.crossroads.heat_battery.info"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.heat_battery.reds"));
		CompoundNBT nbt = stack.getTag();
		if(nbt != null && nbt.contains("temp")){
			tooltip.add(new TranslationTextComponent("tt.crossroads.heat_battery.temp", CRConfig.formatVal(nbt.getDouble("temp"))));
		}
	}

	@Override
	public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, TileEntity te, ItemStack stackIn){
		if(!(te instanceof HeatReservoirTileEntity)){
			super.harvestBlock(worldIn, player, pos, state, te, stackIn);
		}else{
			player.addExhaustion(0.005F);
			ItemStack stack = new ItemStack(this, 1);
			stack.setTag(((HeatReservoirTileEntity) te).getDropNBT());
			spawnAsEntity(worldIn, pos, stack);
		}
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		TileEntity te;
		CompoundNBT nbt;
		if((nbt = stack.getTag()) != null && (te = world.getTileEntity(pos)) instanceof HeatReservoirTileEntity){
			LazyOptional<IHeatHandler> heatOpt = te.getCapability(Capabilities.HEAT_CAPABILITY, null);
			if(heatOpt.isPresent()){
				heatOpt.orElseThrow(NullPointerException::new).setTemp(nbt.getDouble("temp"));
			}
		}
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state){
		return true;
	}

	@Override
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos){
		TileEntity te = worldIn.getTileEntity(pos);
		LazyOptional<IHeatHandler> heatOpt;
		if(te != null && (heatOpt = te.getCapability(Capabilities.HEAT_CAPABILITY, null)).isPresent()){
			return (int) Math.max(15, Math.round(heatOpt.orElseThrow(NullPointerException::new).getTemp()));
		}
		return 0;
	}

	@Override
	public float read(World world, BlockPos pos, BlockState state){
		TileEntity te = world.getTileEntity(pos);
		LazyOptional<IHeatHandler> heatOpt;
		if(te != null && (heatOpt = te.getCapability(Capabilities.HEAT_CAPABILITY, null)).isPresent()){
			return Math.round(HeatUtil.toKelvin(heatOpt.orElseThrow(NullPointerException::new).getTemp()));
		}
		return 0;
	}
}
