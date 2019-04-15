package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.ModConfig;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class ChargeEffect implements IEffect{

	@Override
	public void doEffect(World worldIn, BlockPos pos, int mult, EnumFacing dir){
		TileEntity te = worldIn.getTileEntity(pos);
		IEnergyStorage energy;
		if(te != null && (energy = te.getCapability(CapabilityEnergy.ENERGY, dir)) != null){
			energy.receiveEnergy(ModConfig.getConfigInt(ModConfig.fePerCharge, false), false);
			return;
		}

		if(mult >= 16){
			worldIn.addWeatherEffect(new EntityLightningBolt(worldIn, pos.getX(), pos.getY(), pos.getZ(), false));
		}

		IBlockState state = worldIn.getBlockState(pos);
		if(state.getMaterial() == Material.ROCK && MiscUtil.canBreak(state, false)){
			worldIn.setBlockState(pos, Blocks.REDSTONE_BLOCK.getDefaultState());
		}
	}
	
	public static class VoidChargeEffect implements IEffect{

		@Override
		public void doEffect(World worldIn, BlockPos pos, int mult, EnumFacing dir){
			TileEntity te = worldIn.getTileEntity(pos);
			IEnergyStorage energy;
			if(te != null && (energy = te.getCapability(CapabilityEnergy.ENERGY, dir)) != null){
				energy.extractEnergy(ModConfig.getConfigInt(ModConfig.fePerCharge, false), false);
				return;
			}

			if(worldIn.getBlockState(pos).getBlock() == Blocks.REDSTONE_BLOCK){
				worldIn.setBlockState(pos, Blocks.STONE.getDefaultState());
			}
		}
	}
}
